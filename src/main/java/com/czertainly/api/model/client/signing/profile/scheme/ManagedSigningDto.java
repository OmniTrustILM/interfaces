package com.czertainly.api.model.client.signing.profile.scheme;

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
 * Abstract base for all managed signing response configurations.
 *
 * <p>The managed signing scheme uses a two-level polymorphic structure:
 * <ol>
 *   <li>The outer level, owned by {@link SigningSchemeDto}, discriminates on {@code signingScheme}
 *       and maps the value {@code "managed"} to this class.</li>
 *   <li>This inner level discriminates on {@code managedSigningType} to select the concrete
 *       subclass: {@link StaticKeyManagedSigningDto} or {@link OneTimeKeyManagedSigningDto}.</li>
 * </ol>
 *
 * <p>This class uses {@link Deserializer} (via {@code @JsonDeserialize}) instead of a nested {@code @JsonTypeInfo}
 * because Jackson's polymorphic resolution chain does not chain two levels of {@code @JsonTypeInfo} through an abstract
 * intermediate type. Concrete subclasses must carry {@code @JsonDeserialize(using = JsonDeserializer.None.class)}
 * to shadow the inherited {@code @JsonDeserialize} and prevent infinite recursion when {@link Deserializer} delegates
 * to them via {@link DeserializationContext#readTreeAsValue}. Concrete subclasses must also carry
 * {@code @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)} to nullify the {@code TypeDeserializer} inherited from
 * {@link SigningSchemeRequestDto}, which would otherwise wrap the bean deserializer and trigger a failed type-id resolution.</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(using = ManagedSigningDto.Deserializer.class)
@Schema(implementation = ManagedSigningSchemeInterface.class)
public abstract class ManagedSigningDto extends SigningSchemeDto implements ManagedSigningSchemeInterface {

    @NotNull
    @Schema(description = "Managed signing type", requiredMode = Schema.RequiredMode.REQUIRED)
    private final ManagedSigningType managedSigningType;

    protected ManagedSigningDto(ManagedSigningType managedSigningType) {
        super(SigningScheme.MANAGED);
        this.managedSigningType = managedSigningType;
    }

    /**
     * Custom deserializer that implements the second-level type resolution for managed signing.
     *
     * <p>Reads the full JSON object into a tree to inspect {@code managedSigningType} without
     * consuming the parser, then delegates to the appropriate concrete class. Throws
     * {@link InvalidTypeIdException} (consistent with Jackson's own polymorphic error handling)
     * when the value is absent or does not match a known {@link ManagedSigningType}.
     */
    public static class Deserializer extends StdDeserializer<ManagedSigningDto> {

        public Deserializer() {
            super(ManagedSigningDto.class);
        }

        @Override
        public ManagedSigningDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            ObjectNode tree = p.readValueAsTree();
            JsonNode typeNode = tree.get("managedSigningType");
            String typeId = (typeNode != null && !typeNode.isNull()) ? typeNode.asText() : null;
            if (ManagedSigningType.Codes.STATIC_KEY.equals(typeId)) {
                return ctxt.readTreeAsValue(tree, StaticKeyManagedSigningDto.class);
            } else if (ManagedSigningType.Codes.ONE_TIME_KEY.equals(typeId)) {
                return ctxt.readTreeAsValue(tree, OneTimeKeyManagedSigningDto.class);
            }
            String errorMessage = typeId == null ? "Missing managedSigningType" : "Unknown managedSigningType: " + typeId;
            throw InvalidTypeIdException.from(p, errorMessage, ctxt.constructType(ManagedSigningDto.class), typeId);
        }
    }
}
