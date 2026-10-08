package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.certificate.SearchRequestDto;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestBody;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every {@link SearchRequestDto} body must carry {@code @Valid}, because the constraints on the shape are inert without
 * it, and must document the 422 it can now return. The compiled controllers are scanned so that a listing endpoint
 * cannot omit either.
 */
class SearchRequestValidationContractTest {

    private static final Path CONTROLLER_ROOT = controllerRoot();

    @Test
    void everySearchRequestBodyIsBeanValidated() {
        List<String> unvalidated = new ArrayList<>();
        int bodies = 0;

        for (Class<?> controller : controllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                for (Parameter parameter : method.getParameters()) {
                    if (!isSearchRequestBody(parameter)) {
                        continue;
                    }
                    bodies++;
                    if (parameter.getAnnotation(Valid.class) == null) {
                        unvalidated.add(controller.getSimpleName() + "." + method.getName());
                    }
                }
            }
        }

        assertTrue(bodies >= 17, "expected the scan to reach every listing endpoint, found only " + bodies);
        assertEquals(List.of(), unvalidated,
                "these listing endpoints bind a search request without validating it: " + unvalidated);
    }

    @Test
    void everyValidatedSearchOperationDocumentsTheValidationFailure() {
        List<String> undocumented = new ArrayList<>();

        for (Class<?> controller : controllers()) {
            for (Method method : controller.getDeclaredMethods()) {
                boolean takesSearchBody = Arrays
                        .stream(method.getParameters())
                        .anyMatch(SearchRequestValidationContractTest::isSearchRequestBody);
                if (takesSearchBody && !documentsUnprocessableContent(method)) {
                    undocumented.add(controller.getSimpleName() + "." + method.getName());
                }
            }
        }

        assertEquals(List.of(), undocumented,
                "these listing endpoints validate a search request without documenting the 422 it can return: "
                        + undocumented);
    }

    private static boolean documentsUnprocessableContent(Method method) {
        ApiResponses responses = method.getAnnotation(ApiResponses.class);
        return responses != null
                && Arrays.stream(responses.value()).anyMatch(response -> "422".equals(response.responseCode()));
    }

    private static boolean isSearchRequestBody(Parameter parameter) {
        return parameter.getAnnotation(RequestBody.class) != null
                && SearchRequestDto.class.isAssignableFrom(parameter.getType());
    }

    private static List<Class<?>> controllers() {
        try (Stream<Path> tree = Files.walk(CONTROLLER_ROOT)) {
            return tree
                    .filter(path -> path.getFileName().toString().endsWith("Controller.class"))
                    .map(SearchRequestValidationContractTest::load)
                    .filter(Class::isInterface)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Class<?> load(Path classFile) {
        String relative = CONTROLLER_ROOT.relativize(classFile).toString();
        String name = ListViewController.class.getPackageName() + "."
                + relative
                        .substring(0, relative.length() - ".class".length())
                        .replace(classFile.getFileSystem().getSeparator(), ".");
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("compiled class not on the test classpath: " + name, e);
        }
    }

    private static Path controllerRoot() {
        try {
            Path classes = Path
                    .of(ListViewController.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path root = classes.resolve(ListViewController.class.getPackageName().replace('.', '/'));
            assertTrue(Files.isDirectory(root), "compiled controllers not found at " + root);
            return root;
        } catch (URISyntaxException e) {
            throw new AssertionError("cannot locate the compiled controllers", e);
        }
    }

    @Test
    void scansTheControllersItClaimsTo() {
        assertTrue(controllers().contains(CertificateController.class),
                "the scan missed CertificateController, so a scan matching nothing would report every body validated");
        assertTrue(controllers().size() > 30, "expected the scan to see every controller in the package");
    }

    @Test
    void reachesControllersInNestedPackages() {
        assertTrue(controllers().contains(com.otilm.api.interfaces.core.web.v2.ConnectorController.class),
                "the scan does not descend into nested controller packages");
    }

    @Test
    void ignoresBodiesThatAreNotSearchRequests() {
        Method create = Arrays
                .stream(ListViewController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("createView"))
                .findFirst()
                .orElseThrow();
        assertTrue(Arrays
                .stream(create.getParameters())
                .noneMatch(SearchRequestValidationContractTest::isSearchRequestBody));
    }
}
