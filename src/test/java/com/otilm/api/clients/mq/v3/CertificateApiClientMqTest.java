package com.otilm.api.clients.mq.v3;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.connector.v3.certificate.CertificateAttributeListRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoResponseDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Delegation tests for the MQ-based v3 Certificate client's attribute-schema listings. The MQ path constants are an
 * independent second copy of the wire paths (the REST twin is pinned by WireMock in
 * {@code clients/v3/CertificateApiClientTest}); a typo here would ship silently and surface as a connector 404 —
 * indistinguishable, for an optional endpoint, from "this connector does not serve it".
 */
class CertificateApiClientMqTest {

    private RecordingProxyClient proxyClient;
    private CertificateApiClient client;
    private ConnectorDto connector;
    private CertificateAttributeListRequestDtoV3 request;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        client = new CertificateApiClient(proxyClient);
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
        request = new CertificateAttributeListRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        request.setRaProfileAttributes(List.of());
    }

    @Test
    void listRequestAttributes_delegates() throws ConnectorException {
        DataAttributeV3 expected = new DataAttributeV3();
        proxyClient.respondWith(new BaseAttribute[]{expected});

        List<BaseAttribute> result = client.listRequestAttributes(connector, request);

        Assertions.assertEquals(List.of(expected), result);
        assertDelegated("/v3/authorityProvider/certificates/request/attributes");
    }

    @Test
    void listRenewAttributes_delegates() throws ConnectorException {
        DataAttributeV3 expected = new DataAttributeV3();
        proxyClient.respondWith(new BaseAttribute[]{expected});

        List<BaseAttribute> result = client.listRenewAttributes(connector, request);

        Assertions.assertEquals(List.of(expected), result);
        assertDelegated("/v3/authorityProvider/certificates/renew/attributes");
    }

    @Test
    void listIdentifyAttributes_delegates() throws ConnectorException {
        DataAttributeV3 expected = new DataAttributeV3();
        proxyClient.respondWith(new BaseAttribute[]{expected});

        List<BaseAttribute> result = client.listIdentifyAttributes(connector, request);

        Assertions.assertEquals(List.of(expected), result);
        assertDelegated("/v3/authorityProvider/certificates/identify/attributes");
    }

    @Test
    void renewalInfo_delegates() throws ConnectorException {
        CertificateRenewalInfoResponseDto expected = new CertificateRenewalInfoResponseDto();
        proxyClient.respondWith(expected);
        CertificateRenewalInfoRequestDtoV3 renewalInfoRequest = new CertificateRenewalInfoRequestDtoV3();

        CertificateRenewalInfoResponseDto result = client.renewalInfo(connector, renewalInfoRequest);

        Assertions.assertSame(expected, result);
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        Assertions.assertSame(connector, invocation.connector());
        Assertions.assertEquals("/v3/authorityProvider/certificates/renewalInfo", invocation.path());
        Assertions.assertEquals("POST", invocation.method());
        Assertions.assertSame(renewalInfoRequest, invocation.body());
        Assertions.assertEquals(CertificateRenewalInfoResponseDto.class, invocation.responseType());
    }

    private void assertDelegated(String path) {
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        Assertions.assertSame(connector, invocation.connector());
        Assertions.assertEquals(path, invocation.path());
        Assertions.assertEquals("POST", invocation.method());
        Assertions.assertSame(request, invocation.body());
        Assertions.assertEquals(BaseAttribute[].class, invocation.responseType());
    }
}
