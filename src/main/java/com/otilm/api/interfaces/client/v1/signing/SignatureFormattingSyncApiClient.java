package com.otilm.api.interfaces.client.v1.signing;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormatDtbsResponseDto;
import com.otilm.api.model.connector.signatures.formatting.FormatResponseRequestDto;
import com.otilm.api.model.connector.signatures.formatting.FormattedResponseDto;

import java.util.List;

public interface SignatureFormattingSyncApiClient {

    List<BaseAttribute> listFormattingAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, FormatDtbsRequestDto requestDto) throws ConnectorException;

    FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, FormatResponseRequestDto requestDto) throws ConnectorException;

}
