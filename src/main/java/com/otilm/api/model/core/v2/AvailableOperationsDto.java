package com.otilm.api.model.core.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response body for the {@code GET /availableOperations} operator endpoint.
 *
 * <p>Advertises per-operation support flags for the authority + RA profile pair.
 * One {@link OperationSupport} entry is returned for each operation kind the
 * authority knows about (ISSUE, RENEW, REVOKE, REGISTER). Operations not
 * present in the list are implicitly unsupported.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "AvailableOperations",
        description = "Per-RA-profile capability advertisement. Operators query this before invoking "
                + "register/cancel/etc. to know which actions an authority supports."
)
public class AvailableOperationsDto {

    @Schema(
            description = "List of operations the authority advertises for this RA profile."
    )
    private List<OperationSupport> operations;

}
