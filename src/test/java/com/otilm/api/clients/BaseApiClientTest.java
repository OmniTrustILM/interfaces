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
import com.otilm.api.model.core.proxy.ProxyDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.*;

import org.bouncycastle.asn1.x500.X500Name;
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
    void setUp() {
        mockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());
        mockServer.stubFor(get(anyUrl()).willReturn(aResponse().withStatus(200)));

        client = new TestApiClient(BaseApiClient.prepareWebClient());
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

    /**
     * Starts an in-memory SSLServerSocket with needClientAuth=true and a truststore containing only
     * the generated client cert. The request succeeds only if BaseApiClient wires the SSL context
     * (including the client keystore) into the WebClient — the bug this test guards was that the
     * mutated WebClient was discarded, so no client cert was ever sent.
     */
    @Test
    void prepareRequest_certificateAuth_clientCertPresentedDuringMtls() throws Exception {
        KeyPair serverKeyPair = generateKeyPair();
        X509Certificate serverCert = generateCert(serverKeyPair, "localhost");
        KeyPair clientKeyPair = generateKeyPair();
        X509Certificate clientCert = generateCert(clientKeyPair, "client");

        MtlsServer server = startMtlsServer(serverKeyPair, serverCert, clientCert);
        try {
            String clientKsBase64 = Base64.getEncoder().encodeToString(
                    buildPkcs12(clientKeyPair, clientCert, "client", "clientPass"));
            String clientTsBase64 = Base64.getEncoder().encodeToString(
                    buildTrustStore(serverCert, "server", "clientTrustPass"));

            FileAttributeContentData ksData = new FileAttributeContentData();
            ksData.setContent(clientKsBase64);
            ksData.setFileName("client.p12");

            FileAttributeContentData tsData = new FileAttributeContentData();
            tsData.setContent(clientTsBase64);
            tsData.setFileName("trust.p12");

            List<ResponseAttribute> authAttributes = List.of(
                    responseAttribute("keyStoreType", AttributeContentType.STRING, new StringAttributeContentV2("PKCS12")),
                    responseAttribute("keyStore", AttributeContentType.FILE, new FileAttributeContentV2(null, ksData)),
                    responseAttribute("keyStorePassword", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("clientPass"))),
                    responseAttribute("trustStoreType", AttributeContentType.STRING, new StringAttributeContentV2("PKCS12")),
                    responseAttribute("trustStore", AttributeContentType.FILE, new FileAttributeContentV2(null, tsData)),
                    responseAttribute("trustStorePassword", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("clientTrustPass")))
            );

            String url = "https://localhost:" + server.port();
            TestConnectorInfo connector = new TestConnectorInfo(url, AuthType.CERTIFICATE, authAttributes);

            assertDoesNotThrow(() ->
                    client.prepareRequest(HttpMethod.GET, connector, false)
                            .uri(url + "/test")
                            .retrieve()
                            .toBodilessEntity()
                            .block()
            );

            // Would have thrown on handshake failure if the client cert was not wired in
            server.future().get(5, TimeUnit.SECONDS);
        } finally {
            server.close();
        }
    }

    @Test
    void prepareRequest_certificateAuth_failsWithoutClientKeystore() throws Exception {
        KeyPair serverKeyPair = generateKeyPair();
        X509Certificate serverCert = generateCert(serverKeyPair, "localhost");
        KeyPair clientKeyPair = generateKeyPair();
        X509Certificate clientCert = generateCert(clientKeyPair, "client");

        MtlsServer server = startMtlsServer(serverKeyPair, serverCert, clientCert);
        try {
            // Only truststore — no keystore, so no client cert is sent
            String clientTsBase64 = Base64.getEncoder().encodeToString(
                    buildTrustStore(serverCert, "server", "clientTrustPass"));

            FileAttributeContentData tsData = new FileAttributeContentData();
            tsData.setContent(clientTsBase64);
            tsData.setFileName("trust.p12");

            List<ResponseAttribute> authAttributes = List.of(
                    responseAttribute("trustStoreType", AttributeContentType.STRING, new StringAttributeContentV2("PKCS12")),
                    responseAttribute("trustStore", AttributeContentType.FILE, new FileAttributeContentV2(null, tsData)),
                    responseAttribute("trustStorePassword", AttributeContentType.SECRET, new SecretAttributeContentV2(null, new SecretAttributeContentData("clientTrustPass")))
            );

            String url = "https://localhost:" + server.port();
            TestConnectorInfo connector = new TestConnectorInfo(url, AuthType.CERTIFICATE, authAttributes);

            Assertions.assertThrows(Exception.class, () ->
                    client.prepareRequest(HttpMethod.GET, connector, false)
                            .uri(url + "/test")
                            .retrieve()
                            .toBodilessEntity()
                            .block()
            );
        } finally {
            server.close();
        }
    }

    private MtlsServer startMtlsServer(KeyPair serverKeyPair, X509Certificate serverCert, X509Certificate trustedClientCert) throws Exception {
        KeyManagerFactory serverKmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        serverKmf.init(loadKeyStore(buildPkcs12(serverKeyPair, serverCert, "server", "serverPass"), "serverPass"), "serverPass".toCharArray());
        TrustManagerFactory serverTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        serverTmf.init(loadKeyStore(buildTrustStore(trustedClientCert, "client", "serverTrustPass"), "serverTrustPass"));
        SSLContext serverSslCtx = SSLContext.getInstance("TLS");
        serverSslCtx.init(serverKmf.getKeyManagers(), serverTmf.getTrustManagers(), null);

        SSLServerSocket serverSocket = (SSLServerSocket) serverSslCtx.getServerSocketFactory().createServerSocket(0);
        serverSocket.setNeedClientAuth(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            try (SSLSocket socket = (SSLSocket) serverSocket.accept()) {
                socket.setSoTimeout(5000);
                byte[] buf = new byte[4096];
                socket.getInputStream().read(buf);
                socket.getOutputStream().write(
                        "HTTP/1.1 200 OK\r\nContent-Length: 0\r\nConnection: close\r\n\r\n".getBytes());
                socket.getOutputStream().flush();
            }
            return null;
        });

        return new MtlsServer(serverSocket, executor, future);
    }

    private record MtlsServer(SSLServerSocket socket, ExecutorService executor, Future<?> future) {
        int port() { return socket.getLocalPort(); }
        void close() throws Exception {
            executor.shutdownNow();
            socket.close();
        }
    }

    @Test
    void prepareRequest_basicAuth_missingCredentials_throwsIllegalArgumentException() {
        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.BASIC, List.of());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                client.prepareRequest(HttpMethod.GET, connector, false));
    }

    @Test
    void prepareRequest_apiKeyAuth_missingCredentials_throwsIllegalArgumentException() {
        TestConnectorInfo connector = new TestConnectorInfo("http://localhost:" + mockServer.port(), AuthType.API_KEY, List.of());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                client.prepareRequest(HttpMethod.GET, connector, false));
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    private static X509Certificate generateCert(KeyPair keyPair, String cn) throws Exception {
        X500Name subject = new X500Name("CN=" + cn);
        Date notBefore = new Date(System.currentTimeMillis() - 1000);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().getCertificate(
                new JcaX509v3CertificateBuilder(subject, BigInteger.ONE, notBefore, notAfter, subject, keyPair.getPublic())
                        .build(signer));
    }

    private static byte[] buildPkcs12(KeyPair keyPair, X509Certificate cert, String alias, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null);
        ks.setKeyEntry(alias, keyPair.getPrivate(), password.toCharArray(), new Certificate[]{cert});
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ks.store(out, password.toCharArray());
        return out.toByteArray();
    }

    private static KeyStore loadKeyStore(byte[] bytes, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new ByteArrayInputStream(bytes), password.toCharArray());
        return ks;
    }

    private static byte[] buildTrustStore(X509Certificate cert, String alias, String password) throws Exception {
        KeyStore ts = KeyStore.getInstance("PKCS12");
        ts.load(null, null);
        ts.setCertificateEntry(alias, cert);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ts.store(out, password.toCharArray());
        return out.toByteArray();
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
        TestApiClient(WebClient webClient) {
            super(webClient, null);
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
        public ConnectorStatus getStatus() { return ConnectorStatus.CONNECTED; }
        public AuthType getAuthType() { return authType; }
        public List<ResponseAttribute> getAuthAttributes() { return authAttributes; }
        public ProxyDto getProxy() { return null; }
    }
}
