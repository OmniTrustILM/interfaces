package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.key.KeyExportableAttribute;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureAlgorithmAttribute;
import com.otilm.api.testsupport.ValidatorFixture;
import java.io.IOException;
import java.security.KeyPair;
import java.util.List;
import java.util.stream.Stream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.CERTIFICATE_REQUEST_ALGORITHM;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.EVENT_TIME;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.SIGNER_ID_ATTRIBUTE_UUID;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.certificationRequestInfo;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.keyPairFor;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.pkcs10;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.pkcs10WithFillerSignature;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.pkcs10WithIndefiniteLength;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.signerIdDefinition;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundCertificateRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateRequestResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validUnboundCertificateRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

/**
 * Validates connector response contracts for the key lifecycle hooks.
 */
class KeyLifecycleResponseValidatorTest {

    private static final String EVENT_WITHOUT_TYPE_MESSAGE = "Connector response validation failed: events[0].type: type is required";
    private static final String ALGORITHM_IDENTITY = "attribute with name '" + SignatureAlgorithmAttribute.NAME
            + "' and UUID '" + SignatureAlgorithmAttribute.ATTRIBUTE_UUID + "'";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    @Test
    void validateSignerAttributeList_acceptsV3DataAttributes() {
        // given
        List<BaseAttribute> listing = List.of(signerIdDefinition());

        // when
        OperationValidationResult result = VALIDATOR.validateSignerAttributeList(listing);

        // then
        assertValid(result);
    }

    @ParameterizedTest
    @MethodSource("signerAttributesOfAnotherKind")
    void validateSignerAttributeList_rejectsAttributesOfAnotherKind(BaseAttribute attribute) {
        // given
        List<BaseAttribute> listing = List.of(signerIdDefinition(), attribute);

        // when
        OperationValidationResult result = VALIDATOR.validateSignerAttributeList(listing);

        // then
        assertInvalid(result, "Signer attributes must be v3 data attributes");
    }

    static Stream<Named<BaseAttribute>> signerAttributesOfAnotherKind() {
        InfoAttributeV3 info = new InfoAttributeV3();
        info.setUuid(SIGNER_ID_ATTRIBUTE_UUID.toString());
        info.setName("signerNotice");
        DataAttributeV2 v2Definition = new DataAttributeV2();
        v2Definition.setUuid(SIGNER_ID_ATTRIBUTE_UUID.toString());
        v2Definition.setName("signerDomain");
        v2Definition.setContentType(AttributeContentType.STRING);
        return Stream.of(named("an info attribute", info), named("a v2 data attribute", v2Definition));
    }

