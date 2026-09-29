package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One entry a caller chose to import, and where its key material goes.
 *
 * <p>
 * Every entry carrying key material states its own destination, because a file can hold entries of different key types
 * and each key type has its own provider attribute schema: one set of attributes cannot serve both.
 * </p>
 */
@Getter
@Setter
@ToString
@Schema(name = "CertificateImportEntryDto", description = "An entry to import and the destination of its key material")
public class CertificateImportEntryDto {

    @Schema(description = """
            Reference of the entry to import. Read the file first to learn it, or compute it from content already
            held.

            The lowercase hex SHA-256 of the entry's DER: of the certificate for a certificate, of the
            `SubjectPublicKeyInfo` for a key pair or private key, of the key as the file holds it for a secret key or
            a key of an algorithm the platform does not support, and of the request for a certificate request.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "entryReference is required")
    private String entryReference;

    @Schema(description = """
            Identifier of this entry's import, so a retry cannot import the same entry twice.

            A replay is the same import only when this entry's reference and destination match the first submission,
            and the file carries the same content for it. A replay returns what that entry produced the first time;
            reuse with anything else changed is refused.

            The identifier is per entry rather than per request because entries succeed and fail on their own.
            Repeating a request whose entries partly failed returns what already succeeded and retries only what did
            not, without the caller having to work out which is which.
            """, requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 256)
    @NotBlank(message = "importId is required")
    @Size(min = 1, max = 256, message = "importId must contain between 1 and 256 characters")
    private String importId;

    @Valid
    @Schema(description = "Where this entry's key material is stored. Required when the entry carries key material, "
            + "and refused for an entry that carries only a certificate.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateEntryKeyDestinationDto keyDestination;
}
