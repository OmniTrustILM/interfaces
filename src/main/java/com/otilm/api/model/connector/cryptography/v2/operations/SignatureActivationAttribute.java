package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.ObjectAttributeContentV3;
import com.otilm.api.model.common.validation.NullableNotBlankValidator;
import com.otilm.api.model.connector.cryptography.v2.PlatformReservedAttribute;
import com.otilm.api.model.connector.cryptography.v2.ReservedAttributeStatement;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Signature attribute carrying the platform's decision on a sign to a connector that implements the key lifecycle
 * interface. Only the platform fills it.
 */
public final class SignatureActivationAttribute implements PlatformReservedAttribute {

    public static final String NAME = "signatureActivation";
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("ed4eb7a8-2d0c-42ff-bbfa-4df695d39b78");

    private static final Set<String> CONTENT_FIELDS = Set
            .of(SignatureActivation.DECISION_FIELD, SignatureActivation.SIGNER_FIELD);

    private SignatureActivationAttribute() {
    }

    /**
     * A connector publishes it from {@code /sign/attributes} as a required, hidden attribute without a default. Only
     * the platform states its value.
     *
     * @return the reserved object attribute definition
     */
    public static DataAttributeV3 definition() {
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Signature activation");
        properties.setRequired(true);
        properties.setVisible(false);

        DataAttributeV3 definition = new DataAttributeV3();
        definition.setUuid(ATTRIBUTE_UUID.toString());
        definition.setName(NAME);
        definition.setDescription("Authorization decision for the signature and the signer it authorized.");
        definition.setContentType(AttributeContentType.OBJECT);
        definition.setProperties(properties);
        return definition;
    }

    /**
     * The attribute that states the platform's decision on a sign.
     *
     * @param activation the decision and the signer it authorized
     * @return the reserved attribute carrying the activation as its one content item
     * @throws IllegalArgumentException if the decision is missing or the signer is blank
     */
    public static RequestAttributeV3 request(SignatureActivation activation) {
        if (activation.decision() == null) {
            throw new IllegalArgumentException(NAME + " decision must not be null");
        }
        if (NullableNotBlankValidator.readsAsNoValue(activation.signer())) {
            throw new IllegalArgumentException(NAME + " signer must not be blank");
        }
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.OBJECT,
                List.of(new ObjectAttributeContentV3(activation)));
    }

    /**
     * Reads the platform's decision from the signature attributes of a sign request.
     *
     * @param signatureAttributes the signature attributes supplied with the request, may be {@code null}
     * @return the stated activation, or empty when the platform stated none
     * @throws ValidationException if the attribute is supplied more than once, under only one of its reserved
     * identifiers, as a v2 attribute, or with content other than one object whose only fields are a known decision and
     * a non-blank signer
     */
    public static Optional<SignatureActivation> statedActivation(List<? extends RequestAttribute> signatureAttributes) {
        return ReservedAttributeStatement
                .find(signatureAttributes, ATTRIBUTE_UUID, NAME, SignatureActivationAttribute::violation)
                .map(SignatureActivationAttribute::activationOf);
    }

    /** Jackson reads the object content as a map. An in-process request carries the record itself. */
    private static SignatureActivation activationOf(ReservedAttributeStatement statement) {
        if (!(statement.item() instanceof ObjectAttributeContentV3 item)
                || statement.contentType() != AttributeContentType.OBJECT) {
            throw violation("must carry an object value.");
        }
        Serializable data = item.getData();
        if (data instanceof SignatureActivation activation) {
            return new SignatureActivation(decisionOf(activation.decision()), signerOf(activation.signer()));
        }
        if (!(data instanceof Map<?, ?> fields)) {
            throw violation("must carry an object value.");
        }
        if (!CONTENT_FIELDS.containsAll(fields.keySet())) {
            throw violation("must carry only the fields '" + SignatureActivation.DECISION_FIELD + "' and '"
                    + SignatureActivation.SIGNER_FIELD + "'.");
        }
        return new SignatureActivation(decisionOf(fields.get(SignatureActivation.DECISION_FIELD)),
                signerOf(fields.get(SignatureActivation.SIGNER_FIELD)));
    }

    private static SignatureActivationDecision decisionOf(Object decision) {
        if (decision instanceof SignatureActivationDecision stated) {
            return stated;
        }
        if (decision instanceof String code) {
            return SignatureActivationDecision
                    .lookupByCode(code)
                    .orElseThrow(SignatureActivationAttribute::unknownDecision);
        }
        throw unknownDecision();
    }

    private static ValidationException unknownDecision() {
        return violation("must carry a known decision.");
    }

    private static String signerOf(Object signer) {
        if (!(signer instanceof String text) || NullableNotBlankValidator.readsAsNoValue(text)) {
            throw violation("must carry a non-blank signer.");
        }
        return text;
    }

    private static ValidationException violation(String rule) {
        return new ValidationException(ValidationError
                .create("Signature attribute with name '{}' and UUID '{}' " + rule, NAME, ATTRIBUTE_UUID));
    }
}
