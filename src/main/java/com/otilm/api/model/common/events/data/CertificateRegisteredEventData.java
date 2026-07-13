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

    @Schema(description = "Deadline by which the completion request must be presented — a valid challenge must be "
            + "supplied before this instant. It gates the completion request, not final issuance: an approved or "
            + "otherwise asynchronous completion may finalize shortly after. Format \"yyyy-MM-dd'T'HH:mm:ssXXX\".")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private ZonedDateTime completionDeadline;

    // The one-time credential (the pre-registration challenge the operator supplied as authorizationSecret) that the
    // recipient presents to complete issuance. Serialized to external notification providers, but excluded from
    // toString and equals/hashCode so it cannot leak into logs or tracing spans (both event envelopes carry this
    // object in their toString). That exclusion covers the toString path only — downstream must not JSON-log or
    // persist the event payload; core keeps the credential out of internal notifications and event-history.
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(description = "One-time credential the recipient presents to complete issuance on the public portal")
    private String credential;
}
