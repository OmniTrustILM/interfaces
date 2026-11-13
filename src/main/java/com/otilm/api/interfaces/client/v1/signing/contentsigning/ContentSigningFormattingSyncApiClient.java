package com.otilm.api.interfaces.client.v1.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
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
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Typed client for the content-signing formatting contract, implemented once over REST and once over the MQ proxy. The
 * connector is stateless, so every call carries what that operation needs.
 */
public interface ContentSigningFormattingSyncApiClient {

    ContentSigningDeclarationDto getDeclaration(ApiClientConnectorInfo connector) throws ConnectorException;

    /** The attribute schema configuring one operation. */
    List<BaseAttribute> listFormattingAttributes(ApiClientConnectorInfo connector,
            ContentSigningFormattingOperation operation) throws ConnectorException;

    ComputeDtbsResponseDto computeDtbs(ApiClientConnectorInfo connector, ComputeDtbsRequestDto request)
            throws ConnectorException;

    SignedDocumentResponseDto embedSignatureValue(ApiClientConnectorInfo connector,
            EmbedSignatureValueRequestDto request) throws ConnectorException;

    TimestampImprintResponseDto computeSignatureTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto request) throws ConnectorException;

    SignedDocumentResponseDto embedSignatureTimestamp(ApiClientConnectorInfo connector,
            EmbedTimestampRequestDto request) throws ConnectorException;

    TimestampImprintResponseDto computeArchiveTimestampImprint(ApiClientConnectorInfo connector,
            SignedDocumentRequestDto request) throws ConnectorException;

    SignedDocumentResponseDto embedArchiveTimestamp(ApiClientConnectorInfo connector, EmbedTimestampRequestDto request)
            throws ConnectorException;

    /**
     * The one operation that fetches, and the one that may answer 202. The platform asks for synchronous execution, so
     * a 200 carries the extended document; the status is preserved because a 202 body carries only the tracking handle
     * and must not be mistaken for a result.
     */
    ResponseEntity<ExtendToLevelResponseDto> extendToLevel(ApiClientConnectorInfo connector,
            ExtendToLevelRequestDto request) throws ConnectorException;

    /** An unknown handle fails as a connector error rather than returning; the transports raise on any non-2xx. */
    ExtendOperationStatusResponseDto getExtendToLevelStatus(ApiClientConnectorInfo connector,
            ExtendOperationScopedRequestDto request) throws ConnectorException;

    /**
     * Cancellation succeeds with a bodiless 204, which is the status this entity carries. The failure modes — 404 for
     * an unknown handle, 422 for an operation already terminal — arrive as a connector error instead, because both
     * transports raise on any non-2xx.
     */
    ResponseEntity<Void> cancelExtendToLevel(ApiClientConnectorInfo connector, ExtendOperationScopedRequestDto request)
            throws ConnectorException;
}
