package com.otilm.api.model.connector.signatures.contentsigning.declaration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** A declaration as a connector writes it and Core reads it back. */
class ContentSigningDeclarationDtoJsonTest {

    private static final List<SignatureLevelDeclarationDto> PADES_LEVELS = List
            .of(new SignatureLevelDeclarationDto(SignatureLevel.SIGNED, "PAdES-B-B"),
                    new SignatureLevelDeclarationDto(SignatureLevel.TIMESTAMPED, "PAdES-B-T"));
    private static final Set<SignatureAlgorithm> PADES_ALGORITHMS = Set
            .of(SignatureAlgorithm.SHA256_WITH_RSA, SignatureAlgorithm.SHA256_WITH_ECDSA);

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void writesTheFamilyCodeAsAPlainString() throws Exception {
        // given
        ContentSigningDeclarationDto declaration = new ContentSigningDeclarationDto(List.of(padesUpToTimestamped()));

        // when
        JsonNode code = mapper.readTree(mapper.writeValueAsString(declaration)).at("/families/0/code");

        // then
        assertTrue(code.isTextual(), () -> "code must be a string on the wire, was " + code);
        assertEquals("pades", code.asText());
    }

    @Test
    void readsBackEveryFieldItWrote() throws Exception {
        // given
        ContentSigningDeclarationDto written = new ContentSigningDeclarationDto(List.of(padesUpToTimestamped()));

        // when
        ContentSigningDeclarationDto read = mapper
                .readValue(mapper.writeValueAsString(written), ContentSigningDeclarationDto.class);

        // then
        SignatureFamilyDeclarationDto family = read.getFamilies().getFirst();
        assertEquals(1, read.getFamilies().size());
        assertEquals(SignatureFamily.PADES, family.getCode());
        assertEquals("PAdES", family.getLabel());
        assertEquals("PDF Advanced Electronic Signatures, ETSI EN 319 142", family.getDescription());
        assertEquals(List.of("SIGNED PAdES-B-B", "TIMESTAMPED PAdES-B-T"), levelsOf(family));
        assertEquals(Set.of(DocumentTransferMode.INLINE), family.getTransferModes());
        assertEquals(PADES_ALGORITHMS, family.getSignatureAlgorithms());
    }

    private static SignatureFamilyDeclarationDto padesUpToTimestamped() {
        return SignatureFamilyDeclarationDto
                .builder()
                .code(SignatureFamily.PADES)
                .label("PAdES")
                .description("PDF Advanced Electronic Signatures, ETSI EN 319 142")
                .levels(PADES_LEVELS)
                .transferModes(Set.of(DocumentTransferMode.INLINE))
                .signatureAlgorithms(PADES_ALGORITHMS)
                .build();
    }

    private static List<String> levelsOf(SignatureFamilyDeclarationDto family) {
        return family.getLevels().stream().map(level -> level.getLevel() + " " + level.getName()).toList();
    }
}
