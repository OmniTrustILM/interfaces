package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @Valid
    @Schema(description = "Where this entry's key material is stored. Required when the entry carries key material, "
            + "and refused for an entry that carries only a certificate.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateEntryKeyDestinationDto keyDestination;
}
