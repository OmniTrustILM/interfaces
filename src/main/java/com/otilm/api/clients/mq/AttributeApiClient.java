package com.otilm.api.clients.mq;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.interfaces.client.v1.AttributeSyncApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.core.connector.FunctionGroupCode;
import org.springframework.web.util.UriUtils;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Message queue based attribute API client.
 * Uses ProxyClient to send requests via message queue instead of direct REST calls.
 *
 * <p>This client maintains the same signature as the REST-based AttributeApiClient
 * to ensure compatibility with existing code.</p>
 */
public class AttributeApiClient implements AttributeSyncApiClient {

    private static final String ATTRIBUTE_BASE_CONTEXT = "/v1/{functionGroup}/{kind}/attributes";
    private static final String ATTRIBUTE_VALIDATION_CONTEXT = ATTRIBUTE_BASE_CONTEXT + "/validate";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public AttributeApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    /**
     * List attribute definitions for a connector.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @param functionGroupCode Function group code
     * @param kind Connector kind
     * @return List of base attributes
     * @throws ConnectorException If request fails
     */
    @Override
    public List<BaseAttribute> listAttributeDefinitions(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            String kind) throws ConnectorException {

        String path = buildPath(ATTRIBUTE_BASE_CONTEXT, functionGroupCode.getCode(), kind);
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    /**
     * Validate attributes.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @param functionGroupCode Function group code
     * @param attributes Attributes to validate
     * @param functionGroupType Function group type (kind)
     * @return null on success
     * @throws ValidationException If validation fails
     * @throws ConnectorException If request fails
     */
    @Override
    public Void validateAttributes(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            List<RequestAttribute> attributes,
            String functionGroupType) throws ValidationException, ConnectorException {

        String path = buildPath(ATTRIBUTE_VALIDATION_CONTEXT, functionGroupCode.getCode(), functionGroupType);
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                attributes,
                Void.class
        );
    }

    /**
     * Execute attribute callback.
     *
     * @param connector Connector configuration (must have proxyId set)
     * @param callback Callback configuration with method and context
     * @param callbackRequest Request with path variables, query params, and body
     * @return Callback response object
     * @throws ConnectorException If request fails
     */
    @Override
    public Object attributeCallback(
            ApiClientConnectorInfo connector,
            AttributeCallback callback,
            RequestAttributeCallback callbackRequest) throws ConnectorException {

        String path = buildCallbackPath(callback, callbackRequest);
        String method = callback.getCallbackMethod();
        Object body = callbackRequest.getBody();

        return proxyClient.sendRequest(
                connector,
                path,
                method,
                body,
                Object.class
        );
    }

    // ==================== Async variants ====================

    /**
     * Async version of listAttributeDefinitions.
     */
    public CompletableFuture<List<BaseAttribute>> listAttributeDefinitionsAsync(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            String kind) {

        String path = buildPath(ATTRIBUTE_BASE_CONTEXT, functionGroupCode.getCode(), kind);
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        ).thenApply(Arrays::asList);
    }

    /**
     * Async version of validateAttributes.
     */
    public CompletableFuture<Void> validateAttributesAsync(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            List<RequestAttribute> attributes,
            String functionGroupType) {

        String path = buildPath(ATTRIBUTE_VALIDATION_CONTEXT, functionGroupCode.getCode(), functionGroupType);
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_POST,
                attributes,
                Void.class
        );
    }

    /**
     * Async version of attributeCallback.
     */
    public CompletableFuture<Object> attributeCallbackAsync(
            ApiClientConnectorInfo connector,
            AttributeCallback callback,
            RequestAttributeCallback callbackRequest) {

        String path = buildCallbackPath(callback, callbackRequest);
        String method = callback.getCallbackMethod();
        Object body = callbackRequest.getBody();

        return proxyClient.sendRequestAsync(
                connector,
                path,
                method,
                body,
                Object.class
        );
    }

    // ==================== Helper methods ====================

    /**
     * Build path by substituting path variables in template.
     */
    private String buildPath(String template, String functionGroup, String kind) {
        return template
                .replace("{functionGroup}", functionGroup)
                .replace("{kind}", kind);
    }

    /**
     * Build callback path with path variables and query parameters.
     */
    private String buildCallbackPath(AttributeCallback callback, RequestAttributeCallback callbackRequest) {
        String path = callback.getCallbackContext();

        // Substitute path variables (encoded as path segments to prevent injection of
        // reserved characters such as '/', '?', or spaces)
        if (callbackRequest.getPathVariable() != null) {
            for (Map.Entry<String, Serializable> entry : callbackRequest.getPathVariable().entrySet()) {
                String value = extractValue(entry.getValue());
                path = path.replace("{" + entry.getKey() + "}", UriUtils.encodePathSegment(value, StandardCharsets.UTF_8));
            }
        }

        // Append query parameters
        if (callbackRequest.getRequestParameter() != null) {
            String queryString = callbackRequest.getRequestParameter().entrySet().stream()
                    .filter(e -> e.getValue() != null)
                    .map(e -> encodeParam(e.getKey()) + "=" + encodeParam(extractValue(e.getValue())))
                    .collect(Collectors.joining("&"));

            if (!queryString.isEmpty()) {
                path = path + "?" + queryString;
            }
        }

        return path;
    }

    /**
     * Extract value from object, handling Map wrapper case.
     * Some attributes come wrapped in a Map with "value" key.
     */
    @SuppressWarnings("unchecked")
    private String extractValue(Object value) {
        if (value instanceof Map) {
            Object mapValue = ((Map<?, ?>) value).get("value");
            return mapValue != null ? mapValue.toString() : "";
        }
        return value != null ? value.toString() : "";
    }

    /**
     * URL encode a parameter.
     */
    private String encodeParam(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}