package com.otilm.api.model.connector.discovery.v2;

import lombok.ToString;

/**
 * Body of the discovery v2 initiate call. Adds nothing to the scoped base; a type of its own so the operation has one.
 */
@ToString(callSuper = true)
public class DiscoveryInitiateRequestDto extends DiscoveryV2ScopedRequestDto {
}
