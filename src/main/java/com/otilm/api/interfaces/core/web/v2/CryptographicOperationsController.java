package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.cryptography.operations.SignDataRequestDto;
import com.otilm.api.model.client.cryptography.operations.SignDataResponseDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/operations/tokens/{tokenUuid}/tokenProfiles/{profileUuid}/keys/{keyUuid}/items/{itemUuid}")
@Tag(name = "Cryptographic Operations v2")
public interface CryptographicOperationsController extends AuthProtectedController {

    @Operation(summary = "List signing attributes for a v2 key item")
    @GetMapping(path = "/signature/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignatureAttributes(@PathVariable UUID tokenUuid,
                                                @PathVariable UUID profileUuid,
                                                @PathVariable UUID keyUuid,
                                                @PathVariable UUID itemUuid)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Sign data with a v2 key item")
    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    SignDataResponseDto signData(@PathVariable UUID tokenUuid,
                                 @PathVariable UUID profileUuid,
                                 @PathVariable UUID keyUuid,
                                 @PathVariable UUID itemUuid,
                                 @RequestBody @Valid SignDataRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;
}
