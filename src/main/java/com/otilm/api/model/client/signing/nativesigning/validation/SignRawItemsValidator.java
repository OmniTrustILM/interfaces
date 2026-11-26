package com.otilm.api.model.client.signing.nativesigning.validation;

import com.otilm.api.model.client.signing.nativesigning.SignRawItemDto;
import com.otilm.api.model.client.signing.nativesigning.SignRawRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Each violation names the item field it concerns: Core's 422 body lists only field errors. A missing mode, algorithm
 * or item, and an empty item, answer to their own constraints.
 */
public class SignRawItemsValidator implements ConstraintValidator<ValidSignRawItems, SignRawRequestDto> {

    private static final String LENGTH_MISMATCH = "data length must match the digest size of digestAlgorithm";

    @Override
    public boolean isValid(SignRawRequestDto request, ConstraintValidatorContext context) {
        if (request == null || request.getSignatureInputMode() == null) {
            return true;
        }
        return switch (request.getSignatureInputMode()) {
            case DATA -> true;
            case DATA_DIGEST -> digestsMatchTheirAlgorithm(request, context);
        };
    }

    private static boolean digestsMatchTheirAlgorithm(SignRawRequestDto request, ConstraintValidatorContext context) {
        List<SignRawItemDto> items = request.getItems();
        if (request.getDigestAlgorithm() == null || items == null) {
            return true;
        }
        int digestSize = request.getDigestAlgorithm().getDigestSizeBytes();
        boolean valid = true;
        for (int index = 0; index < items.size(); index++) {
            SignRawItemDto item = items.get(index);
            byte[] data = item == null ? null : item.getData();
            if (data != null && data.length > 0 && data.length != digestSize) {
                if (valid) {
                    context.disableDefaultConstraintViolation();
                }
                context
                        .buildConstraintViolationWithTemplate(LENGTH_MISMATCH)
                        .addPropertyNode("items")
                        .addPropertyNode("data")
                        .inIterable()
                        .atIndex(index)
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }
}
