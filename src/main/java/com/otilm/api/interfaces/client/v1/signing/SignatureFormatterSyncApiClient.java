package com.otilm.api.interfaces.client.v1.signing;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.signatures.formatter.FormatDtbsRequestDto;
import com.otilm.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.otilm.api.model.connector.signatures.formatter.FormatResponseRequestDto;
import com.otilm.api.model.connector.signatures.formatter.FormattedResponseDto;

import java.util.List;

public interface SignatureFormatterSyncApiClient {

    List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, FormatDtbsRequestDto requestDto) throws ConnectorException;

    FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, FormatResponseRequestDto requestDto) throws ConnectorException;

}
