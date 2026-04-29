# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

`interfaces` is a **shared Java library** (jar, not a runnable application) that defines the API contracts and shared DTOs of the ILM platform. It is consumed by the platform core, by `Connectors` that extend the platform, and by integrators. Changes here ripple into every downstream service, so the library is intentionally interface- and model-only with very little executable code.

The artifact is published to Maven Central (`central` profile) and GitHub Packages (`github` profile). The `build.yml` workflow also triggers the sibling `interface-documentation` repo to regenerate OpenAPI docs on every push to `main` — so changes to controller interfaces or models are visible externally.

## Build / test commands

- Java 21 (Temurin in CI) and Maven; the parent POM `com.otilm:dependencies` controls plugin and dependency versions.
- Build + run tests + JaCoCo coverage: `mvn -B verify`
- Compile only: `mvn -B compile`
- Run a single test class: `mvn -B test -Dtest=AttributeDefinitionUtilsTest`
- Run a single test method: `mvn -B test -Dtest=AttributeDefinitionUtilsTest#methodName`
- Publish to Maven Central (signs with GPG, uploads bundle, leaves it in `VALIDATED` for manual Publish): `mvn -B -Pcentral deploy`
- Publish to GitHub Packages: `mvn -B -Pgithub deploy`

There are only four test files (`src/test/java/...`) — three util tests plus `ApiClientTest`. Most of the codebase is interfaces and DTOs, which are excluded from Sonar coverage/CPD via `sonar.coverage.exclusions` and `sonar.cpd.exclusions` in `pom.xml`.

## Architecture

Three layers under `com.otilm`:

1. **`api.interfaces.*`** — Spring controller interfaces (annotated with `@RestController`, `@RequestMapping`, `@Operation`, etc.). These are the **server-side contracts**; consumers implement them. Organized by audience:
   - `interfaces.core.web` — endpoints exposed to the web UI / admin.
   - `interfaces.core.client` (+ `client/v2`) — endpoints exposed to API clients.
   - `interfaces.core.acme` / `cmp` / `scep` — protocol endpoints (each has a flat variant and a `RaProfile` variant for RA‑profile-scoped routing).
   - `interfaces.core.connector`, `interfaces.core.local` — registration and local-only endpoints.
   - `interfaces.connector.*` — contracts a `Connector` must implement so the platform can talk to it.
   - Top-level marker interfaces `AuthProtectedController`, `AuthProtectedConnectorController`, `NoAuthController` describe auth posture and are consumed by the security layer downstream.

2. **`api.clients.*`** — `WebClient`-based (Spring WebFlux + Reactor Netty) HTTP clients. Two unrelated base classes live side-by-side: `BaseApiClient` is the heavy one used to call **Connectors** (TLS via `KeyStoreUtils`, auth-attribute support, `validateConnectorStatus`); `PlatformBaseApiClient` is the much smaller base for calling **internal platform services** (currently only `SchedulerApiClient` extends it). Three parallel client trees exist:
   - `clients/*` — REST clients (default transport).
   - `clients/mq/*` — message-queue proxy variants (added for SaaS deployments where Connectors are reached via MQ instead of direct HTTP). Same surface as the REST clients, different transport.
   - `clients/v2/*` — versioned clients for the v2 API.
   Keep these three trees in sync when a Connector-facing operation is added or changed.

3. **`api.model.*`** — Jackson DTOs shared on the wire between platform, clients, and connectors. Subpackages mirror domains (`certificate`, `cryptography`, `acme`, `scep`, `cmp`, `compliance`, `discovery`, `notification`, `proxy`, `vault`, `workflows`, …). `model.common.attribute.v2` is the current attribute model; `V2AttributeMigrationUtils` and `AttributeMigrationUtils` exist to translate from the v1 shape — touch carefully because connectors in the wild send both.

Cross-cutting:
- `api.exception.*` — typed exceptions used in interface signatures (e.g. `ConnectorException`, `ValidationException`, `AcmeProblemDocumentException`). New error modes should generally extend an existing exception rather than introducing a new top-level type, because every exception type changes the published throws-clause of controller interfaces.
- `api.config.OpenApiConfig` and `config.serializer` — Swagger / Jackson tuning that affects the generated OpenAPI spec consumed by `interface-documentation`.
- `core.util.*` — shared helpers (`AttributeDefinitionUtils`, `KeyStoreUtils`, `BitMaskEnum` patterns). These are the only meaningfully testable code in the repo and are the home of the existing test suite.

## Things to keep in mind when changing code

- Adding/removing a method on a controller interface is a breaking change for the platform and every connector — prefer additive changes and default methods where feasible.
- The OpenAPI doc is generated from springdoc annotations on these interfaces; missing `@Operation` / `@Schema` annotations silently degrade the published docs.
- When adding a new Connector-facing operation, add it to the REST client, the `mq` client, and (if v2-relevant) the v2 client — otherwise SaaS deployments will be missing the call path.
- DTOs are serialized with Jackson + `jackson-datatype-jsr310`; renaming or retyping a field on an existing DTO breaks wire compatibility with deployed connectors.
- Lombok and `springdoc-openapi-starter-common` are on the compile classpath; `jsr305` and `slf4j-simple` are intentionally `provided`/`test` only — don't promote them.
