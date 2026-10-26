package com.otilm.api.model.connector.cryptography.v2.utils;

import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventType;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventV2Dto;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;

/**
 * Key lifecycle request and response fixtures. A PKCS#10 carries a real self-signature for the contract checks to
 * verify.
 */
public final class KeyLifecycleDtoFixtures {

    public static final String BOUND_SIGNER = "jane.doe|signers.example";
    public static final String SIGNER_ID = "jane.doe";
    public static final UUID SIGNER_ID_ATTRIBUTE_UUID = UUID.fromString("2b7e1d3c-9a4f-4e6b-b1c8-0d5a7f9e3c21");
    public static final UUID CERTIFICATE_UUID = UUID.fromString("6f1c2a9e-4b3d-4c5e-8f70-1a2b3c4d5e6f");
    public static final SignatureAlgorithm CERTIFICATE_REQUEST_ALGORITHM = SignatureAlgorithm.SHA256_WITH_RSA;
    public static final OffsetDateTime EVENT_TIME = OffsetDateTime.of(2026, 10, 25, 10, 0, 0, 0, ZoneOffset.UTC);
    public static final String REQUESTED_SUBJECT = "CN=Jane Doe";

    private static final String SIGNER_ID_NAME = "id";
    private static final int FILLER_SIGNATURE_LENGTH = 256;
    private static final int RSA_KEY_SIZE = 2048;
    private static final Provider BOUNCY_CASTLE = new BouncyCastleProvider();
    private static final KeyPair REQUEST_KEY_PAIR = rsaKeyPair();

    private KeyLifecycleDtoFixtures() {
    }

    public static byte[] certificationRequestInfo() {
        return certificationRequestInfo(REQUESTED_SUBJECT);
    }

    public static byte[] certificationRequestInfo(String subject) {
        return requestInfo(new X500Name(subject), REQUEST_KEY_PAIR.getPublic());
    }

    public static byte[] certificationRequestInfo(PublicKey publicKey) {
        return requestInfo(new X500Name(REQUESTED_SUBJECT), publicKey);
    }

    public static byte[] certificationRequestInfo(X500Name subject, Attribute... attributes) {
        return requestInfo(subject, REQUEST_KEY_PAIR.getPublic(), attributes);
    }

    public static byte[] pkcs10(byte[] certificationRequestInfo, SignatureAlgorithm algorithm) {
        return pkcs10(certificationRequestInfo, algorithm, REQUEST_KEY_PAIR.getPrivate());
    }

    public static byte[] pkcs10(byte[] certificationRequestInfo, SignatureAlgorithm algorithm, PrivateKey signingKey) {
        return assemble(certificationRequestInfo, algorithm.getAlgorithmIdentifier(),
                sign(certificationRequestInfo, algorithm, signingKey));
    }

    /**
     * Signs under the algorithm and names it with another encoding of its identifier.
     */
    public static byte[] pkcs10(byte[] certificationRequestInfo, SignatureAlgorithm algorithm,
            AlgorithmIdentifier encodedAs) {
        return assemble(certificationRequestInfo, encodedAs,
                sign(certificationRequestInfo, algorithm, REQUEST_KEY_PAIR.getPrivate()));
    }

    public static byte[] pkcs10WithFillerSignature(byte[] certificationRequestInfo,
            AlgorithmIdentifier signatureAlgorithm) {
        return assemble(certificationRequestInfo, signatureAlgorithm, new byte[FILLER_SIGNATURE_LENGTH]);
    }

    /**
     * A fresh key pair the algorithm signs with.
     */
    public static KeyPair keyPairFor(SignatureAlgorithm algorithm) {
        return switch (algorithm) {
            case SHA256_WITH_RSA, SHA384_WITH_RSA, SHA512_WITH_RSA, SHA256_WITH_RSA_PSS, SHA384_WITH_RSA_PSS,
                    SHA512_WITH_RSA_PSS ->
                rsaKeyPair();
            case SHA256_WITH_ECDSA, SHA384_WITH_ECDSA, SHA512_WITH_ECDSA -> ecKeyPair();
            default -> keyPair(algorithm.getCode());
        };
    }

