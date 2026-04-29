package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.common.NameAndIdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/v1/authorityProvider/authorities/{uuid}/endEntityProfiles")
@Tag(name = "End Entity Profiles", description = "End Entity Profiles API")
public interface EndEntityProfilesController extends AuthProtectedConnectorController {

    @GetMapping(produces = {"application/json"})
    @Operation(
            summary = "List available end entity profiles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "End entities retrieved")
    })
    List<NameAndIdDto> listEntityProfiles(
            @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Authority Instance UUID")
            @PathVariable String uuid
    )
            throws NotFoundException;

    @GetMapping(path = "/{endEntityProfileId}/certificateprofiles", produces = {"application/json"})
    @Operation(
            summary = "List available certificate profiles for given end entity profile"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate profiles retrieved")
    })
    List<NameAndIdDto> listCertificateProfiles(
            @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Authority Instance UUID")
            @PathVariable String uuid,
            @Parameter(in = ParameterIn.PATH, name = "endEntityProfileId", description = "End Entity Profile Id")
            @PathVariable Integer endEntityProfileId
    )
            throws NotFoundException;

    @GetMapping(path = "/{endEntityProfileId}/cas", produces = {"application/json"})
    @Operation(
            summary = "List available certification authorities for given end entity profile"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CAs retrieved")
    })
    List<NameAndIdDto> listCAsInProfile(
            @Parameter(in = ParameterIn.PATH, name = "uuid", description = "Authority Instance UUID")
            @PathVariable String uuid,
            @Parameter(in = ParameterIn.PATH, name = "endEntityProfileId", description = "End Entity Profile Id")
            @PathVariable Integer endEntityProfileId
    )
            throws NotFoundException;

}