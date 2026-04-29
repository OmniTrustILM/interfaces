package com.otilm.api.model.common.attribute.common.content.data;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Setter
@Getter
public class SecretAttributeContentData implements AttributeContentData {

    @Sensitive
    @Schema(description = "Secret attribute data")
    private String secret;

    @Schema(description = "Level of protection of the data")
    private ProtectionLevel protectionLevel;

    public SecretAttributeContentData() {
    }

    public SecretAttributeContentData(String secret) {
        this.secret = secret;
    }

    public SecretAttributeContentData(String secret, ProtectionLevel protectionLevel) {
        this.secret = secret;
        this.protectionLevel = protectionLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecretAttributeContentData)) return false;
        SecretAttributeContentData that = (SecretAttributeContentData) o;
        return Objects.equals(this.secret, that.secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("secret", secret)
                .append("protectionLevel", protectionLevel)
                .toString();
    }

    @Override
    public void validate() throws ValidationException {
        if (secret == null) throw new ValidationException("Secret is not present in secret attribute content data");
    }
}
