package com.otilm.api.model.core.other;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.common.events.data.*;
import com.otilm.api.model.core.auth.Resource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Schema(enumAsRef = true)
public enum ResourceEvent implements IPlatformEnum {

    // Certificates
    CERTIFICATE_STATUS_CHANGED(Codes.CERTIFICATE_STATUS_CHANGED, "Certificate validation status changed", "Event when the certificate changes validation status with detail about the certificate", Resource.CERTIFICATE, List.of(Resource.RA_PROFILE, Resource.GROUP), CertificateStatusChangedEventData.class, false),
    CERTIFICATE_ACTION_PERFORMED(Codes.CERTIFICATE_ACTION_PERFORMED, "Certificate action performed", "Event after certificate action (e.g.: issue, renew, rekey, revoke, etc.) was completed with detail about its execution", Resource.CERTIFICATE, List.of(Resource.RA_PROFILE, Resource.GROUP), CertificateActionPerformedEventData.class, false),
    CERTIFICATE_DISCOVERED(Codes.CERTIFICATE_DISCOVERED, "Certificate discovered", "Event when the certificate has been newly discovered by some discovery", Resource.CERTIFICATE, List.of(Resource.DISCOVERY), CertificateDiscoveredEventData.class, false),
    CERTIFICATE_EXPIRING(Codes.CERTIFICATE_EXPIRING, "Certificate expiring", "Event to trigger actions associated with expiring certificates without renewal", Resource.CERTIFICATE, List.of(Resource.RA_PROFILE, Resource.GROUP), CertificateExpiringEventData.class, true),
    CERTIFICATE_NOT_COMPLIANT(Codes.CERTIFICATE_NOT_COMPLIANT, "Certificate not compliant", "Event when the certificate is evaluated as not compliant", Resource.CERTIFICATE, List.of(Resource.RA_PROFILE), CertificateNotCompliantEventData.class, false),

    // Discoveries
    DISCOVERY_FINISHED(Codes.DISCOVERY_FINISHED, "Discovery Finished", "Event when discovery has been finished.", Resource.DISCOVERY, DiscoveryFinishedEventData.class, false),

    // Approval
    APPROVAL_REQUESTED(Codes.APPROVAL_REQUESTED, "Approval requested", "Event about requesting approval on specific operation defined by current approval step", Resource.APPROVAL, ApprovalEventData.class, false),
    APPROVAL_CLOSED(Codes.APPROVAL_CLOSED, "Approval closed", "Event after approval was closed informing about the result of approval process", Resource.APPROVAL, ApprovalEventData.class, false),

    // Scheduler
    SCHEDULED_JOB_FINISHED(Codes.SCHEDULED_JOB_FINISHED, "Scheduled job finished", "Notification about scheduled job execution finished with result and detail of its execution", Resource.SCHEDULED_JOB, ScheduledJobFinishedEventData.class, false);

    private static final ResourceEvent[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final Resource resource;
    private final List<Resource> overridingResources;
    private final Class<? extends EventData> eventData;
    private final boolean monitoring;

    ResourceEvent(final String code, final String label, final String description, final Resource resource, Class<? extends EventData> eventData, boolean monitoring) {
        this(code, label, description, resource, List.of(), eventData, monitoring);
    }

    ResourceEvent(final String code, final String label, final String description, final Resource resource, final List<Resource> overridingResources, Class<? extends EventData> eventData, boolean monitoring) {
        this.code = code;
        this.label = label;
        this.description =description;
        this.resource = resource;
        this.overridingResources = overridingResources == null ? List.of() : overridingResources;
        this.eventData = eventData;
        this.monitoring = monitoring;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static ResourceEvent findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Resource event {}", code)));
    }

    public static List<ResourceEvent> listEventsByResource(Resource resource) {
        return Arrays.stream(VALUES).filter(event -> event.resource == resource || event.overridingResources.contains(resource)).toList();
    }

    public static boolean isResourceOfEvent(Resource resource) {
        return !Arrays.stream(VALUES).filter(event -> event.resource == resource).toList().isEmpty();
    }

    public static class Codes {

        public static final String CERTIFICATE_STATUS_CHANGED = "certificate_status_changed";
        public static final String CERTIFICATE_ACTION_PERFORMED = "certificate_action_performed";
        public static final String CERTIFICATE_DISCOVERED = "certificate_discovered";
        public static final String CERTIFICATE_EXPIRING = "certificate_expiring";

        public static final String DISCOVERY_FINISHED = "discovery_finished";

        public static final String APPROVAL_REQUESTED = "approval_requested";
        public static final String APPROVAL_CLOSED = "approval_closed";

        public static final String SCHEDULED_JOB_FINISHED = "scheduled_job_finished";
        public static final String CERTIFICATE_NOT_COMPLIANT = "certificate_not_compliant";

        private Codes() {

        }
    }

}
