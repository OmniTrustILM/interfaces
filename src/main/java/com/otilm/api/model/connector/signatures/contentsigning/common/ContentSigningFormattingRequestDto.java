package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.validation.SignatureFamilyFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Abstract base for every content-signing formatting request body.
 *
 * <p>
 * The connector holds no state between operations, so each request carries everything the operation needs: which family
 * to produce, and the attribute values the operator fixed on the Signing Profile. Nothing is looked up from a previous
 * call.
 * </p>
 */
@Getter
@Setter
@ToString
public abstract class ContentSigningFormattingRequestDto {

    @Schema(description = "Signature family this request targets; a connector rejects a family its declaration does "
            + "not list with 422 (errorCode `PARAMETER_UNSUPPORTED`)", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "pades")
    @NotNull(message = "family is required")
    @Pattern(regexp = SignatureFamilyFormat.REGEX, message = SignatureFamilyFormat.MESSAGE)
    private String family;

    @ToString.Exclude
    @Schema(description = "Attribute values the operator set on the Signing Profile, each checked against its "
            + "definition before the platform sends it. The list carries the profile's whole set: validateProfile "
            + "judges all of it, and an operation with an attribute listing reads only the names its own listing "
            + "declares. May be an empty list, but must be present.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "formattingAttributes may be an empty list, but must be present")
    private List<@NotNull(
            message = "formattingAttributes must not contain null items") RequestAttribute> formattingAttributes;
}
