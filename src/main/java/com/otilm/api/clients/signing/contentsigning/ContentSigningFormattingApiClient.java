package com.otilm.api.clients.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v1.signing.contentsigning.ContentSigningFormattingSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedSignatureValueRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedTimestampRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationScopedRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationStatusResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.TimestampImprintResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.ContentSigningDeclarationDto;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.TrustManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * WebClient (HTTP) implementation of the content-signing formatting client, bounded by the shared
 * {@link com.otilm.api.clients.ClientTuning#responseTimeout()} with no per-operation budget.
 */
public class ContentSigningFormattingApiClient extends BaseApiClient implements ContentSigningFormattingSyncApiClient {

    private final ContentSigningDeclarationValidator declarationValidator;

    public ContentSigningFormattingApiClient(WebClient webClient, TrustManager[] defaultTrustManagers,
            Validator validator) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
        this.declarationValidator = new ContentSigningDeclarationValidator(validator);
    }

    @Override
    public ContentSigningDeclarationDto getDeclaration(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        ResponseEntity<ContentSigningDeclarationDto> response = processRequest(r -> r
                .uri(connector.getUrl() + ContentSigningFormattingPaths.DECLARATION)
                .retrieve()
                .toEntity(ContentSigningDeclarationDto.class)
                .block(), request, connector);
        return declarationValidator.requireValid(response == null ? null : response.getBody(), connector);
    }

    /**
     * Decodes an array rather than using {@code toEntityList}, whose per-element tokenizer would degrade the codec's
     * {@code maxInMemorySize} into a per-element cap.
     */
    @Override
    public List<BaseAttribute> listFormattingAttributes(ApiClientConnectorInfo connector,
            ContentSigningFormattingOperation operation) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> toMutableList(requireBody(r
                .uri(connector.getUrl() + ContentSigningFormattingPaths.attributes(operation))
                .retrieve()
                .toEntity(BaseAttribute[].class), "listFormattingAttributes")), request, connector);
    }

    @Override
    public ComputeDtbsResponseDto computeDtbs(ApiClientConnectorInfo connector, ComputeDtbsRequestDto requestDto)
            throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_DTBS, requestDto, ComputeDtbsRequestDto.class,
                ComputeDtbsResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedSignatureValue(ApiClientConnectorInfo connector,
            EmbedSignatureValueRequestDto requestDto) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_SIGNATURE_VALUE, requestDto,
                EmbedSignatureValueRequestDto.class, SignedDocumentResponseDto.class);
    }

    @Override
    public TimestampImprintResponseDto computeSignatureTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto requestDto) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_SIGNATURE_TIMESTAMP_IMPRINT, requestDto,
                SignedDocumentRequestDto.class, TimestampImprintResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedSignatureTimestamp(ApiClientConnectorInfo connector,
            EmbedTimestampRequestDto requestDto) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_SIGNATURE_TIMESTAMP, requestDto,
                EmbedTimestampRequestDto.class, SignedDocumentResponseDto.class);
    }

    @Override
    public TimestampImprintResponseDto computeArchiveTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto requestDto) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_ARCHIVE_TIMESTAMP_IMPRINT, requestDto,
                SignedDocumentRequestDto.class, TimestampImprintResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedArchiveTimestamp(ApiClientConnectorInfo connector,
            EmbedTimestampRequestDto requestDto) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_ARCHIVE_TIMESTAMP, requestDto,
                EmbedTimestampRequestDto.class, SignedDocumentResponseDto.class);
    }

    /**
     * Extension may fetch and therefore uses the connector deployment's timeout without a client-side override. The
     * response status is preserved so a 202 tracking response is not mistaken for a synchronous document.
     */
    @Override
    public ResponseEntity<ExtendToLevelResponseDto> extendToLevel(ApiClientConnectorInfo connector,
            ExtendToLevelRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireEntityBody(
                r
                        .uri(connector.getUrl() + ContentSigningFormattingPaths
                                .operation(ContentSigningFormattingOperation.EXTEND_TO_LEVEL))
                        .body(Mono.just(requestDto), ExtendToLevelRequestDto.class)
                        .retrieve()
                        .toEntity(ExtendToLevelResponseDto.class),
                ContentSigningFormattingOperation.EXTEND_TO_LEVEL.getCode()), request, connector);
    }

    /** Both success statuses carry a body, so an absent one is a contract violation rather than an empty result. */
    private static <T> ResponseEntity<T> requireEntityBody(Mono<ResponseEntity<T>> mono, String context) {
        ResponseEntity<T> entity = requireResponse(mono, context);
        if (entity.getBody() == null) {
            throw new IllegalStateException("Connector returned an empty body for " + context);
        }
        return entity;
    }

    @Override
    public ExtendOperationStatusResponseDto getExtendToLevelStatus(ApiClientConnectorInfo connector,
            ExtendOperationScopedRequestDto requestDto) throws ConnectorException {
        return postTo(connector, ContentSigningFormattingPaths.EXTEND_TO_LEVEL_STATUS, "getExtendToLevelStatus",
                requestDto, ExtendOperationScopedRequestDto.class, ExtendOperationStatusResponseDto.class);
    }

    /** Cancellation answers 204 with no body, so there is nothing to decode; any non-2xx raises instead. */
    @Override
    public ResponseEntity<Void> cancelExtendToLevel(ApiClientConnectorInfo connector,
            ExtendOperationScopedRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + ContentSigningFormattingPaths.EXTEND_TO_LEVEL_CANCEL)
                .body(Mono.just(requestDto), ExtendOperationScopedRequestDto.class)
                .retrieve()
                .toBodilessEntity(), "cancelExtendToLevel"), request, connector);
    }

    private <B, R> R post(ApiClientConnectorInfo connector, ContentSigningFormattingOperation operation, B requestDto,
            Class<B> bodyType, Class<R> responseType) throws ConnectorException {
        return postTo(connector, ContentSigningFormattingPaths.operation(operation), operation.getCode(), requestDto,
                bodyType, responseType);
    }

    private <B, R> R postTo(ApiClientConnectorInfo connector, String path, String operationName, B requestDto,
            Class<B> bodyType, Class<R> responseType) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> requireBody(r
                .uri(connector.getUrl() + path)
                .body(Mono.just(requestDto), bodyType)
                .retrieve()
                .toEntity(responseType), operationName), request, connector);
    }

    /** A fresh {@link ArrayList}, not an {@link Arrays#asList} view, so a caller may sort or filter in place. */
    private static <T> List<T> toMutableList(T[] result) {
        return new ArrayList<>(Arrays.asList(result));
    }
}
