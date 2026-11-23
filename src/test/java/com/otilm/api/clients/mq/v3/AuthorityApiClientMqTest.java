package com.otilm.api.clients.mq.v3;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import com.otilm.api.model.core.certificate.CertificateType;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Delegation test for the MQ-based v3 Authority client's certificate types listing. The MQ path constant is a second
 * copy of the wire path, and a typo in it would look like a connector that does not serve the optional operation.
 */
class AuthorityApiClientMqTest {

    private RecordingProxyClient proxyClient;
    private AuthorityApiClient client;
    private ConnectorDto connector;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        client = new AuthorityApiClient(proxyClient);
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
    }

    @Test
    void listCertificateTypes_delegates() throws ConnectorException {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        SupportedCertificateTypeDtoV3 expected = SupportedCertificateTypeDtoV3.of(CertificateType.SSH);
        proxyClient.respondWith(new SupportedCertificateTypeDtoV3[]{expected});

        // when
        List<SupportedCertificateTypeDtoV3> result = client.listCertificateTypes(connector, request);

        // then
        Assertions.assertEquals(List.of(expected), result);
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        Assertions.assertSame(connector, invocation.connector());
        Assertions.assertEquals("/v3/authorityProvider/authorities/certificateTypes", invocation.path());
        Assertions.assertEquals("POST", invocation.method());
        Assertions.assertSame(request, invocation.body());
        Assertions.assertEquals(SupportedCertificateTypeDtoV3[].class, invocation.responseType());
    }

    @Test
    void listCertificateTypes_failsWhenTheConnectorListsATypeTwice() {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        SupportedCertificateTypeDtoV3 repeated = SupportedCertificateTypeDtoV3.of(CertificateType.SSH);
        proxyClient.respondWith(new SupportedCertificateTypeDtoV3[]{repeated, repeated});

        // when
        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions.assertEquals("Connector listed the certificate type SSH more than once", thrown.getMessage());
    }
}
