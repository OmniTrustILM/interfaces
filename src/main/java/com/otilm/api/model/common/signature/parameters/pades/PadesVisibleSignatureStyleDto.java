package com.otilm.api.model.common.signature.parameters.pades;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParameterContainer;
import com.otilm.api.model.common.signature.parameters.SignatureParameterSink;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Style is request-capable so that a caller who owns its own branding can carry it with each document. A profile fixes
 * the brand by keeping the group out of its allow-list.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Schema(name = "PadesVisibleSignatureStyle",
        description = "Style of a PAdES visible signature. A field that neither the request nor the profile default "
                + "sets keeps the Signature Formatting Provider's built-in value.")
public class PadesVisibleSignatureStyleDto implements SignatureParameterContainer {

    static final String COLOR = "^#[0-9A-Fa-f]{6}$";

    @Pattern(regexp = COLOR, message = "textColor must be a #RRGGBB colour")
    @Schema(description = "Colour of the caption text, as #RRGGBB", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            pattern = COLOR, example = "#171717")
    private String textColor;

    @Pattern(regexp = COLOR, message = "textBackgroundColor must be a #RRGGBB colour")
    @Schema(description = "Colour behind the caption text, as #RRGGBB", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            pattern = COLOR, example = "#FFFFFF")
    private String textBackgroundColor;

    @Pattern(regexp = COLOR, message = "backgroundColor must be a #RRGGBB colour")
    @Schema(description = "Colour of the whole signature box, as #RRGGBB",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, pattern = COLOR, example = "#FFFFFF")
    private String backgroundColor;

    @Min(value = 0, message = "padding must not be negative")
    @Max(value = 100, message = "padding must be at most 100 points")
    @Schema(description = "Space between the caption text and the edge of its area, in PDF points",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "5")
    private Integer padding;

    @Schema(description = "How the caption is fitted into its area", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TextWrapping textWrapping;

    @Schema(description = "Side of the stamp image the caption sits on, when both are drawn",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TextPosition textPosition;

    @Schema(description = "Horizontal alignment of the caption within its area",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TextHorizontalAlignment textHorizontalAlignment;

    @Schema(description = "Vertical alignment of the caption within its area",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TextVerticalAlignment textVerticalAlignment;

    @Schema(description = "How the stamp image is fitted into the signature box",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ImageScaling imageScaling;

    @Min(value = 72, message = "dpi must be at least 72")
    @Max(value = 1200, message = "dpi must be at most 1200")
    @Schema(description = "Resolution the signature appearance is rendered at, in dots per inch",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "300")
    private Integer dpi;

    @Override
    public void describe(SignatureParameterSink sink) {
        SignatureParameterGroup style = SignatureParameterGroup.VISIBLE_SIGNATURE_STYLE;
        sink.leaf("textColor", style, textColor);
        sink.leaf("textBackgroundColor", style, textBackgroundColor);
        sink.leaf("backgroundColor", style, backgroundColor);
        sink.leaf("padding", style, padding);
        sink.leaf("textWrapping", style, textWrapping);
        sink.leaf("textPosition", style, textPosition);
        sink.leaf("textHorizontalAlignment", style, textHorizontalAlignment);
        sink.leaf("textVerticalAlignment", style, textVerticalAlignment);
        sink.leaf("imageScaling", style, imageScaling);
        sink.leaf("dpi", style, dpi);
    }
}
