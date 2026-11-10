package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.core.signing.parameters.SignatureFamilyParameterGroupsDto;
import com.otilm.api.model.core.signing.parameters.SignatureParameterGroupTable;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pins the published group table to what {@code describe()} reports over a fully populated tree. */
class SignatureParameterGroupTableTest {

    @Test
    void theTableListsEveryFieldOfAFullyPopulatedTreeInOrder() {
        for (Class<? extends SignatureParametersDto> subtype : ParameterTreeFixture.registeredSubtypes()) {
            SignatureParametersDto populated = ParameterTreeFixture.populated(subtype);

            assertEquals(List.copyOf(SignatureParameterPaths.byGroup(populated).entrySet()),
                    listedFieldsByGroup(populated.getFamily()), subtype.getSimpleName());
        }
    }

    @Test
    void theTableHasOneEntryPerFamilyWithParameters() {
        List<SignatureFamily> familiesWithParameters = ParameterTreeFixture
                .registeredSubtypes()
                .stream()
                .map(subtype -> ParameterTreeFixture.populated(subtype).getFamily())
                .toList();

        assertEquals(familiesWithParameters,
                SignatureParameterGroupTable
                        .forEveryFamily()
                        .stream()
                        .map(SignatureFamilyParameterGroupsDto::getFamily)
                        .toList());
    }

    private static List<Map.Entry<SignatureParameterGroup, List<String>>> listedFieldsByGroup(SignatureFamily family) {
        SignatureFamilyParameterGroupsDto familyEntry = SignatureParameterGroupTable
                .forEveryFamily()
                .stream()
                .filter(entry -> entry.getFamily() == family)
                .findFirst()
                .orElseThrow(() -> new AssertionError("the table has no entry for " + family));
        return familyEntry
                .getGroups()
                .stream()
                .map(groupFields -> Map.entry(groupFields.getGroup(), groupFields.getFields()))
                .toList();
    }
}
