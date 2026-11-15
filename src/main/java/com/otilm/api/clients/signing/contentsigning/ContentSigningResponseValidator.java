package com.otilm.api.clients.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingAttributesDto;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.ContentSigningDeclarationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Refuses a connector's response when it is absent or breaks the rules its DTOs carry. */
public final class ContentSigningResponseValidator {

    private final Validator validator;

    public ContentSigningResponseValidator(Validator validator) {
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    public ContentSigningDeclarationDto requireValidDeclaration(ContentSigningDeclarationDto declaration,
            ApiClientConnectorInfo connector) throws ConnectorException {
        return requireValid(declaration, "getDeclaration", "content-signing declaration", connector);
    }

    public ContentSigningFormattingAttributesDto requireValidListing(ContentSigningFormattingAttributesDto listing,
            ApiClientConnectorInfo connector) throws ConnectorException {
        return requireValid(listing, "listFormattingAttributes", "formatting attribute listing", connector);
    }

    private <T> T requireValid(T response, String operation, String subject, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (response == null) {
            throw new ConnectorException("Connector returned an empty body for " + operation, connector);
        }
        Set<ConstraintViolation<T>> violations = validator.validate(response);
        if (!violations.isEmpty()) {
            throw new ConnectorException("Connector returned an invalid " + subject + ": "
                    + violations
                            .stream()
                            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                            .sorted()
                            .collect(Collectors.joining(", ")),
                    connector);
        }
        return response;
    }
}
