package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Client interface for communicating with connectors via message queue proxy.
 * Provides both synchronous and asynchronous methods for connector communication.
 *
 * <p>The ProxyClient sends requests to a message queue topic where a proxy service
 * forwards them to the actual connector via HTTP. Responses are received through
 * a response queue and correlated using correlation IDs.</p>
 *
 * <p>Synchronous methods block until a response is received or timeout occurs.
 * Asynchronous methods return CompletableFuture that completes when response arrives.</p>
 */
public interface ProxyClient {

    /**
     * Send a request to the connector and wait for response synchronously.
     * Uses the default configured timeout.
     *
     * @param connector    Connector configuration with URL, auth, and proxyId
     * @param path         Request path (e.g., "/v1/health")
     * @param method       HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body         Request body (can be null for GET requests)
     * @param responseType Expected response type class
     * @param <T>          Response type
     * @return Deserialized response object
     * @throws ConnectorException If request fails or times out
     */
    <T> T sendRequest(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body,
            Class<T> responseType
    ) throws ConnectorException;

    /**
     * Send a request to the connector and wait for response synchronously with custom timeout.
     *
     * @param connector    Connector configuration with URL, auth, and proxyId
     * @param path         Request path (e.g., "/v1/health")
     * @param method       HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body         Request body (can be null for GET requests)
     * @param responseType Expected response type class
     * @param timeout      Custom timeout duration
     * @param <T>          Response type
     * @return Deserialized response object
     * @throws ConnectorException If request fails or times out
     */
    <T> T sendRequest(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body,
            Class<T> responseType,
            Duration timeout
    ) throws ConnectorException;

    /**
     * Send a request to the connector and wait for response synchronously with path variables.
     *
     * @param connector     Connector configuration with URL, auth, and proxyId
     * @param path          Request path with variables (e.g., "/v1/authorities/{uuid}")
     * @param method        HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param pathVariables Map of path variable names to values
     * @param body          Request body (can be null for GET requests)
     * @param responseType  Expected response type class
     * @param <T>           Response type
     * @return Deserialized response object
     * @throws ConnectorException If request fails or times out
     */
    <T> T sendRequest(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Map<String, String> pathVariables,
            Object body,
            Class<T> responseType
    ) throws ConnectorException;

    /**
     * Send an asynchronous request to the connector.
     * Uses the default configured timeout.
     *
     * @param connector    Connector configuration with URL, auth, and proxyId
     * @param path         Request path (e.g., "/v1/health")
     * @param method       HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body         Request body (can be null for GET requests)
     * @param responseType Expected response type class
     * @param <T>          Response type
     * @return CompletableFuture that completes with the response
     */
    <T> CompletableFuture<T> sendRequestAsync(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body,
            Class<T> responseType
    );

    /**
     * Send an asynchronous request to the connector with custom timeout.
     *
     * @param connector    Connector configuration with URL, auth, and proxyId
     * @param path         Request path (e.g., "/v1/health")
     * @param method       HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body         Request body (can be null for GET requests)
     * @param responseType Expected response type class
     * @param timeout      Custom timeout duration
     * @param <T>          Response type
     * @return CompletableFuture that completes with the response
     */
    <T> CompletableFuture<T> sendRequestAsync(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body,
            Class<T> responseType,
            Duration timeout
    );

    /**
     * Send an asynchronous request with path variables and custom timeout.
     *
     * @param connector     Connector configuration with URL, auth, and proxyId
     * @param path          Request path with variables (e.g., "/v1/authorities/{uuid}")
     * @param method        HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param pathVariables Map of path variable names to values
     * @param body          Request body (can be null for GET requests)
     * @param responseType  Expected response type class
     * @param timeout       Custom timeout duration
     * @param <T>           Response type
     * @return CompletableFuture that completes with the response
     */
    <T> CompletableFuture<T> sendRequestAsync(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Map<String, String> pathVariables,
            Object body,
            Class<T> responseType,
            Duration timeout
    );

    /**
     * Send a fire-and-forget request to the connector.
     *
     * <p>This method sends the request without waiting for a response. It is useful for
     * operations that do not require a response, such as notifications or async triggers.</p>
     *
     * <p>The response, if any, will be handled by a {@link MessageTypeResponseHandler}
     * registered for the corresponding message type pattern.</p>
     *
     * @param connector Connector configuration with URL, auth, and proxyId
     * @param path      Request path (e.g., "/v1/notifications")
     * @param method    HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body      Request body (can be null for GET requests)
     */
    void sendFireAndForget(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body
    );

    /**
     * Send a fire-and-forget request with custom message type.
     *
     * <p>This method allows overriding the message type for routing purposes.
     * The message type determines which {@link MessageTypeResponseHandler} will
     * handle the response, using RabbitMQ topic exchange pattern matching.</p>
     *
     * @param connector   Connector configuration with URL, auth, and proxyId
     * @param path        Request path (e.g., "/v1/notifications")
     * @param method      HTTP method (GET, POST, PUT, DELETE, PATCH)
     * @param body        Request body (can be null for GET requests)
     * @param messageType Custom message type for handler routing (e.g., "discovery.trigger")
     */
    void sendFireAndForget(
            ApiClientConnectorInfo connector,
            String path,
            String method,
            Object body,
            String messageType
    );

}
