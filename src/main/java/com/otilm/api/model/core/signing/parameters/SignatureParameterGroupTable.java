package com.otilm.api.model.core.signing.parameters;

import com.otilm.api.model.common.signature.parameters.SignatureParameterPaths;
import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignaturePlacementDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureStyleDto;
import java.util.List;

/**
 * Lists which group governs each signature parameter, as {@code describe()} declares it.
 */
public final class SignatureParameterGroupTable {

    private SignatureParameterGroupTable() {
    }

    /** One entry per signature family that has parameters. */
    public static List<SignatureFamilyParameterGroupsDto> forEveryFamily() {
        return List.of(groupsOf(padesWithEveryContainer()));
    }

    /** The walk reaches a container's fields only through the container. */
    private static PadesSignatureParametersDto padesWithEveryContainer() {
        PadesVisibleSignatureDto visibleSignature = new PadesVisibleSignatureDto();
        visibleSignature.setPlacement(new PadesVisibleSignaturePlacementDto());
        visibleSignature.setStyle(new PadesVisibleSignatureStyleDto());
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setVisibleSignature(visibleSignature);
        return parameters;
    }

    private static SignatureFamilyParameterGroupsDto groupsOf(SignatureParametersDto parameters) {
        List<SignatureParameterGroupFieldsDto> groups = SignatureParameterPaths
                .byGroup(parameters)
                .entrySet()
                .stream()
                .map(entry -> new SignatureParameterGroupFieldsDto(entry.getKey(), entry.getValue()))
                .toList();
        return new SignatureFamilyParameterGroupsDto(parameters.getFamily(), groups);
    }
}
