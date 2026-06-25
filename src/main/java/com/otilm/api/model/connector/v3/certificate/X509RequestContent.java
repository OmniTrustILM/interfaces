package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Typed identity content of an X.509 certificate request")
public class X509RequestContent extends CertificateRequestContent {

    @Schema(description = "Ordered subject DN components",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RdnEntry> subject;

    @Schema(description = "Subject Alternative Name entries; SAN is never duplicated in extensions",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<GeneralNameEntry> subjectAltNames;

    @Schema(description = "Requested X.509 extensions, excluding SAN",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestedExtension> extensions;

    @AssertTrue(message = "At least one of subject, subjectAltNames or extensions must be provided")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isRequestContentProvided() {
        return (subject != null && !subject.isEmpty())
                || (subjectAltNames != null && !subjectAltNames.isEmpty())
                || (extensions != null && !extensions.isEmpty());
    }
}
