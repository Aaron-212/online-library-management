package com.aaron212.onlinelibrarymanagement.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "borrowing_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Rule key is required")
    private String ruleKey;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "Rule value is required")
    private String ruleValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Value type is required")
    private ValueType valueType;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp lastUpdateTime;

    public enum ValueType {
        INTEGER,    // For numbers like max books, loan days
        DECIMAL,    // For monetary values like fine per day
        STRING,     // For text values
        BOOLEAN     // For true/false rules
    }

    // Helper methods to get typed values
    public Integer getIntegerValue() {
        if (valueType == ValueType.INTEGER) {
            return Integer.valueOf(ruleValue);
        }
        throw new IllegalStateException("Rule value is not an integer: " + ruleKey);
    }

    public BigDecimal getDecimalValue() {
        if (valueType == ValueType.DECIMAL) {
            return new BigDecimal(ruleValue);
        }
        throw new IllegalStateException("Rule value is not a decimal: " + ruleKey);
    }

    public Boolean getBooleanValue() {
        if (valueType == ValueType.BOOLEAN) {
            return Boolean.valueOf(ruleValue);
        }
        throw new IllegalStateException("Rule value is not a boolean: " + ruleKey);
    }

    public String getStringValue() {
        return ruleValue;
    }
}