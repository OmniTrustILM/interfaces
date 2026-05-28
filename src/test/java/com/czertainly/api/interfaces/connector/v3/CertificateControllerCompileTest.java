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
        public List<BaseAttribute> listIssueAttributes(CertificateAttributeListRequestDto request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> issue(CertificateSignRequestDto body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public CertificateOperationStatusResponseDto getIssueStatus(CertificateOperationStatusRequestDto body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelIssue(CertificateOperationCancelRequestDto body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public ResponseEntity<CertificateDataResponseDto> renew(CertificateRenewRequestDto body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public List<BaseAttribute> listRevokeAttributes(CertificateAttributeListRequestDto request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> revoke(CertificateRevocationRequestDto body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public CertificateOperationStatusResponseDto getRevokeStatus(CertificateOperationStatusRequestDto body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelRevoke(CertificateOperationCancelRequestDto body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public List<BaseAttribute> listRegisterAttributes(CertificateAttributeListRequestDto request) { return List.of(); }
        public ResponseEntity<CertificateDataResponseDto> register(CertificateRegistrationRequestDto body) { return ResponseEntity.ok(new CertificateDataResponseDto()); }
        public CertificateOperationStatusResponseDto getRegisterStatus(CertificateOperationStatusRequestDto body) { return new CertificateOperationStatusResponseDto(); }
        public ResponseEntity<Void> cancelRegister(CertificateOperationCancelRequestDto body) { return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); }
        public CertificateIdentificationResponseDto identify(CertificateIdentificationRequestDto body) { return new CertificateIdentificationResponseDto(); }
    }

    @Test
    void mockImplementsInterface() {
        assertNotNull(new Mock());
    }
}
