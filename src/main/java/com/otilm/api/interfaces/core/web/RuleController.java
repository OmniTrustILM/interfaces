package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.workflows.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/workflows")
@Tag(name = "Workflow Rules Management", description = "Workflow Rules Management API")
public interface RuleController extends AuthProtectedController {

    // CONDITIONS

    @Operation(summary = "List Conditions")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of conditions fetched")})
    @GetMapping(path = "/conditions", produces = {"application/json"})
    List<ConditionDto> listConditions(@RequestParam(required = false) Resource resource);

    @Operation(summary = "Create Condition")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Condition created")})
    @PostMapping(path = "/conditions", consumes = {"application/json"}, produces = {"application/json"})
    ConditionDto createCondition(@RequestBody ConditionRequestDto request) throws AlreadyExistException;

    @Operation(summary = "Get Condition details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Condition details retrieved"),
            @ApiResponse(responseCode = "404", description = "Condition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/conditions/{conditionUuid}", produces = {"application/json"})
    ConditionDto getCondition(@Parameter(description = "Condition UUID") @PathVariable String conditionUuid) throws NotFoundException;

    @Operation(summary = "Update Condition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Condition updated"),
            @ApiResponse(responseCode = "404", description = "Condition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/conditions/{conditionUuid}", consumes = {"application/json"}, produces = {"application/json"})
    ConditionDto updateCondition(@Parameter(description = "Condition UUID") @PathVariable String conditionUuid, @RequestBody UpdateConditionRequestDto request) throws NotFoundException;

    @Operation(summary = "Delete Condition")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Condition deleted"),
            @ApiResponse(responseCode = "404", description = "Condition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/conditions/{conditionUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCondition(@Parameter(description = "Condition UUID") @PathVariable String conditionUuid) throws NotFoundException;

    // RULES

    @Operation(summary = "List Rules")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of rules fetched")})
    @GetMapping(path = "/rules", produces = {"application/json"})
    List<RuleDto> listRules(@RequestParam(required = false) Resource resource);

    @Operation(summary = "Create Rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rule created"),
            @ApiResponse(responseCode = "404", description = "Condition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping(path = "/rules", consumes = {"application/json"}, produces = {"application/json"})
    RuleDetailDto createRule(@RequestBody RuleRequestDto request) throws NotFoundException, AlreadyExistException;

    @Operation(summary = "Get Rule details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule details retrieved"),
            @ApiResponse(responseCode = "404", description = "Rule not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping(path = "/rules/{ruleUuid}", produces = {"application/json"})
    RuleDetailDto getRule(@Parameter(description = "Rule UUID") @PathVariable String ruleUuid) throws NotFoundException;

    @Operation(summary = "Update Rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule details updated"),
            @ApiResponse(responseCode = "404", description = "Rule or condition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping(path = "/rules/{ruleUuid}", consumes = {"application/json"}, produces = {"application/json"})
    RuleDetailDto updateRule(@Parameter(description = "Rule UUID") @PathVariable String ruleUuid, @RequestBody UpdateRuleRequestDto request) throws NotFoundException;

    @Operation(summary = "Delete Rule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rule deleted"),
            @ApiResponse(responseCode = "404", description = "Rule not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping(path = "/rules/{ruleUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRule(@Parameter(description = "Rule UUID") @PathVariable String ruleUuid) throws NotFoundException;

}
