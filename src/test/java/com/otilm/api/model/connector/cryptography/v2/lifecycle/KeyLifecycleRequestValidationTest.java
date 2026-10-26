package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.BOUND_SIGNER;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundCertificateRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validUnboundCertificateRequest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class KeyLifecycleRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("validRequests")
    void validate_hasNoViolations_forValidRequest(Object request) {
        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    static Stream<Named<Object>> validRequests() {
        CertificateIssuedRequestV2Dto withoutChain = validCertificateIssuedRequest();
        withoutChain.setChain(null);
        return Stream
                .of(named("certificate request for a bound key", validBoundCertificateRequest()),
                        named("certificate request for an unbound key", validUnboundCertificateRequest()),
                        named("certificate issued", validCertificateIssuedRequest()),
                        named("certificate issued without a chain", withoutChain));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingRequiredFields")
    void validate_hasExpectedViolation_forMissingRequiredField(InvalidRequest invalidRequest) {
        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(invalidRequest.request());

        // then
        assertHasViolation(violations, invalidRequest.path(), invalidRequest.message());
    }

    static Stream<Named<InvalidRequest>> missingRequiredFields() {
        CreateCertificateRequestV2Dto emptyRequestInfo = validBoundCertificateRequest();
        emptyRequestInfo.setCertificationRequestInfo(new byte[0]);
        return Stream
                .of(invalidRequest("request info", validBoundCertificateRequest(),
                        CreateCertificateRequestV2Dto::setCertificationRequestInfo, "certificationRequestInfo",
                        "certificationRequestInfo is required and must not be empty"),
                        named("empty request info",
                                new InvalidRequest(emptyRequestInfo, "certificationRequestInfo",
                                        "certificationRequestInfo is required and must not be empty")),
                        invalidRequest("signature algorithm", validBoundCertificateRequest(),
                                CreateCertificateRequestV2Dto::setSignatureAlgorithm, "signatureAlgorithm",
                                "signatureAlgorithm is required"),
                        invalidRequest("certificate", validCertificateIssuedRequest(),
                                CertificateIssuedRequestV2Dto::setCertificate, "certificate",
                                "certificate is required and must not be empty"),
                        invalidRequest("certificate identifier", validCertificateIssuedRequest(),
                                CertificateIssuedRequestV2Dto::setCertificateUuid, "certificateUuid",
                                "certificateUuid is required"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequestCollections")
    void validate_hasExpectedViolation_forInvalidRequestCollection(InvalidRequest invalidRequest) {
        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(invalidRequest.request());

        // then
        assertHasViolation(violations, invalidRequest.path(), invalidRequest.message());
    }

    static Stream<Named<InvalidRequest>> invalidRequestCollections() {
        CreateCertificateRequestV2Dto nullSignerAttribute = validUnboundCertificateRequest();
        nullSignerAttribute.setSignerAttributes(Collections.singletonList(null));
        CertificateIssuedRequestV2Dto nullChainItem = validCertificateIssuedRequest();
        nullChainItem.setChain(Collections.singletonList(null));
        return Stream
                .of(named("null signer attribute element",
                        new InvalidRequest(nullSignerAttribute, "signerAttributes[0].<list element>",
                                "signerAttributes must not contain null items")),
                        named("null chain element", new InvalidRequest(nullChainItem, "chain[0].<list element>",
                                "chain must not contain null items")));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signerSourceViolations")
    void validate_reportsOneSignerSourceOnSigner_forOtherThanOneSignerSource(CreateCertificateRequestV2Dto request) {
        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "signer", "exactly one of signer and signerAttributes is required");
    }

    static Stream<Named<CreateCertificateRequestV2Dto>> signerSourceViolations() {
        CreateCertificateRequestV2Dto both = validUnboundCertificateRequest();
        both.setSigner(BOUND_SIGNER);
        CreateCertificateRequestV2Dto neither = validBoundCertificateRequest();
        neither.setSigner(null);
        CreateCertificateRequestV2Dto blankSignerBesideAttributes = validUnboundCertificateRequest();
        blankSignerBesideAttributes.setSigner(" ");
        CreateCertificateRequestV2Dto signerBesideEmptyAttributes = validBoundCertificateRequest();
        signerBesideEmptyAttributes.setSignerAttributes(List.of());
        return Stream
                .of(named("signer and signer attributes", both), named("neither signer nor signer attributes", neither),
                        named("blank signer beside signer attributes", blankSignerBesideAttributes),
                        named("signer beside empty signer attributes", signerBesideEmptyAttributes));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("emptySignerSources")
    void validate_hasExpectedViolation_forEmptySignerSource(InvalidRequest invalidRequest) {
        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(invalidRequest.request());

        // then
        assertHasViolation(violations, invalidRequest.path(), invalidRequest.message());
    }

    static Stream<Named<InvalidRequest>> emptySignerSources() {
        CreateCertificateRequestV2Dto blankSigner = validBoundCertificateRequest();
        blankSigner.setSigner(" ");
        CreateCertificateRequestV2Dto emptySignerAttributes = validUnboundCertificateRequest();
        emptySignerAttributes.setSignerAttributes(List.of());
        return Stream
                .of(named("blank signer", new InvalidRequest(blankSigner, "signer", "signer must not be blank")),
                        named("empty signer attributes", new InvalidRequest(emptySignerAttributes, "signerAttributes",
                                "signerAttributes must not be empty")));
    }

    private static <T, V> Named<InvalidRequest> invalidRequest(String name, T request, BiConsumer<T, V> clearField,
            String path, String message) {
        clearField.accept(request, null);
        return named(name, new InvalidRequest(request, path, message));
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got "
                        + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
    }

    private record InvalidRequest(Object request, String path, String message) {
    }
}
