package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.common.BulkActionMessageDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Pins the bulk archive response: it archives what it can and lists the trusted certificates it skipped, so it answers
 * 200 with a body.
 */
class CertificateControllerArchiveContractTest {

    private static Method bulkArchiveCertificate() {
        return Arrays
                .stream(CertificateController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("bulkArchiveCertificate"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CertificateController declares no bulkArchiveCertificate"));
    }

    @Test
    void bulkArchiveReturnsTheSkippedCertificates() {
        Method method = bulkArchiveCertificate();
        assertEquals(List.class, method.getReturnType());
        assertEquals(BulkActionMessageDto.class,
                ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
    }

    @Test
    void bulkArchiveAnswersOk() {
        Method method = bulkArchiveCertificate();
        assertNull(method.getAnnotation(ResponseStatus.class));
        Set<String> documentedStatuses = Arrays
                .stream(method.getAnnotation(ApiResponses.class).value())
                .map(ApiResponse::responseCode)
                .collect(Collectors.toSet());
        assertEquals(Set.of("200"), documentedStatuses);
    }
}
