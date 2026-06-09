package com.otilm.api.interfaces.connector.common.v2;

import com.otilm.api.model.common.error.ProblemDetailExtended;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

// Suppressing `Define a constant instead of duplicating this literal` rule.
// All the string literals in this class are related to metrics definition and examples, and defining constants for them would reduce readability and maintainability of the code.
@SuppressWarnings("java:S1192")
@RequestMapping("/v1/metrics")
@Tag(

        name = "Metrics API",
        description = "Connector metrics API. " +
                "Connector metrics API is exposing standard Prometheus/OpenMetrics metrics that can be scraped by observability solutions and collectors."
)
public interface MetricsController extends AuthProtectedConnectorController {

    Map<String, Object> METRICS_CONFIG = Map.of(
            "version", 1,
            "histograms", Map.of(
                    "http_server_latency_buckets_seconds", List.of(
                            0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0, 10.0
                    ),
                    "http_client_latency_buckets_seconds", List.of(
                            0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0, 10.0
                    )
            ),
            "required", List.of(
                    Map.of(
                            "name", "app_build_info",
                            "type", "gauge",
                            "unit", "1",
                            "labels", List.of("version", "commit", "runtime")
                    ),
                    Map.of(
                            "name", "process_start_time_seconds",
                            "type", "gauge",
                            "unit", "seconds",
                            "labels", List.of()
                    ),
                    Map.of(
                            "name", "process_cpu_seconds_total",
                            "type", "counter",
                            "unit", "seconds",
                            "labels", List.of()
                    ),
                    Map.of(
                            "name", "process_resident_memory_bytes",
                            "type", "gauge",
                            "unit", "bytes",
                            "labels", List.of()
                    ),
                    Map.of(
                            "name", "http_requests_total",
                            "type", "counter",
                            "unit", "requests",
                            "labels", List.of("method", "route", "status")
                    ),
                    Map.of(
                            "name", "http_request_duration_seconds",
                            "type", "histogram",
                            "unit", "seconds",
                            "labels", List.of("method", "route"),
                            "buckets_ref", "http_server_latency_buckets_seconds"
                    ),
                    Map.of(
                            "name", "http_server_in_flight_requests",
                            "type", "gauge",
                            "unit", "requests",
                            "labels", List.of("route")
                    ),
                    Map.of(
                            "name", "http_client_requests_total",
                            "type", "counter",
                            "unit", "requests",
                            "labels", List.of("method", "target", "status")
                    ),
                    Map.of(
                            "name", "http_client_request_duration_seconds",
                            "type", "histogram",
                            "unit", "seconds",
                            "labels", List.of("method", "target"),
                            "buckets_ref", "http_client_latency_buckets_seconds"
                    ),
                    Map.of(
                            "name", "connector_events_total",
                            "type", "counter",
                            "unit", "events",
                            "labels", List.of("event", "outcome")
                    )
            )
    );

    @GetMapping
    @Operation(
            summary = "Get metrics"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Metrics retrieved successfully",
                            content = {
                                    @Content(
                                            mediaType = "application/openmetrics-text; version=1.0.0; charset=utf-8",
                                            schema = @Schema(
                                                    type = "string",
                                                    description = "[OpenMetrics v1.0.0 text format](https://prometheus.io/docs/specs/om/open_metrics_spec/#text-format)"
                                            ),
                                            examples = {@ExampleObject("# TYPE http_requests_total counter\nhttp_requests_total{method=\"GET\"} 42")}
                                    ),
                                    @Content(
                                            mediaType = "text/plain",
                                            schema = @Schema(
                                                    type = "string",
                                                    description = "[Legacy Prometheus 0.0.4 text format](https://github.com/Showmax/prometheus-docs/blob/master/content/docs/instrumenting/exposition_formats.md#format-version-004)"
                                            ),
                                            examples = {@ExampleObject("# HELP http_requests_total Total requests\n# TYPE http_requests_total counter\nhttp_requests_total{method=\"GET\"} 42")}
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable. Metrics could not be retrieved",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetailExtended.class)
                            )
                    )
            })
    String getMetrics();

}
