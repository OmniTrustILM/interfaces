package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.material.EncryptedKeyMaterialV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/keys/import}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ImportKeyRequestV2Dto", description = """
        Key material to import, already re-protected by the platform.

        The platform opens whatever a user supplied, re-protects the key under a passphrase it generates for this
        request alone and sends it here, so a connector always receives the same protection profile and never sees a
        user-chosen password. Certificates are not part of this contract: the platform keeps them.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ImportKeyRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    private static final String UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    @Schema(description = """
            Identifier of a key import operation, so a retry cannot create a second key in the technology.

            A replay is the same operation only when every one of these matches the first submission: `executionMode`,
            `keyRequestType`, `keyReference`, `tokenAttributes`, `tokenProfileAttributes`,
            `importKeyAttributes`, `exportable`, and the imported key itself. Because the platform re-protects the
            material for every submission, the key's identity decides equivalence rather than the envelope bytes.

            - An asynchronous replay answers HTTP 202 with the original `operationMeta`.
            - A synchronous replay answers HTTP 200 with the original result.
            - Reuse with anything else changed answers `RESOURCE_ALREADY_EXISTS` (HTTP 409).
            """, requiredMode = Schema.RequiredMode.REQUIRED, minLength = 1, maxLength = 256)
    @NotBlank(message = "keyImportId is required")
    @Size(min = 1, max = 256, message = "keyImportId must contain between 1 and 256 characters")
    private String keyImportId;

    @Schema(description = """
            Durable identity the platform assigns to the imported key.

            The connector must bind it in the technology wherever the technology supports one — a PKCS#11 `CKA_ID` and
            label, a keystore alias, a KMIP attribute, a cloud resource tag. Where the technology carries no such
            channel, the connector must keep its own durable record from this reference to whatever handle the
            technology did give it. Either way the binding is what lets a key be found again after a lost response, so
            it must survive connector restarts and must not be derived from anything request-scoped.

            The connector does not return the reference: the platform supplied it, and resolves a lost import through
            `POST /v2/cryptographyProvider/keys/import/result` instead.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "keyReference is required")
    @Pattern(regexp = UUID_PATTERN, message = "keyReference must be a canonical UUID")
    private String keyReference;

    @Schema(description = "Caller-selected execution mode. The connector must not switch modes implicitly: a "
            + "`synchronous` request is answered with HTTP 200 and a result, an `asynchronous` request with HTTP 202 "
            + "and a tracking handle.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "executionMode is required")
    private OperationExecutionMode executionMode;

    @Schema(description = "Type of key the material carries. The connector checks it against the importable key types "
            + "it advertises for the token context before it decrypts anything, and refuses a mismatch with "
            + "`KEY_TYPE_NOT_IMPORTABLE`.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    @Schema(description = "Attributes to import the key, following the schema from "
            + "`POST /v2/cryptographyProvider/keys/import/attributes`. A connector must not define an attribute that "
            + "carries key material, a passphrase or a certificate: the material travels only in `material`.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "importKeyAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(
            message = "importKeyAttributes must not contain null entries") RequestAttribute> importKeyAttributes;

    @Valid
    @Schema(description = "Protected key material to import. Its `encryptedPrivateKeyInfo` states the one protection "
            + "profile the contract accepts.", requiredMode = Schema.RequiredMode.REQUIRED,
            accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotNull(message = "material is required")
    private EncryptedKeyMaterialV2Dto material;

    @ToString.Exclude
    @Schema(description = """
            Passphrase protecting `material`. The platform generates it for this request alone, it is never a
            user-supplied password, and the connector must discard it once the material is decrypted.
            """, requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "passphrase is required")
    private String passphrase;

    @Schema(description = """
            Whether the imported key may later be exported. The platform states it explicitly and only sends `true` to a
            connector that declares key export for the key type. A connector that cannot honour it must refuse the
            import with `EXPORTABLE_NOT_SUPPORTED` rather than import a key whose flag it cannot deliver.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "exportable is required")
    private Boolean exportable;
}
