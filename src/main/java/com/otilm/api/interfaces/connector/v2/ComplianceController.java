package com.otilm.api.interfaces.connector.v2;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.connector.compliance.v2.ComplianceRequestDto;
import com.otilm.api.model.connector.compliance.v2.ComplianceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@RequestMapping("/v2/complianceProvider/{kind}")
@Tag(
        name = "Compliance",
        description = "Compliance Provider API. " +
                "Used to check the compliance of resource objects. The provider contains the " +
                "list of rules for checking the compliance. The user can choose the list of compliance checks " +
                "that has to be performed. To check for the compliance of a resource object, the Connector accepts " +
                "its content and the list of rule references. Once the values are received, compliance is " +
                "checked and status will be returned"
)
public interface ComplianceController extends AuthProtectedConnectorController {
    @PostMapping(
            path = "/compliance",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Check resource object compliance"
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
                                          @RequestBody @Valid ComplianceRequestDto request) throws IOException, NotFoundException;
}
