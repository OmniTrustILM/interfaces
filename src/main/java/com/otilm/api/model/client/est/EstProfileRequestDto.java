package com.otilm.api.model.client.est;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.common.validation.ValidName;
import com.otilm.api.model.core.est.EstPaths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EstProfileRequestDto extends BaseEstProfileRequestDto {

    @NotBlank
    @ValidName
    @Schema(description = "Name of the EST Profile. It is the profile's label in EST URLs and cannot be changed later",
            requiredMode = Schema.RequiredMode.REQUIRED, examples = {"est-devices"})
    private String name;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "Name must not be an EST operation name or a dot segment")
    public boolean isNameNotReserved() {
        return !EstPaths.isReservedLabel(name);
    }
}
