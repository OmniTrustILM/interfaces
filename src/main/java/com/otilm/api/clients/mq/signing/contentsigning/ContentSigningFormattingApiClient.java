package com.otilm.api.clients.mq.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.clients.signing.contentsigning.ContentSigningDeclarationValidator;
import com.otilm.api.clients.signing.contentsigning.ContentSigningFormattingPaths;
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
import java.util.Objects;
import org.springframework.http.ResponseEntity;

/**
 * MQ-proxy implementation of the content-signing formatting client. Every call takes the proxy's own default timeout;
 * this client adds no operation-specific budget of its own.
 */
public class ContentSigningFormattingApiClient implements ContentSigningFormattingSyncApiClient {

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;
    private final ContentSigningDeclarationValidator declarationValidator;

    public ContentSigningFormattingApiClient(ProxyClient proxyClient, Validator validator) {
        this.proxyClient = Objects.requireNonNull(proxyClient, "proxyClient is required");
        this.declarationValidator = new ContentSigningDeclarationValidator(validator);
    }

    @Override
    public ContentSigningDeclarationDto getDeclaration(ApiClientConnectorInfo connector) throws ConnectorException {
        ContentSigningDeclarationDto result = proxyClient
                .sendRequest(connector, ContentSigningFormattingPaths.DECLARATION, HTTP_METHOD_GET, null,
                        ContentSigningDeclarationDto.class);
        return declarationValidator.requireValid(result, connector);
    }

    @Override
    public List<BaseAttribute> listFormattingAttributes(ApiClientConnectorInfo connector,
            ContentSigningFormattingOperation operation) throws ConnectorException {
        BaseAttribute[] result = proxyClient
                .sendRequest(connector, ContentSigningFormattingPaths.attributes(operation), HTTP_METHOD_GET, null,
                        BaseAttribute[].class);
        return new ArrayList<>(Arrays.asList(requireBody(result, "listFormattingAttributes", connector)));
    }

    @Override
    public ComputeDtbsResponseDto computeDtbs(ApiClientConnectorInfo connector, ComputeDtbsRequestDto request)
            throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_DTBS, request, ComputeDtbsResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedSignatureValue(ApiClientConnectorInfo connector,
            EmbedSignatureValueRequestDto request) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_SIGNATURE_VALUE, request,
                SignedDocumentResponseDto.class);
    }

    @Override
    public TimestampImprintResponseDto computeSignatureTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto request) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_SIGNATURE_TIMESTAMP_IMPRINT, request,
                TimestampImprintResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedSignatureTimestamp(ApiClientConnectorInfo connector,
            EmbedTimestampRequestDto request) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_SIGNATURE_TIMESTAMP, request,
                SignedDocumentResponseDto.class);
    }

    @Override
    public TimestampImprintResponseDto computeArchiveTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto request) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.COMPUTE_ARCHIVE_TIMESTAMP_IMPRINT, request,
                TimestampImprintResponseDto.class);
    }

    @Override
    public SignedDocumentResponseDto embedArchiveTimestamp(ApiClientConnectorInfo connector,
            EmbedTimestampRequestDto request) throws ConnectorException {
        return post(connector, ContentSigningFormattingOperation.EMBED_ARCHIVE_TIMESTAMP, request,
                SignedDocumentResponseDto.class);
    }

    /**
     * Calls {@code sendRequestForEntity} so a 202 carrying only the tracking handle is not returned as a synchronous
     * result with no document; 200 and 202 cannot be told apart here, so this relies on the proxy overriding that
     * default rather than collapsing both to 200. Both success statuses carry a body, so an absent one is a contract
     * violation rather than an empty result.
     */
    @Override
    public ResponseEntity<ExtendToLevelResponseDto> extendToLevel(ApiClientConnectorInfo connector,
            ExtendToLevelRequestDto request) throws ConnectorException {
        ResponseEntity<ExtendToLevelResponseDto> entity = proxyClient
                .sendRequestForEntity(connector,
                        ContentSigningFormattingPaths.operation(ContentSigningFormattingOperation.EXTEND_TO_LEVEL),
                        HTTP_METHOD_POST, request, ExtendToLevelResponseDto.class);
        requireBody(entity == null ? null : entity.getBody(),
                ContentSigningFormattingOperation.EXTEND_TO_LEVEL.getCode(), connector);
        return entity;
    }

    @Override
    public ExtendOperationStatusResponseDto getExtendToLevelStatus(ApiClientConnectorInfo connector,
            ExtendOperationScopedRequestDto request) throws ConnectorException {
        ExtendOperationStatusResponseDto result = proxyClient
                .sendRequest(connector, ContentSigningFormattingPaths.EXTEND_TO_LEVEL_STATUS, HTTP_METHOD_POST, request,
                        ExtendOperationStatusResponseDto.class);
        return requireBody(result, "getExtendToLevelStatus", connector);
    }

    /**
     * Normalizes any successful proxy status to cancellation's only contract outcome, a bodiless 204 — the default
     * {@code sendRequestForEntity} wraps the result in {@code ResponseEntity.ok}, which would answer 200 instead. A
     * non-2xx raises, since the sync contract allows cancellation no other returned outcome.
     */
    @Override
    public ResponseEntity<Void> cancelExtendToLevel(ApiClientConnectorInfo connector,
            ExtendOperationScopedRequestDto request) throws ConnectorException {
        ResponseEntity<Void> response = proxyClient
                .sendRequestForEntity(connector, ContentSigningFormattingPaths.EXTEND_TO_LEVEL_CANCEL, HTTP_METHOD_POST,
                        request, Void.class);
        if (response == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new ConnectorException("Cancellation of extendToLevel was not accepted", connector);
        }
        return ResponseEntity.noContent().build();
    }

    private <R> R post(ApiClientConnectorInfo connector, ContentSigningFormattingOperation operation, Object request,
            Class<R> responseType) throws ConnectorException {
        R result = proxyClient
                .sendRequest(connector, ContentSigningFormattingPaths.operation(operation), HTTP_METHOD_POST, request,
                        responseType);
        return requireBody(result, operation.getCode(), connector);
    }

    /** An absent body is a non-conformant response, not an empty result: every route here must answer with one. */
    private static <T> T requireBody(T result, String operation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (result == null) {
            throw new ConnectorException("Connector returned an empty body for " + operation, connector);
        }
        return result;
    }
}
