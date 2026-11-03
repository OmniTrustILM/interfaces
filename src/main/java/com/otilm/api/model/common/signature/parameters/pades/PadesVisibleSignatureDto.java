package com.otilm.api.model.common.signature.parameters.pades;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParameterContainer;
import com.otilm.api.model.common.signature.parameters.SignatureParameterSink;
import com.otilm.api.model.common.signature.parameters.pades.validation.ImageAndMimeTypeTogether;
import com.otilm.api.model.common.signature.parameters.pades.validation.StampImageSize;
import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What a PAdES visible signature shows, where it goes, and how it is drawn.
 *
 * <p>
 * The image travels as resolved bytes, so the connector stays stateless. A stamp from the profile is indistinguishable
 * from one sent with the request.
 * </p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@ImageAndMimeTypeTogether
@Schema(name = "PadesVisibleSignature",
        description = "Content of a PAdES visible signature. A stamp image and its media type travel together: one "
                + "without the other is rejected.")
public class PadesVisibleSignatureDto implements SignatureParameterContainer {

    @Schema(description = "Whether the signature is drawn on the page at all. On a request, absent means the "
            + "Signing Profile's default decides. In the effective parameters false is never sent: resolving to "
            + "false omits this whole container instead. Absent inside a container that is present leaves the draw "
            + "decision to the connector.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true")
    private Boolean visible;

    @ToString.Exclude
    @Size(max = 4096, message = "text must be at most 4096 characters")
    @NullableNotBlank(message = "text must not be blank if provided")
    @Schema(description = "Caption drawn in the signature. Plain text: any transport encoding belongs to the door "
            + "that used it, not to this contract.", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "Signed by Jane Doe")
    private String text;

    @ToString.Exclude
    @StampImageSize(max = 262144)
    // 349528 base64 characters carry the 262144 decoded bytes above.
    @Schema(description = "Stamp image drawn in the signature, base64-encoded in JSON. At most 256 KiB decoded. "
            + "Always resolved bytes: this contract carries no reference, no identifier and no path.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minLength = 4, maxLength = 349528)
    private byte[] image;

    @Pattern(regexp = "image/png|image/jpeg", message = "imageMimeType must be image/png or image/jpeg")
    @Schema(description = "Media type of the stamp image. PNG and JPEG only; SVG is excluded because the renderer "
            + "wants a raster. Matched exactly: image/png or image/jpeg, lower-case, with no parameters.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"image/png", "image/jpeg"},
            example = "image/png")
    private String imageMimeType;

    @Valid
    @Schema(description = "Where the signature lands on the page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PadesVisibleSignaturePlacementDto placement;

    @Valid
    @Schema(description = "How the signature is drawn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PadesVisibleSignatureStyleDto style;

    @Override
    public void describe(SignatureParameterSink sink) {
        SignatureParameterGroup content = SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT;
        sink.leaf("visible", content, visible);
        sink.leaf("text", content, text);
        sink.leaf("image", content, image);
        sink.leaf("imageMimeType", content, imageMimeType);
        sink.container("placement", placement);
        sink.container("style", style);
    }
}
