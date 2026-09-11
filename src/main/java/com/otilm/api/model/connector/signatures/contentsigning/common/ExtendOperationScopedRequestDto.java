package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Identifies the asynchronous extension to poll or cancel.
 *
 * <p>
 * A connector that only ever runs the extension synchronously never mints a handle, so it answers 404 here rather than
 * carrying a stub implementation.
 * </p>
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "ExtendOperationScopedRequest",
        description = "Tracking handle identifying the asynchronous extension to act on")
public class ExtendOperationScopedRequestDto extends ContentSigningFormattingRequestDto {

    @NotEmpty(message = "operationMeta is required and must not be empty")
    @Schema(description = "Connector-defined metadata returned in the original extendToLevel 202 Accepted response",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull(
            message = "operationMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> operationMeta;
}
