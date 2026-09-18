package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.discovery.DiscoveryDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ValueConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the published wire shape of the discovery v2 additions to {@link DiscoveryController} against annotation values
 * rather than prose. These paths are what the frontend's generated client calls and what Core implements, so a typo in
 * one of them has to fail a build rather than ship.
 *
 * <p>
 * Only the new members are asserted. The pre-existing ones are deliberately left alone: the v2 extension is additive,
 * and a test that swept every declared method would start failing the moment an unrelated endpoint was added.
 */
class DiscoveryControllerContractTest {

    private static final String ITEMS_PATH = "/{uuid}/items";
    private static final String MESSAGES_PATH = "/{uuid}/messages";
    private static final Set<String> LIFECYCLE_METHODS = Set.of("stopDiscovery", "resumeDiscovery", "cancelDiscovery");

    private Method method(String name) {
        return Arrays
                .stream(DiscoveryController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("DiscoveryController declares no method named " + name));
    }

    @Test
    void basePathIsUnchanged() {
        RequestMapping mapping = DiscoveryController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping on DiscoveryController");
        assertEquals(1, mapping.value().length, "expected exactly one base path");
        assertEquals("/v1/discoveries", mapping.value()[0]);
    }

    @Test
    void itemsListingIsAGetOnItsDocumentedPath() {
        Method items = method("getDiscoveryItems");
        GetMapping mapping = items.getAnnotation(GetMapping.class);
        assertNotNull(mapping, "expected @GetMapping on getDiscoveryItems");
        assertEquals(1, mapping.path().length, "expected exactly one path on getDiscoveryItems");
        assertEquals(ITEMS_PATH, mapping.path()[0]);
    }

    @Test
    void itemsListingCarriesBothPagingParamsWithTheDocumentedDefaults() {
        // Each default asserted against the name that carries it. Looking these up by type and default instead
        // would survive the two swapping places, since "some int param defaults to 10" stays true either way.
        RequestParam itemsPerPage = requestParamNamed("getDiscoveryItems", "itemsPerPage");
        assertEquals("10", itemsPerPage.defaultValue());
        assertFalse(itemsPerPage.required(), "itemsPerPage must stay optional");

        RequestParam pageNumber = requestParamNamed("getDiscoveryItems", "pageNumber");
        assertEquals("0", pageNumber.defaultValue());
        assertFalse(pageNumber.required(), "pageNumber must stay optional");
    }

    /**
     * The filter is not cosmetic parity. For certificates, "was this already in inventory" decides whether the run
     * enters its processing phase at all, and it is the axis the results view separates on; a resource-agnostic listing
     * that cannot express it would make keys second-class for no reason other than omission.
     */
    @Test
    void itemsListingOffersTheSameNewlyDiscoveredFilterAsTheCertificateListing() {
        RequestParam onItems = requestParamNamed("getDiscoveryItems", "newlyDiscovered");
        RequestParam onCertificates = requestParamNamed("getDiscoveryCertificates", "newlyDiscovered");

        assertEquals(onCertificates.required(), onItems.required());
        assertFalse(onItems.required(), "the filter must be optional — omitting it lists everything");
    }

    @Test
    void itemsListingPagingMatchesTheCertificateListingConvention() {
        // The new listing must not invent its own paging contract: the two defaults are the house
        // convention, and the frontend pages both listings with the same helper.
        assertEquals(pagingDefaults("getDiscoveryCertificates"), pagingDefaults("getDiscoveryItems"),
                "the items listing must page exactly like the certificate listing");
    }

    @Test
    void runMessagesListingIsAGetOnItsDocumentedPath() {
        Method messages = method("getDiscoveryRunMessages");
        GetMapping mapping = messages.getAnnotation(GetMapping.class);
        assertNotNull(mapping, "expected @GetMapping on getDiscoveryRunMessages");
        assertEquals(1, mapping.path().length, "expected exactly one path on getDiscoveryRunMessages");
        assertEquals(MESSAGES_PATH, mapping.path()[0]);
    }

