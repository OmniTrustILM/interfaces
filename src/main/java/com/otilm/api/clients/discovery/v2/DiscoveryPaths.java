package com.otilm.api.clients.discovery.v2;

import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadDto;
import com.otilm.api.model.core.auth.Resource;

/**
 * The discovery v2 connector routes, defined once for both transports.
 *
 * <p>
 * The REST and MQ clients address the same connector contract, so a route that differs between them is a defect no test
 * in either suite would catch. Both import these constants rather than declaring their own.
 *
 * <p>
 * Streaming ({@code POST /v2/discoveryProvider/discoveries/stream}) is deliberately absent: no client implements it
 * yet. See {@link com.otilm.api.interfaces.client.v2.DiscoverySyncApiClient}.
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public final class DiscoveryPaths {

    /** Both the run-level and the per-resource attribute routes end in this segment. */
    private static final String ATTRIBUTES_SEGMENT = "/attributes";

    public static final String BASE = "/v2/discoveryProvider";
    public static final String RESOURCES = BASE + "/resources";
    public static final String ATTRIBUTES = BASE + ATTRIBUTES_SEGMENT;

    public static final String RUNS = BASE + "/discoveries";
    public static final String INITIATE = RUNS + "/initiate";
    public static final String STATUS = RUNS + "/status";
    public static final String RESULTS = RUNS + "/results";
    public static final String STOP = RUNS + "/stop";
    public static final String RESUME = RUNS + "/resume";
    public static final String CANCEL = RUNS + "/cancel";

    private DiscoveryPaths() {
    }

    /**
     * The per-resource attribute route, bound by the resource's wire code ({@code "certificates"}, {@code "keys"})
     * rather than its Java enum name.
     *
     * <p>
     * Rejects a resource discovery cannot report. Unchecked, {@code Resource.DISCOVERY} (code {@code "discoveries"})
     * would compose to {@code /v2/discoveryProvider/discoveries/attributes}, inside the run-lifecycle namespace, so a
     * caller mistake would surface as a 404 from an apparent lifecycle route rather than as the argument error it is.
     *
     * @throws IllegalArgumentException if {@code resource} is not discoverable
     */
    public static String resourceAttributes(Resource resource) {
        if (!DiscoveredItemPayloadDto.DISCOVERABLE.contains(resource)) {
            throw new IllegalArgumentException("Resource " + resource + " is not discoverable; expected one of "
                    + DiscoveredItemPayloadDto.DISCOVERABLE);
        }
        return BASE + "/" + resource.getCode() + ATTRIBUTES_SEGMENT;
    }
}
