package com.otilm.api.model.connector.compliance.v2;

import com.otilm.api.model.core.auth.Resource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
Contains the list of parameters required for creating a new compliance check request.
This object should only be used by the core and connector and not by the client.
When a new compliance check is to be initiated, Core should use this class to frame the
request for the connector. And the connector should accept only this object as input for
any incoming compliance check request
 */
@Getter
@Setter
@ToString
@Schema(name = "ComplianceRequestDtoV2", description = "Request for Compliance Check V2")
public class ComplianceRequestDto {

    @Schema(description = "Resource of rules to be checked", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {Resource.Codes.CERTIFICATE})
    private Resource resource;

    @Schema(description = "Type of the resource object that is sent to compliance check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"X.509"})
    private String type;

    @Schema(description = "Format of the resource object data that are sent to compliance check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"pkcs7"})
    private String format;

    @NotNull
    @Schema(description = "Base64 encoded content of resource object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

    @NotNull
    @Schema(description = "List of UUIDs of Compliance rules", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceRuleRequestDto> rules = new ArrayList<>();

    @NotNull
    @Schema(description = "List of UUIDs of Compliance groups", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UUID> groups = new ArrayList<>();

    @JsonIgnore
    @AssertTrue(message = "If type or format is specified, resource is required.")
    public boolean isValid() {
        return (type == null && format == null) || resource != null;
    }

}