    @Test
    void runMessagesListingPagesLikeEveryOtherListing() {
        // Same reasoning as the items listing: the two defaults are the house convention and the frontend pages
        // all three listings with one helper, so a listing that invents its own contract breaks that helper.
        assertEquals(pagingDefaults("getDiscoveryCertificates"), pagingDefaults("getDiscoveryRunMessages"),
                "the run messages listing must page exactly like the certificate listing");

        RequestParam itemsPerPage = requestParamNamed("getDiscoveryRunMessages", "itemsPerPage");
        assertFalse(itemsPerPage.required(), "itemsPerPage must stay optional");
        RequestParam pageNumber = requestParamNamed("getDiscoveryRunMessages", "pageNumber");
        assertFalse(pageNumber.required(), "pageNumber must stay optional");
    }

    /**
     * The log is reachable only by uuid, so a run that does not exist and a run with an empty log must not look the
     * same to a client — the first is a 404, the second a page with no items.
     */
    @Test
    void runMessagesListingDocumentsTheNotFoundAnswer() {
        ApiResponses responses = method("getDiscoveryRunMessages").getAnnotation(ApiResponses.class);
        assertNotNull(responses, "expected @ApiResponses on getDiscoveryRunMessages");
        Set<String> codes = Arrays.stream(responses.value()).map(ApiResponse::responseCode).collect(Collectors.toSet());
        assertTrue(codes.contains("200"), "the success answer must be documented: " + codes);
        assertTrue(codes.contains("404"), "an unknown run must be documented as 404, not an empty page: " + codes);
    }

    @Test
    void resourceFilterIsOptionalAndUndefaulted() {
        Parameter resource = parameterOfType("getDiscoveryItems", Resource.class);
        RequestParam param = resource.getAnnotation(RequestParam.class);
        assertNotNull(param, "the resource filter must be a @RequestParam, not a path variable");
        assertFalse(param.required(), "the resource filter must stay optional");
        // Defaulting the filter would silently narrow an unfiltered listing to one resource type,
        // which is a different endpoint than the one documented.
        assertEquals(ValueConstants.DEFAULT_NONE, param.defaultValue(),
                "the resource filter must carry no default: absent means every resource type");
    }

    @ParameterizedTest
    @ValueSource(strings = {"stopDiscovery", "resumeDiscovery", "cancelDiscovery"})
    void lifecycleOperationIsAPatchOnItsDocumentedPath(String name) {
        Method m = method(name);
        PatchMapping mapping = m.getAnnotation(PatchMapping.class);
        assertNotNull(mapping, "expected @PatchMapping on " + name + " — PATCH is the house verb for state "
                + "transitions (enable/disable/approve/archive), and v2 ConnectorController deliberately migrated "
                + "approve from PUT");
        assertEquals(1, mapping.path().length, "expected exactly one path on " + name);

        String verb = name.substring(0, name.length() - "Discovery".length());
        assertEquals("/{uuid}/" + verb, mapping.path()[0], "path mismatch on " + name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"stopDiscovery", "resumeDiscovery", "cancelDiscovery"})
    void lifecycleOperationAnswersNoContent(String name) {
        Method m = method(name);
        ResponseStatus status = m.getAnnotation(ResponseStatus.class);
        assertNotNull(status, "expected @ResponseStatus on " + name);
        assertEquals(HttpStatus.NO_CONTENT, status.value(), "lifecycle operations answer 204 on success");
        assertTrue(documentedCodes(m).contains("204"), "204 must also be documented on " + name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"stopDiscovery", "resumeDiscovery", "cancelDiscovery"})
    void lifecycleOperationDocumentsTheLegalityMatrixOutcomes(String name) {
        Set<String> codes = documentedCodes(method(name));
        // Every refusal is a 422 — an illegal status for the run and an unsupporting Provider alike —
        // matching how the platform reports every other illegal state transition (ValidationException).
        // 409 is pinned absent: Core's only CONFLICT mapping is AlreadyExistException, so a documented
        // 409 here would be a contract no implementation can produce.
        assertTrue(codes.contains("422"),
                "422 (illegal status or unsupporting Provider) must be documented on " + name);
        assertFalse(codes.contains("409"), "409 must not be documented on " + name
                + " — the platform has no CONFLICT mapping for run state, refusals are 422");
        assertTrue(codes.contains("404"), "404 (Discovery not found) must be documented on " + name);
    }

