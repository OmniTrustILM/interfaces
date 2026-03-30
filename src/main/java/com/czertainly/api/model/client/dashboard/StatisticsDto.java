package com.czertainly.api.model.client.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

import lombok.Data;

@Data
public class StatisticsDto {
    // Total Count of the objects. This can be used by the FE for percentage calculation
    @Schema(description = "Number Certificates available")
    private Long totalCertificates;
	@Schema(description = "Number of Groups")
    private Long totalGroups;
	@Schema(description = "Number of Discoveries triggered")
    private Long totalDiscoveries;
	@Schema(description = "Number of Connectors added")
    private Long totalConnectors;
	@Schema(description = "Number of RA Profiles in the platform")
    private Long totalRaProfiles;
	@Schema(description = "Number of Credentials")
    private Long totalCredentials;
	@Schema(description = "Number of Authority instances")
    private Long totalAuthorities;
	@Schema(description = "Number of Administrators")
    private Long totalAdministrators;
	@Schema(description = "Number of Clients added")
    private Long totalClients;

    // Data of the certificate count by group, and RA Profile assignment
	@Schema(description = "Map of Certificate count by Group")
    private Map<String, Long> groupStatByCertificateCount;
	@Schema(description = "Map of Certificate count by RA Profile")
    private Map<String, Long> raProfileStatByCertificateCount;

    //Certificate related statistics
	@Schema(description = "Map of Certificate count by Type")
    private Map<String, Long> certificateStatByType;
	@Schema(description = "Map of Certificate count by expiry date")
    private Map<String, Long> certificateStatByExpiry;
	@Schema(description = "Map of Certificate count by key size")
    private Map<String, Long> certificateStatByKeySize;
	@Schema(description = "Map of Certificate count by subject type")
    private Map<String, Long> certificateStatBySubjectType;
	@Schema(description = "Map of Certificate count by state")
    private Map<String, Long> certificateStatByState;
    @Schema(description = "Map of Certificate count by validationStatus")
    private Map<String, Long> certificateStatByValidationStatus;
    @Schema(description = "Map of Certificate count by compliance status")
    private Map<String, Long> certificateStatByComplianceStatus;

    // Secrets related statistics
    @Schema(description = "Number of Secrets")
    private Long totalSecrets;
    @Schema(description = "Number of Vault Instances")
    private Long totalVaultInstances;
    @Schema(description = "Number of Vault Profiles")
    private Long totalVaultProfiles;
    @Schema(description = "Map of Secret count by type")
    private Map<String, Long> secretStatByType;
    @Schema(description = "Map of Secret count by state")
    private Map<String, Long> secretStatByState;
    @Schema(description = "Map of Secret count by compliance status")
    private Map<String, Long> secretStatByComplianceStatus;
    @Schema(description = "Map of Secret count by vault profile")
    private Map<String, Long> secretStatByVaultProfile;
    @Schema(description = "Map of Secret count by group")
    private Map<String, Long> secretStatByGroup;

    //Other Entities by Status
	@Schema(description = "Map of Connector count by status")
    private Map<String, Long> connectorStatByStatus;
	@Schema(description = "Map of RA Profile count by status")
    private Map<String, Long> raProfileStatByStatus;
	@Schema(description = "Map of Administrator count by status")
    private Map<String, Long> administratorStatByStatus;
	@Schema(description = "Map of Client count by status")
    private Map<String, Long> clientStatByStatus;

}
