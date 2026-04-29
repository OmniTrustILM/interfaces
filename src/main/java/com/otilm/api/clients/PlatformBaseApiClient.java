package com.otilm.api.clients;

import com.otilm.api.exception.ConnectionServiceException;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class PlatformBaseApiClient {

    private static final Log logger = LogFactory.getLog(PlatformBaseApiClient.class);

    protected WebClient client;

    protected abstract String getServiceUrl();

    public static <T, R> R processRequest(Function<T, R> func, T request) {
        try {
            return func.apply(request);
        } catch (Exception e) {
            Throwable unwrapped = Exceptions.unwrap(e);
            logger.error(unwrapped.getMessage(), unwrapped);
            throw e;
        }
    }

    public WebClient.RequestBodyUriSpec prepareRequest(final HttpMethod method) {
        final WebClient.RequestBodySpec request = getClient(null).method(method);
        return (WebClient.RequestBodyUriSpec) request;

    }

    public WebClient getClient(final String customServiceUrl) {
        if (client == null) {
            if(customServiceUrl != null){
                client = WebClient
                        .builder()
                        .filter(ExchangeFilterFunction.ofResponseProcessor(getHttpExceptionHandler()))
                        .baseUrl(customServiceUrl)
                        .build();
            } else {
                client = WebClient
                        .builder()
                        .filter(ExchangeFilterFunction.ofResponseProcessor(PlatformBaseApiClient::handleHttpExceptions))
                        .baseUrl(getServiceUrl())
                        .build();
            }
        }
        return client;
    }

    protected Function<ClientResponse, Mono<ClientResponse>> getHttpExceptionHandler() {
        return PlatformBaseApiClient::handleHttpExceptions;
    }

    private static final ParameterizedTypeReference<List<String>> ERROR_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    static Mono<ClientResponse> handleHttpExceptions(final ClientResponse clientResponse) {
        if (HttpStatus.UNPROCESSABLE_ENTITY.equals(clientResponse.statusCode())) {
            return clientResponse.bodyToMono(ERROR_LIST_TYPE_REF).flatMap(body ->
                    Mono.error(new ValidationException(body.stream()
                                    .map(ValidationError::create)
                                    .collect(Collectors.toList())
                            )
                    )
            );
        }
        if (clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectionServiceException(body, HttpStatus.valueOf(clientResponse.statusCode().value()))));
        }

        return Mono.just(clientResponse);
    }
}