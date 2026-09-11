package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.model.connector.signatures.contentsigning.cades.CadesComputeDtbsRequestDto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Validation and wire behaviour of the family-invariant formatting request and response bodies. */
class ContentSigningFormattingDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final byte[] SHA256_DIGEST = new byte[DigestAlgorithm.SHA_256.getDigestSizeBytes()];

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void aFullyPopulatedComputeDtbsRequestIsValid() {
        assertTrue(VALIDATOR.validate(validComputeDtbs()).isEmpty());
    }

    @Test
    void computeDtbsRequiresItsCoreSuppliedFields() {
        CadesComputeDtbsRequestDto dto = new CadesComputeDtbsRequestDto();

        Set<String> paths = paths(VALIDATOR.validate(dto));

        assertTrue(paths.contains("document"), paths.toString());
        assertTrue(paths.contains("signerCertificateChain"), paths.toString());
        assertTrue(paths.contains("signingTime"), paths.toString());
        assertTrue(paths.contains("signatureAlgorithm"), paths.toString());
        assertTrue(paths.contains("formattingAttributes"), paths.toString());
    }

    /** An empty chain is not a chain: without a signer certificate there is nothing to build a signature around. */
    @Test
    void computeDtbsRejectsAnEmptySignerCertificateChain() {
        CadesComputeDtbsRequestDto dto = validComputeDtbs();
        dto.setSignerCertificateChain(List.of());

        assertTrue(paths(VALIDATOR.validate(dto)).contains("signerCertificateChain"));
    }

    /**
     * The profile's attribute list may legitimately be empty, but it must be present — an absent list and an empty one
     * would otherwise be indistinguishable, and only one of them means "the operator configured nothing".
     */
    @Test
    void anEmptyFormattingAttributeListIsAccepted() {
        CadesComputeDtbsRequestDto dto = validComputeDtbs();
        dto.setFormattingAttributes(List.of());

        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    /** The nested transport's own rule must be enforced through the request, not only when validated on its own. */
    @Test
    void anInvalidDocumentTransferFailsThroughTheRequest() {
        CadesComputeDtbsRequestDto dto = validComputeDtbs();
        dto.setDocument(new DigestOnlyDocumentTransferDto(new byte[]{7, 7, 7}, null));

        assertTrue(paths(VALIDATOR.validate(dto)).contains("document.digestAlgorithm"),
                "the document transport rule did not cascade from the request");
    }

    @Test
    void computeDtbsRoundTripsItsSigningTime() throws Exception {
        CadesComputeDtbsRequestDto original = validComputeDtbs();

        String json = mapper.writeValueAsString(original);
        ComputeDtbsRequestDto deserialized = mapper.readValue(json, ComputeDtbsRequestDto.class);

        assertTrue(json.contains("\"signingTime\":\"2026-08-11T09:30:00Z\""), json);
        assertEquals(original.getSigningTime(), deserialized.getSigningTime());
    }

    /** The connector matches the algorithm by code, so the enum's spelling is what has to reach the wire. */
    @Test
    void computeDtbsCarriesItsSignatureAlgorithmCodeOverTheWire() throws Exception {
        CadesComputeDtbsRequestDto original = validComputeDtbs();
        original.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA_PSS);

        String json = mapper.writeValueAsString(original);
        ComputeDtbsRequestDto deserialized = mapper.readValue(json, ComputeDtbsRequestDto.class);

        assertTrue(json.contains("\"signatureAlgorithm\":\"SHA256withRSAandMGF1\""), json);
        assertEquals(SignatureAlgorithm.SHA256_WITH_RSA_PSS, deserialized.getSignatureAlgorithm());
    }

    @Test
    void embedSignatureValueRequiresItsCoreSuppliedFields() {
        Set<String> paths = paths(VALIDATOR.validate(new EmbedSignatureValueRequestDto()));

        assertTrue(paths.contains("signatureValue"), paths.toString());
        assertTrue(paths.contains("formattingContext"), paths.toString());
        assertTrue(paths.contains("signatureAlgorithm"), paths.toString());
    }

    @Test
    void embedSignatureValueRoundTrips() throws Exception {
        EmbedSignatureValueRequestDto original = new EmbedSignatureValueRequestDto();
        original.setFamily(SignatureFamily.XADES);
        original.setFormattingAttributes(List.of());
        original.setSignatureValue(new byte[]{1, 2, 3});
        original.setSignatureAlgorithm(SignatureAlgorithm.SHA384_WITH_ECDSA);
        original.setFormattingContext(new byte[]{4, 5, 6});

        EmbedSignatureValueRequestDto deserialized = mapper
                .readValue(mapper.writeValueAsString(original), EmbedSignatureValueRequestDto.class);

        assertEquals(SignatureFamily.XADES, deserialized.getFamily());
        assertArrayEquals(original.getSignatureValue(), deserialized.getSignatureValue());
        assertEquals(SignatureAlgorithm.SHA384_WITH_ECDSA, deserialized.getSignatureAlgorithm());
        assertArrayEquals(original.getFormattingContext(), deserialized.getFormattingContext());
    }

    /** The opaque context can be most of a document's size, so it must not be rendered into a log line. */
    @Test
    void embedSignatureValueToStringOmitsTheFormattingContext() {
        EmbedSignatureValueRequestDto dto = new EmbedSignatureValueRequestDto();
        dto.setFormattingContext(new byte[]{4, 5, 6});

        assertFalse(dto.toString().contains("formattingContext=["), dto.toString());
    }

    @Test
    void aSignedDocumentRequestRequiresItsDocument() {
        assertTrue(paths(VALIDATOR.validate(new SignedDocumentRequestDto())).contains("signedDocument"));
    }

    /** Detached content is optional; requiring it would make every enveloped request carry a field it cannot fill. */
    @Test
    void aSignedDocumentRequestNeedsNoDetachedContent() {
        SignedDocumentRequestDto dto = new SignedDocumentRequestDto();
        dto.setFamily(SignatureFamily.PADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});

        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    /**
     * An enveloped request must leave the property out rather than send {@code null}: the published schema types
     * {@code detachedContent} as a non-nullable {@code DocumentTransfer}, so an explicit null fails validation at a
     * strict client.
     */
    @Test
    void anEnvelopedRequestOmitsDetachedContentEntirely() throws Exception {
        SignedDocumentRequestDto dto = new SignedDocumentRequestDto();
        dto.setFamily(SignatureFamily.PADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});

        assertFalse(mapper.readTree(mapper.writeValueAsString(dto)).has("detachedContent"));
    }

    /** The inclusion rule is inherited, so the post-SIGNED subtypes must not start emitting a null either. */
    @Test
    void anEnvelopedTimestampRequestOmitsDetachedContentEntirely() throws Exception {
        EmbedTimestampRequestDto dto = new EmbedTimestampRequestDto();
        dto.setFamily(SignatureFamily.PADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});
        dto.setTimestampToken(new byte[]{4, 5, 6});

        assertFalse(mapper.readTree(mapper.writeValueAsString(dto)).has("detachedContent"));
    }

    /** Omission must not cost the detached case its field: a supplied transfer still reaches the wire. */
    @Test
    void aDetachedRequestStillCarriesItsDetachedContent() throws Exception {
        SignedDocumentRequestDto dto = new SignedDocumentRequestDto();
        dto.setFamily(SignatureFamily.CADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});
        DigestOnlyDocumentTransferDto detached = new DigestOnlyDocumentTransferDto(SHA256_DIGEST,
                DigestAlgorithm.SHA_256);
        dto.setDetachedContent(detached);

        SignedDocumentRequestDto deserialized = mapper
                .readValue(mapper.writeValueAsString(dto), SignedDocumentRequestDto.class);

        assertEquals(detached, deserialized.getDetachedContent());
    }

    @Test
    void detachedContentIsValidatedWhenSupplied() {
        SignedDocumentRequestDto dto = new SignedDocumentRequestDto();
        dto.setFamily(SignatureFamily.CADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});
        dto.setDetachedContent(new InlineDocumentTransferDto(null));

        assertTrue(paths(VALIDATOR.validate(dto)).contains("detachedContent.document"),
                "an empty detached transfer must be rejected through the request");
    }

    @Test
    void embedTimestampRequiresItsToken() {
        assertTrue(paths(VALIDATOR.validate(new EmbedTimestampRequestDto())).contains("timestampToken"));
    }

    /** The imprint twins share this request type, so a field added for one silently arrives at the other. */
    @Test
    void embedTimestampInheritsTheSignedDocumentRequirement() {
        assertTrue(paths(VALIDATOR.validate(new EmbedTimestampRequestDto())).contains("signedDocument"));
    }

    /** Each required field is reported on its own path, so an empty request names all three rather than the first. */
    @Test
    void extendToLevelRequiresItsChainAndTarget() {
        Set<String> paths = paths(VALIDATOR.validate(new ExtendToLevelRequestDto()));

        assertTrue(paths.contains("certificateChain"), paths.toString());
        assertTrue(paths.contains("targetLevel"), paths.toString());
        assertTrue(paths.contains("executionMode"), paths.toString());
    }

    /**
     * Both lists must be present even when empty. An absent list would let a connector read "nothing was resolved" as
     * "nobody looked", which are different situations.
     */
    @Test
    void extendToLevelRequiresEveryListOfItsChain() {
        ExtendToLevelRequestDto dto = new ExtendToLevelRequestDto();
        dto.setCertificateChain(new CertificateChainDto());

        Set<String> paths = paths(VALIDATOR.validate(dto));

        assertTrue(paths.contains("certificateChain.certificates"), paths.toString());
        assertTrue(paths.contains("certificateChain.trustAnchors"), paths.toString());
    }

    @Test
    void extendToLevelAcceptsEmptyChainLists() {
        assertTrue(VALIDATOR.validate(validExtendToLevel()).isEmpty());
    }

    /**
     * The anchor designation may legitimately be empty, in which case every certificate is untrusted. That must be an
     * ordinary accepted request rather than something a connector has to special-case.
     */
    @Test
    void extendToLevelAcceptsAnUndesignatedAnchorSlot() {
        ExtendToLevelRequestDto dto = validExtendToLevel();
        dto.getCertificateChain().setCertificates(List.of(new byte[]{1, 1, 1}));

        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    /** The pre-fetched set is the reserved supply-side seam, so omitting it must stay valid. */
    @Test
    void extendToLevelAcceptsAnAbsentPrefetchedMaterialSet() {
        ExtendToLevelRequestDto dto = validExtendToLevel();
        dto.setPrefetchedMaterial(null);

        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    /** Supplying the set at all commits to supplying all of it, for the same reason the chain lists are required. */
    @Test
    void extendToLevelRequiresEveryListOfASuppliedPrefetchedMaterialSet() {
        ExtendToLevelRequestDto dto = validExtendToLevel();
        dto.setPrefetchedMaterial(new ValidationMaterialDto());

        Set<String> paths = paths(VALIDATOR.validate(dto));

        assertTrue(paths.contains("prefetchedMaterial.certificates"), paths.toString());
        assertTrue(paths.contains("prefetchedMaterial.crls"), paths.toString());
        assertTrue(paths.contains("prefetchedMaterial.ocspResponses"), paths.toString());
    }

    @Test
    void theChainRoundTripsThroughTheWire() throws Exception {
        ExtendToLevelRequestDto dto = validExtendToLevel();
        dto.getCertificateChain().setCertificates(List.of(new byte[]{1, 1, 1}));
        dto.getCertificateChain().setTrustAnchors(List.of(new byte[]{2, 2, 2}));

        String json = mapper.writeValueAsString(dto);
        ExtendToLevelRequestDto deserialized = mapper.readValue(json, ExtendToLevelRequestDto.class);

        assertTrue(json.contains("\"certificateChain\""), json);
        assertTrue(json.contains("\"targetLevel\":\"long_term\""), json);
        assertArrayEquals(new byte[]{2, 2, 2}, deserialized.getCertificateChain().getTrustAnchors().get(0));
        assertArrayEquals(new byte[]{1, 1, 1}, deserialized.getCertificateChain().getCertificates().get(0));
        assertEquals(SignatureLevel.LONG_TERM, deserialized.getTargetLevel());
    }

    /**
     * The manifest is the platform's only account of traffic it did not make itself, so a synchronous response must
     * carry it even when there was nothing to fetch.
     */
    @Test
    void aSynchronousExtendToLevelResponseCarriesItsDocumentAndManifest() {
        ExtendToLevelResponseDto dto = new ExtendToLevelResponseDto();

        Set<String> paths = paths(VALIDATOR.validate(dto, SynchronousResponse.class));

        assertTrue(paths.contains("extendedDocument"), paths.toString());
        assertTrue(paths.contains("fetchManifest"), paths.toString());
    }

    @Test
    void aSynchronousExtendToLevelResponseAcceptsAnEmptyManifest() {
        ExtendToLevelResponseDto dto = new ExtendToLevelResponseDto();
        dto.setExtendedDocument(new byte[]{1, 2, 3});
        dto.setFetchManifest(List.of());

        assertTrue(VALIDATOR.validate(dto, SynchronousResponse.class).isEmpty());
    }

    /** A 202 promises a later result, so returning one now would contradict the handle it is returned with. */
    @Test
    void anAsynchronousExtendToLevelResponseCarriesOnlyItsHandle() {
        ExtendToLevelResponseDto dto = new ExtendToLevelResponseDto();
        dto.setExtendedDocument(new byte[]{1, 2, 3});
        dto.setFetchManifest(List.of());

        Set<String> paths = paths(VALIDATOR.validate(dto, AsynchronousResponse.class));

        assertTrue(paths.contains("extendedDocument"), paths.toString());
        assertTrue(paths.contains("fetchManifest"), paths.toString());
        assertTrue(paths.contains("extendOperationMeta"), paths.toString());
    }

    @Test
    void theFetchManifestRoundTripsThroughTheWire() throws Exception {
        FetchedArtifactDto artifact = new FetchedArtifactDto();
        artifact.setSourceUrl("http://crl.example.com/issuing-ca.crl");
        artifact.setKind(FetchedArtifactKind.CRL);
        artifact.setSha256(SHA256_DIGEST);
        artifact.setFetchedAt(OffsetDateTime.parse("2026-08-15T10:15:30Z"));
        ExtendToLevelResponseDto original = new ExtendToLevelResponseDto();
        original.setExtendedDocument(new byte[]{1, 2, 3});
        original.setFetchManifest(List.of(artifact));

        String json = mapper.writeValueAsString(original);
        ExtendToLevelResponseDto deserialized = mapper.readValue(json, ExtendToLevelResponseDto.class);

        assertTrue(json.contains("\"kind\":\"crl\""), json);
        assertEquals(FetchedArtifactKind.CRL, deserialized.getFetchManifest().get(0).getKind());
        assertEquals(artifact.getFetchedAt(), deserialized.getFetchManifest().get(0).getFetchedAt());
    }

    /** A handle the connector never minted cannot identify anything, so an empty one must be rejected. */
    @Test
    void extendOperationScopedRequestRequiresItsHandle() {
        assertTrue(paths(VALIDATOR.validate(new ExtendOperationScopedRequestDto())).contains("operationMeta"));
    }

    private static ExtendToLevelRequestDto validExtendToLevel() {
        ExtendToLevelRequestDto dto = new ExtendToLevelRequestDto();
        dto.setFamily(SignatureFamily.CADES);
        dto.setFormattingAttributes(List.of());
        dto.setSignedDocument(new byte[]{1, 2, 3});
        dto.setTargetLevel(SignatureLevel.LONG_TERM);
        dto.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        dto.setCertificateChain(emptyChain());
        dto.setPrefetchedMaterial(emptyValidationMaterial());
        return dto;
    }

    private static CertificateChainDto emptyChain() {
        CertificateChainDto chain = new CertificateChainDto();
        chain.setCertificates(List.of());
        chain.setTrustAnchors(List.of());
        return chain;
    }

    private static ValidationMaterialDto emptyValidationMaterial() {
        ValidationMaterialDto material = new ValidationMaterialDto();
        material.setCertificates(List.of());
        material.setCrls(List.of());
        material.setOcspResponses(List.of());
        return material;
    }

    @Test
    void computeDtbsResponseCarriesBothHalvesOfThePair() throws Exception {
        ComputeDtbsResponseDto original = new ComputeDtbsResponseDto();
        original.setDtbs(new byte[]{1, 2, 3});
        original.setFormattingContext(new byte[]{4, 5, 6});
        original.setDocumentDigest(SHA256_DIGEST);
        original.setDocumentDigestAlgorithm(DigestAlgorithm.SHA_256);

        ComputeDtbsResponseDto deserialized = mapper
                .readValue(mapper.writeValueAsString(original), ComputeDtbsResponseDto.class);

        assertArrayEquals(original.getDtbs(), deserialized.getDtbs());
        assertArrayEquals(original.getFormattingContext(), deserialized.getFormattingContext());
    }

    /**
     * The echo is what the platform compares against the digest the user authorized before it releases the signing key,
     * so a response without it would leave nothing to compare and the check would silently pass.
     */
    @Test
    void computeDtbsResponseRequiresTheDocumentDigestEcho() {
        Set<String> paths = paths(VALIDATOR.validate(new ComputeDtbsResponseDto()));

        assertTrue(paths.contains("dtbs"), paths.toString());
        assertTrue(paths.contains("documentDigest"), paths.toString());
        assertTrue(paths.contains("documentDigestAlgorithm"), paths.toString());
        assertTrue(paths.contains("formattingContext"), paths.toString());
    }

    /** Every field the schema calls REQUIRED must also be refused when absent, not only the digest pair. */
    @Test
    void embedResponsesRequireTheirPayload() {
        assertTrue(paths(VALIDATOR.validate(new SignedDocumentResponseDto())).contains("signedDocument"));

        Set<String> imprintPaths = paths(VALIDATOR.validate(new TimestampImprintResponseDto()));
        assertTrue(imprintPaths.contains("imprint"), imprintPaths.toString());
        assertTrue(imprintPaths.contains("digestAlgorithm"), imprintPaths.toString());
    }

    @Test
    void computeDtbsResponseCarriesTheEchoOverTheWire() throws Exception {
        ComputeDtbsResponseDto original = new ComputeDtbsResponseDto();
        original.setDtbs(new byte[]{1, 2, 3});
        original.setFormattingContext(new byte[]{4, 5, 6});
        original.setDocumentDigest(SHA256_DIGEST);
        original.setDocumentDigestAlgorithm(DigestAlgorithm.SHA_256);

        String json = mapper.writeValueAsString(original);
        ComputeDtbsResponseDto deserialized = mapper.readValue(json, ComputeDtbsResponseDto.class);

        assertTrue(json.contains("\"documentDigestAlgorithm\":\"SHA-256\""), json);
        assertArrayEquals(SHA256_DIGEST, deserialized.getDocumentDigest());
        assertEquals(DigestAlgorithm.SHA_256, deserialized.getDocumentDigestAlgorithm());
    }

    /** The digest names the customer document, so it stays out of a log line for the same reason the bytes do. */
    @Test
    void computeDtbsResponseToStringOmitsTheDocumentDigest() {
        ComputeDtbsResponseDto dto = new ComputeDtbsResponseDto();
        dto.setDocumentDigest(SHA256_DIGEST);

        assertFalse(dto.toString().contains("documentDigest=["), dto.toString());
    }

    @Test
    void timestampImprintResponseNamesTheAlgorithmItUsed() throws Exception {
        TimestampImprintResponseDto original = new TimestampImprintResponseDto();
        original.setImprint(new byte[]{1, 2, 3});
        original.setDigestAlgorithm(DigestAlgorithm.SHA_512);

        String json = mapper.writeValueAsString(original);
        TimestampImprintResponseDto deserialized = mapper.readValue(json, TimestampImprintResponseDto.class);

        assertTrue(json.contains("\"digestAlgorithm\":\"SHA-512\""), json);
        assertEquals(DigestAlgorithm.SHA_512, deserialized.getDigestAlgorithm());
    }

    @Test
    void signedDocumentResponseRoundTrips() throws Exception {
        SignedDocumentResponseDto original = new SignedDocumentResponseDto();
        original.setSignedDocument(new byte[]{1, 2, 3});

        SignedDocumentResponseDto deserialized = mapper
                .readValue(mapper.writeValueAsString(original), SignedDocumentResponseDto.class);

        assertArrayEquals(original.getSignedDocument(), deserialized.getSignedDocument());
    }

    @Test
    void signedDocumentResponseToStringOmitsTheSignedDocument() {
        SignedDocumentResponseDto dto = new SignedDocumentResponseDto();
        dto.setSignedDocument(new byte[]{1, 2, 3});

        assertFalse(dto.toString().contains("signedDocument=["), dto.toString());
    }

    @Test
    void signedDocumentRequestToStringOmitsTheSignedDocument() {
        SignedDocumentRequestDto dto = new SignedDocumentRequestDto();
        dto.setSignedDocument(new byte[]{1, 2, 3});

        assertFalse(dto.toString().contains("signedDocument=["), dto.toString());
    }

    @Test
    void computeDtbsResponseToStringOmitsTheFormattingContext() {
        ComputeDtbsResponseDto dto = new ComputeDtbsResponseDto();
        dto.setFormattingContext(new byte[]{4, 5, 6});

        assertFalse(dto.toString().contains("formattingContext=["), dto.toString());
    }

    /**
     * Attribute values are operator-supplied profile configuration and can carry credentials for the systems a family
     * implementation talks to. {@link com.otilm.api.model.client.attribute.RequestAttributeV3} renders its own content
     * through Lombok, so the exclusion on the base is the only thing keeping those values out of a log line — and it is
     * the one exclusion enforced through {@code callSuper} across every request subtype, so it fails silently if a
     * subtype ever declares {@code @ToString} without it.
     */
    @Test
    void toStringOmitsFormattingAttributesOnEveryRequestSubtype() {
        String secret = "s3cr3t-token-value";
        RequestAttributeV3 attribute = new RequestAttributeV3();
        attribute.setName("apiToken");
        attribute.setContent(List.of(new StringAttributeContentV3(secret)));

        for (ContentSigningFormattingRequestDto dto : List
                .of(new CadesComputeDtbsRequestDto(), new EmbedSignatureValueRequestDto(),
                        new SignedDocumentRequestDto(), new EmbedTimestampRequestDto(), new ExtendToLevelRequestDto(),
                        new ExtendOperationScopedRequestDto())) {
            dto.setFormattingAttributes(List.of(attribute));

            String rendered = dto.toString();

            assertFalse(rendered.contains("formattingAttributes="),
                    dto.getClass().getSimpleName() + " rendered formattingAttributes: " + rendered);
            assertFalse(rendered.contains(secret),
                    dto.getClass().getSimpleName() + " leaked an attribute value: " + rendered);
        }
    }

    /**
     * {@code family} is the entitlement guard a connector reads to decide whether to serve the request at all, so an
     * absent one must be rejected rather than reaching a connector as null.
     */
    @Test
    void familyIsRequiredOnEveryRequestBody() {
        assertTrue(paths(VALIDATOR.validate(new EmbedSignatureValueRequestDto())).contains("family"));
        assertTrue(paths(VALIDATOR.validate(new SignedDocumentRequestDto())).contains("family"));
        assertTrue(paths(VALIDATOR.validate(new ExtendToLevelRequestDto())).contains("family"));
    }

    /** A family subtype fixes its own discriminator, so the caller never supplies it and it is never missing. */
    @Test
    void computeDtbsSubtypesSupplyTheirOwnFamily() {
        assertEquals(SignatureFamily.CADES, new CadesComputeDtbsRequestDto().getFamily());
        assertFalse(paths(VALIDATOR.validate(new CadesComputeDtbsRequestDto())).contains("family"));
    }

    /**
     * An empty manifest is a distinct claim from an absent one — "the connector looked and found nothing to fetch"
     * rather than "the connector did not look" — so the inclusion rule must not collapse the two.
     */
    @Test
    void aSynchronousResultKeepsItsEmptyFetchManifest() throws Exception {
        ExtendToLevelResponseDto dto = new ExtendToLevelResponseDto();
        dto.setExtendedDocument(new byte[]{1, 2, 3});
        dto.setFetchManifest(List.of());

        assertTrue(mapper.readTree(mapper.writeValueAsString(dto)).has("fetchManifest"));
    }

    /** A 200 carries no tracking handle, so the asynchronous-only property still stays off the wire. */
    @Test
    void aSynchronousResultOmitsTheTrackingHandle() throws Exception {
        ExtendToLevelResponseDto dto = new ExtendToLevelResponseDto();
        dto.setExtendedDocument(new byte[]{1, 2, 3});
        dto.setFetchManifest(List.of());

        assertFalse(mapper.readTree(mapper.writeValueAsString(dto)).has("extendOperationMeta"));
    }

    /** A completed poll makes the same claim as a synchronous result, so it keeps an empty manifest too. */
    @Test
    void aCompletedPollKeepsItsEmptyFetchManifest() throws Exception {
        ExtendOperationStatusResponseDto dto = new ExtendOperationStatusResponseDto();
        dto.setStatus(OperationStatus.COMPLETED);
        dto.setExtendedDocument(new byte[]{1, 2, 3});
        dto.setFetchManifest(List.of());

        assertTrue(mapper.readTree(mapper.writeValueAsString(dto)).has("fetchManifest"));
    }

    /** A terminal success with nothing in it leaves a poller with no result to act on. */
    @Test
    void aCompletedPollMustCarryItsResult() {
        ExtendOperationStatusResponseDto dto = new ExtendOperationStatusResponseDto();
        dto.setStatus(OperationStatus.COMPLETED);

        assertTrue(paths(VALIDATOR.validate(dto)).contains("resultConsistentWithStatus"));
    }

    /** A failed run has to say why, or the poller learns only that something went wrong. */
    @Test
    void aFailedPollMustCarryItsReason() {
        ExtendOperationStatusResponseDto dto = new ExtendOperationStatusResponseDto();
        dto.setStatus(OperationStatus.FAILED);

        assertTrue(paths(VALIDATOR.validate(dto)).contains("resultConsistentWithStatus"));
    }

    /** A result attached to a run still in flight contradicts the status it is attached to. */
    @Test
    void anInProgressPollCarriesNoResult() {
        ExtendOperationStatusResponseDto dto = new ExtendOperationStatusResponseDto();
        dto.setStatus(OperationStatus.IN_PROGRESS);
        dto.setExtendedDocument(new byte[]{1, 2, 3});

        assertTrue(paths(VALIDATOR.validate(dto)).contains("resultConsistentWithStatus"));
    }

    @Test
    void aConsistentTerminalPollIsAccepted() {
        ExtendOperationStatusResponseDto completed = new ExtendOperationStatusResponseDto();
        completed.setStatus(OperationStatus.COMPLETED);
        completed.setExtendedDocument(new byte[]{1, 2, 3});
        completed.setFetchManifest(List.of());

        ExtendOperationStatusResponseDto cancelled = new ExtendOperationStatusResponseDto();
        cancelled.setStatus(OperationStatus.CANCELLED);
        cancelled.setReason("cancelled by the operator");

        assertFalse(paths(VALIDATOR.validate(completed)).contains("resultConsistentWithStatus"));
        assertFalse(paths(VALIDATOR.validate(cancelled)).contains("resultConsistentWithStatus"));
    }

    /** The imprint and embed pair reaches TIMESTAMPED and ARCHIVAL; this operation serves LONG_TERM alone. */
    @Test
    void extendToLevelRefusesALevelItDoesNotServe() {
        ExtendToLevelRequestDto dto = new ExtendToLevelRequestDto();
        dto.setTargetLevel(SignatureLevel.ARCHIVAL);

        assertTrue(paths(VALIDATOR.validate(dto)).contains("targetLevelServedByThisOperation"));
    }

    @Test
    void extendToLevelAcceptsLongTerm() {
        ExtendToLevelRequestDto dto = new ExtendToLevelRequestDto();
        dto.setTargetLevel(SignatureLevel.LONG_TERM);

        assertFalse(paths(VALIDATOR.validate(dto)).contains("targetLevelServedByThisOperation"));
    }

    /** The manifest entry identifies exact bytes, so a digest of the wrong width identifies nothing. */
    @Test
    void aFetchedArtifactDigestMustBeThirtyTwoBytes() {
        FetchedArtifactDto artifact = new FetchedArtifactDto();
        artifact.setSha256(new byte[]{1});

        assertTrue(paths(VALIDATOR.validate(artifact)).contains("digestSha256Sized"));
    }

    private static CadesComputeDtbsRequestDto validComputeDtbs() {
        CadesComputeDtbsRequestDto dto = new CadesComputeDtbsRequestDto();
        dto.setFormattingAttributes(List.of());
        dto.setSignerCertificateChain(List.of(new byte[]{4, 5, 6}));
        dto.setSigningTime(OffsetDateTime.parse("2026-08-11T09:30:00Z"));
        dto.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA);
        dto.setDocument(new InlineDocumentTransferDto(new byte[]{1, 2, 3}));
        return dto;
    }

    private static <T> Set<String> paths(Set<ConstraintViolation<T>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    }
}
