package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureActivationAttribute;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureActivationDecision;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the signature activation contract on the public OpenAPI surface of {@link CryptographicOperationsController}.
 * A connector author reads only the generated document.
 */
class CryptographicOperationsControllerDocTest {

    @Test
    void theSignListingNamesTheReservedSignatureActivation() {
        String prose = operation("listSignAttributes")
                .getAnnotation(Operation.class)
                .description()
                .replaceAll("\\s+", " ");

        assertTrue(prose.contains(SignatureActivationAttribute.NAME),
                "the sign listing must name the reserved attribute a key lifecycle connector has to publish");
        assertTrue(prose.contains(SignatureActivationAttribute.ATTRIBUTE_UUID.toString()),
                "the sign listing must name the reserved attribute's UUID");
        assertTrue(prose.contains(SignatureActivationDecision.PLATFORM_ASSERTED.getCode()),
                "the sign listing must name the decision codes the platform states");
    }

    @Test
    void theSignOperationDocumentsItsSignatureActivationRefusals() {
        ApiResponse conflict = response("signData", "409");
        ApiResponse unprocessable = response("signData", "422");

        assertTrue(conflict.description().contains(ErrorCode.SIGNER_MISMATCH.name()),
                "signData's 409 must name SIGNER_MISMATCH; was: " + conflict.description());
        assertTrue(declaresProblemDetail(conflict), "signData's 409 must declare ProblemDetailExtended content");
        assertTrue(unprocessable.description().contains(SignatureActivationAttribute.NAME),
                "signData's 422 must name the missing signatureActivation; was: " + unprocessable.description());
        assertTrue(unprocessable.description().contains(ErrorCode.VALIDATION_FAILED.name()),
                "signData's 422 must name VALIDATION_FAILED; was: " + unprocessable.description());
    }

    @Test
    void theSignOperationRequiresSignatureActivationForEveryKey() {
        String prose = operation("signData").getAnnotation(Operation.class).description().replaceAll("\\s+", " ");
        ApiResponse unprocessable = response("signData", "422");

        assertTrue(prose.contains("must refuse to sign unless that attribute names the key's signer"),
                "signData must refuse a sign whose signatureActivation names no signer of the key; was: " + prose);
        assertTrue(unprocessable.description().contains("or `" + SignatureActivationAttribute.NAME + "` is missing"),
                "signData's 422 must refuse a missing signatureActivation for every key; was: "
                        + unprocessable.description());
    }

    private static boolean declaresProblemDetail(ApiResponse response) {
        return Arrays
                .stream(response.content())
                .anyMatch(c -> c.mediaType().equals(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        && c.schema().implementation() == ProblemDetailExtended.class);
    }

    private static ApiResponse response(String operationName, String responseCode) {
        return Arrays
                .stream(operation(operationName).getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals(responseCode))
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError("expected a documented " + responseCode + " on " + operationName));
    }

    private static Method operation(String name) {
        return Arrays
                .stream(CryptographicOperationsController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a " + name + " operation"));
    }
}
