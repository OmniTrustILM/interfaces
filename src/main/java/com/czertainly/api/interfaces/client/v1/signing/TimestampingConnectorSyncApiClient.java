package com.czertainly.api.interfaces.client.v1.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatResponseRequestDto;

import java.util.List;

public interface TimestampingConnectorSyncApiClient {

    List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, TimestampingFormatDtbsRequestDto requestDto) throws ConnectorException;

    FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, TimestampingFormatResponseRequestDto requestDto) throws ConnectorException;

}
