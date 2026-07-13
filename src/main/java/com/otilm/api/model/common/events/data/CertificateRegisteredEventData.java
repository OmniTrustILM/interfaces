package com.otilm.api.model.common.events.data;

import com.otilm.api.model.common.attribute.v1.content.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateRegisteredEventData extends CertificateEventAuthorityData {

    @Schema(description = "Deadline by which the pre-registered certificate must be issued, in \"yyyy-MM-dd'T'HH:mm:ssXXX\" format")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime issuanceDeadline;

    // The one-time credential (pre-registration challenge) the recipient presents to complete issuance. It is
    // serialized to external notification providers, but excluded from toString and equals/hashCode so it cannot
    // leak into logs or tracing spans (both event envelopes carry this object in their toString).
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "One-time credential the recipient presents to complete issuance on the public portal")
    private String credential;
}
