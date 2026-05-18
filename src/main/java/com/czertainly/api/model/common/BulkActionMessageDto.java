package com.czertainly.api.model.common;

import com.czertainly.api.exception.PlatformException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class BulkActionMessageDto extends NameAndUuidDto {
    @Schema(description = "Message describing the associations of the objects that are preventing the bulk operation",
            examples = {"Object is associated with other items"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private final String message;

    /**
     * Safe factory for exception-driven bulk failures. Uses {@link PlatformException#safeMessage}
     * so raw third-party exception detail never reaches the caller.
     */
    public static BulkActionMessageDto failure(String uuid, String name, Throwable t, String fallback) {
        return new BulkActionMessageDto(uuid, name, PlatformException.safeMessage(t, fallback));
    }

    /**
     * Variant for cases where there is no exception and the caller supplies a pre-formed message.
     * <strong>The caller is responsible for ensuring {@code message} contains no raw exception detail.</strong>
     * Never pass {@code e.getMessage()} directly — use {@link #failure(String, String, Throwable, String)} instead.
     */
    public static BulkActionMessageDto failureWithMessage(String uuid, String name, String message) {
        return new BulkActionMessageDto(uuid, name, message);
    }

    @JsonCreator
    private BulkActionMessageDto(
            @JsonProperty("uuid") String uuid,
            @JsonProperty("name") String name,
            @JsonProperty("message") String message) {
        super(uuid, name);
        this.message = message;
    }
}
