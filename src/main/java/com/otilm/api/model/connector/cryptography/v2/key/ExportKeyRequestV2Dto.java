package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/keys/export}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ExportKeyRequestV2Dto", description = """
        The key to export and the passphrase that will protect it.

        Export is synchronous and its output is always protected: the connector answers with the key as a PKCS#8
        EncryptedPrivateKeyInfo under the passphrase supplied here. Certificates are not part of this contract: the
        platform assembles any user-facing container around the envelope it receives.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ExportKeyRequestV2Dto extends KeyScopedRequestV2Dto {

    private static final String UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    @Schema(description = "Type of key the metadata identifies. The connector must refuse the export with "
            + "`KEY_MATERIAL_MISMATCH` when the key it finds is of another type, so a mismatch is caught before any "
            + "material is decrypted.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    @Schema(description = """
            Durable identity the platform holds for the key, present only for keys that carry one.

            - When it is supplied, the connector must read it from the key it exports and echo it unchanged.
            - When it is absent, the connector must not return one.

            Either way the platform can confirm the material it received belongs to the key it asked for.
            """, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Pattern(regexp = UUID_PATTERN, message = "keyReference must be a canonical UUID")
    private String keyReference;

    @Schema(description = "Attributes to export the key, following the schema from "
            + "`POST /v2/cryptographyProvider/keys/export/attributes`. A connector must not define an attribute that "
            + "carries key material, a passphrase or a certificate: the passphrase travels only in `passphrase`, and "
            + "the material only in the `material` of the response.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "exportKeyAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(
            message = "exportKeyAttributes must not contain null entries") RequestAttribute> exportKeyAttributes;

    @ToString.Exclude
    @Schema(description = """
            Passphrase that protects the returned material. The resulting envelope has to open in external tools, so the
            passphrase is used exactly as supplied, encoded as UTF-8 in Unicode normalization form C. A minimum length
            is platform policy, applied before the request is sent.
            """, requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    @NotBlank(message = "passphrase is required")
    @Sensitive
    private String passphrase;
}
