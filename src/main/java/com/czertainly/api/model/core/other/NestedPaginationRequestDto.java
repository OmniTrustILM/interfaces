package com.czertainly.api.model.core.other;

import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springdoc.core.annotations.ParameterObject;

@EqualsAndHashCode(callSuper = true)
@Data
@ParameterObject
public class NestedPaginationRequestDto extends PaginationRequestDto {

    @Schema(description = "Number of entries per page of nested items", defaultValue = "10", maximum = "1000")
    private Integer innerItemsPerPage;

    @Schema(description = "Page number of the nested items for the request", defaultValue = "1")
    private Integer innerPageNumber;
}
