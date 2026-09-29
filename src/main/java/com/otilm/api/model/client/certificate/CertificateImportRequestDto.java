package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.upload.UploadRequestDto;
import com.otilm.api.model.core.logging.Sensitive;
import com.otilm.api.model.core.secret.Passphrase;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body of a certificate import: the file, the entries to take from it, and where their key material goes.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "CertificateImportRequestDto",
        description = "The file to import from, the entries to import, and where their key material goes")
public class CertificateImportRequestDto extends UploadRequestDto {

    @ToString.Exclude
    @Sensitive
    @Schema(description = "Passphrase that opens the uploaded file. Absent for a file that carries no protection of "
            + "its own.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Passphrase passphrase;

    @Valid
    @Schema(description = """
            Entries to import. Nothing else in the file is touched, so an import can never take in material the caller
            did not ask for.

            An entry is named by the reference the inspection reported, which is derived from the entry's own content:
            a certificate fingerprint, a public key fingerprint, or a digest of the protected key. A caller that
            already knows what it is importing can compute the reference itself and never read the file first.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "entries must contain at least one entry")
    private List<@NotNull(message = "entries must not contain null items") CertificateImportEntryDto> entries;

    @Schema(description = "Custom attributes for the imported certificates",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes;

    /**
     * @return whether each entry is named once
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "entries must not name the same entryReference twice")
    public boolean isEachEntryNamedOnce() {
        return isDistinct(CertificateImportEntryDto::getEntryReference);
    }

    /**
     * Two entries sharing an identifier would make one of them look like a replay of the other.
     *
     * @return whether each entry carries an identifier of its own
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "entries must not share an importId")
    public boolean isEachImportIdentifiedOnce() {
        return isDistinct(CertificateImportEntryDto::getImportId);
    }

    private boolean isDistinct(Function<CertificateImportEntryDto, String> value) {
        if (entries == null) {
            return true;
        }
        List<String> present = entries.stream().filter(Objects::nonNull).map(value).filter(Objects::nonNull).toList();
        return present.size() == Set.copyOf(present).size();
    }
}
