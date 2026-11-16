package com.otilm.api.testsupport;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;

/** Configurable, single-call {@link ProxyClient} test double that records synchronous MQ delegation. */
public final class RecordingProxyClient implements ProxyClient {

    private Object response;
    private ResponseEntity<?> entityResponse;
    private Exception failure;
    private Invocation invocation;

    public void respondWith(Object response) {
        this.response = response;
    }

    public void respondWithEntity(ResponseEntity<?> response) {
        this.entityResponse = response;
    }

    public void failWith(RuntimeException failure) {
        this.failure = failure;
    }

    public void failWith(ConnectorException failure) {
        this.failure = failure;
    }

    public Invocation invocation() {
        return invocation;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
            Class<T> responseType) throws ConnectorException {
        recordInvocation(connector, path, method, body, responseType, false);
        throwFailure();
        return (T) response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path, String method,
            Object body, Class<T> responseType) throws ConnectorException {
        recordInvocation(connector, path, method, body, responseType, true);
        throwFailure();
        return (ResponseEntity<T>) entityResponse;
    }

    private void recordInvocation(ApiClientConnectorInfo connector, String path, String method, Object body,
            Class<?> responseType, boolean entityResponse) {
        if (invocation != null) {
            throw new IllegalStateException("RecordingProxyClient supports one invocation per test");
        }
        invocation = new Invocation(connector, path, method, body, responseType, entityResponse);
    }

    private void throwFailure() throws ConnectorException {
        if (failure instanceof RuntimeException runtimeFailure) {
            throw runtimeFailure;
        }
        if (failure instanceof ConnectorException connectorFailure) {
            throw connectorFailure;
        }
    }

    @Override
    public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
            Class<T> responseType, Duration timeout) {
        throw unsupported();
    }

    @Override
    public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method,
            Map<String, String> pathVariables, Object body, Class<T> responseType) {
        throw unsupported();
    }

    @Override
    public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
            Object body, Class<T> responseType) {
        throw unsupported();
    }

    @Override
    public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
            Object body, Class<T> responseType, Duration timeout) {
        throw unsupported();
    }

    @Override
    public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
            Map<String, String> pathVariables, Object body, Class<T> responseType, Duration timeout) {
        throw unsupported();
    }

    @Override
    public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body) {
        throw unsupported();
    }

    @Override
    public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body,
            String messageType) {
        throw unsupported();
    }

    private static UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Unexpected ProxyClient overload");
    }

    public record Invocation(ApiClientConnectorInfo connector, String path, String method, Object body,
            Class<?> responseType, boolean entityResponse) {
    }
}
