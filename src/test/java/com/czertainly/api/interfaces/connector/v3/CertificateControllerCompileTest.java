package com.czertainly.api.interfaces.connector.v3;

import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateControllerCompileTest {

    /** Minimal mock impl — compilation alone proves the interface signatures are coherent. */
    static class Mock implements CertificateController {
        public List<BaseAttribute> listIssueAttributes(CertificateAttributeListRequestDtoV3 request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> issue(CertificateSignRequestDtoV3 body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public CertificateOperationStatusResponseDto getIssueStatus(CertificateOperationStatusRequestDtoV3 body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelIssue(CertificateOperationCancelRequestDtoV3 body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public ResponseEntity<CertificateDataResponseDto> renew(CertificateRenewRequestDtoV3 body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public List<BaseAttribute> listRevokeAttributes(CertificateAttributeListRequestDtoV3 request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> revoke(CertificateRevocationRequestDtoV3 body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public CertificateOperationStatusResponseDto getRevokeStatus(CertificateOperationStatusRequestDtoV3 body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelRevoke(CertificateOperationCancelRequestDtoV3 body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public List<BaseAttribute> listRegisterAttributes(CertificateAttributeListRequestDtoV3 request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> register(CertificateRegistrationRequestDtoV3 body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public CertificateOperationStatusResponseDto getRegisterStatus(CertificateOperationStatusRequestDtoV3 body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelRegister(CertificateOperationCancelRequestDtoV3 body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public CertificateIdentificationResponseDto identify(CertificateIdentificationRequestDtoV3 body) { return new CertificateIdentificationResponseDto(); }
    }

    @Test
    void mockImplementsInterface() {
        assertNotNull(new Mock());
    }
}
