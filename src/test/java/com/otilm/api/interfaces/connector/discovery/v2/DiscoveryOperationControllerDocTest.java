package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryV2ScopedRequestDto;
import com.otilm.api.testsupport.OpenApiProseAssertions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(methods.length >= 6, "expected at least 6 endpoints, found " + methods.length);

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
        List<String> lifecycleOps = List.of("status", "results", "stop", "resume", "cancel");
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

    /**
     * Every operation whose body extends the scoped base can answer 422 for a missing runId or an empty resources set,
     * so every one of them documents it. Derived from the parameter types rather than a list, so a new operation cannot
     * slip past by not being named here.
     */
    @Test
    void everyBodyTakingOperationDocuments422() {
        List<String> covered = new ArrayList<>();
        for (Method m : DiscoveryOperationController.class.getDeclaredMethods()) {
            boolean takesScopedBody = Arrays
                    .stream(m.getParameters())
                    .anyMatch(p -> DiscoveryV2ScopedRequestDto.class.isAssignableFrom(p.getType()));
            if (!takesScopedBody) {
                continue;
            }
            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            boolean has422 = Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().equals("422"));
            assertTrue(has422, "expected a documented 422 on " + m.getName());
            covered.add(m.getName());
        }
        assertEquals(Set.of("initiate", "status", "results", "stop", "resume", "cancel"), Set.copyOf(covered),
                "the scoped base is replayed by exactly these operations");
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

    @Test
    void resultsDescriptionCarriesTheCheckpointedDeliveryRules() {
        String description = operation("results").getAnnotation(Operation.class).description();
        for (String rule : List
                .of("afterSequence + 1", "the checkpoint after its items", "call again now",
                        "nothing more is ready now", "100 items", "acknowledgement", "20 seconds", "503")) {
            assertTrue(description.contains(rule), "results description must state \"" + rule + "\": " + description);
        }
    }

    @Test
    void resultsDocumentsABusyConnector503() {
        ApiResponses responses = operation("results").getAnnotation(ApiResponses.class);
        assertTrue(Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().equals("503")),
                "expected a documented 503 (connector busy) on results");
    }

    @Test
    void initiateAndStopDescribeDeliveryModes() {
        assertTrue(operation("initiate").getAnnotation(Operation.class).description().contains("delivery modes"));
        assertTrue(operation("stop").getAnnotation(Operation.class).description().contains("without a checkpoint"));
    }

    @Test
    void requestCheckpointNamesTheResultsPageAsItsSourceInCheckpointedDelivery() throws NoSuchFieldException {
        String description = DiscoveryV2ScopedRequestDto.class
                .getDeclaredField("checkpoint")
                .getAnnotation(Schema.class)
                .description();
        assertTrue(description.contains("checkpointed delivery, results"), description);
    }

    @Test
    void resultsBusyResponseIsScopedToCheckpointedDelivery() {
        String description = response("results", "503").description();
        assertTrue(description.contains("In checkpointed delivery"), description);
        assertTrue(description.contains("does not offer checkpointed"), description);
    }

    @Test
    void resumeResponsesFollowTheDeliveryModeRules() {
        String accepted = response("resume", "202").description();
        String refused = response("resume", "422").description();
        assertTrue(accepted.contains("in buffered delivery"), accepted);
        assertTrue(refused.contains("delivery modes"), refused);
    }

    @Test
    void moreIsDescribedInTheWordsOfTheResultsContract() throws NoSuchFieldException {
        String field = DiscoveryResultsResponseDto.class
                .getDeclaredField("more")
                .getAnnotation(Schema.class)
                .description();
        String page = response("results", "200").description();
        assertTrue(field.contains("nothing more is ready now"), field);
        assertTrue(page.contains("nothing more is ready now"), page);
    }

    @Test
    void offerSaysItTravelsOnEveryCallAndWhatAnEmptyListMeans() throws NoSuchFieldException {
        String offer = DiscoveryV2ScopedRequestDto.class
                .getDeclaredField("deliveryModes")
                .getAnnotation(ArraySchema.class)
                .arraySchema()
                .description();
        String results = operation("results").getAnnotation(Operation.class).description();
        assertTrue(offer.contains("a call without it comes from a Core"), offer);
        assertTrue(offer.contains("An empty list offers no mode"), offer);
        assertTrue(results.contains("does not offer checkpointed"), results);
        assertTrue(results.contains("comes from a Core that does not drive checkpointed runs"), results);
        assertTrue(results.contains("a Core that offers checkpointed can take the retry"), results);
    }

    @Test
    void tagScopes404ToRunsThatCannotBeServedFromTheRequest() {
        String tag = DiscoveryOperationController.class.getAnnotation(Tag.class).description();
        assertFalse(tag.contains("Answer 404 only for a runId you cannot rebuild"), tag);
        assertTrue(tag.contains("In buffered delivery, if you can rebuild the run"), tag);
        assertTrue(tag.contains("a replayed checkpoint that disagrees with afterSequence"), tag);
        assertTrue(tag.contains("a checkpoint you cannot read is an invalid request"), tag);
    }

    @Test
    void anyCheckpointed422OnResultsEndsTheRun() {
        String refused = response("results", "422").description();
        String results = operation("results").getAnnotation(Operation.class).description();
        assertTrue(refused.contains("any 422 ends the run"), refused);
        assertTrue(results.contains("cannot read"), results);
    }

    @Test
    void busyRetryNamesNoTransportCarrier() {
        String results = operation("results").getAnnotation(Operation.class).description();
        assertFalse(results.contains("Retry-After"), results);
    }

    @Test
    void resumeNamesTheCheckpointCoreActuallyHolds() {
        String resume = operation("resume").getAnnotation(Operation.class).description();
        assertTrue(resume.contains("the stop's checkpoint"), resume);
        assertTrue(resume.contains("from initiate"), resume);
        assertFalse(resume.contains("Core keeps the one it stored with the last page"), resume);
    }

    @Test
    void checkpointFieldsStateTheirCheckpointedDeliveryRules() throws NoSuchFieldException {
        String stop = schemaDescription(DiscoveryStopResponseDto.class, "checkpoint");
        String initiate = schemaDescription(DiscoveryInitiateResponseDto.class, "checkpoint");
        String results = schemaDescription(DiscoveryResultsResponseDto.class, "checkpoint");
        assertTrue(stop.contains("In checkpointed delivery, omit it"), stop);
        assertTrue(stop.contains("Core keeps the checkpoint it holds"), stop);
        assertTrue(initiate.contains("a resume response omits it"), initiate);
        assertTrue(results.contains("a larger one fails the run"), results);
    }

    @Test
    void maxBytesCountsTheItemsAndCoreLeavesRoomForTheCheckpointAndTheResponse() throws NoSuchFieldException {
        String maxBytes = schemaDescription(DiscoveryDrainRequestDto.class, "maxBytes");
        String checkpoint = schemaDescription(DiscoveryResultsResponseDto.class, "checkpoint");
        assertEquals(131_072L, DiscoveryDrainRequestDto.CHECKPOINTED_RESPONSE_RESERVE);
        assertTrue(maxBytes
                .contains("10 MiB less 128 KB (" + DiscoveryDrainRequestDto.CHECKPOINTED_RESPONSE_RESERVE + " bytes)"),
                maxBytes);
        assertTrue(maxBytes.contains("the page's checkpoint (at most 64 KB) and the rest of the response"), maxBytes);
        assertTrue(maxBytes.contains("leaving the same room"), maxBytes);
        assertTrue(checkpoint.contains("does not count against maxBytes"), checkpoint);
    }

    @Test
    void stopNamesTheCallsCoreSuspends() {
        String stop = operation("stop").getAnnotation(Operation.class).description();
        assertTrue(stop.contains("no status or results call"), stop);
        assertFalse(stop.contains("does not call the connector"), stop);
        assertFalse(stop.contains("last page"), stop);
        assertTrue(stop.contains("A stop past the point of no return is still refused with 422"), stop);
    }

    private static String schemaDescription(Class<?> type, String field) throws NoSuchFieldException {
        return type.getDeclaredField(field).getAnnotation(Schema.class).description();
    }

    private static ApiResponse response(String operation, String code) {
        return Arrays
                .stream(operation(operation).getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals(code))
                .findFirst()
                .orElseThrow();
    }

    private static Method operation(String name) {
        return Arrays
                .stream(DiscoveryOperationController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private static boolean isErrorStatus(String responseCode) {
        return responseCode.startsWith("4") || responseCode.startsWith("5");
    }
}
