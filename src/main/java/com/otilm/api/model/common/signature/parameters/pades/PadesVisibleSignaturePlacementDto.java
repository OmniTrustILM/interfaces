package com.otilm.api.model.common.signature.parameters.pades;

import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParameterContainer;
import com.otilm.api.model.common.signature.parameters.SignatureParameterSink;
import com.otilm.api.model.common.signature.parameters.pades.validation.ExclusivePlacementMode;
import com.otilm.api.model.common.signature.parameters.pades.validation.QuarterTurn;
import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Where a visible signature lands on the page.
 *
 * <p>
 * Where the stamp goes is a per-document fact: the signature line moves with the contract. That is why placement is
 * request-capable rather than fixed by the operator.
 * </p>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@ExclusivePlacementMode
@Schema(name = "PadesVisibleSignaturePlacement",
        description = "Placement of a PAdES visible signature, in one addressing mode only: a named signature field "
                + "(fieldId alone), explicit coordinates, or an anchor. Coordinates are in PDF points (1/72 inch) "
                + "measured from the top-left corner of the page. Whether a mode is complete is decided after the "
                + "request has been merged over the profile's defaults.")
public class PadesVisibleSignaturePlacementDto implements SignatureParameterContainer {

    @ToString.Exclude
    @Size(max = 256, message = "fieldId must be at most 256 characters")
    @NullableNotBlank(message = "fieldId must not be blank if provided")
    @Schema(description = "Name of an existing signature field to sign into. Addresses the placement on its own: no "
            + "page, coordinates, anchor or rotation may accompany it, because the field already carries all of them.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Signature1")
    private String fieldId;

    @Min(value = 1, message = "page must be 1 or greater")
    @Schema(description = "Page to place the signature on, counting from 1. Shared by the coordinate and anchor modes.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Integer page;

    @DecimalMin(value = "0", message = "originX must not be negative")
    @Schema(description = "Distance from the left edge of the page to the left edge of the signature, in PDF points",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "20")
    private Float originX;

    @DecimalMin(value = "0", message = "originY must not be negative")
    @Schema(description = "Distance from the top edge of the page to the top edge of the signature, in PDF points",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "30")
    private Float originY;

    @DecimalMin(value = "0", inclusive = false, message = "width must be greater than zero")
    @Schema(description = "Width of the signature, in PDF points", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "180")
    private Float width;

    @DecimalMin(value = "0", inclusive = false, message = "height must be greater than zero")
    @Schema(description = "Height of the signature, in PDF points", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "60")
    private Float height;

    @QuarterTurn
    @Schema(description = "Clockwise rotation of the signature, in degrees. Quarter turns only.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, allowableValues = {"0", "90", "180", "270"}, example = "0")
    private Integer rotation;

    @Schema(description = "Horizontal edge to anchor the signature to, in the anchor mode",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private HorizontalAlignment alignmentHorizontal;

    @Schema(description = "Vertical edge to anchor the signature to, in the anchor mode",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private VerticalAlignment alignmentVertical;

    @DecimalMin(value = "0", inclusive = false, message = "zoom must be greater than zero")
    @DecimalMax(value = "1000", message = "zoom must be at most 1000 percent")
    @Schema(description = "Size of the anchored signature as a percentage of its natural size, where 100 is natural "
            + "size. Belongs to the anchor mode; an explicit width and height leave nothing to scale.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "100")
    private Float zoom;

    @Override
    public void describe(SignatureParameterSink sink) {
        SignatureParameterGroup placement = SignatureParameterGroup.VISIBLE_SIGNATURE_PLACEMENT;
        sink.leaf("fieldId", placement, fieldId);
        sink.leaf("page", placement, page);
        sink.leaf("originX", placement, originX);
        sink.leaf("originY", placement, originY);
        sink.leaf("width", placement, width);
        sink.leaf("height", placement, height);
        sink.leaf("rotation", placement, rotation);
        sink.leaf("alignmentHorizontal", placement, alignmentHorizontal);
        sink.leaf("alignmentVertical", placement, alignmentVertical);
        sink.leaf("zoom", placement, zoom);
    }
}
