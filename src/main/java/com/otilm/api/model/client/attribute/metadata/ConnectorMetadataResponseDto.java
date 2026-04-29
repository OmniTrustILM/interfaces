package com.otilm.api.model.client.attribute.metadata;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConnectorMetadataResponseDto {
    @Schema(description = "Metadata Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Metadata UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Content Type of the Metadata", requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeContentType contentType;

    @Schema(description = "Metadata Label", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @Schema(description = "Connector UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public AttributeContentType getContentType() {
        return contentType;
    }

    public void setContentType(AttributeContentType contentType) {
        this.contentType = contentType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("uuid", uuid)
                .append("contentType", contentType)
                .append("label", label)
                .append("connectorUuid", connectorUuid)
                .toString();
    }
}
