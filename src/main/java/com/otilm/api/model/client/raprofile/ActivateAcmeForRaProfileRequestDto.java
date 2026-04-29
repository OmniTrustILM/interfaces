package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ActivateAcmeForRaProfileRequestDto {
    @Schema(description = "List of Attributes to issue Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> issueCertificateAttributes;

    @Schema(description = "List of Attributes to revoke Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> revokeCertificateAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("issueCertificateAttributes", issueCertificateAttributes)
                .append("revokeCertificateAttributes", revokeCertificateAttributes)
                .toString();
    }
}
