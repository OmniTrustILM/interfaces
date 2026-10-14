package com.otilm.api.model.core.discovery;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * One kind of problem a discovery run recorded, with how often it happened. Repeats aggregate onto one entry, so a
 * fault hit thousands of times cannot bury the first and most telling entry.
 */
// No class-level NON_NULL: every field is required, so it could only hide a mapping bug.
@Schema(description = "One kind of problem a discovery run recorded and how often it happened. Repeats are counted "
        + "on the existing entry.")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DiscoveryMessageDto {

    // No description: it would be hoisted onto the shared DiscoveryMessageSeverity component.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private DiscoveryMessageSeverity severity;

    @Schema(description = "Kind of problem, from a closed vocabulary. Entries aggregate by code and message, so "
            + "several entries can share a code.", maxLength = 64, requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "The problem, written by the platform for a person to read; never a raw exception message "
            + "or text from the Discovery Provider.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;

    // Primitive: a message exists only because something happened at least once.
    @Schema(description = "How many times this problem occurred during the run. At least 1.", minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long occurrences;

    @Schema(description = "When this problem was first seen during the run.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime firstSeenAt;

    @Schema(description = "When this problem was last seen during the run; equal to firstSeenAt if it happened "
            + "once.", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastSeenAt;
}
