package com.otilm.api.model.core.authority;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * EndEntityExtendedInfoDTO
 */
public class EndEntityExtendedInfoDto {

    private String name;
    private String value;

    public EndEntityExtendedInfoDto() { }

    public EndEntityExtendedInfoDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("value", value)
                .toString();
    }
}
