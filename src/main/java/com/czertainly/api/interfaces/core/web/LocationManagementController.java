package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.*;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.LocationsResponseDto;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.client.location.AddLocationRequestDto;
import com.czertainly.api.model.client.location.EditLocationRequestDto;
import com.czertainly.api.model.client.location.IssueToLocationRequestDto;
import com.czertainly.api.model.client.location.PushToLocationRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.UuidDto;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.core.location.LocationDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Location Management", description = "Location Management API")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "502",
                        description = "Connector Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "503",
                        description = "Connector Communication Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
        })

public interface LocationManagementController extends AuthProtectedController {

    @Operation(
            summary = "List Locations"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Locations retrieved"
                    )
            })
    @PostMapping(
            path = "/locations",
            produces = {"application/json"}
    )
    LocationsResponseDto listLocations(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getLocationSearchableFields", summary = "Get Locations searchable fields information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Locations searchable field information retrieved")})
    @GetMapping(path = "/locations/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(
            summary = "Add Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Location added",
                            content = @Content(schema = @Schema(implementation = UuidDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    ),
                    @ApiResponse(responseCode = "404", description = "Entity not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PostMapping(
            path = "/entities/{entityUuid}/locations",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    ResponseEntity<?> addLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @RequestBody AddLocationRequestDto request
    ) throws ConnectorException, AlreadyExistException, LocationException, AttributeException, NotFoundException;

    @Operation(
            summary = "Get Location Details"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Location detail retrieved"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}",
            produces = {"application/json"}
    )
    LocationDto getLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Edit Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Location updated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessible Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PutMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    LocationDto editLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid,
            @RequestBody EditLocationRequestDto request
    ) throws ConnectorException, LocationException, AttributeException, NotFoundException;

    @Operation(
            summary = "Delete Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Location deleted"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @DeleteMapping(path = "/entities/{entityUuid}/locations/{locationUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Disable Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Location disabled"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PatchMapping(path = "/entities/{entityUuid}/locations/{locationUuid}/disable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Enable Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Location enabled"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PatchMapping(path = "/entities/{entityUuid}/locations/{locationUuid}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Get push Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Push attributes list obtained"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/attributes/push",
            produces = {"application/json"}
    )
    List<BaseAttribute> listPushAttributes(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Get CSR Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "CSR Attributes list obtained"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/attributes/issue",
            produces = {"application/json"}
    )
    List<BaseAttribute> listCsrAttributes(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Push Certificate to Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate pushed"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PutMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/certificates/{certificateUuid}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    LocationDto pushCertificate(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody PushToLocationRequestDto request
    ) throws NotFoundException, LocationException, AttributeException;

    @Operation(
            summary = "Remove Certificate from Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate removed"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @DeleteMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/certificates/{certificateUuid}",
            produces = {"application/json"}
    )
    LocationDto removeCertificate(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Issue Certificate to Location",
            operationId = "issueCertificateToLocation"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate issued"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PostMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/certificates",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    LocationDto issueCertificate(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid,
            @RequestBody IssueToLocationRequestDto request
    ) throws ConnectorException, LocationException, NotFoundException;

    @Operation(
            summary = "Sync Location content"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Content updated"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PutMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/sync",
            produces = {"application/json"}
    )
    LocationDto updateLocationContent(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Renew Certificate in Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Content updated"
                    ),
                    @ApiResponse(responseCode = "404", description = "Location not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PatchMapping(
            path = "/entities/{entityUuid}/locations/{locationUuid}/certificates/{certificateUuid}",
            produces = {"application/json"}
    )
    LocationDto renewCertificateInLocation(
            @Parameter(description = "Entity UUID") @PathVariable String entityUuid,
            @Parameter(description = "Location UUID") @PathVariable String locationUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid
    ) throws ConnectorException, LocationException, NotFoundException;
}
