package com.otilm.api.interfaces.connector.v3;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.certificate.CertificateAttributeListRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateIdentificationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateIdentificationResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationCancelRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationStatusRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationStatusResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateRegistrationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateRevocationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateSignRequestDtoV3;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CertificateControllerCompileTest {

    /** Minimal mock impl — compilation alone proves the interface signatures are coherent. */
    static class Mock implements CertificateController {
        public List<BaseAttribute> listIssueAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public List<BaseAttribute> listRequestAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public ResponseEntity<CertificateDataResponseDto> issue(CertificateSignRequestDtoV3 body) {
            return ResponseEntity.ok(new CertificateDataResponseDto());
        }

        public CertificateOperationStatusResponseDto getIssueStatus(CertificateOperationStatusRequestDtoV3 body) {
            return new CertificateOperationStatusResponseDto();
        }

        public ResponseEntity<Void> cancelIssue(CertificateOperationCancelRequestDtoV3 body) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        public List<BaseAttribute> listRenewAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public ResponseEntity<CertificateDataResponseDto> renew(CertificateRenewRequestDtoV3 body) {
            return ResponseEntity.ok(new CertificateDataResponseDto());
        }

        public List<BaseAttribute> listRevokeAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public ResponseEntity<CertificateDataResponseDto> revoke(CertificateRevocationRequestDtoV3 body) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        public CertificateOperationStatusResponseDto getRevokeStatus(CertificateOperationStatusRequestDtoV3 body) {
            return new CertificateOperationStatusResponseDto();
        }

        public ResponseEntity<Void> cancelRevoke(CertificateOperationCancelRequestDtoV3 body) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        public List<BaseAttribute> listRegisterAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public ResponseEntity<CertificateDataResponseDto> register(CertificateRegistrationRequestDtoV3 body) {
            return ResponseEntity.ok(new CertificateDataResponseDto());
        }

        public CertificateOperationStatusResponseDto getRegisterStatus(CertificateOperationStatusRequestDtoV3 body) {
            return new CertificateOperationStatusResponseDto();
        }

        public ResponseEntity<Void> cancelRegister(CertificateOperationCancelRequestDtoV3 body) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        public List<BaseAttribute> listIdentifyAttributes(CertificateAttributeListRequestDtoV3 request) {
            return List.of();
        }

        public CertificateIdentificationResponseDto identify(CertificateIdentificationRequestDtoV3 body) {
            return new CertificateIdentificationResponseDto();
        }

        public CertificateRenewalInfoResponseDto renewalInfo(CertificateRenewalInfoRequestDtoV3 body) {
            return new CertificateRenewalInfoResponseDto();
        }
    }

    @Test
    void mockImplementsInterface() {
        assertNotNull(new Mock());
    }
}
