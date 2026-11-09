package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.Data;

@Data
public class SigningSettingsDto implements Serializable {

    public static final int MIN_ITEMS_PER_REQUEST = 1;
    public static final int DEFAULT_MAX_ITEMS_PER_REQUEST = 100;
    public static final int MAX_ITEMS_PER_REQUEST_CEILING = 1_000;

    @Min(value = MIN_ITEMS_PER_REQUEST, message = "must be at least {value}")
    @Max(value = MAX_ITEMS_PER_REQUEST_CEILING, message = "must not exceed {value}")
    @Schema(description = "Maximum number of items for one request of the Native Signing API",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "" + MIN_ITEMS_PER_REQUEST,
            maximum = "" + MAX_ITEMS_PER_REQUEST_CEILING, defaultValue = "" + DEFAULT_MAX_ITEMS_PER_REQUEST)
    private Integer maxItemsPerRequest;

}
