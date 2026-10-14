package com.otilm.api.model.connector.discovery.v2;

import lombok.ToString;

/**
 * Body of the discovery v2 status, stop, resume and cancel calls, which carry nothing beyond the scoped base.
 */
@ToString(callSuper = true)
public class DiscoveryRunRequestDto extends DiscoveryV2ScopedRequestDto {
}
