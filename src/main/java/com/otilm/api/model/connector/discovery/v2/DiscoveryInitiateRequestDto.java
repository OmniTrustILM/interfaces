package com.otilm.api.model.connector.discovery.v2;

import lombok.ToString;

/**
 * Body for the discovery v2 /initiate call: opens a new run over the resource types and attributes carried by the
 * scoped base. A distinct type so the endpoint signature and its 422 contract hang off it.
 */
@ToString(callSuper = true)
public class DiscoveryInitiateRequestDto extends DiscoveryV2ScopedRequestDto {
}
