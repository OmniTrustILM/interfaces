package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.approval.ApprovalDetailDto;
import com.czertainly.api.model.client.approval.ApprovalResponseDto;
import com.czertainly.api.model.client.approval.ApprovalUserDto;
import com.czertainly.api.model.client.approval.UserApprovalDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.core.scheduler.PaginationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/approvals")
@Tag(name = "Approval Inventory", description = "Approval Inventory API")
public interface ApprovalController extends AuthProtectedController {

    @Operation(summary = "List of Approvals")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all approvals")})
    @GetMapping(produces = {"application/json"})
    ApprovalResponseDto listApprovals(final PaginationRequestDto paginationRequestDto);

    @Operation(summary = "List of User's Approvals")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of all approvals")})
    @GetMapping(path = "/user", produces = {"application/json"})
    ApprovalResponseDto listUserApprovals(final PaginationRequestDto paginationRequestDto,
            @Parameter(in = ParameterIn.QUERY, description = "Select if you want to list all history of approvals by user") ApprovalUserDto approvalUserDto);

    @Operation(summary = "Get Approval Detail")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Approval detail retrieved"), @ApiResponse(responseCode = "404", description = "Approval not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    ApprovalDetailDto getApproval(@Parameter(description = "Approval UUID") @PathVariable String uuid)
            throws NotFoundException;


    @Operation(summary = "Approving of the Approval")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Approval approved"), @ApiResponse(responseCode = "404", description = "Approval not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void approveApproval(@Parameter(description = "Approval UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Rejecting of the Approval")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Approval rejected"), @ApiResponse(responseCode = "404", description = "Approval not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void rejectApproval(@Parameter(description = "Approval UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Approving of Recipient of the Approval")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Approval Recipient approved"), @ApiResponse(responseCode = "404", description = "Approval not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/approveRecipient")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void approveApprovalRecipient(@Parameter(description = "Approval UUID") @PathVariable String uuid,
                                  @RequestBody UserApprovalDto userApprovalDto) throws NotFoundException;

    @Operation(summary = "Rejecting of Recipient of the Approval")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Approval Recipient rejected"), @ApiResponse(responseCode = "404", description = "Approval not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/rejectRecipient")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void rejectApprovalRecipient(@Parameter(description = "Approval UUID") @PathVariable String uuid,
                                 @RequestBody UserApprovalDto userApprovalDto) throws NotFoundException;

}