    @Test
    void validateCertificateRequestAttributeList_acceptsAListingOfferingTheSignatureAlgorithm() {
        // given
        List<BaseAttribute> listing = List
                .of(SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA)));

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateRequestAttributeList(listing);

        // then
        assertValid(result);
    }

    @Test
    void validateCertificateRequestAttributeList_rejectsAListingWithoutTheSignatureAlgorithm() {
        // given
        List<BaseAttribute> listing = List.of(KeyExportableAttribute.definition());

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateRequestAttributeList(listing);

        // then
        assertInvalid(result, "Certificate request attributes must declare exactly one v3 " + ALGORITHM_IDENTITY);
    }

    @Test
    void validateCreateCertificateRequest_acceptsAPkcs10OverTheSentRequestInfo() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateCertificateRequest_rejectsAMissingRequest() {
        // given
        CreateCertificateRequestV2Dto request = null;

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(request, validCertificateRequestResponse());

        // then
        assertInvalid(result, "Certificate request creation request is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsARequestWithoutItsRequestInfo() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setCertificationRequestInfo(null);

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(request, validCertificateRequestResponse());

        // then
        assertInvalid(result, "Certificate request info is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsARequestWithEmptyRequestInfo() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setCertificationRequestInfo(new byte[0]);

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(request, validCertificateRequestResponse());

        // then
        assertInvalid(result, "Certificate request info is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsARequestWithoutItsSignatureAlgorithm() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setSignatureAlgorithm(null);

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(request, validCertificateRequestResponse());

        // then
        assertInvalid(result, "Certificate request signature algorithm is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAMissingResponse() {
        // given
        CertificateRequestCreationResponseV2Dto response = null;

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector response body is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAResponseWithoutItsSigner() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setSigner(" ");

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector response validation failed: signer: signer is required");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAnotherSignerForABoundKey() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setSigner("john.roe|signers.example");

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned another signer for a bound key");
    }

    @Test
    void validateCreateCertificateRequest_acceptsTheSignerTheConnectorDerivesForAnUnboundKey() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setSigner("john.roe|signers.example");

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validUnboundCertificateRequest(), response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateCertificateRequest_rejectsAMalformedPkcs10() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(new byte[]{0x30, 0x03, 0x02, 0x01, 0x01});

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a malformed PKCS#10");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAnEmptyPkcs10Sequence() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(new byte[]{0x30, 0x00});

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a malformed PKCS#10");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10OfIndefiniteLength() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10WithIndefiniteLength(certificationRequestInfo(), CERTIFICATE_REQUEST_ALGORITHM));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 that is not DER-encoded");
    }

    @Test
    void validateCreateCertificateRequest_rejectsARequestWithMalformedRequestInfo() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setCertificationRequestInfo(new byte[]{0x30, 0x00});

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(request, validCertificateRequestResponse());

        // then
        assertInvalid(result, "Certificate request info is malformed");
    }

    @Test
    void validateCreateCertificateRequest_acceptsAPkcs10TheTechnologyBuiltForTheRequestedSubjectAndKey()
            throws IOException {
        // given - the technology re-encodes the subject and requests its own extensions
        X500Name sameSubject = new X500Name(new RDN[]{new RDN(BCStyle.CN, new DERPrintableString("Jane Doe"))});
        Attribute ownExtensions = new Attribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                new DERSet(new Extensions(new Extension(Extension.keyUsage, true,
                        new DEROctetString(new KeyUsage(KeyUsage.nonRepudiation))))));
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(certificationRequestInfo(sameSubject, ownExtensions), CERTIFICATE_REQUEST_ALGORITHM));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertValid(result);
    }

    @ParameterizedTest
    @EnumSource(SignatureAlgorithm.class)
    void validateCreateCertificateRequest_acceptsASelfSignedPkcs10UnderEveryAlgorithm(SignatureAlgorithm algorithm) {
        // given
        KeyPair keyPair = keyPairFor(algorithm);
        byte[] requestInfo = certificationRequestInfo(keyPair.getPublic());
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setCertificationRequestInfo(requestInfo);
        request.setSignatureAlgorithm(algorithm);
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(requestInfo, algorithm, keyPair.getPrivate()));

        // when
        OperationValidationResult result = VALIDATOR.validateCreateCertificateRequest(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10ForAnotherSubject() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(certificationRequestInfo("CN=John Roe"), CERTIFICATE_REQUEST_ALGORITHM));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 for another subject");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10WithTheSubjectReordered() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setCertificationRequestInfo(certificationRequestInfo("CN=Jane Doe,O=Example"));
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(certificationRequestInfo("O=Example,CN=Jane Doe"), CERTIFICATE_REQUEST_ALGORITHM));

        // when
        OperationValidationResult result = VALIDATOR.validateCreateCertificateRequest(request, response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 for another subject");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10ForAnotherPublicKey() {
        // given
        KeyPair anotherKey = keyPairFor(CERTIFICATE_REQUEST_ALGORITHM);
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response
                .setPkcs10(pkcs10(certificationRequestInfo(anotherKey.getPublic()), CERTIFICATE_REQUEST_ALGORITHM,
                        anotherKey.getPrivate()));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 for another public key");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10WhoseSignatureFailsVerification() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response
                .setPkcs10(pkcs10WithFillerSignature(certificationRequestInfo(),
                        CERTIFICATE_REQUEST_ALGORITHM.getAlgorithmIdentifier()));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 whose signature fails verification");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAPkcs10UnderAnotherSignatureAlgorithm() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(certificationRequestInfo(), SignatureAlgorithm.SHA384_WITH_RSA));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 under another signature algorithm");
    }

    @Test
    void validateCreateCertificateRequest_acceptsRsaSignatureParametersLeftAbsent() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response
                .setPkcs10(pkcs10(certificationRequestInfo(), CERTIFICATE_REQUEST_ALGORITHM, new AlgorithmIdentifier(
                        CERTIFICATE_REQUEST_ALGORITHM.getAlgorithmIdentifier().getAlgorithm())));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateCertificateRequest_acceptsPssHashParametersLeftAbsent() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA_PSS);
        AlgorithmIdentifier sha256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        RSASSAPSSparams parameters = new RSASSAPSSparams(sha256,
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, sha256), new ASN1Integer(32),
                RSASSAPSSparams.DEFAULT_TRAILER_FIELD);
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response
                .setPkcs10(pkcs10(certificationRequestInfo(), SignatureAlgorithm.SHA256_WITH_RSA_PSS,
                        new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS, parameters)));

        // when
        OperationValidationResult result = VALIDATOR.validateCreateCertificateRequest(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateCertificateRequest_rejectsNullParametersOnAnAlgorithmThatOmitsThem() {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_ECDSA);
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response
                .setPkcs10(pkcs10WithFillerSignature(certificationRequestInfo(),
                        new AlgorithmIdentifier(
                                SignatureAlgorithm.SHA256_WITH_ECDSA.getAlgorithmIdentifier().getAlgorithm(),
                                DERNull.INSTANCE)));

        // when
        OperationValidationResult result = VALIDATOR.validateCreateCertificateRequest(request, response);

        // then
        assertInvalid(result, "Connector returned a PKCS#10 under another signature algorithm");
    }

    @Test
    void validateCreateCertificateRequest_rejectsAnEventWithoutItsType() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setEvents(List.of(eventWithoutType()));

        // when
        OperationValidationResult result = VALIDATOR
                .validateCreateCertificateRequest(validBoundCertificateRequest(), response);

        // then
        assertInvalid(result, EVENT_WITHOUT_TYPE_MESSAGE);
    }

    @Test
    void validateCertificateIssued_acceptsAResponseWithoutEvents() {
        // given
        CertificateIssuedResponseV2Dto response = new CertificateIssuedResponseV2Dto();

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateIssued(response);

        // then
        assertValid(result);
    }

    @Test
    void validateCertificateIssued_acceptsAResponseWithEvents() {
        // given
        CertificateIssuedResponseV2Dto response = validCertificateIssuedResponse();

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateIssued(response);

        // then
        assertValid(result);
    }

    @Test
    void validateCertificateIssued_rejectsAMissingResponse() {
        // given
        CertificateIssuedResponseV2Dto response = null;

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateIssued(response);

        // then
        assertInvalid(result, "Connector response body is required");
    }

    @Test
    void validateCertificateIssued_rejectsAnEventWithoutItsType() {
        // given
        CertificateIssuedResponseV2Dto response = new CertificateIssuedResponseV2Dto();
        response.setEvents(List.of(eventWithoutType()));

        // when
        OperationValidationResult result = VALIDATOR.validateCertificateIssued(response);

        // then
        assertInvalid(result, EVENT_WITHOUT_TYPE_MESSAGE);
    }

    private static OperationEventV2Dto eventWithoutType() {
        OperationEventV2Dto event = new OperationEventV2Dto();
        event.setAt(EVENT_TIME);
        return event;
    }

    private static void assertValid(OperationValidationResult result) {
        assertTrue(result.isValid(), () -> result.getCause().getMessage());
    }

    private static void assertInvalid(OperationValidationResult result, String message) {
        assertFalse(result.isValid());
        assertEquals(message, result.getCause().getMessage());
    }
}
