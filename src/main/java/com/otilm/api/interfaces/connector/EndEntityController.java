package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.core.authority.AddEndEntityRequestDto;
import com.otilm.api.model.core.authority.EditEndEntityRequestDto;
import com.otilm.api.model.core.authority.EndEntityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/authorityProvider/authorities/{uuid}/endEntityProfiles/{endEntityProfileName}/endEntities")
@Tag(name = "End Entity Management", description = "End Entity Management API")
public interface EndEntityController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List End Entities"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entities retrieved"
                    )
            })
    @GetMapping(produces = {"application/json"})
    List<EndEntityDto> listEndEntities(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName) throws NotFoundException;

    @Operation(
            summary = "Get End Entity"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entity retrieved"
                    )
            })
    @GetMapping(path = "/{endEntityName}", produces = {"application/json"})
    EndEntityDto getEndEntity(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @Parameter(description = "End Entity Name") @PathVariable String endEntityName) throws NotFoundException;

    @Operation(
            summary = "Create End Entity"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entity created"
                    )
            })
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    void createEndEntity(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @RequestBody AddEndEntityRequestDto request) throws NotFoundException, AlreadyExistException;

    @Operation(
            summary = "Update End Entity"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entity updated"
                    )
            })
    @PostMapping(path = "/{endEntityName}", consumes = {"application/json"}, produces = {"application/json"})
    void updateEndEntity(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @Parameter(description = "End Entity Name") @PathVariable String endEntityName,
            @RequestBody EditEndEntityRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Remove End Entity"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entity removed"
                    )
            })
    @DeleteMapping(path = "/{endEntityName}", produces = {"application/json"})
    void removeEndEntity(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @Parameter(description = "End Entity Name") @PathVariable String endEntityName) throws NotFoundException;


    @Operation(
            summary = "Reset End Entity password"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "End Entity password reset"
                    )
            })
    @PutMapping(path = "/{endEntityName}/resetPassword", produces = {"application/json"})
    void resetPassword(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @Parameter(description = "End Entity Name") @PathVariable String endEntityName) throws NotFoundException;

}