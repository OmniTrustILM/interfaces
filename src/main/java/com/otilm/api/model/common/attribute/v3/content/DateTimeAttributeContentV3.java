package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Schema(
        description = "DateTime attribute content in predefined format with timezone",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class DateTimeAttributeContentV3 extends BaseAttributeContentV3<ZonedDateTime> {

    // ISO_OFFSET_DATE_TIME	Date Time with Offset	2011-12-03T10:15:30+01:00'
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @Schema(description = "DateTime attribute value in format yyyy-MM-ddTHH:mm:ss.SSSXXX", requiredMode = Schema.RequiredMode.REQUIRED)
    private ZonedDateTime data;

    public DateTimeAttributeContentV3(ZonedDateTime data) {
        this.data = data;
        setContentType(AttributeContentType.DATETIME);
    }

    public DateTimeAttributeContentV3(String reference, ZonedDateTime data) {
        super(reference, data);
        this.data = data;
        setContentType(AttributeContentType.DATETIME);
    }

    public DateTimeAttributeContentV3() {
    }

    @Override
    public ZonedDateTime getData() {
        return data;
    }

    @Override
    public void setData(ZonedDateTime data) {
        this.data = data;
    }

}
