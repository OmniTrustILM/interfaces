package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.inspection.InspectionRequestDto;
import com.otilm.api.model.client.inspection.InspectionResponseDto;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Reads an uploaded file and reports what it holds.
 *
 * <p>
 * This is one operation rather than one per resource, because what a file holds is the answer rather than the question:
 * a container can carry certificates, a private key, a secret key or a signing request in any combination, and a caller
 * uploading it does not yet know which. An operation under one resource would ask a caller to classify the file before
 * asking what it is.
 * </p>
 */
@RequestMapping("/v1/inspections")
@Tag(name = "Inspection", description = "Reading uploaded files to report what they hold")
public interface InspectionController extends AuthProtectedController {

    @Operation(summary = "Read an uploaded file",
            description = """
                    Report what an uploaded file holds, without importing or storing anything.

                    The file travels base64-encoded in `file`, like every upload in the platform, and is held in memory rather
                    than written anywhere; it is never echoed in an error, since it may carry key material. Every container the
                    platform understands is accepted, so a caller uploads once and then decides: PKCS#12 in any composition, a PEM bundle of certificates with at most one private
                    key, a standalone PKCS#8 key, plain or encrypted, an OpenSSL traditional PEM key, an OpenSSH private key, a
                    single certificate as PEM or DER, and PKCS#7. The container is recognised by its content, not by its file
                    name. JKS and JCEKS stores are read too.

                    Nothing here has to be carried into a later operation. Each entry is reported with a reference derived from
                    the entry's own content, so an operation given that reference acts on that content whichever file it
                    arrives with. Read a file when a user should see what is inside before anything is imported; a caller that
                    already knows what it is sending does not need to.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File read"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "Unprocessable Entity, including a file no supported container format explains, a "
                            + "file the supplied passphrase does not open, and a file beyond the platform's limits",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    InspectionResponseDto inspect(@RequestBody @Valid InspectionRequestDto request)
            throws ValidationException, NotFoundException;
}
