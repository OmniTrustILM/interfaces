package com.otilm.api.model.client.signing.nativesigning.validation;

import com.otilm.api.model.client.signing.nativesigning.SignDocumentItemDto;
import com.otilm.api.model.client.signing.nativesigning.SignDocumentRequestDto;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Each violation names the field it concerns: Core's 422 body lists only field errors. A missing transfer mode or item
 * answers to its own constraint.
 */
public class DocumentTransferValidator implements ConstraintValidator<ValidDocumentTransfer, SignDocumentRequestDto> {

    private static final String DIGEST_ALGORITHM = "digestAlgorithm";
    private static final String DOCUMENT = "document";
    private static final String DOCUMENT_DIGEST = "documentDigest";
    private static final String ITEMS = "items";

    @Override
    public boolean isValid(SignDocumentRequestDto request, ConstraintValidatorContext context) {
        if (request == null || request.getTransferMode() == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        Violations violations = new Violations(context, request.getTransferMode());
        return switch (request.getTransferMode()) {
            case INLINE -> violations.checkInline(request);
            case DIGEST_ONLY -> violations.checkDigestOnly(request);
        };
    }

    private static final class Violations {

        private final ConstraintValidatorContext context;
        private final String modeCode;
        private boolean valid = true;

        private Violations(ConstraintValidatorContext context, DocumentTransferMode mode) {
            this.context = context;
            this.modeCode = mode.getCode();
        }

        private boolean checkInline(SignDocumentRequestDto request) {
            if (request.getDigestAlgorithm() != null) {
                onRequest(DIGEST_ALGORITHM, absentMessage(DIGEST_ALGORITHM));
            }
            forEachPresentItem(request.getItems(), (item, index) -> {
                if (item.getDocument() == null) {
                    onItem(index, DOCUMENT, requiredMessage(DOCUMENT));
                }
                if (item.getDocumentDigest() != null) {
                    onItem(index, DOCUMENT_DIGEST, absentMessage(DOCUMENT_DIGEST));
                }
            });
            return valid;
        }

        private boolean checkDigestOnly(SignDocumentRequestDto request) {
            DigestAlgorithm algorithm = request.getDigestAlgorithm();
            if (algorithm == null) {
                onRequest(DIGEST_ALGORITHM, requiredMessage(DIGEST_ALGORITHM));
            }
            forEachPresentItem(request.getItems(), (item, index) -> {
                byte[] digest = item.getDocumentDigest();
                if (digest == null) {
                    onItem(index, DOCUMENT_DIGEST, requiredMessage(DOCUMENT_DIGEST));
                } else if (algorithm != null && digest.length != algorithm.getDigestSizeBytes()) {
                    onItem(index, DOCUMENT_DIGEST, lengthMismatchMessage());
                }
                if (item.getDocument() != null) {
                    onItem(index, DOCUMENT, absentMessage(DOCUMENT));
                }
            });
            return valid;
        }

        private String requiredMessage(String field) {
            return "%s is required when transferMode is %s".formatted(field, modeCode);
        }

        private String absentMessage(String field) {
            return "%s must be absent when transferMode is %s".formatted(field, modeCode);
        }

        private static String lengthMismatchMessage() {
            return "%s length must match the digest size of %s".formatted(DOCUMENT_DIGEST, DIGEST_ALGORITHM);
        }

        private void onRequest(String field, String message) {
            context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation();
            valid = false;
        }

        private void onItem(int index, String field, String message) {
            context
                    .buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(ITEMS)
                    .addPropertyNode(field)
                    .inIterable()
                    .atIndex(index)
                    .addConstraintViolation();
            valid = false;
        }

        private static void forEachPresentItem(List<SignDocumentItemDto> items, ItemCheck check) {
            if (items == null) {
                return;
            }
            for (int index = 0; index < items.size(); index++) {
                SignDocumentItemDto item = items.get(index);
                if (item != null) {
                    check.apply(item, index);
                }
            }
        }
    }

    @FunctionalInterface
    private interface ItemCheck {
        void apply(SignDocumentItemDto item, int index);
    }
}
