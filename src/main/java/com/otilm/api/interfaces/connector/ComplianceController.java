package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.connector.compliance.ComplianceRequestDto;
import com.otilm.api.model.connector.compliance.ComplianceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/v1/complianceProvider/{kind}")
@Tag(
        name = "Compliance",
        description = "Compliance Provider API. " +
                "Used to check the compliance of a certificate. The provider contains the " +
                "list of rules for checking the compliance. The user can choose the list of compliance checks " +
                "has to be performed. To check for the compliance of a certificate, the Connector accepts " +
                "certificate content and the list of rule references. Once the values are received, compliance is " +
                "checked and status will be returned"
)
public interface ComplianceController extends AuthProtectedConnectorController {
    @PostMapping(
            path = "/compliance",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @Operation(
            summary = "Check certificate compliance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Compliance check completed"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})
                    )
            }
    )
    ComplianceResponseDto checkCompliance(@Parameter(description = "Connector Kind") @PathVariable String kind,
                                          @RequestBody ComplianceRequestDto request) throws IOException, NotFoundException;
}