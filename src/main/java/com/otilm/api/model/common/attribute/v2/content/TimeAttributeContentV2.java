package com.otilm.api.model.common.attribute.v2.content;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.Objects;

@Schema(
        description = "Time attribute content in predefined format",
        type = "object")
public class TimeAttributeContentV2 extends BaseAttributeContentV2<LocalTime> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @Schema(description = "Time attribute value in format HH:mm:ss", requiredMode = Schema.RequiredMode.REQUIRED, implementation = String.class)
    private LocalTime data;

    public TimeAttributeContentV2() {
    }

    public TimeAttributeContentV2(LocalTime data) {
        this.data = data;
    }

    @Override
    public LocalTime getData() {
        return data;
    }

    @Override
    public void setData(LocalTime data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        TimeAttributeContentV2 that = (TimeAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
