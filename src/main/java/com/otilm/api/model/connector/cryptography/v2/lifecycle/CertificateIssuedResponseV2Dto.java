package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "CertificateIssuedResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CertificateIssuedResponseV2Dto {

    @ArraySchema(arraySchema = @Schema(
            description = "Steps the connector took during this call, in order. Absent when it took none."))
    private List<@NotNull(message = "events must not contain null items") @Valid OperationEventV2Dto> events;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 certificate issued response property: " + property);
    }
}
