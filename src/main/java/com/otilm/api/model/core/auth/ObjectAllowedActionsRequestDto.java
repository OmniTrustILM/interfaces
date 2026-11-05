package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objects of one resource to report the caller's allowed actions on")
public class ObjectAllowedActionsRequestDto {

    public static final int MAX_UUIDS = 1000;

    @Schema(description = "UUIDs of the objects. At most " + MAX_UUIDS + ", the size of the largest list page.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "uuids must name at least one object")
    @Size(max = MAX_UUIDS, message = "uuids must contain at most " + MAX_UUIDS + " objects")
    private List<@NotNull(message = "uuids must not contain null items") UUID> uuids;

}
