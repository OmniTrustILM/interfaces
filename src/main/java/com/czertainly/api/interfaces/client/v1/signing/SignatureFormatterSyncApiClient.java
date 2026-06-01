package com.czertainly.api.interfaces.client.v1.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatResponseRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;

import java.util.List;

public interface SignatureFormatterSyncApiClient {

    List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, FormatDtbsRequestDto requestDto) throws ConnectorException;

    FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, FormatResponseRequestDto requestDto) throws ConnectorException;

}
