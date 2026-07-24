package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.cryptography.key.KeyRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/tokens/{tokenUuid}/tokenProfiles/{profileUuid}/keys")
@Tag(name = "Cryptographic Key Management v2")
public interface CryptographicKeyController extends AuthProtectedController {

    @Operation(summary = "List supported key request types")
    @GetMapping(path = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull KeyRequestType> listSupportedKeyTypes(@PathVariable UUID tokenUuid, @PathVariable UUID profileUuid)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "List key creation attributes")
    @GetMapping(path = "/{type}/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCreateKeyAttributes(@PathVariable UUID tokenUuid, @PathVariable UUID profileUuid,
                                                @PathVariable KeyRequestType type)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Create a key")
    @PostMapping(path = "/{type}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    KeyDetailDto createKey(@PathVariable UUID tokenUuid, @PathVariable UUID profileUuid,
                           @PathVariable KeyRequestType type, @RequestBody @Valid KeyRequestDto request)
            throws AlreadyExistException, ConnectorException, AttributeException, NotFoundException;
}
