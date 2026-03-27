package com.czertainly.api.model.client.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

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

    public Long getTotalCertificates() {
        return totalCertificates;
    }

    public void setTotalCertificates(Long totalCertificates) {
        this.totalCertificates = totalCertificates;
    }

    public Long getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(Long totalGroups) {
        this.totalGroups = totalGroups;
    }

    public Long getTotalDiscoveries() {
        return totalDiscoveries;
    }

    public void setTotalDiscoveries(Long totalDiscoveries) {
        this.totalDiscoveries = totalDiscoveries;
    }

    public Long getTotalConnectors() {
        return totalConnectors;
    }

    public void setTotalConnectors(Long totalConnectors) {
        this.totalConnectors = totalConnectors;
    }

    public Long getTotalRaProfiles() {
        return totalRaProfiles;
    }

    public void setTotalRaProfiles(Long totalRaProfiles) {
        this.totalRaProfiles = totalRaProfiles;
    }

    public Long getTotalCredentials() {
        return totalCredentials;
    }

    public void setTotalCredentials(Long totalCredentials) {
        this.totalCredentials = totalCredentials;
    }

    public Long getTotalAuthorities() {
        return totalAuthorities;
    }

    public void setTotalAuthorities(Long totalAuthorities) {
        this.totalAuthorities = totalAuthorities;
    }

    public Long getTotalAdministrators() {
        return totalAdministrators;
    }

    public void setTotalAdministrators(Long totalAdministrators) {
        this.totalAdministrators = totalAdministrators;
    }

    public Long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(Long totalClients) {
        this.totalClients = totalClients;
    }

    public Map<String, Long> getGroupStatByCertificateCount() {
        return groupStatByCertificateCount;
    }

    public void setGroupStatByCertificateCount(Map<String, Long> groupStatByCertificateCount) {
        this.groupStatByCertificateCount = groupStatByCertificateCount;
    }

    public Map<String, Long> getRaProfileStatByCertificateCount() {
        return raProfileStatByCertificateCount;
    }

    public void setRaProfileStatByCertificateCount(Map<String, Long> raProfileStatByCertificateCount) {
        this.raProfileStatByCertificateCount = raProfileStatByCertificateCount;
    }

    public Map<String, Long> getCertificateStatByType() {
        return certificateStatByType;
    }

    public void setCertificateStatByType(Map<String, Long> certificateStatByType) {
        this.certificateStatByType = certificateStatByType;
    }

    public Map<String, Long> getCertificateStatByExpiry() {
        return certificateStatByExpiry;
    }

    public void setCertificateStatByExpiry(Map<String, Long> certificateStatByExpiry) {
        this.certificateStatByExpiry = certificateStatByExpiry;
    }

    public Map<String, Long> getCertificateStatByKeySize() {
        return certificateStatByKeySize;
    }

    public void setCertificateStatByKeySize(Map<String, Long> certificateStatByKeySize) {
        this.certificateStatByKeySize = certificateStatByKeySize;
    }

    public Map<String, Long> getCertificateStatBySubjectType() {
        return certificateStatBySubjectType;
    }

    public void setCertificateStatBySubjectType(Map<String, Long> certificateStatBySubjectType) {
        this.certificateStatBySubjectType = certificateStatBySubjectType;
    }

    public Map<String, Long> getConnectorStatByStatus() {
        return connectorStatByStatus;
    }

    public void setConnectorStatByStatus(Map<String, Long> connectorStatByStatus) {
        this.connectorStatByStatus = connectorStatByStatus;
    }

    public Map<String, Long> getRaProfileStatByStatus() {
        return raProfileStatByStatus;
    }

    public void setRaProfileStatByStatus(Map<String, Long> raProfileStatByStatus) {
        this.raProfileStatByStatus = raProfileStatByStatus;
    }

    public Map<String, Long> getAdministratorStatByStatus() {
        return administratorStatByStatus;
    }

    public void setAdministratorStatByStatus(Map<String, Long> administratorStatByStatus) {
        this.administratorStatByStatus = administratorStatByStatus;
    }

    public Map<String, Long> getClientStatByStatus() {
        return clientStatByStatus;
    }

    public void setClientStatByStatus(Map<String, Long> clientStatByStatus) {
        this.clientStatByStatus = clientStatByStatus;
    }

    public Map<String, Long> getCertificateStatByState() {
        return certificateStatByState;
    }

    public void setCertificateStatByState(Map<String, Long> certificateStatByState) {
        this.certificateStatByState = certificateStatByState;
    }

    public Map<String, Long> getCertificateStatByValidationStatus() {
        return certificateStatByValidationStatus;
    }

    public void setCertificateStatByValidationStatus(Map<String, Long> certificateStatByValidationStatus) {
        this.certificateStatByValidationStatus = certificateStatByValidationStatus;
    }

    public Map<String, Long> getCertificateStatByComplianceStatus() {
        return certificateStatByComplianceStatus;
    }

    public void setCertificateStatByComplianceStatus(Map<String, Long> certificateStatByComplianceStatus) {
        this.certificateStatByComplianceStatus = certificateStatByComplianceStatus;
    }

    public Long getTotalSecrets() {
        return totalSecrets;
    }

    public void setTotalSecrets(Long totalSecrets) {
        this.totalSecrets = totalSecrets;
    }

    public Long getTotalVaultInstances() {
        return totalVaultInstances;
    }

    public void setTotalVaultInstances(Long totalVaultInstances) {
        this.totalVaultInstances = totalVaultInstances;
    }

    public Long getTotalVaultProfiles() {
        return totalVaultProfiles;
    }

    public void setTotalVaultProfiles(Long totalVaultProfiles) {
        this.totalVaultProfiles = totalVaultProfiles;
    }

    public Map<String, Long> getSecretStatByType() {
        return secretStatByType;
    }

    public void setSecretStatByType(Map<String, Long> secretStatByType) {
        this.secretStatByType = secretStatByType;
    }

    public Map<String, Long> getSecretStatByState() {
        return secretStatByState;
    }

    public void setSecretStatByState(Map<String, Long> secretStatByState) {
        this.secretStatByState = secretStatByState;
    }

    public Map<String, Long> getSecretStatByComplianceStatus() {
        return secretStatByComplianceStatus;
    }

    public void setSecretStatByComplianceStatus(Map<String, Long> secretStatByComplianceStatus) {
        this.secretStatByComplianceStatus = secretStatByComplianceStatus;
    }

    public Map<String, Long> getSecretStatByVaultProfile() {
        return secretStatByVaultProfile;
    }

    public void setSecretStatByVaultProfile(Map<String, Long> secretStatByVaultProfile) {
        this.secretStatByVaultProfile = secretStatByVaultProfile;
    }

    public Map<String, Long> getSecretStatByGroup() {
        return secretStatByGroup;
    }

    public void setSecretStatByGroup(Map<String, Long> secretStatByGroup) {
        this.secretStatByGroup = secretStatByGroup;
    }

    public StatisticsDto() {
        super();
    }
}
