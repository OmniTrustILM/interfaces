package com.otilm.api.clients.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.ContentSigningDeclarationDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Refuses a connector's declaration when it is absent or breaks the rules its DTOs carry. */
public final class ContentSigningDeclarationValidator {

    private final Validator validator;

    public ContentSigningDeclarationValidator(Validator validator) {
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    public ContentSigningDeclarationDto requireValid(ContentSigningDeclarationDto declaration,
            ApiClientConnectorInfo connector) throws ConnectorException {
        if (declaration == null) {
            throw new ConnectorException("Connector returned an empty body for getDeclaration", connector);
        }
        Set<ConstraintViolation<ContentSigningDeclarationDto>> violations = validator.validate(declaration);
        if (!violations.isEmpty()) {
            throw new ConnectorException("Connector returned an invalid content-signing declaration: " + violations
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", ")), connector);
        }
        return declaration;
    }
}
