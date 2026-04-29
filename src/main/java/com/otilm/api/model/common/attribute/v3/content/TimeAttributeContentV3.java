package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Schema(
        description = "Time attribute content in predefined format",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class TimeAttributeContentV3 extends BaseAttributeContentV3<LocalTime> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @Schema(description = "Time attribute value in format HH:mm:ss", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private LocalTime data;

    public TimeAttributeContentV3() {
        setContentType(AttributeContentType.TIME);
    }

    public TimeAttributeContentV3(LocalTime data) {
        this.data = data;
        setContentType(AttributeContentType.TIME);
    }

    @Override
    public LocalTime getData() {
        return data;
    }

    @Override
    public void setData(LocalTime data) {
        this.data = data;
    }
}
