package com.otilm.api.model.client.inspection;

import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One entry the platform found in an uploaded file, described well enough for a caller to choose what to import before
 * it commits to anything.
 */
@Getter
@Setter
@ToString
@Schema(name = "InspectedEntryDto", description = "One entry found in an uploaded file")
public class InspectedEntryDto {

    @Schema(description = """
            Reference for this entry, used to select it when importing.

            The lowercase hex SHA-256 of the entry's DER: of the certificate for a certificate, of the
            `SubjectPublicKeyInfo` for a key pair or private key, of the key as the file holds it for a secret key or
            a key of an algorithm the platform does not support, and of the request for a certificate request. It is
            never derived from the entry's position in the file, so a reference cannot come to mean a different entry
            between inspecting and importing.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private String entryReference;

    @Schema(description = "What this entry turned out to be", requiredMode = Schema.RequiredMode.REQUIRED)
    private InspectedEntryKind kind;

    @Schema(description = "Subject distinguished name of the entry's certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Issuer distinguished name of the entry's certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String issuerDn;

    @Schema(description = "Serial number of the entry's certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String serialNumber;

    @Schema(description = "SHA-256 fingerprint of the entry's certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Start of the certificate's validity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime notBefore;

    @Schema(description = "End of the certificate's validity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime notAfter;

    @Schema(description = "Subject alternative names of the entry's certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> subjectAlternativeNames;

    @Schema(description = "Algorithm of the entry's key, where it can be determined without opening protected material",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyAlgorithm keyAlgorithm;

    @Schema(description = "Length of the entry's key in bits, where it can be determined",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer keyLength;

    @Schema(description = "Number of certificates found with this entry, including the entry's own certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer chainLength;

    @Schema(description = "Whether the entry's key material can be imported into the token profile the inspection "
            + "named. Absent when the inspection named no token profile.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean importable;

    @Schema(description = "Why the entry cannot be imported, when it cannot",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String notImportableReason;
}
