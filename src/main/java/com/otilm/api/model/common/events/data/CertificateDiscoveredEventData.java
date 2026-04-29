package com.otilm.api.model.common.events.data;

import com.otilm.api.model.common.attribute.v1.content.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CertificateDiscoveredEventData extends CertificateEventData {

    @Schema(description = "Discovery UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID discoveryUuid;

    @Schema(description = "Discovery name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String discoveryName;

    @Schema(description = "UUID of user that run discovery", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID discoveryUserUuid;

    @Schema(description = "Discovery connector UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID discoveryConnectorUuid;

    @Schema(description = "Discovery connector name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String discoveryConnectorName;

    @Schema(description = "Certificate validity start date in \"yyyy-MM-dd'T'HH:mm:ssXXX\" format")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime notBefore;

    @Schema(description = "Certificate expiration date in \"yyyy-MM-dd'T'HH:mm:ssXXX\" format")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime expiresAt;
}
