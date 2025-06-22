package com.aaron212.onlinelibrarymanagement.backend.dto;

import com.aaron212.onlinelibrarymanagement.backend.model.BorrowingRule;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.sql.Timestamp;

@Schema(description = "Borrowing rule data transfer object")
public record BorrowingRuleDto(
        @JsonProperty("id") @Schema(description = "Rule ID", example = "1") Long id,
        @JsonProperty("ruleKey") @Schema(description = "Unique rule key", example = "MAX_BORROW_BOOKS") String ruleKey,
        @JsonProperty("ruleName") @Schema(description = "Human-readable rule name", example = "最大可借阅图书数量")
                String ruleName,
        @JsonProperty("description") @Schema(description = "Rule description", example = "单个用户同时可借阅的最大图书数量")
                String description,
        @JsonProperty("ruleValue") @Schema(description = "Rule value", example = "5") String ruleValue,
        @JsonProperty("valueType") @Schema(description = "Value data type", example = "INTEGER")
                BorrowingRule.ValueType valueType,
        @JsonProperty("lastUpdateTime") @Schema(description = "Last update timestamp") Timestamp lastUpdateTime) {
    public static BorrowingRuleDto fromEntity(BorrowingRule rule) {
        return new BorrowingRuleDto(
                rule.getId(),
                rule.getRuleKey(),
                rule.getRuleName(),
                rule.getDescription(),
                rule.getRuleValue(),
                rule.getValueType(),
                rule.getLastUpdateTime());
    }
}
