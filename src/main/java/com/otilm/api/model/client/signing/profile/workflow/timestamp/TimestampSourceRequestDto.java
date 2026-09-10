package com.otilm.api.model.client.signing.profile.workflow.timestamp;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Where a content-signing profile gets its timestamps, as supplied on create and update. The reference is discriminated
 * so an external RFC 3161 source can be added without reshaping the contract. The schema it publishes is
 * {@link TimestampSourceRequestInterface}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({@Type(value = InternalTimestampSourceRequestDto.class, name = TimestampSourceType.Codes.INTERNAL)})
@Schema(implementation = TimestampSourceRequestInterface.class)
public sealed interface TimestampSourceRequestDto extends TimestampSourceRequestInterface
        permits InternalTimestampSourceRequestDto {
}
