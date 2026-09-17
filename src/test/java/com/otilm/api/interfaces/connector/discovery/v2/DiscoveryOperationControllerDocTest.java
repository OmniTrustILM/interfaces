package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.discovery.v2.DiscoveryEvent;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryV2ScopedRequestDto;
import com.otilm.api.testsupport.OpenApiProseAssertions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link DiscoveryOperationController}: every operation is documented, every
 * non-2xx response names a real {@link ErrorCode} and carries the {@link ProblemDetailExtended} content schema, no
 * Core-internal design jargon leaks into the published prose (summaries, descriptions, response text, and parameter
 * text alike; see {@link OpenApiProseAssertions} for the shared banned-term list), and the controller extends the NG
 * (common.v2) auth base rather than any legacy one.
 */
class DiscoveryOperationControllerDocTest {

    @Test
    void everyOperationIsDocumented() {
        Method[] methods = DiscoveryOperationController.class.getDeclaredMethods();
        assertTrue(methods.length >= 7, "expected at least 7 endpoints, found " + methods.length);

        for (Method m : methods) {
            Operation op = m.getAnnotation(Operation.class);
            assertNotNull(op, "missing @Operation on " + m.getName());
            assertFalse(op.summary().isBlank(), "blank @Operation summary on " + m.getName());
            assertFalse(op.description().isBlank(), "blank @Operation description on " + m.getName());

            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            boolean hasSuccess = Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().startsWith("2"));
            assertTrue(hasSuccess, "no 2xx response documented on " + m.getName());
            for (ApiResponse r : responses.value()) {
                assertFalse(r.description().isBlank(), "blank response description on " + m.getName());
                assertNoJargon(m.getName() + " " + r.responseCode() + " response", r.description());
            }

            for (Parameter p : m.getParameters()) {
                // Fully qualified deliberately: Parameter is already java.lang.reflect.Parameter
                // here, the reflected method parameter this loop walks. Do not "clean up" into an
                // import - it would shadow the reflection type this file depends on.
                io.swagger.v3.oas.annotations.Parameter param = p
                        .getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                if (param != null) {
                    assertFalse(param.description().isBlank(), "blank @Parameter description on " + m.getName());
                    assertNoJargon(m.getName() + " parameter", param.description());
                }
            }

            assertNoJargon(m.getName(), op.summary());
            assertNoJargon(m.getName(), op.description());
        }
    }

    @Test
    void controllerExtendsCommonV2AuthBase() {
        List<String> supers = Arrays
                .stream(DiscoveryOperationController.class.getInterfaces())
                .map(Class::getName)
                .toList();
        assertTrue(supers.contains("com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController"),
                "DiscoveryOperationController must extend the common.v2 auth base, not a legacy one; found " + supers);
    }

    @Test
    void tagDescriptionIsDocumentedAndJargonFree() {
        Tag tag = DiscoveryOperationController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "missing @Tag on DiscoveryOperationController");
        assertFalse(tag.description().isBlank(), "blank @Tag description");
        assertNoJargon("@Tag", tag.description());
    }

    @Test
    void everyLifecycleOperationDocumentsNotTracked404() {
        List<String> lifecycleOps = List.of("status", "results", "stream", "stop", "resume", "cancel");
        for (Method m : DiscoveryOperationController.class.getDeclaredMethods()) {
            if (!lifecycleOps.contains(m.getName())) {
                continue;
            }
            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            boolean has404 = Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().equals("404"));
            assertTrue(has404, "expected a documented 404 (run not tracked) on " + m.getName());
        }
    }

    @Test
    void resumeDocuments410CheckpointLost() {
        Method resume = Arrays
                .stream(DiscoveryOperationController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("resume"))
                .findFirst()
                .orElseThrow();
        ApiResponses responses = resume.getAnnotation(ApiResponses.class);
        assertNotNull(responses, "missing @ApiResponses on resume");
        boolean has410 = Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().equals("410"));
        assertTrue(has410, "expected a documented 410 (checkpoint lost) on resume");
    }

    /**
     * Without an explicit {@code content}, springdoc unwraps the method's return type ({@code Flux<DiscoveryEvent>})
     * into a plain array schema, which tells a codegen client to parse the response body as a single JSON array — the
     * opposite of the NDJSON contract (one event object per line) the operation's own description promises. This is the
     * same argument {@link #every4xxOr5xxResponseDeclaresProblemDetailContent()} makes for error responses, extended to
     * stream's 200.
     */
    @Test
    void streamOperationDeclares200AsNdjsonOfDiscoveryEvent() {
        Method stream = Arrays
                .stream(DiscoveryOperationController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("stream"))
                .findFirst()
                .orElseThrow();
        ApiResponses responses = stream.getAnnotation(ApiResponses.class);
        assertNotNull(responses, "missing @ApiResponses on stream");
        ApiResponse ok = Arrays
                .stream(responses.value())
                .filter(r -> r.responseCode().equals("200"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a documented 200 on stream"));

        boolean declaresNdjsonOfDiscoveryEvent = Arrays
                .stream(ok.content())
                .anyMatch(c -> c.mediaType().equals(MediaType.APPLICATION_NDJSON_VALUE)
                        && c.schema().implementation() == DiscoveryEvent.class);
        assertTrue(declaresNdjsonOfDiscoveryEvent,
                "stream's 200 must declare application/x-ndjson content of DiscoveryEvent, or springdoc "
                        + "unwraps the Flux<DiscoveryEvent> return type into a JSON array schema instead");
    }

    /**
     * {@code checkpoint} is declared on {@link DiscoveryV2ScopedRequestDto}, so every operation whose request body
     * extends that base replays it. The two responses that mint it must name all of them, initiate excepted: that is
     * the call it comes back from.
     */
    @Test
    void handleMintingResponsesNameEveryOperationThatReplaysTheCheckpoint() throws NoSuchFieldException {
        List<String> replayingOps = Arrays
                .stream(DiscoveryOperationController.class.getDeclaredMethods())
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> DiscoveryV2ScopedRequestDto.class.isAssignableFrom(m.getParameterTypes()[0]))
                .map(Method::getName)
                .filter(name -> !name.equals("initiate"))
                .toList();
        assertFalse(replayingOps.isEmpty(), "expected at least one operation replaying the checkpoint");

        for (Class<?> response : List.of(DiscoveryInitiateResponseDto.class, DiscoveryStopResponseDto.class)) {
            String description = response.getDeclaredField("checkpoint").getAnnotation(Schema.class).description();
            for (String op : replayingOps) {
                assertTrue(description.contains(op), response.getSimpleName() + "'s checkpoint description must name "
                        + "the " + op + " operation, which replays it; was: " + description);
            }
        }
    }

    @Test
    void initiateStopAndCancelDocument422() {
        List<String> opsExpecting422 = List.of("initiate", "stop", "cancel");
        for (Method m : DiscoveryOperationController.class.getDeclaredMethods()) {
            if (!opsExpecting422.contains(m.getName())) {
                continue;
            }
            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            boolean has422 = Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().equals("422"));
            assertTrue(has422, "expected a documented 422 on " + m.getName());
        }
    }

    /**
     * Every non-2xx response must name a real {@link ErrorCode} in its description, so a connector author reading the
     * generated document (not the enum) knows which value to populate on {@code ProblemDetailExtended.errorCode} — a
     * required field. A typo like "OPERATION_NOT_TRACKKED" fails this test instead of publishing silently.
     */
    @Test
    void every4xxOr5xxResponseNamesAKnownErrorCode() {
        for (Method m : DiscoveryOperationController.class.getDeclaredMethods()) {
            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            for (ApiResponse r : responses.value()) {
                if (!isErrorStatus(r.responseCode())) {
                    continue;
                }
                assertTrue(Arrays.stream(ErrorCode.values()).anyMatch(c -> r.description().contains(c.name())),
                        "response " + r.responseCode() + " on " + m.getName() + " does not name a known ErrorCode");
            }
        }
    }

    /**
     * Every non-2xx response must declare the {@link ProblemDetailExtended} content schema explicitly. The base
     * {@code AuthProtectedConnectorController} only supplies it for 401/404/500, and springdoc merges by response code
     * with method-level precedence — so an unadorned method-level {@code @ApiResponse} for one of those codes (e.g.
     * 404) would strip the base's schema rather than inherit it, and a code the base never declares (422, 410) would
     * carry no schema at all. Without this, a generated client has no error model for the error branch.
     */
    @Test
    void every4xxOr5xxResponseDeclaresProblemDetailContent() {
        for (Method m : DiscoveryOperationController.class.getDeclaredMethods()) {
            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            for (ApiResponse r : responses.value()) {
                if (!isErrorStatus(r.responseCode())) {
                    continue;
                }
                Content[] content = r.content();
                boolean hasProblemDetail = Arrays
                        .stream(content)
                        .anyMatch(c -> c.mediaType().equals(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                && c.schema().implementation() == ProblemDetailExtended.class);
                assertTrue(hasProblemDetail, "response " + r.responseCode() + " on " + m.getName()
                        + " does not declare ProblemDetailExtended content");
            }
        }
    }

    private static boolean isErrorStatus(String responseCode) {
        return responseCode.startsWith("4") || responseCode.startsWith("5");
    }
}
