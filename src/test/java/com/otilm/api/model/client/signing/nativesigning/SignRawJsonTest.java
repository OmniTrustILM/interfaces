package com.otilm.api.model.client.signing.nativesigning;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureInputMode;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** What a client's JSON becomes on the way in, and what it reads on the way out. */
class SignRawJsonTest {

    private static final byte[] DIGEST = new byte[32];

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    /** The cryptography v2 request turns data mode into an absent field; here Core must still see it to refuse it. */
    @Test
    void dataModeSurvivesDecoding() throws JsonProcessingException {
        SignRawRequestDto request = mapper.readValue(requestJson("data"), SignRawRequestDto.class);

        assertEquals(SignatureInputMode.DATA, request.getSignatureInputMode());
        assertEquals("data", mapper.readTree(mapper.writeValueAsString(request)).get("signatureInputMode").asText());
    }

    @Test
    void dataDigestModeAndBase64DataDecode() throws JsonProcessingException {
        SignRawRequestDto request = mapper.readValue(requestJson("dataDigest"), SignRawRequestDto.class);

        assertEquals(SignatureInputMode.DATA_DIGEST, request.getSignatureInputMode());
        assertEquals("first", request.getItems().getFirst().getIdentifier());
        assertArrayEquals(DIGEST, request.getItems().getFirst().getData());
    }

    /** The input mode was renamed before release, so the earlier name must not quietly mean anything. */
    @Test
    void anUnknownModeIsRejectedWhileDecoding() {
        assertThrows(JsonProcessingException.class,
                () -> mapper.readValue(requestJson("prehashedDigest"), SignRawRequestDto.class));
    }

    @Test
    void theResponseCarriesBytesAsBase64AndTheAlgorithmByCode() throws JsonProcessingException {
        byte[] certificate = {0x30, 0x03, 0x02, 0x01, 0x01};
        byte[] signature = {0x01, 0x02, 0x03};
        UUID record = UUID.randomUUID();
        SignRawResponseDto response = new SignRawResponseDto(SignatureAlgorithm.SHA256_WITH_ECDSA, List.of(certificate),
                List.of(new SignRawResultDto("first", record, signature)));

        JsonNode json = mapper.readTree(mapper.writeValueAsString(response));

        assertEquals(SignatureAlgorithm.SHA256_WITH_ECDSA.getCode(), json.get("signatureAlgorithm").asText());
        assertEquals(Base64.getEncoder().encodeToString(certificate),
                json.get("signerCertificateChain").get(0).asText());
        JsonNode result = json.get("results").get(0);
        assertEquals("first", result.get("identifier").asText());
        assertEquals(record.toString(), result.get("signingRecordUuid").asText());
        assertEquals(Base64.getEncoder().encodeToString(signature), result.get("signature").asText());
    }

    @Test
    void bytesStayOutOfToString() {
        SignRawRequestDto request = new SignRawRequestDto();
        request.setItems(List.of(new SignRawItemDto("first", DIGEST)));
        SignRawResponseDto response = new SignRawResponseDto(SignatureAlgorithm.SHA256_WITH_ECDSA,
                List.of(new byte[]{0x30}), List.of(new SignRawResultDto("first", null, new byte[]{0x01})));

        String renderedRequest = request.toString();
        String renderedResponse = response.toString();

        assertTrue(renderedRequest.contains("first"), renderedRequest);
        assertFalse(renderedRequest.contains("data="), renderedRequest);
        assertTrue(renderedResponse.contains("first"), renderedResponse);
        assertFalse(renderedResponse.contains("signature="), renderedResponse);
        assertFalse(renderedResponse.contains("signerCertificateChain="), renderedResponse);
    }

    private static String requestJson(String mode) {
        return """
                {"signatureInputMode":"%s","digestAlgorithm":"SHA-256",
                 "items":[{"identifier":"first","data":"%s"}]}"""
                .formatted(mode, Base64.getEncoder().encodeToString(DIGEST));
    }
}
