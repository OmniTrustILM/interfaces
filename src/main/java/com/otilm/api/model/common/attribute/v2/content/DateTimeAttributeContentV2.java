package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.common.attribute.common.content.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.Objects;

@Schema(
        description = "DateTime attribute content in predefined format with timezone",
        type = "object")
public class DateTimeAttributeContentV2 extends BaseAttributeContentV2<ZonedDateTime> {

    // ISO_OFFSET_DATE_TIME	Date Time with Offset	2011-12-03T10:15:30+01:00'
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @Schema(description = "DateTime attribute value in format yyyy-MM-ddTHH:mm:ss.SSSXXX", requiredMode = Schema.RequiredMode.REQUIRED)
    private ZonedDateTime data;

    public DateTimeAttributeContentV2(ZonedDateTime data) {
        this.data = data;
    }

    public DateTimeAttributeContentV2(String reference, ZonedDateTime data) {
        super(reference, data);
        this.data = data;
    }

    public DateTimeAttributeContentV2() {
    }

    @Override
    public ZonedDateTime getData() {
        return data;
    }

    @Override
    public void setData(ZonedDateTime data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateTimeAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        DateTimeAttributeContentV2 that = (DateTimeAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
