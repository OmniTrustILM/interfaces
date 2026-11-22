package com.otilm.api.clients.signing.contentsigning;

import com.otilm.api.interfaces.connector.signing.contentsigning.ContentSigningFormattingController;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the routes both clients dial against the routes {@link ContentSigningFormattingController} declares. Several
 * operations share a Java signature, so a client addressing the wrong one would satisfy any test that only checked the
 * response.
 */
class ContentSigningFormattingPathsTest {

    /** {@code /extendToLevel/status} and {@code /extendToLevel/cancel}, which carry no operation code of their own. */
    private static final int ENVELOPE_COMPANION_ROUTES = 2;

    /** {@code /validateProfile}, which carries no operation code of its own. */
    private static final int PROFILE_ROUTES = 1;

    @Test
    void theBasePathMatchesTheController() {
        RequestMapping mapping = ContentSigningFormattingController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping on ContentSigningFormattingController");
        assertEquals(mapping.value()[0], ContentSigningFormattingPaths.BASE);
    }

    /** Each controller method is named for the operation it serves, which is also that operation's wire code. */
    @Test
    void everyOperationRouteMatchesItsControllerMapping() {
        for (ContentSigningFormattingOperation operation : ContentSigningFormattingOperation.values()) {
            Method method = controllerMethod(operation.getCode());
            PostMapping mapping = method.getAnnotation(PostMapping.class);

            assertNotNull(mapping, "missing @PostMapping on " + method.getName());
            assertEquals(ContentSigningFormattingPaths.BASE + mapping.path()[0],
                    ContentSigningFormattingPaths.operation(operation), operation.name());
        }
    }

    @Test
    void everyAttributeRouteMatchesTheControllerTemplate() {
        GetMapping mapping = controllerMethod("listFormattingAttributes").getAnnotation(GetMapping.class);
        assertNotNull(mapping, "missing @GetMapping on listFormattingAttributes");

        for (ContentSigningFormattingOperation operation : ContentSigningFormattingOperation.values()) {
            String expected = ContentSigningFormattingPaths.BASE
                    + mapping.path()[0].replace("{operation}", operation.getCode());

            assertEquals(expected, ContentSigningFormattingPaths.attributes(operation), operation.name());
        }
    }

    @Test
    void everyAttributeRouteExtendsItsOperationRoute() {
        for (ContentSigningFormattingOperation operation : ContentSigningFormattingOperation.values()) {
            assertEquals(ContentSigningFormattingPaths.operation(operation) + "/attributes",
                    ContentSigningFormattingPaths.attributes(operation), operation.name());
        }
    }

    @Test
    void routesUseTheWireCode() {
        assertEquals("/v1/signatureProvider/contentSigning/computeDtbs",
                ContentSigningFormattingPaths.operation(ContentSigningFormattingOperation.COMPUTE_DTBS));
        assertEquals("/v1/signatureProvider/contentSigning/embedArchiveTimestamp/attributes",
                ContentSigningFormattingPaths.attributes(ContentSigningFormattingOperation.EMBED_ARCHIVE_TIMESTAMP));
    }

    /**
     * The extension's status and cancel routes are envelope companions rather than ladder operations, so they have no
     * enum code — but they are still routes both clients dial, so they are pinned here.
     */
    @Test
    void theEnvelopeCompanionsExtendTheExtensionRoute() {
        String extendToLevel = ContentSigningFormattingPaths
                .operation(ContentSigningFormattingOperation.EXTEND_TO_LEVEL);

        assertEquals(extendToLevel + "/status", ContentSigningFormattingPaths.EXTEND_TO_LEVEL_STATUS);
        assertEquals(extendToLevel + "/cancel", ContentSigningFormattingPaths.EXTEND_TO_LEVEL_CANCEL);

        assertEquals(ContentSigningFormattingPaths.EXTEND_TO_LEVEL_STATUS, companionRoute("getExtendToLevelStatus"));
        assertEquals(ContentSigningFormattingPaths.EXTEND_TO_LEVEL_CANCEL, companionRoute("cancelExtendToLevel"));
    }

    @Test
    void theProfileRouteMatchesTheController() {
        assertEquals(ContentSigningFormattingPaths.BASE + "/validateProfile",
                ContentSigningFormattingPaths.VALIDATE_PROFILE);
        assertEquals(ContentSigningFormattingPaths.VALIDATE_PROFILE, companionRoute("validateProfile"));
    }

    private static String companionRoute(String methodName) {
        PostMapping mapping = controllerMethod(methodName).getAnnotation(PostMapping.class);

        assertNotNull(mapping, "missing @PostMapping on " + methodName);
        return ContentSigningFormattingPaths.BASE + mapping.path()[0];
    }

    @Test
    void theControllerDeclaresNoRouteTheEnumDoesNotName() {
        long postRoutes = Arrays
                .stream(ContentSigningFormattingController.class.getDeclaredMethods())
                .filter(m -> m.getAnnotation(PostMapping.class) != null)
                .count();

        assertEquals(ContentSigningFormattingOperation.values().length + ENVELOPE_COMPANION_ROUTES + PROFILE_ROUTES,
                postRoutes, "the controller posts to a route outside the enum codes, the envelope companions and the "
                        + "profile route");
        assertTrue(postRoutes > 0);
    }

    private static Method controllerMethod(String name) {
        return Arrays
                .stream(ContentSigningFormattingController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no controller method named " + name));
    }
}