    /** Stands in for a DER certificate where only its presence matters. */
    public static byte[] certificateFiller() {
        return new byte[]{0x30, 0x00};
    }

    public static CreateCertificateRequestV2Dto validBoundCertificateRequest() {
        CreateCertificateRequestV2Dto request = certificateRequest();
        request.setSigner(BOUND_SIGNER);
        return request;
    }

    public static CreateCertificateRequestV2Dto validUnboundCertificateRequest() {
        CreateCertificateRequestV2Dto request = certificateRequest();
        request.setSignerAttributes(List.of(signerIdAttribute()));
        return request;
    }

    public static DataAttributeV3 signerIdDefinition() {
        return aDataAttribute().withUuid(SIGNER_ID_ATTRIBUTE_UUID.toString()).withName(SIGNER_ID_NAME).build();
    }

    public static RequestAttributeV3 signerIdAttribute() {
        return new RequestAttributeV3(SIGNER_ID_ATTRIBUTE_UUID, SIGNER_ID_NAME, AttributeContentType.STRING,
                List.of(new StringAttributeContentV3(SIGNER_ID)));
    }

    public static CertificateRequestCreationResponseV2Dto validCertificateRequestResponse() {
        CertificateRequestCreationResponseV2Dto response = new CertificateRequestCreationResponseV2Dto();
        response.setPkcs10(pkcs10(certificationRequestInfo(), CERTIFICATE_REQUEST_ALGORITHM));
        response.setSigner(BOUND_SIGNER);
        response.setEvents(List.of(validEvent(OperationEventType.AUTHORIZATION_GRANTED)));
        return response;
    }

    public static CertificateIssuedRequestV2Dto validCertificateIssuedRequest() {
        CertificateIssuedRequestV2Dto request = withValidTokenProfileScope(new CertificateIssuedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setCertificate(certificateFiller());
        request.setChain(List.of(certificateFiller()));
        request.setCertificateUuid(CERTIFICATE_UUID);
        return request;
    }

    public static CertificateIssuedResponseV2Dto validCertificateIssuedResponse() {
        CertificateIssuedResponseV2Dto response = new CertificateIssuedResponseV2Dto();
        response.setEvents(List.of(validEvent(OperationEventType.OPERATION_ACCEPTED)));
        return response;
    }

    public static OperationEventV2Dto validEvent(OperationEventType type) {
        OperationEventV2Dto event = new OperationEventV2Dto();
        event.setType(type);
        event.setAt(EVENT_TIME);
        return event;
    }

    private static CreateCertificateRequestV2Dto certificateRequest() {
        CreateCertificateRequestV2Dto request = withValidTokenProfileScope(new CreateCertificateRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setCertificationRequestInfo(certificationRequestInfo());
        request.setSignatureAlgorithm(CERTIFICATE_REQUEST_ALGORITHM);
        return request;
    }

    private static byte[] requestInfo(X500Name subject, PublicKey publicKey, Attribute... attributes) {
        return der(new CertificationRequestInfo(subject, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()),
                new DERSet(attributes)));
    }

    private static byte[] assemble(byte[] certificationRequestInfo, AlgorithmIdentifier signatureAlgorithm,
            byte[] signature) {
        return der(new CertificationRequest(CertificationRequestInfo.getInstance(certificationRequestInfo),
                signatureAlgorithm, new DERBitString(signature)));
    }

    private static byte[] sign(byte[] data, SignatureAlgorithm algorithm, PrivateKey signingKey) {
        try {
            Signature signer = Signature.getInstance(algorithm.getCode(), BOUNCY_CASTLE);
            signer.initSign(signingKey);
            signer.update(data);
            return signer.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static KeyPair rsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", BOUNCY_CASTLE);
            generator.initialize(RSA_KEY_SIZE);
            return generator.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static KeyPair ecKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE);
            generator.initialize(new ECGenParameterSpec("secp256r1"));
            return generator.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static KeyPair keyPair(String algorithm) {
        try {
            return KeyPairGenerator.getInstance(algorithm, BOUNCY_CASTLE).generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] der(ASN1Object object) {
        try {
            return object.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
