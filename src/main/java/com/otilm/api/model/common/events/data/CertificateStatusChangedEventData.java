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
public class CertificateStatusChangedEventData extends CertificateEventAuthorityData {

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

    @Schema(description = "Old status of the object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldStatus;

    @Schema(description = "New status of the object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newStatus;
}
