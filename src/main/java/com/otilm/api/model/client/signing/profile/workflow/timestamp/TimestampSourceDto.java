package com.otilm.api.model.client.signing.profile.workflow.timestamp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Where a content-signing profile gets the timestamps for its TIMESTAMPED, LONG_TERM and ARCHIVAL levels. The reference
 * is discriminated so an external RFC 3161 source can be added without reshaping the contract. The schema it publishes
 * is {@link TimestampSourceInterface}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({@Type(value = InternalTimestampSourceDto.class, name = TimestampSourceType.Codes.INTERNAL)})
@Schema(implementation = TimestampSourceInterface.class)
public sealed interface TimestampSourceDto extends TimestampSourceInterface permits InternalTimestampSourceDto {
}
