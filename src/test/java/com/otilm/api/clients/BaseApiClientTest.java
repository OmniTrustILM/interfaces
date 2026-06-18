package com.otilm.api.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.attribute.ResponseAttributeV2;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.otilm.api.model.common.attribute.v2.content.FileAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.SecretAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.core.connector.AuthType;
import com.otilm.api.model.core.connector.ConnectorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BaseApiClientTest {

    private WireMockServer mockServer;
    private TestApiClient client;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException, KeyStoreException {
        mockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());
        mockServer.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

        javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance(javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);
        client = new TestApiClient(BaseApiClient.prepareWebClient(), tmf.getTrustManagers());
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void prepareRequest_basicAuth_sendsCorrectAuthorizationHeader() {
        List<ResponseAttribute> authAttributes = List.of(
                responseAttribute("username", AttributeContentType.STRING, new StringAttributeContentV2("admin")),
                responseAttribute("password", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("secret123")))
        );

        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.BASIC, authAttributes);

        assertDoesNotThrow(() ->
                client.prepareRequest(HttpMethod.GET, connector, false)
                        .uri("http://localhost:" + mockServer.port() + "/test")
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );

        String expectedHeader = "Basic " + Base64.getEncoder().encodeToString("admin:secret123".getBytes(StandardCharsets.UTF_8));
        mockServer.verify(getRequestedFor(urlEqualTo("/test"))
                .withHeader("Authorization", equalTo(expectedHeader)));
    }

    @Test
    void prepareRequest_apiKeyAuth_sendsCorrectApiKeyHeader() {
        List<ResponseAttribute> authAttributes = List.of(
                responseAttribute("apiKeyHeader", AttributeContentType.STRING, new StringAttributeContentV2("X-API-KEY")),
                responseAttribute("apiKey", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("my-api-key")))
        );

        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.API_KEY, authAttributes);

        assertDoesNotThrow(() ->
                client.prepareRequest(HttpMethod.GET, connector, false)
                        .uri("http://localhost:" + mockServer.port() + "/test")
                        .retrieve()
                        .toBodilessEntity()
                        .block()
        );

        mockServer.verify(getRequestedFor(urlEqualTo("/test"))
                .withHeader("X-API-KEY", equalTo("my-api-key")));
    }

    @Test
    void prepareRequest_certificateAuth_buildsSslContextWithoutThrowing() throws Exception {
        String keystoreBase64 = generatePkcs12Base64();

        FileAttributeContentData fileData = new FileAttributeContentData();
        fileData.setContent(keystoreBase64);
        fileData.setFileName("keystore.p12");

        List<ResponseAttribute> authAttributes = List.of(
                responseAttribute("keyStoreType", AttributeContentType.STRING, new StringAttributeContentV2("PKCS12")),
                responseAttribute("keyStore", AttributeContentType.FILE, new FileAttributeContentV2(null, fileData)),
                responseAttribute("keyStorePassword", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("keystorePass")))
        );

        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.CERTIFICATE, authAttributes);

        assertDoesNotThrow(() -> client.prepareRequest(HttpMethod.GET, connector, false));
    }

    @Test
    void prepareRequest_basicAuth_missingCredentials_throwsIllegalArgumentException() {
        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.BASIC, List.of());

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                client.prepareRequest(HttpMethod.GET, connector, false));
    }

    @Test
    void prepareRequest_apiKeyAuth_missingCredentials_throwsIllegalArgumentException() {
        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.API_KEY, List.of());

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                client.prepareRequest(HttpMethod.GET, connector, false));
    }

    private String generatePkcs12Base64() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        X500Name subject = new X500Name("CN=test");
        java.math.BigInteger serial = java.math.BigInteger.valueOf(1);
        Date notBefore = new Date(System.currentTimeMillis() - 1000);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(subject, serial, notBefore, notAfter, subject, keyPair.getPublic());
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certBuilder.build(signer));

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("testAlias", keyPair.getPrivate(), "keystorePass".toCharArray(), new java.security.cert.Certificate[]{cert});

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        keyStore.store(out, "keystorePass".toCharArray());
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private ResponseAttributeV2 responseAttribute(String name, AttributeContentType contentType, com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2<?> content) {
        ResponseAttributeV2 attr = new ResponseAttributeV2();
        attr.setUuid(UUID.randomUUID());
        attr.setName(name);
        attr.setContentType(contentType);
        attr.setContent(List.of(content));
        return attr;
    }

    private static class TestApiClient extends BaseApiClient {
        TestApiClient(WebClient webClient, javax.net.ssl.TrustManager[] trustManagers) {
            super(webClient, trustManagers);
        }
    }

    private record TestConnectorInfo(
            String url,
            AuthType authType,
            List<ResponseAttribute> authAttributes
    ) implements ApiClientConnectorInfo {
        public String getUuid() { return "test-uuid"; }
        public String getName() { return "test-connector"; }
        public String getUrl() { return url; }
        public com.otilm.api.model.core.connector.ConnectorStatus getStatus() { return ConnectorStatus.CONNECTED; }
        public AuthType getAuthType() { return authType; }
        public List<ResponseAttribute> getAuthAttributes() { return authAttributes; }
        public com.otilm.api.model.core.proxy.ProxyDto getProxy() { return null; }
    }
}
