package com.otilm.api.model.connector.signatures.contentsigning.declaration.validation;

import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.SignatureLevelDeclarationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class LevelLadderValidator implements ConstraintValidator<LevelLadder, List<SignatureLevelDeclarationDto>> {

    private static final SignatureLevel[] LADDER = SignatureLevel.values();

    @Override
    public boolean isValid(List<SignatureLevelDeclarationDto> levels, ConstraintValidatorContext context) {
        if (levels == null || levels.stream().anyMatch(level -> level == null || level.getLevel() == null)) {
            return true;
        }
        if (levels.size() > LADDER.length) {
            return refuse(context, "it declares %d levels".formatted(levels.size()));
        }
        for (int rung = 0; rung < levels.size(); rung++) {
            SignatureLevel declared = levels.get(rung).getLevel();
            if (declared != LADDER[rung]) {
                return refuse(context, "%s comes where %s belongs".formatted(declared, LADDER[rung]));
            }
        }
        return true;
    }

    private static boolean refuse(ConstraintValidatorContext context, String detail) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate() + ", but " + detail)
                .addConstraintViolation();
        return false;
    }
}
