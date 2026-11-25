package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRenewalInfoDtosTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private static CertificateRenewalInfoRequestItemDtoV3 item(String reference) {
        CertificateRenewalInfoRequestItemDtoV3 item = new CertificateRenewalInfoRequestItemDtoV3();
        item.setReference(reference);
        item.setCertificate("MIIBkjCCATs...");
        return item;
    }

    private static CertificateRenewalInfoRequestDtoV3 request(List<CertificateRenewalInfoRequestItemDtoV3> items) {
        CertificateRenewalInfoRequestDtoV3 dto = new CertificateRenewalInfoRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setCertificates(items);
        return dto;
    }

    private static List<CertificateRenewalInfoRequestItemDtoV3> items(int count) {
        return IntStream.range(0, count).mapToObj(i -> item("ref-" + i)).toList();
    }

    @Test
    void acceptsOneToFiftyItems() {
        assertTrue(VALIDATOR.validate(request(items(1))).isEmpty());
        assertTrue(VALIDATOR.validate(request(items(50))).isEmpty());
    }

    @Test
    void rejectsZeroItems() {
        assertFalse(VALIDATOR.validate(request(List.of())).isEmpty());
    }

    @Test
    void rejectsMissingItems() {
        assertFalse(VALIDATOR.validate(request(null)).isEmpty());
    }

    @Test
    void rejectsMoreThanFiftyItems() {
        assertFalse(VALIDATOR.validate(request(items(51))).isEmpty());
    }

    @Test
    void rejectsDuplicateReferences() {
        assertFalse(VALIDATOR.validate(request(List.of(item("same"), item("same")))).isEmpty());
    }

    @Test
    void rejectsNullItem() {
        assertFalse(VALIDATOR.validate(request(Collections.singletonList(null))).isEmpty());
    }

    @Test
    void rejectsBlankReferenceOrCertificate() {
        CertificateRenewalInfoRequestItemDtoV3 blank = new CertificateRenewalInfoRequestItemDtoV3();
        assertEquals(2, VALIDATOR.validate(blank).size());
        assertFalse(VALIDATOR.validate(request(List.of(blank))).isEmpty(), "@Valid must cascade into the items");
    }

    @Test
    void statusCodesSerialiseAsCamelCase() throws Exception {
        assertEquals("\"notSupported\"", mapper.writeValueAsString(CertificateRenewalInfoStatus.NOT_SUPPORTED));
        assertEquals("\"available\"", mapper.writeValueAsString(CertificateRenewalInfoStatus.AVAILABLE));
        assertEquals(CertificateRenewalInfoStatus.NOT_SUPPORTED,
                mapper.readValue("\"notSupported\"", CertificateRenewalInfoStatus.class));
    }

    @Test
    void responseRoundTrips() throws Exception {
        String json = """
                {"retryAfter":3600,"results":[{"reference":"a","status":"available",
                "windowStart":"2026-12-01T00:00:00Z","windowEnd":"2026-12-03T00:00:00Z",
                "explanationUrl":"https://ca.example/why","retryAfter":600,"reason":"ok"}]}""";

        CertificateRenewalInfoResponseDto dto = mapper.readValue(json, CertificateRenewalInfoResponseDto.class);

        assertEquals(3600, dto.getRetryAfter());
        CertificateRenewalInfoResultDtoV3 result = dto.getResults().get(0);
        assertEquals(CertificateRenewalInfoStatus.AVAILABLE, result.getStatus());
        assertTrue(result.getWindowStart().isEqual(OffsetDateTime.parse("2026-12-01T00:00:00Z")));
        assertEquals(600, result.getRetryAfter());
        JsonNode back = mapper.readTree(mapper.writeValueAsString(dto));
        assertEquals("available", back.get("results").get(0).get("status").asText());
    }

    @Test
    void responseRejectsNegativeRetryAfter() {
        CertificateRenewalInfoResponseDto dto = new CertificateRenewalInfoResponseDto();
        dto.setRetryAfter(-1);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void statusExposesLabelDescriptionAndLookup() {
        for (CertificateRenewalInfoStatus status : CertificateRenewalInfoStatus.values()) {
            assertNotNull(status.getLabel());
            assertNotNull(status.getDescription());
            assertEquals(status, CertificateRenewalInfoStatus.findByCode(status.getCode()));
        }
    }

    @Test
    void unknownStatusCodeInAResponseIsReadAsNull() throws Exception {
        CertificateRenewalInfoResponseDto dto = mapper
                .readValue(
                        "{\"results\":[{\"reference\":\"a\",\"status\":\"bogus\"},{\"reference\":\"b\",\"status\":\"retry\"}]}",
                        CertificateRenewalInfoResponseDto.class);

        assertNull(dto.getResults().get(0).getStatus());
        assertEquals(CertificateRenewalInfoStatus.RETRY, dto.getResults().get(1).getStatus());
    }

    @Test
    void unknownStatusCodeIsRejected() {
        assertThrows(ValidationException.class, () -> CertificateRenewalInfoStatus.findByCode("bogus"));
    }
}
