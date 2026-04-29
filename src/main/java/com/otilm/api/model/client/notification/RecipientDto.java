package com.otilm.api.model.client.notification;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.notification.RecipientType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientDto {

    @Schema(description = "Recipient type", requiredMode = Schema.RequiredMode.REQUIRED)
    private RecipientType type;

    @Schema(description = "Recipient object UUID", examples = {"7b55ge1c-844f-11dc-a8a3-0242ac120002"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String uuid;

    @Schema(description = "Recipient object name", examples = {"Name"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;

    public RecipientDto(RecipientType type, NameAndUuidDto recipientInfo) {
        this.type = type;
        if (recipientInfo != null) {
            this.uuid = recipientInfo.getUuid();
            this.name = recipientInfo.getName();
        }
    }
}
