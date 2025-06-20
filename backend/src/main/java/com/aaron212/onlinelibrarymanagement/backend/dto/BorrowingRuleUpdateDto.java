package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BorrowingRule;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO for updating borrowing rules")
public record BorrowingRuleUpdateDto(
        @JsonProperty("ruleName")
                @Schema(description = "Human-readable rule name", example = "最大可借阅图书数量")
                @NotBlank(message = "Rule name is required")
                String ruleName,
        @JsonProperty("description") @Schema(description = "Rule description", example = "单个用户同时可借阅的最大图书数量")
                String description,
        @JsonProperty("ruleValue")
                @Schema(description = "Rule value", example = "5")
                @NotBlank(message = "Rule value is required")
                String ruleValue,
        @JsonProperty("valueType")
                @Schema(description = "Value data type", example = "INTEGER")
                @NotNull(message = "Value type is required")
                BorrowingRule.ValueType valueType) {}
