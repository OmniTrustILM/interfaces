package com.otilm.api.interfaces.connector.v3;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthorityControllerCompileTest {

    static class Mock implements AuthorityController {
        public List<BaseAttribute> listAuthorityAttributes() {
            return List.of();
        }

        public ResponseEntity<Void> checkAuthorityConnection(List<RequestAttribute> body) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        public List<BaseAttribute> listRaProfileAttributes(List<RequestAttribute> body) {
            return List.of();
        }

        public CrlResponseDto getCrl(CrlRequestDtoV3 body) {
            return new CrlResponseDto();
        }

        public CaCertificatesResponseDto getCaCertificates(CaCertificatesRequestDtoV3 body) {
            return new CaCertificatesResponseDto();
        }

        public List<SupportedCertificateTypeDtoV3> listCertificateTypes(CertificateTypesRequestDtoV3 body) {
            return List.of();
        }
    }

    @Test
    void mockImplementsInterface() {
        assertNotNull(new Mock());
    }
}
