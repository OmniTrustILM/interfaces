package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import java.util.Base64;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.BOUND_SIGNER;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.CERTIFICATE_UUID;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.EVENT_TIME;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.certificateFiller;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundCertificateRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundKeyPair;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateRequestResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCreatedKeyPair;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validUnboundCertificateRequest;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the wire form a Go or Python connector decodes.
 */
class KeyLifecycleJsonContractTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void certificateRequestResponse_writesThePkcs10AsBase64AndTheEventTypeAsItsCode() throws Exception {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();

        // when
        String json = mapper.writeValueAsString(response);
        JsonNode tree = mapper.readTree(json);
        CertificateRequestCreationResponseV2Dto roundTripped = mapper
                .readValue(json, CertificateRequestCreationResponseV2Dto.class);

        // then
        assertEquals(Base64.getEncoder().encodeToString(response.getPkcs10()), tree.get("pkcs10").textValue());
        assertEquals("authorizationGranted", tree.get("events").get(0).get("type").textValue());
        assertFalse(tree.get("events").get(0).has("vendorDetail"));
        assertArrayEquals(response.getPkcs10(), roundTripped.getPkcs10());
        assertEquals(BOUND_SIGNER, roundTripped.getSigner());
        assertEquals(OperationEventType.AUTHORIZATION_GRANTED, roundTripped.getEvents().get(0).getType());
        assertTrue(EVENT_TIME.isEqual(roundTripped.getEvents().get(0).getAt()));
    }

    @Test
    void certificateIssuedRequest_writesTheCertificateAndChainAsBase64AndTheIdentifierAsText() throws Exception {
        // given
        CertificateIssuedRequestV2Dto request = validCertificateIssuedRequest();
        String filler = Base64.getEncoder().encodeToString(certificateFiller());

        // when
        String json = mapper.writeValueAsString(request);
        JsonNode tree = mapper.readTree(json);
        CertificateIssuedRequestV2Dto roundTripped = mapper.readValue(json, CertificateIssuedRequestV2Dto.class);

        // then
        assertEquals(filler, tree.get("certificate").textValue());
        assertEquals(filler, tree.get("chain").get(0).textValue());
        assertEquals(CERTIFICATE_UUID.toString(), tree.get("certificateUuid").textValue());
        assertEquals(CERTIFICATE_UUID, roundTripped.getCertificateUuid());
        assertArrayEquals(certificateFiller(), roundTripped.getChain().get(0));
    }

    @Test
    void certificateRequest_writesTheAlgorithmAsItsCodeAndOmitsTheAbsentSignerSource() throws Exception {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();

        // when
        JsonNode tree = mapper.readTree(mapper.writeValueAsString(request));

        // then
        assertEquals("SHA256withRSA", tree.get("signatureAlgorithm").textValue());
        assertEquals(BOUND_SIGNER, tree.get("signer").textValue());
        assertFalse(tree.has("signerAttributes"));
    }

    @Test
    void certificateRequest_writesEachSignerAttributeAsV3WithAContentTypeOnEveryItem() throws Exception {
        // given
        CreateCertificateRequestV2Dto request = validUnboundCertificateRequest();

        // when
        JsonNode attribute = mapper.readTree(mapper.writeValueAsString(request)).get("signerAttributes").get(0);

        // then
        assertEquals("v3", attribute.get("version").textValue());
        assertEquals("string", attribute.get("contentType").textValue());
        assertEquals(1, attribute.get("content").size());
        assertEquals("string", attribute.get("content").get(0).get("contentType").textValue());
    }

    @Test
    void keyPairCreationResponse_readsTheSigner() throws Exception {
        // given
        String json = mapper.writeValueAsString(validBoundKeyPair());

        // when
        KeyCreationResponseV2Dto roundTripped = mapper.readValue(json, KeyCreationResponseV2Dto.class);

        // then
        assertEquals(BOUND_SIGNER, assertInstanceOf(KeyPairDataResponseV2Dto.class, roundTripped).getSigner());
    }

    @Test
    void keyPairCreationResponse_omitsTheAbsentSigner() throws Exception {
        // given
        KeyPairDataResponseV2Dto keyPair = validCreatedKeyPair();

        // when
        JsonNode tree = mapper.readTree(mapper.writeValueAsString(keyPair));

        // then
        assertFalse(tree.has("signer"));
    }
}
