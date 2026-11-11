package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.signature.SignatureFamily;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
            + "not list with 422 (errorCode `PARAMETER_UNSUPPORTED`)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "family is required")
    private SignatureFamily family;

    @ToString.Exclude
    @Schema(description = "Attribute values the operator fixed on the Signing Profile, validated when the profile "
            + "was saved and replayed here on every call. This is the profile's whole set, not this operation's share "
            + "of it, so it may carry names declared by sibling operations; this operation reads the names its own "
            + "schema declares and ignores the rest. May be an empty list, but must be present.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "formattingAttributes may be an empty list, but must be present")
    private List<@NotNull(
            message = "formattingAttributes must not contain null items") RequestAttribute> formattingAttributes;
}
