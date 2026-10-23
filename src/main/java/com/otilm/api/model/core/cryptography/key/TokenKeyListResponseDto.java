package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.PaginationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TokenKeyListResponseDto", description = "One page of the keys found in the key storage")
public class TokenKeyListResponseDto extends PaginationResponseDto<TokenKeyDto> {
}
