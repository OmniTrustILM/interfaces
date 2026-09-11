package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * The checks every connector response goes through, whichever operation returned it: the response exists, its status
 * matches the execution mode the caller selected, and its own field constraints hold.
 *
 * <p>
 * Each validator states what one family of operations additionally requires and inherits these. A check that applies to
 * more than one family belongs here, so the two validators cannot answer the same question differently.
 * </p>
 */
abstract class ResponseChecks {

    private final Validator validator;

    ResponseChecks(Validator validator) {
        this.validator = Objects.requireNonNull(validator, "validator is required");
    }

    /** The validator this class was built with, for a subclass that builds a collaborator of its own. */
    final Validator validator() {
        return validator;
    }

    static OperationValidationResult validate(Runnable validation) {
        try {
            validation.run();
            return OperationValidationResult.valid();
        } catch (IllegalArgumentException e) {
            return OperationValidationResult.invalid(e);
        }
    }

    static void requireRequest(Object request, String operation) {
        if (request == null) {
            throw new IllegalArgumentException(operation + " request is required");
        }
    }

    static void requireExecutionMode(OperationExecutionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Execution mode is required");
        }
    }

    static void requireResponseStatus(ResponseEntity<?> response, HttpStatus expectedStatus) {
        if (response == null) {
            throw new IllegalArgumentException("Connector returned no response");
        }
        if (response.getStatusCode().value() != expectedStatus.value()) {
            throw new IllegalArgumentException("Connector returned HTTP " + response.getStatusCode().value()
                    + "; expected HTTP " + expectedStatus.value());
        }
    }

    static <T> T requireBody(ResponseEntity<T> response) {
        T body = response.getBody();
        if (body == null) {
            throw new IllegalArgumentException("Connector response body is required");
        }
        return body;
    }

    final void validateOperationResponseConstraints(OperationExecutionMode mode, ResponseEntity<?> response) {
        requireExecutionMode(mode);
        if (mode == OperationExecutionMode.SYNCHRONOUS) {
            requireResponseStatus(response, HttpStatus.OK);
            validateBean(requireBody(response), SynchronousResponse.class);
        } else {
            requireResponseStatus(response, HttpStatus.ACCEPTED);
            validateBean(requireBody(response), AsynchronousResponse.class);
        }
    }

    /**
     * A key that arrives from either operation that produces one: the response honors the selected execution mode, and
     * the key it reports is of the type that was asked for.
     */
    final void validateKeyCreationOutcome(KeyRequestType requestedType, OperationExecutionMode executionMode,
            ResponseEntity<? extends KeyCreationResponseV2Dto> response) {
        if (requestedType == null) {
            throw new IllegalArgumentException("Key request type is required");
        }
        validateOperationResponseConstraints(executionMode, response);
        KeyRequestType responseType = requireBody(response).getKeyRequestType();
        if (requestedType != responseType) {
            throw new IllegalArgumentException(
                    "Connector returned key request type " + responseType + "; expected " + requestedType);
        }
    }

    final OperationValidationResult validateBeanConstraints(Object response) {
        return validate(() -> validateRequiredBean(response));
    }

    final void validateRequiredBean(Object response) {
        if (response == null) {
            throw new IllegalArgumentException("Connector response body is required");
        }
        validateBean(response, Default.class);
    }

    final OperationValidationResult validateResponseElements(List<?> response, String itemName) {
        return validate(() -> {
            if (response == null) {
                throw new IllegalArgumentException("Connector response body is required");
            }
            for (Object item : response) {
                if (item == null) {
                    throw new IllegalArgumentException("Connector response must not contain a null " + itemName);
                }
                validateBean(item, Default.class);
            }
        });
    }

    final void validateBean(Object body, Class<?> group) {
        Set<ConstraintViolation<Object>> violations = validator.validate(body, group);
        if (!violations.isEmpty()) {
            String details = violations
                    .stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Connector response validation failed: " + details);
        }
    }
}
