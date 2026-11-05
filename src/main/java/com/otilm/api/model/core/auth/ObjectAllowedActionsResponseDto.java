package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "The caller's allowed actions on the requested objects")
public class ObjectAllowedActionsResponseDto {

    @Schema(description = """
            One item per requested object the caller may list or view, each object at most once. An object the \
            caller may neither list nor view is left out, the same as an object that does not exist, so a missing \
            item says nothing about whether the object exists.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ObjectAllowedActionsDto> items;

}
