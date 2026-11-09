package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.core.signing.parameters.SignatureFamilyParameterGroupsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/signatureParameters")
@Tag(name = "Signature Parameters", description = "Signature Parameters API")
public interface SignatureParameterController extends AuthProtectedController {

    @Operation(operationId = "listSignatureParameterGroups", summary = "List signature parameter groups",
            description = "Returns, for each signature family that has signature parameters, its signature parameter "
                    + "groups and the request fields each group governs. A signing request may set a field only when "
                    + "its Signing Profile allows the field's group. Any authenticated user may call this operation.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signature parameter groups retrieved")})
    @GetMapping(path = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SignatureFamilyParameterGroupsDto> listSignatureParameterGroups();
}
