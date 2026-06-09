package com.otilm.api.model.core.connector;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Lightweight reference to a Connector. Carries the fields needed to render a Connector in
 * list-of-references contexts (e.g., the connectors associated with a Proxy) without pulling in
 * the full {@link ConnectorDto}, which contains a back-reference to {@link com.otilm.api.model.core.proxy.ProxyDto}
 * and would otherwise create a circular schema reference.
 */
@Setter
@Getter
@ToString(callSuper = true)
public class ConnectorSummaryDto extends NameAndUuidDto {

    @Schema(description = "URL of the Connector",
            examples = {"http://network-discovery-provider:8080"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(description = "Status of the Connector",
            examples = {"CONNECTED"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ConnectorStatus status;

    public ConnectorSummaryDto() {
        super();
    }

    public ConnectorSummaryDto(String uuid, String name, String url, ConnectorStatus status) {
        super(uuid, name);
        this.url = url;
        this.status = status;
    }
}
