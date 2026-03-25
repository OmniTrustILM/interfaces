package com.czertainly.api.model.core.signing.signatureprofile.scheme;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

/**
 * Abstract base for all managed signing request configurations.
 *
 * <p>This is the request-side counterpart of {@link ManagedSigningDto} and follows an identical
 * two-level polymorphic structure.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(using = ManagedSigningRequestDto.Deserializer.class)
@Schema(implementation = ManagedSigningRequestSchemeInterface.class)
public abstract class ManagedSigningRequestDto extends SigningSchemeRequestDto implements ManagedSigningRequestSchemeInterface {

    @NotNull
    @Schema(description = "Managed signing type", requiredMode = Schema.RequiredMode.REQUIRED)
    private final ManagedSigningType managedSigningType;

    protected ManagedSigningRequestDto(ManagedSigningType managedSigningType) {
        super(SigningScheme.MANAGED);
        this.managedSigningType = managedSigningType;
    }

    /**
     * Custom deserializer that implements the second-level type resolution for managed signing
     * requests. See {@link ManagedSigningDto.Deserializer} for a detailed explanation.
     */
    public static class Deserializer extends StdDeserializer<ManagedSigningRequestDto> {

        public Deserializer() {
            super(ManagedSigningRequestDto.class);
        }

        @Override
        public ManagedSigningRequestDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectNode tree = p.readValueAsTree();
            JsonNode typeNode = tree.get("managedSigningType");
            String typeId = (typeNode != null && !typeNode.isNull()) ? typeNode.asText() : null;
            if (ManagedSigningType.Codes.STATIC_KEY.equals(typeId)) {
                return ctxt.readTreeAsValue(tree, StaticKeyManagedSigningRequestDto.class);
            } else if (ManagedSigningType.Codes.ONE_TIME_KEY.equals(typeId)) {
                return ctxt.readTreeAsValue(tree, OneTimeKeyManagedSigningRequestDto.class);
            }
            throw InvalidTypeIdException.from(p,
                    "Unknown or missing managedSigningType: " + typeId,
                    ctxt.constructType(ManagedSigningRequestDto.class), typeId);
        }
    }
}
