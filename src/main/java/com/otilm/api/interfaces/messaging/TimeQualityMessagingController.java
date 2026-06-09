package com.otilm.api.interfaces.messaging;

import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.messaging.timequality.TimeQualityConfigRequest;
import com.otilm.api.model.messaging.timequality.TimeQualityConfigSnapshot;
import com.otilm.api.model.messaging.timequality.TimeQualityResultMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Dummy controller interface that documents the RabbitMQ message contracts between Core and Time Quality Monitor.
 *
 * <p>There are no real HTTP endpoints behind these paths. The controller exists solely so the OpenAPI generator
 * can discover the messaging DTO schemas and include them in the generated OpenAPI documentation.
 * Each operation represents one RabbitMQ message flow; the direction (publisher → consumer) is stated in the
 * operation summary and is reinforced by the {@code x-transport: rabbitmq} extension.</p>
 */
@RequestMapping("/v1/internal/messaging/timeQuality")
@Tag(name = "Time Quality Messaging", description = "RabbitMQ message contracts between Core and Time Quality Monitor. " +
        "These are not HTTP endpoints — the paths exist only to anchor each message schema in the generated OpenAPI document.")
public interface TimeQualityMessagingController extends NoAuthController {

    @Operation(
            operationId = "receiveTimeQualityResult",
            summary = "Time Quality Result [RabbitMQ: Monitor → Core]",
            description = "Message published by Time Quality Monitor and consumed by Core. " +
                    "Carries the NTP check outcome — overall status, measured drift, per-server details — " +
                    "for a specific time quality configuration profile.",
            extensions = @Extension(properties = @ExtensionProperty(name = "x-transport", value = "rabbitmq"))
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Message accepted")})
    @PostMapping(path = "/result", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void receiveTimeQualityResult(@Valid @RequestBody TimeQualityResultMessage message);

    @Operation(
            operationId = "receiveTimeQualityConfigRequest",
            summary = "Time Quality Config Request [RabbitMQ: Monitor → Core]",
            description = "Message sent by Time Quality Monitor to Core requesting a fresh snapshot of all active time quality " +
                    "configurations. Core responds by publishing a TimeQualityConfigSnapshot.",
            extensions = @Extension(properties = @ExtensionProperty(name = "x-transport", value = "rabbitmq"))
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Message accepted")})
    @PostMapping(path = "/configRequest", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void receiveTimeQualityConfigRequest(@Valid @RequestBody TimeQualityConfigRequest message);

    @Operation(
            operationId = "receiveTimeQualityConfigSnapshot",
            summary = "Time Quality Config Snapshot [RabbitMQ: Core → Monitor]",
            description = "Message published by Core and consumed by Time Quality Monitor. Carries a full snapshot of all active " +
                    "NTP-based time quality configurations; always a complete replacement, never a delta.",
            extensions = @Extension(properties = @ExtensionProperty(name = "x-transport", value = "rabbitmq"))
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Message accepted")})
    @PostMapping(path = "/configSnapshot", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void receiveTimeQualityConfigSnapshot(@Valid @RequestBody TimeQualityConfigSnapshot message);
}
