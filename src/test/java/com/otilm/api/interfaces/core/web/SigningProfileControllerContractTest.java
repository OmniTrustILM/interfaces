package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ValueConstants;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the signing-profile routes the admin UI and Core build against, namely the formatting attribute discovery routes
 * and the ILM Native activation pair, against their annotation values. A changed path, verb, parameter or response
 * shape has to fail a build rather than surface as an empty form or a broken client.
 */
class SigningProfileControllerContractTest {

    private static final String TIMESTAMPING_PATH = "/signatureFormattingConnectors/{connectorUuid}/formattingAttributes";

    private static final String CONTENT_SIGNING_PATH = "/signatureFormattingConnectors/{connectorUuid}/contentSigningFormattingAttributes";

    private static Method method(String name) {
        return Arrays
                .stream(SigningProfileController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SigningProfileController declares no method named " + name));
    }

    private static Parameter parameterOfType(Method method, Class<?> type) {
        return Arrays
                .stream(method.getParameters())
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        method.getName() + " declares no " + type.getSimpleName() + " parameter"));
    }

    @Test
    void contentSigningDiscoveryIsItsOwnRouteBesideTheTimestampingOne() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");
        Method timestamping = method("listSignatureFormattingConnectorAttributes");

        // when
        GetMapping contentSigningMapping = contentSigning.getAnnotation(GetMapping.class);
        GetMapping timestampingMapping = timestamping.getAnnotation(GetMapping.class);

        // then
        assertNotNull(contentSigningMapping, "content signing discovery must be a GET");
        assertArrayEquals(new String[]{CONTENT_SIGNING_PATH}, contentSigningMapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, contentSigningMapping.produces());
        assertNotNull(timestampingMapping, "the timestamping route must stay a GET");
        assertArrayEquals(new String[]{TIMESTAMPING_PATH}, timestampingMapping.path(),
                "the timestamping route keeps its own path");
    }

    @Test
    void contentSigningDiscoveryReturnsAFlatBaseAttributeList() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");

        // when
        ParameterizedType returnType = (ParameterizedType) contentSigning.getGenericReturnType();

        // then
        assertEquals(List.class, contentSigning.getReturnType());
        assertEquals(BaseAttribute.class, returnType.getActualTypeArguments()[0]);
    }

    /**
     * Both are unsaved form state at the only moments discovery is called, so the endpoint has no stored value to fall
     * back on and must not invent one.
     */
    @Test
    void familyAndMaxLevelAreRequiredQueryParameters() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");

        // when
        RequestParam family = parameterOfType(contentSigning, SignatureFamily.class).getAnnotation(RequestParam.class);
        RequestParam maxLevel = parameterOfType(contentSigning, SignatureLevel.class).getAnnotation(RequestParam.class);

        // then
        assertNotNull(family, "family must be a request parameter");
        assertNotNull(maxLevel, "maxLevel must be a request parameter");
        assertTrue(family.required(), "family must be required");
        assertTrue(maxLevel.required(), "maxLevel must be required");
        assertEquals(ValueConstants.DEFAULT_NONE, family.defaultValue(),
                "a default would make family omittable, which is the invented value this endpoint must not have");
        assertEquals(ValueConstants.DEFAULT_NONE, maxLevel.defaultValue(),
                "a default would make maxLevel omittable, which is the invented value this endpoint must not have");
    }

    @Test
    void theConnectorIsAPathVariableAndTheProfileAnOptionalQueryParameter() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");

        // when
        List<Parameter> uuidParameters = Arrays
                .stream(contentSigning.getParameters())
                .filter(p -> p.getType() == UUID.class)
                .toList();

        // then
        assertEquals(2, uuidParameters.size(), "the connector and the authorizing profile are the two UUID inputs");
        assertNotNull(uuidParameters.get(0).getAnnotation(PathVariable.class), "the connector rides the path");
        RequestParam signingProfileUuid = uuidParameters.get(1).getAnnotation(RequestParam.class);
        assertNotNull(signingProfileUuid, "signingProfileUuid must be a request parameter");
        assertFalse(signingProfileUuid.required(),
                "signingProfileUuid is authorization-only, so a create-time form can omit it");
    }

    /**
     * Discovery reuses the save-time aggregation, so the rejections a form can provoke — a name declared twice with two
     * definitions, an unadvertised family, a maxLevel above the connector's ceiling, and a maxLevel this platform
     * version cannot execute — reach the caller as the 422 that profile save produces.
     */
    @Test
    void contentSigningDiscoveryDocumentsTheSaveTimeRejection() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");

        // when
        Set<String> documentedStatuses = Arrays
                .stream(contentSigning.getAnnotation(ApiResponses.class).value())
                .map(ApiResponse::responseCode)
                .collect(Collectors.toSet());

        // then
        assertEquals(Set.of("200", "400", "404", "422", "502", "503"), documentedStatuses);
    }

    @Test
    void theOperationIdNamesTheContentSigningContract() {
        // given
        Method contentSigning = method("listContentSigningFormattingConnectorAttributes");

        // when
        Operation operation = contentSigning.getAnnotation(Operation.class);

        // then
        assertEquals("listContentSigningFormattingConnectorAttributes", operation.operationId(),
                "the generated client method name is contract");
        assertTrue(operation.description().contains("does not narrow the returned descriptors"),
                "the description must state that family does not filter, which callers cannot infer");
    }

    @Test
    void ilmNativeActivationPairIsPatchedAndAnswersNoContent() {
        // given
        Method activate = method("activateIlmNative");
        Method deactivate = method("deactivateIlmNative");

        // when
        String[] activatePath = activate.getAnnotation(PatchMapping.class).path();
        String[] deactivatePath = deactivate.getAnnotation(PatchMapping.class).path();

        // then
        assertArrayEquals(new String[]{"/{uuid}/protocols/ilmNative/activate"}, activatePath);
        assertArrayEquals(new String[]{"/{uuid}/protocols/ilmNative/deactivate"}, deactivatePath);
        assertEquals(HttpStatus.NO_CONTENT, activate.getAnnotation(ResponseStatus.class).value());
        assertEquals(HttpStatus.NO_CONTENT, deactivate.getAnnotation(ResponseStatus.class).value());
        assertEquals("ILM Native API activated", documented(activate, "204").description());
        assertEquals("ILM Native API deactivated", documented(deactivate, "204").description());
        for (Method operation : List.of(activate, deactivate)) {
            ApiResponse notFound = documented(operation, "404");
            assertEquals("Signing Profile not found", notFound.description());
            assertEquals(ErrorMessageDto.class, notFound.content()[0].schema().implementation());
        }
        assertTrue(activate
                .getAnnotation(Operation.class)
                .description()
                .contains("Refused with 422 on a timestamping profile and on a profile that uses delegated signing."));
        ApiResponse unprocessable = documented(activate, "422");
        assertEquals("Unprocessable Content", unprocessable.description());
        assertEquals(String.class, unprocessable.content()[0].array().schema().implementation(),
                "Core renders a validation failure as an array of messages");
    }

    private static ApiResponse documented(Method operation, String status) {
        return Arrays
                .stream(operation.getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals(status))
                .findFirst()
                .orElseThrow(() -> new AssertionError(operation.getName() + " documents no " + status + " response"));
    }
}
