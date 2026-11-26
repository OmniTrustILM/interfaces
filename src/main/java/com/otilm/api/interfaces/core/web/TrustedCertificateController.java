package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/trustedCertificates")
@Tag(name = "Trusted Certificate Management", description = "Trusted Certificate Management API")
@ApiResponses(value = @ApiResponse(responseCode = "404", description = "Not Found",
        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))))
public interface TrustedCertificateController extends AuthProtectedController {

    String DEPRECATION = "Deprecated, removed in the release after 2.21.0: use /v2/trustedCertificates. Lists and "
            + "changes platform trust only.";

    /**
     * @deprecated As of release 2.21.0, removed in the release after it. Use
     * {@link com.otilm.api.interfaces.core.web.v2.TrustedCertificateController}.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    @Operation(deprecated = true, summary = "List Trusted Certificates", description = DEPRECATION)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Trusted Certificates")})
    @GetMapping(produces = {"application/json"})
    List<TrustedCertificateDto> listTrustedCertificates();

    /**
     * @deprecated As of release 2.21.0, removed in the release after it. Use
     * {@link com.otilm.api.interfaces.core.web.v2.TrustedCertificateController}.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    @Operation(deprecated = true, summary = "Get details of a Trusted Certificate", description = DEPRECATION)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trusted Certificate details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    TrustedCertificateDto getTrustedCertificate(
            @Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid) throws NotFoundException;

    /**
     * @deprecated As of release 2.21.0, removed in the release after it. Use
     * {@link com.otilm.api.interfaces.core.web.v2.TrustedCertificateController}.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    @Operation(deprecated = true, summary = "Create a new Trusted Certificate", description = DEPRECATION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New Trusted Certificate created",
                    content = @Content(schema = @Schema(implementation = UuidDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createTrustedCertificate(@RequestBody TrustedCertificateRequestDto request)
            throws AlreadyExistException;

    /**
     * @deprecated As of release 2.21.0, removed in the release after it. Use
     * {@link com.otilm.api.interfaces.core.web.v2.TrustedCertificateController}.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    @Operation(deprecated = true, summary = "Delete a Trusted Certificate", description = DEPRECATION)
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Trusted Certificate deleted")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteTrustedCertificate(@Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid)
            throws NotFoundException;

}