    /**
     * The jakarta constraints on {@link DiscoveryDto} are live only through this one token: without {@code @Valid} on
     * the request body they document intent and enforce nothing.
     */
    @Test
    void createRequestBodyIsBeanValidated() {
        Parameter request = parameterOfType("createDiscovery", DiscoveryDto.class);
        assertNotNull(request.getAnnotation(Valid.class),
                "createDiscovery's request body must carry @Valid — the DTO's constraints are inert without it");
    }

    @Test
    void everyLifecycleOperationIsAccountedFor() {
        Set<String> declared = Arrays
                .stream(DiscoveryController.class.getDeclaredMethods())
                .map(Method::getName)
                .filter(LIFECYCLE_METHODS::contains)
                .collect(Collectors.toSet());
        assertEquals(LIFECYCLE_METHODS, declared, "the three lifecycle operations must all be declared");
    }

    @Test
    void deleteDocumentsTheLiveRunRefusal() {
        assertTrue(documentedCodes(method("deleteDiscovery")).contains("422"),
                "a live v2 run is refused deletion with 422; the contract must say so");
    }

    private Set<String> documentedCodes(Method m) {
        ApiResponses responses = m.getAnnotation(ApiResponses.class);
        assertNotNull(responses, "missing @ApiResponses on " + m.getName());
        return Arrays.stream(responses.value()).map(ApiResponse::responseCode).collect(Collectors.toSet());
    }

    /**
     * Each paging parameter as {@code name=default/required}, keyed by published name and sorted.
     *
     * <p>
     * Keyed rather than positional so the comparison actually constrains the pair: an unkeyed list of {@code int}
     * defaults reads the same for both listings even if one of them swapped its two parameters.
     */
    private String pagingDefaults(String methodName) {
        return Arrays
                .stream(method(methodName).getParameters())
                .filter(p -> p.getType() == int.class && p.getAnnotation(RequestParam.class) != null)
                .map(p -> publishedNameOf(p) + "=" + p.getAnnotation(RequestParam.class).defaultValue() + "/required="
                        + p.getAnnotation(RequestParam.class).required())
                .sorted()
                .collect(Collectors.joining(","));
    }

    /**
     * Looks a {@code @RequestParam} up by the name it publishes, not by its Java type.
     *
     * <p>
     * A type-based lookup cannot see the thing these tests assert. Every claim here is about the wire — that the filter
     * is spelled {@code newlyDiscovered} on both listings, that paging is {@code itemsPerPage}/{@code
     * pageNumber} — and matching on {@code Boolean.class} or {@code int.class} stays green through a rename, or through
     * the two paging parameters being swapped.
     */
    private RequestParam requestParamNamed(String methodName, String publishedName) {
        return Arrays
                .stream(method(methodName).getParameters())
                .filter(p -> publishedName.equals(publishedNameOf(p)))
                .map(p -> p.getAnnotation(RequestParam.class))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        methodName + " publishes no @RequestParam named '" + publishedName + "'"));
    }

    /** The name Spring binds by: the annotation's explicit value, else the compiled parameter name. */
    private static String publishedNameOf(Parameter p) {
        RequestParam annotation = p.getAnnotation(RequestParam.class);
        if (annotation == null) {
            return null;
        }
        return annotation.value().isEmpty() ? p.getName() : annotation.value();
    }

    private Parameter parameterOfType(String methodName, Class<?> type) {
        return Arrays
                .stream(method(methodName).getParameters())
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError(methodName + " takes no " + type.getSimpleName() + " parameter"));
    }
}
