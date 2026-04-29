package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Schema(
        description = "Date attribute content in predefined format",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class DateAttributeContentV3 extends BaseAttributeContentV3<LocalDate> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Schema(description = "Date attribute value in format yyyy-MM-dd", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate data;

    public DateAttributeContentV3() {
        setContentType(AttributeContentType.DATE);
    }

    public DateAttributeContentV3(LocalDate data) {
        this.data = data;
        setContentType(AttributeContentType.DATE);
    }

}
