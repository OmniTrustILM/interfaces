package com.otilm.api.model.core.logging;

import com.otilm.api.model.client.auth.UserIdentificationRequestDto;
import com.otilm.api.model.client.authority.ClientBaseEndEntityRequestDto;
import com.otilm.api.model.client.authority.LegacyClientCertificateSignRequestDto;
import com.otilm.api.model.client.cmp.BaseCmpProfileRequestDto;
import com.otilm.api.model.client.scep.BaseScepProfileRequestDto;
import com.otilm.api.model.client.signing.protocols.tsp.TspBasicCredentialCreateRequestDto;
import com.otilm.api.model.client.signing.protocols.tsp.TspBasicCredentialUpdateRequestDto;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.otilm.api.model.common.events.data.CertificateRegisteredEventData;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.secrets.content.ApiKeySecretContent;
import com.otilm.api.model.connector.secrets.content.BasicAuthSecretContent;
import com.otilm.api.model.connector.secrets.content.GenericSecretContent;
import com.otilm.api.model.connector.secrets.content.JwtTokenSecretContent;
import com.otilm.api.model.connector.secrets.content.KeyStoreSecretContent;
import com.otilm.api.model.connector.secrets.content.KeyValueSecretContent;
import com.otilm.api.model.connector.secrets.content.PrivateKeySecretContent;
import com.otilm.api.model.connector.secrets.content.SecretKeySecretContent;
import com.otilm.api.model.core.acme.AcmeEabKeyDto;
import com.otilm.api.model.core.authority.BaseEndEntityRequestDto;
import com.otilm.api.model.core.authority.CertificateSignRequestDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.settings.authentication.OAuth2ProviderSettingsUpdateDto;
import com.otilm.api.model.core.v2.ClientCertificateIssueRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRegistrationDto;
import com.otilm.api.model.core.v2.ClientCertificateRekeyRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRenewRequestDto;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Core writes a field marked {@link Sensitive} as redacted wherever it logs or records a DTO, so every field that holds
 * a secret carries the mark.
 */
class SensitiveFieldsTest {

    static Stream<Arguments> secretFields() {
        return Stream
                .of(Arguments.of(ApiKeySecretContent.class, "content"),
                        Arguments.of(BasicAuthSecretContent.class, "password"),
                        Arguments.of(GenericSecretContent.class, "content"),
                        Arguments.of(JwtTokenSecretContent.class, "content"),
                        Arguments.of(KeyStoreSecretContent.class, "content"),
                        Arguments.of(KeyStoreSecretContent.class, "password"),
                        Arguments.of(KeyValueSecretContent.class, "content"),
                        Arguments.of(PrivateKeySecretContent.class, "content"),
                        Arguments.of(SecretKeySecretContent.class, "content"),
                        Arguments.of(BaseScepProfileRequestDto.class, "challengePassword"),
                        Arguments.of(BaseScepProfileRequestDto.class, "intuneApplicationKey"),
                        Arguments.of(BaseCmpProfileRequestDto.class, "sharedSecret"),
                        Arguments.of(TspBasicCredentialCreateRequestDto.class, "password"),
                        Arguments.of(TspBasicCredentialUpdateRequestDto.class, "password"),
                        Arguments.of(CertificateSignRequestDto.class, "password"),
                        Arguments.of(LegacyClientCertificateSignRequestDto.class, "password"),
                        Arguments.of(ExportKeyRequestV2Dto.class, "passphrase"),
                        Arguments.of(ImportKeyRequestV2Dto.class, "passphrase"),
                        Arguments.of(KeyItemDetailDto.class, "keyData"),
                        Arguments.of(OAuth2ProviderSettingsUpdateDto.class, "clientSecret"),
                        Arguments.of(UserIdentificationRequestDto.class, "authenticationToken"),
                        Arguments.of(FileAttributeContentData.class, "content"),
                        Arguments.of(ClientCertificateIssueRequestDto.class, "authorizationSecret"),
                        Arguments.of(ClientCertificateRekeyRequestDto.class, "authorizationSecret"),
                        Arguments.of(ClientCertificateRegistrationDto.class, "authorizationSecret"),
                        Arguments.of(ClientCertificateRenewRequestDto.class, "authorizationSecret"),
                        Arguments.of(CertificateRegisteredEventData.class, "credential"),
                        Arguments.of(AcmeEabKeyDto.class, "key"),
                        Arguments.of(BaseEndEntityRequestDto.class, "password"),
                        Arguments.of(ClientBaseEndEntityRequestDto.class, "password"));
    }

    @ParameterizedTest(name = "{0}.{1}")
    @MethodSource("secretFields")
    void everySecretField_isMarkedSensitive(Class<?> type, String fieldName) throws NoSuchFieldException {
        assertTrue(type.getDeclaredField(fieldName).isAnnotationPresent(Sensitive.class));
    }

    @Test
    void oAuth2ClientSecret_staysOutOfToString() {
        OAuth2ProviderSettingsUpdateDto settings = new OAuth2ProviderSettingsUpdateDto();
        settings.setClientId("core-client");
        settings.setClientSecret("oauth2-client-secret-value");

        String rendered = settings.toString();

        assertTrue(rendered.contains("core-client"));
        assertFalse(rendered.contains("oauth2-client-secret-value"));
    }

    @Test
    void authenticationToken_staysOutOfToString() {
        UserIdentificationRequestDto request = new UserIdentificationRequestDto();
        request.setCertificateContent("certificate-content-value");
        request.setAuthenticationToken("header.payload.signature");

        String rendered = request.toString();

        assertTrue(rendered.contains("certificate-content-value"));
        assertFalse(rendered.contains("header.payload.signature"));
    }

    @Test
    void fileAttributeContent_toStringShowsLengthNotContent() {
        FileAttributeContentData data = new FileAttributeContentData();
        String secretBytes = "TOP-SECRET-FILE-CONTENT-BYTES";
        data.setContent(secretBytes);
        data.setFileName("keystore.p12");
        data.setMimeType("application/x-pkcs12");

        String rendered = data.toString();

        assertTrue(rendered.contains("keystore.p12"));
        assertTrue(rendered.contains("application/x-pkcs12"));
        assertTrue(rendered.contains(String.valueOf(secretBytes.length())));
        assertFalse(rendered.contains(secretBytes));
    }

    @Test
    void fileAttributeContent_toStringShowsNoneWhenContentAbsent() {
        FileAttributeContentData data = new FileAttributeContentData();
        data.setFileName("keystore.p12");
        data.setMimeType("application/x-pkcs12");

        assertTrue(data.toString().contains("none"));
    }

    @Test
    void acmeEabKey_staysOutOfToString() {
        AcmeEabKeyDto dto = new AcmeEabKeyDto();
        dto.setKey("eab-hmac-key-value");

        String rendered = dto.toString();

        assertFalse(rendered.contains("eab-hmac-key-value"));
    }
}
