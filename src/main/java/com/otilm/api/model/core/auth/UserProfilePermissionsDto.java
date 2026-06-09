package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfilePermissionsDto {

    @Schema(description = "Allowed resource listings", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Resource> allowedListings;

}
