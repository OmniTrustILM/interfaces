package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustedCertificateRequestDtosTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final String UUID_1 = "5f0b1c2e-0000-4000-8000-000000000001";

    @Test
    void addRequestDocumentsExactlyOneCertificateSource() {
        Schema schema = TrustedCertificateAddRequestDto.class.getAnnotation(Schema.class);
        assertTrue(schema.description().contains("Exactly one of certificateUuids and certificateContent"));
    }

    @Test
    void applyRequestAllowsNoUuids() throws Exception {
        TrustedCertificateApplyRequestDto dto = new ObjectMapper()
                .readValue("{}", TrustedCertificateApplyRequestDto.class);
        assertNull(dto.getUuids());
    }

    @Test
    void applyRefusesAnEmptyListButNotAnAbsentOne() {
        TrustedCertificateApplyRequestDto dto = new TrustedCertificateApplyRequestDto();
        assertTrue(VALIDATOR.validate(dto).isEmpty());
        dto.setUuids(List.of());
        assertEquals(Set.of("uuids"), invalidPaths(dto));
    }

    @Test
    void bulkRequestsRefuseANullUuid() {
        List<String> withNull = Arrays.asList(UUID_1, null);
        TrustedCertificateAddRequestDto add = new TrustedCertificateAddRequestDto();
        add.setCertificateUuids(withNull);
        add.setPurposes(Set.of(TrustedCertificatePurpose.VALIDATION));
        TrustedCertificateUpdateRequestDto update = new TrustedCertificateUpdateRequestDto();
        update.setUuids(withNull);
        update.setPurposes(Set.of(TrustedCertificatePurpose.VALIDATION));
        TrustedCertificateRemoveRequestDto remove = new TrustedCertificateRemoveRequestDto();
        remove.setUuids(withNull);
        TrustedCertificateApplyRequestDto apply = new TrustedCertificateApplyRequestDto();
        apply.setUuids(withNull);

        assertEquals(Set.of("certificateUuids[1].<list element>"), invalidPaths(add));
        assertEquals(Set.of("uuids[1].<list element>"), invalidPaths(update));
        assertEquals(Set.of("uuids[1].<list element>"), invalidPaths(remove));
        assertEquals(Set.of("uuids[1].<list element>"), invalidPaths(apply));
    }

    @Test
    void purposesRefuseAnEmptySetAndANullPurpose() {
        TrustedCertificateAddRequestDto add = new TrustedCertificateAddRequestDto();
        add.setCertificateUuids(List.of(UUID_1));
        add.setPurposes(Set.of());
        assertEquals(Set.of("purposes"), invalidPaths(add));
        add.setPurposes(new HashSet<>(Arrays.asList((TrustedCertificatePurpose) null)));
        assertEquals(Set.of("purposes[].<iterable element>"), invalidPaths(add));
    }

    @Test
    void updateAndRemoveRefuseEmptyLists() {
        TrustedCertificateUpdateRequestDto update = new TrustedCertificateUpdateRequestDto();
        update.setUuids(List.of());
        update.setPurposes(Set.of());
        assertEquals(Set.of("uuids", "purposes"), invalidPaths(update));
        TrustedCertificateRemoveRequestDto remove = new TrustedCertificateRemoveRequestDto();
        remove.setUuids(List.of());
        assertEquals(Set.of("uuids"), invalidPaths(remove));
    }

    @Test
    void requestsLogNoDataOrNamesAndEveryUuid() {
        List<Loggable> requests = List
                .of(new TrustedCertificateAddRequestDto(), new TrustedCertificateUpdateRequestDto(),
                        new TrustedCertificateRemoveRequestDto(), new TrustedCertificateApplyRequestDto());
        for (Loggable request : requests) {
            assertNull(request.toLogData());
            assertTrue(request.toLogResourceObjectsNames().isEmpty());
            assertTrue(request.toLogResourceObjectsUuids().isEmpty());
        }
        TrustedCertificateAddRequestDto add = new TrustedCertificateAddRequestDto();
        add.setCertificateUuids(List.of(UUID_1));
        TrustedCertificateUpdateRequestDto update = new TrustedCertificateUpdateRequestDto();
        update.setUuids(List.of(UUID_1));
        TrustedCertificateRemoveRequestDto remove = new TrustedCertificateRemoveRequestDto();
        remove.setUuids(List.of(UUID_1));
        TrustedCertificateApplyRequestDto apply = new TrustedCertificateApplyRequestDto();
        apply.setUuids(List.of(UUID_1));
        for (Loggable request : List.of(add, update, remove, apply)) {
            assertEquals(List.of(UUID.fromString(UUID_1)), request.toLogResourceObjectsUuids());
        }
    }

    private static Set<String> invalidPaths(Object request) {
        return VALIDATOR
                .validate(request)
                .stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
