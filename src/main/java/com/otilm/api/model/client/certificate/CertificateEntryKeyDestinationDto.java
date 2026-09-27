package com.otilm.api.model.client.certificate;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Where the key material of a selected entry is to be stored, and under what name.
 */
@Getter
@Setter
@ToString
@Schema(name = "CertificateEntryKeyDestinationDto",
        description = "Token profile and naming for key material imported with a certificate")
public class CertificateEntryKeyDestinationDto {

    @Schema(description = "Token profile that will hold the imported key material",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "tokenProfileUuid is required")
    private String tokenProfileUuid;

    @Schema(description = "Name for the imported key. The platform takes the entry's alias when this is absent, and "
            + "otherwise its certificate's common name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyName;

    @Schema(description = """
            Whether the imported key may later be exported. Defaults to false, and false is final. It can only be
            requested where the token profile reports key export as available for the key type.
            """, requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private Boolean exportable;

    @Schema(description = "Attributes required by the provider to import a key, from the import attribute schema",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> importAttributes;

    @Schema(description = "Custom attributes for the imported key. A key that arrives inside a container carries the "
            + "same custom attributes as one imported on its own.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes;
}
