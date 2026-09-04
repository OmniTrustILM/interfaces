package com.otilm.api.model.core.connector.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.connector.FunctionGroupDto;
import com.otilm.api.model.core.proxy.ProxyDto;
import com.otilm.api.model.core.search.AttributeProjectable;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ConnectorDtoV2")
public class ConnectorDto extends NameAndUuidDto implements AttributeProjectable {

    @Schema(description = "Version of the Connector based on the implemented interfaces.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "v2")
    private ConnectorVersion version;

    @Schema(description = "URL of the Connector", examples = {"http://network-discovery-provider:8080"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Status of the Connector", examples = {"CONNECTED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorStatus status;

    @Schema(description = "List of Function Groups implemented by the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FunctionGroupDto> functionGroups = new ArrayList<>();

    // Worded to describe one connector interface rather than this field's use of them, so it reads correctly wherever
    // swagger-core puts it. ConnectorInterfaceDto's own class-level description is what keeps this prose off it.
    @Schema(description = "A connector interface implemented by the Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ConnectorInterfaceDto> interfaces = new ArrayList<>();

    @Schema(description = "Proxy for message queue routing. "
            + "When set, connector communicates via message queue proxy. "
            + "When null, connector uses direct REST communication.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProxyDto proxy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = AttributeProjectable.ATTRIBUTE_VALUES_DESCRIPTION,
            example = AttributeProjectable.ATTRIBUTE_VALUES_EXAMPLE, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<FilterFieldSource, Map<String, List<BaseAttributeContentV3<?>>>> attributeValues;
}
