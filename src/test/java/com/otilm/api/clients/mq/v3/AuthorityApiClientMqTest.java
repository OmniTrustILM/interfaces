package com.otilm.api.clients.mq.v3;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.connector.v3.authority.AuthorityConnectionResponseDto;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.ListedCertificateType;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import com.otilm.api.model.core.certificate.CertificateType;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    void checkAuthorityConnection_returnsMetadata() throws ConnectorException {
        // given
        List<RequestAttribute> attributes = List.of();
        AuthorityConnectionResponseDto body = new AuthorityConnectionResponseDto();
        body.setMetadata(List.of(new MetadataAttributeV3()));
        proxyClient.respondWithEntity(ResponseEntity.ok(body));

        // when
        ResponseEntity<AuthorityConnectionResponseDto> response = client
                .checkAuthorityConnection(connector, attributes);

        // then
        Assertions.assertSame(body, response.getBody());
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        Assertions.assertEquals("/v3/authorityProvider/authorities", invocation.path());
        Assertions.assertEquals("POST", invocation.method());
        Assertions.assertSame(attributes, invocation.body());
        Assertions.assertEquals(AuthorityConnectionResponseDto.class, invocation.responseType());
    }

    @Test
    void checkAuthorityConnection_returnsNoBodyOn204() throws ConnectorException {
        // given
        proxyClient.respondWithEntity(ResponseEntity.noContent().build());

        // when
        ResponseEntity<AuthorityConnectionResponseDto> response = client.checkAuthorityConnection(connector, List.of());

        // then
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    void listCertificateTypes_delegates() throws ConnectorException {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        SupportedCertificateTypeDtoV3 expected = SupportedCertificateTypeDtoV3.of(CertificateType.SSH);
        proxyClient.respondWith(new ListedCertificateType[]{ListedCertificateType.of("SSH")});

        // when
        List<SupportedCertificateTypeDtoV3> result = client.listCertificateTypes(connector, request);

        // then
        Assertions.assertEquals(List.of(expected), result);
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        Assertions.assertSame(connector, invocation.connector());
        Assertions.assertEquals("/v3/authorityProvider/authorities/certificateTypes", invocation.path());
        Assertions.assertEquals("POST", invocation.method());
        Assertions.assertSame(request, invocation.body());
        Assertions.assertEquals(ListedCertificateType[].class, invocation.responseType());
    }

    @Test
    void listCertificateTypes_failsWhenTheConnectorListsATypeTwice() {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        ListedCertificateType repeated = ListedCertificateType.of("SSH");
        proxyClient.respondWith(new ListedCertificateType[]{repeated, repeated});

        // when
        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions.assertEquals("Connector listed the certificate type SSH more than once", thrown.getMessage());
    }

    @Test
    void listCertificateTypes_leavesOutATypeItDoesNotKnow() throws ConnectorException {
        // given
        var unknownCode = "a-type-of-a-later-version";
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        proxyClient
                .respondWith(new ListedCertificateType[]{
                        ListedCertificateType.of("X.509"),
                        ListedCertificateType.of(unknownCode)});

        // when
        List<SupportedCertificateTypeDtoV3> result = client.listCertificateTypes(connector, request);

        // then
        Assertions.assertEquals(List.of(SupportedCertificateTypeDtoV3.of(CertificateType.X509)), result);
    }

    @Test
    void listCertificateTypes_failsWhenTheConnectorListsOnlyTypesItDoesNotKnow() {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        proxyClient.respondWith(new ListedCertificateType[]{ListedCertificateType.of("a-type-of-a-later-version")});

        // when
        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions
                .assertEquals("Connector listed only certificate types this version does not know",
                        thrown.getMessage());
    }

    @Test
    void listCertificateTypes_returnsNoTypesWhenTheConnectorAnswersWithoutABody() throws ConnectorException {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        proxyClient.respondWith(null);

        // when
        List<SupportedCertificateTypeDtoV3> result = client.listCertificateTypes(connector, request);

        // then
        Assertions.assertEquals(List.of(), result);
    }
}
