package com.otilm.api.model.common.signature.parameters;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignaturePlacementDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureStyleDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureParameterPathsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void byGroupListsAFieldThatCarriesNoValue() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();

        Map<SignatureParameterGroup, List<String>> byGroup = SignatureParameterPaths.byGroup(parameters);

        assertTrue(byGroup.getOrDefault(SignatureParameterGroup.SIGNATURE_CONTEXT, List.of()).contains("reason"),
                byGroup.toString());
    }

    @Test
    void byGroupSkipsTheFieldsOfAnAbsentContainer() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();

        Map<SignatureParameterGroup, List<String>> byGroup = SignatureParameterPaths.byGroup(parameters);

        assertFalse(byGroup.containsKey(SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT), byGroup.toString());
    }

    @Test
    void presentByGroupListsNothingWhenOnlyTheDiscriminatorIsSet() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();

        assertEquals(Map.of(), SignatureParameterPaths.presentByGroup(parameters));
    }

    @Test
    void presentByGroupListsNothingForAnAbsentTree() {
        assertEquals(Map.of(), SignatureParameterPaths.presentByGroup(null));
    }

    @Test
    void presentByGroupListsNothingForAContainerThatCarriesNothing() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setVisibleSignature(new PadesVisibleSignatureDto());

        assertEquals(Map.of(), SignatureParameterPaths.presentByGroup(parameters));
    }

    @Test
    void presentByGroupCountsAnEmptyListAsAbsent() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setClaimedRoles(new ArrayList<>());

        assertEquals(Map.of(), SignatureParameterPaths.presentByGroup(parameters));
    }

    @Test
    void presentByGroupListsASetFieldUnderItsOwnGroupOnly() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setReason("Contract approval");

        assertEquals(Map.of(SignatureParameterGroup.SIGNATURE_CONTEXT, List.of("reason")),
                SignatureParameterPaths.presentByGroup(parameters));
    }

    @Test
    void presentByGroupListsANonEmptyList() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setClaimedRoles(List.of("approver"));

        assertEquals(Map.of(SignatureParameterGroup.SIGNED_ATTRIBUTES, List.of("claimedRoles")),
                SignatureParameterPaths.presentByGroup(parameters));
    }

    @Test
    void namesEachNestedFieldByItsDottedPathUnderItsGroup() {
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setPage(2);
        PadesVisibleSignatureStyleDto style = new PadesVisibleSignatureStyleDto();
        style.setDpi(300);
        PadesVisibleSignatureDto visibleSignature = new PadesVisibleSignatureDto();
        visibleSignature.setVisible(true);
        visibleSignature.setPlacement(placement);
        visibleSignature.setStyle(style);
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setVisibleSignature(visibleSignature);

        Map<SignatureParameterGroup, List<String>> eachFieldUnderItsGroup = Map
                .of(SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT, List.of("visibleSignature.visible"),
                        SignatureParameterGroup.VISIBLE_SIGNATURE_PLACEMENT, List.of("visibleSignature.placement.page"),
                        SignatureParameterGroup.VISIBLE_SIGNATURE_STYLE, List.of("visibleSignature.style.dpi"));
        assertEquals(eachFieldUnderItsGroup, SignatureParameterPaths.presentByGroup(parameters));
    }

    /** {@code describe()} reports the signature scope before the signed attributes, which the enum puts first. */
    @Test
    void listsGroupsInEnumOrder() {
        PadesSignatureParametersDto parameters = ParameterTreeFixture.populated(PadesSignatureParametersDto.class);

        List<SignatureParameterGroup> groups = List.copyOf(SignatureParameterPaths.byGroup(parameters).keySet());

        assertEquals(groups.stream().sorted().toList(), groups);
    }

    @Test
    void listsFieldsInDescribeOrder() {
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();

        assertEquals(List.of("reason", "location", "contactInfo"),
                SignatureParameterPaths.byGroup(parameters).get(SignatureParameterGroup.SIGNATURE_CONTEXT));
    }

    @Test
    void everyPathNamesAPropertyInTheParametersJson() {
        for (Class<? extends SignatureParametersDto> subtype : ParameterTreeFixture.registeredSubtypes()) {
            SignatureParametersDto populated = ParameterTreeFixture.populated(subtype);
            JsonNode json = MAPPER.valueToTree(populated);
            List<String> paths = SignatureParameterPaths
                    .byGroup(populated)
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .toList();

            assertFalse(paths.isEmpty(), "no path for " + subtype.getSimpleName());
            for (String path : paths) {
                assertFalse(json.at(JsonPointer.compile("/" + path.replace('.', '/'))).isMissingNode(),
                        path + " names no property in the JSON of " + subtype.getSimpleName());
            }
        }
    }
}
