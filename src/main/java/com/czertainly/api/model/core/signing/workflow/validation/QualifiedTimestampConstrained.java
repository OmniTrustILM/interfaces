package com.czertainly.api.model.core.signing.workflow.validation;

import java.util.UUID;

/**
 * Marker interface for request DTOs that carry both a {@code qualifiedTimestamp} flag
 * and an optional {@code timeQualityConfigurationUuid}.
 *
 * <p>Implementing this interface allows {@link QualifiedTimestampValidator} to enforce
 * the cross-field invariant — that a qualified timestamp requires a time-quality
 * configuration — on both create and update request DTOs without duplicating validator
 * logic.</p>
 *
 * <p>Lombok's {@code @Data} generates the required getters automatically; implementors
 * only need to declare the fields and add {@code implements QualifiedTimestampConstrained}.</p>
 */
public interface QualifiedTimestampConstrained {

    Boolean getQualifiedTimestamp();

    UUID getTimeQualityConfigurationUuid();
}
