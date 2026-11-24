package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.otilm.api.model.common.attribute.v1.content.ZonedDateTimeDeserializer;
import com.otilm.api.model.core.certificate.RenewalWindowSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateRenewalDueEventData extends CertificateExpiringEventData {

    @Schema(description = "Start of the renewal window in \"yyyy-MM-dd'T'HH:mm:ssXXX\" format")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime windowStart;

    @Schema(description = "End of the renewal window in \"yyyy-MM-dd'T'HH:mm:ssXXX\" format")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime windowEnd;

    @Schema(description = "URL where the authority explains the renewal window")
    private String explanationUrl;

    @Schema(description = "Origin of the renewal window")
    private RenewalWindowSource source;

    @Schema(description = "Whether the renewal was brought forward from the originally planned time")
    private boolean advanced;

}
