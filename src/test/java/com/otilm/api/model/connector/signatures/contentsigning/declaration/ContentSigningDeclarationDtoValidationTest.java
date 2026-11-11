package com.otilm.api.model.connector.signatures.contentsigning.declaration;

import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentSigningDeclarationDtoValidationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void aFamilyUpToArchivalIsValid() {
        assertEquals(List.of(), violations(declarationOf(pades(SignatureLevel.ARCHIVAL))));
    }

    @Test
    void aFamilyOnlyTheConnectorKnowsIsValid() {
        assertEquals(List.of(), violations(declarationOf(mrtd())));
    }

    @Test
    void aDeclarationWithoutFamiliesIsInvalid() {
        ContentSigningDeclarationDto withoutFamilies = declarationOf();
        withoutFamilies.setFamilies(null);

        assertEquals(List.of("families: must not be null"), violations(withoutFamilies));
    }

    @Test
    void twoFamiliesWithOneCodeAreInvalid() {
        ContentSigningDeclarationDto twoPades = declarationOf(pades(SignatureLevel.SIGNED),
                pades(SignatureLevel.SIGNED));

        assertEquals(List.of("families[1].code: must not repeat a family code; duplicates the family at index 0"),
                violations(twoPades));
    }

    @Test
    void aFamilyWithoutACodeIsInvalid() {
        SignatureFamilyDeclarationDto codeless = pades(SignatureLevel.SIGNED);
        codeless.setCode(null);

        assertEquals(List.of("families[0].code: must not be null"), violations(declarationOf(codeless)));
    }

    @Test
    void anEmptyFamilyEntryIsInvalid() {
        ContentSigningDeclarationDto withEmptyEntry = declarationOf(pades(SignatureLevel.SIGNED));
        withEmptyEntry.getFamilies().add(null);

        assertEquals(List.of("families[1].<list element>: must not be null"), violations(withEmptyEntry));
    }

    @Test
    void anEmptyLevelEntryIsInvalid() {
        SignatureFamilyDeclarationDto withEmptyLevel = pades(SignatureLevel.SIGNED);
        withEmptyLevel.getLevels().add(null);

        assertEquals(List.of("families[0].levels[1].<list element>: must not be null"),
                violations(declarationOf(withEmptyLevel)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void aFamilyWithoutALabelIsInvalid(String missingLabel) {
        SignatureFamilyDeclarationDto unlabelled = mrtd();
        unlabelled.setLabel(missingLabel);

        assertEquals(List.of("families[0].label: must not be blank"), violations(declarationOf(unlabelled)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void aLevelWithoutANameIsInvalid(String missingName) {
        SignatureFamilyDeclarationDto unnamedLevel = mrtd();
        unnamedLevel.getLevels().getFirst().setName(missingName);

        assertEquals(List.of("families[0].levels[0].name: must not be blank"), violations(declarationOf(unnamedLevel)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void aFamilyWithoutATransferModeIsInvalid(Set<DocumentTransferMode> noTransferModes) {
        SignatureFamilyDeclarationDto untransferable = pades(SignatureLevel.SIGNED);
        untransferable.setTransferModes(noTransferModes);

        assertEquals(List.of("families[0].transferModes: must not be empty"),
                violations(declarationOf(untransferable)));
    }

    @Test
    void anEmptyTransferModeEntryIsInvalid() {
        SignatureFamilyDeclarationDto family = pades(SignatureLevel.SIGNED);
        family.setTransferModes(new HashSet<>(Arrays.asList(DocumentTransferMode.INLINE, null)));

        assertEquals(List.of("families[0].transferModes[].<iterable element>: must not be null"),
                violations(declarationOf(family)));
    }

    @Test
    void anEmptySignatureAlgorithmEntryIsInvalid() {
        SignatureFamilyDeclarationDto family = pades(SignatureLevel.SIGNED);
        family.setSignatureAlgorithms(new HashSet<>(Arrays.asList(SignatureAlgorithm.SHA256_WITH_RSA, null)));

        assertEquals(List.of("families[0].signatureAlgorithms[].<iterable element>: must not be null"),
                violations(declarationOf(family)));
    }

    @Test
    void anEmptySetOfSignatureAlgorithmsIsInvalid() {
        SignatureFamilyDeclarationDto unsignable = pades(SignatureLevel.SIGNED);
        unsignable.setSignatureAlgorithms(Set.of());

        assertEquals(List.of("families[0].signatureAlgorithms: size must be between 1 and 2147483647"),
                violations(declarationOf(unsignable)));
    }

    @Test
    void levelsSkippingARungAreInvalid() {
        SignatureFamilyDeclarationDto withoutTimestamped = pades(SignatureLevel.LONG_TERM);
        withoutTimestamped.getLevels().removeIf(level -> level.getLevel() == SignatureLevel.TIMESTAMPED);

        assertEquals(List
                .of("families[0].levels: must run up from SIGNED without a gap, but LONG_TERM comes where "
                        + "TIMESTAMPED belongs"),
                violations(declarationOf(withoutTimestamped)));
    }

    @Test
    void levelsStartingAboveSignedAreInvalid() {
        SignatureFamilyDeclarationDto fromTimestamped = pades(SignatureLevel.TIMESTAMPED);
        fromTimestamped.getLevels().removeFirst();

        assertEquals(List
                .of("families[0].levels: must run up from SIGNED without a gap, but TIMESTAMPED comes where "
                        + "SIGNED belongs"),
                violations(declarationOf(fromTimestamped)));
    }

    @Test
    void moreLevelsThanTheLadderHasAreInvalid() {
        SignatureFamilyDeclarationDto overlong = pades(SignatureLevel.ARCHIVAL);
        overlong.getLevels().add(new SignatureLevelDeclarationDto(SignatureLevel.ARCHIVAL, "PAdES-B-LTA"));

        assertEquals(List.of("families[0].levels: must run up from SIGNED without a gap, but it declares 5 levels"),
                violations(declarationOf(overlong)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void aFamilyWithoutLevelsIsInvalid(List<SignatureLevelDeclarationDto> noLevels) {
        SignatureFamilyDeclarationDto levelless = pades(SignatureLevel.SIGNED);
        levelless.setLevels(noLevels);

        assertEquals(List.of("families[0].levels: must not be empty"), violations(declarationOf(levelless)));
    }

    private static List<String> violations(ContentSigningDeclarationDto declaration) {
        return VALIDATOR
                .validate(declaration)
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .sorted()
                .toList();
    }

    private static ContentSigningDeclarationDto declarationOf(SignatureFamilyDeclarationDto... families) {
        return new ContentSigningDeclarationDto(new ArrayList<>(List.of(families)));
    }

    private static SignatureFamilyDeclarationDto pades(SignatureLevel highest) {
        SignatureFamilyDeclarationDto family = familyUpTo(highest);
        family.setCode(SignatureFamily.PADES);
        family.setLabel("PAdES");
        family.getLevels().forEach(level -> level.setName("PAdES " + level.getLevel().name()));
        return family;
    }

    private static SignatureFamilyDeclarationDto mrtd() {
        SignatureFamilyDeclarationDto family = familyUpTo(SignatureLevel.SIGNED);
        family.setCode(new SignatureFamily("mrtd"));
        family.setLabel("Machine-readable travel document");
        family.getLevels().getFirst().setName("ICAO 9303");
        return family;
    }

    private static SignatureFamilyDeclarationDto familyUpTo(SignatureLevel highest) {
        List<SignatureLevelDeclarationDto> levels = Arrays
                .stream(SignatureLevel.values())
                .filter(level -> level.compareTo(highest) <= 0)
                .map(level -> new SignatureLevelDeclarationDto(level, null))
                .collect(Collectors.toCollection(ArrayList::new));
        SignatureFamilyDeclarationDto family = new SignatureFamilyDeclarationDto();
        family.setLevels(levels);
        family.setTransferModes(new HashSet<>(Set.of(DocumentTransferMode.INLINE)));
        return family;
    }
}
