package com.otilm.api.model.common.signature.parameters.pades.validation;

import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignaturePlacementDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExclusivePlacementModeValidator
        implements
            ConstraintValidator<ExclusivePlacementMode, PadesVisibleSignaturePlacementDto> {

    @Override
    public boolean isValid(PadesVisibleSignaturePlacementDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        boolean coordinates = value.getOriginX() != null || value.getOriginY() != null || value.getWidth() != null
                || value.getHeight() != null;
        boolean anchor = value.getAlignmentHorizontal() != null || value.getAlignmentVertical() != null
                || value.getZoom() != null;

        if (value.getFieldId() == null) {
            // A mixture belongs to no single field, so its violation stays on the object.
            return !(coordinates && anchor);
        }
        if (!coordinates && !anchor && value.getPage() == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("fieldId")
                .addConstraintViolation();
        return false;
    }
}
