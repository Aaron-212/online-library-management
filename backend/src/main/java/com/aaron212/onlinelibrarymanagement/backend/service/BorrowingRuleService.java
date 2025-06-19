package com.aaron212.onlinelibrarymanagement.backend.service;

import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowingRuleDto;
import com.aaron212.onlinelibrarymanagement.backend.dto.BorrowingRuleUpdateDto;
import com.aaron212.onlinelibrarymanagement.backend.exception.ResourceNotFoundException;
import com.aaron212.onlinelibrarymanagement.backend.model.BorrowingRule;
import com.aaron212.onlinelibrarymanagement.backend.repository.BorrowingRuleRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowingRuleService {

    private final BorrowingRuleRepository borrowingRuleRepository;
    private static final Logger logger = LoggerFactory.getLogger(BorrowingRuleService.class);

    // Default rule keys
    public static final String MAX_BORROW_BOOKS = "MAX_BORROW_BOOKS";
    public static final String LOAN_PERIOD_DAYS = "LOAN_PERIOD_DAYS";
    public static final String RENEWAL_PERIOD_DAYS = "RENEWAL_PERIOD_DAYS";
    public static final String FINE_PER_DAY = "FINE_PER_DAY";
    public static final String MAX_RENEWAL_TIMES = "MAX_RENEWAL_TIMES";
    public static final String ALLOW_RENEWALS = "ALLOW_RENEWALS";
    public static final String ADVANCE_RESERVE_DAYS = "ADVANCE_RESERVE_DAYS";

    public BorrowingRuleService(BorrowingRuleRepository borrowingRuleRepository) {
        this.borrowingRuleRepository = borrowingRuleRepository;
    }

    @PostConstruct
    public void initializeDefaultRules() {
        logger.info("Initializing default borrowing rules...");
        
        createDefaultRuleIfNotExists(MAX_BORROW_BOOKS, "最大可借阅图书数量", 
                "单个用户同时可借阅的最大图书数量", "5", BorrowingRule.ValueType.INTEGER);
        
        createDefaultRuleIfNotExists(LOAN_PERIOD_DAYS, "借阅期限（天）", 
                "图书的标准借阅期限", "30", BorrowingRule.ValueType.INTEGER);
        
        createDefaultRuleIfNotExists(RENEWAL_PERIOD_DAYS, "续借期限（天）", 
                "图书续借时增加的天数", "15", BorrowingRule.ValueType.INTEGER);
        
        createDefaultRuleIfNotExists(FINE_PER_DAY, "每日逾期罚金（元）", 
                "图书逾期时每天的罚金", "0.50", BorrowingRule.ValueType.DECIMAL);
        
        createDefaultRuleIfNotExists(MAX_RENEWAL_TIMES, "最大续借次数", 
                "单本图书最大可续借次数", "2", BorrowingRule.ValueType.INTEGER);
        
        createDefaultRuleIfNotExists(ALLOW_RENEWALS, "允许续借", 
                "是否允许用户续借图书", "true", BorrowingRule.ValueType.BOOLEAN);
        
        createDefaultRuleIfNotExists(ADVANCE_RESERVE_DAYS, "预约提前天数", 
                "用户可以提前多少天预约即将到期归还的图书", "3", BorrowingRule.ValueType.INTEGER);
        
        logger.info("Default borrowing rules initialization completed");
    }

    private void createDefaultRuleIfNotExists(String ruleKey, String ruleName, String description, 
                                            String ruleValue, BorrowingRule.ValueType valueType) {
        if (!borrowingRuleRepository.existsByRuleKey(ruleKey)) {
            BorrowingRule rule = new BorrowingRule();
            rule.setRuleKey(ruleKey);
            rule.setRuleName(ruleName);
            rule.setDescription(description);
            rule.setRuleValue(ruleValue);
            rule.setValueType(valueType);
            borrowingRuleRepository.save(rule);
            logger.info("Created default rule: {} = {}", ruleKey, ruleValue);
        }
    }

    /**
     * Get all borrowing rules
     */
    public List<BorrowingRuleDto> getAllRules() {
        return borrowingRuleRepository.findAll()
                .stream()
                .map(BorrowingRuleDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific rule by key
     */
    public Optional<BorrowingRuleDto> getRuleByKey(String ruleKey) {
        return borrowingRuleRepository.findByRuleKey(ruleKey)
                .map(BorrowingRuleDto::fromEntity);
    }

    /**
     * Update a rule by key
     */
    public BorrowingRuleDto updateRule(String ruleKey, BorrowingRuleUpdateDto updateDto) {
        BorrowingRule rule = borrowingRuleRepository.findByRuleKey(ruleKey)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingRule", "ruleKey", ruleKey));

        // Validate the rule value based on type
        validateRuleValue(updateDto.ruleValue(), updateDto.valueType());

        rule.setRuleName(updateDto.ruleName());
        rule.setDescription(updateDto.description());
        rule.setRuleValue(updateDto.ruleValue());
        rule.setValueType(updateDto.valueType());

        BorrowingRule savedRule = borrowingRuleRepository.save(rule);
        logger.info("Updated borrowing rule: {} = {}", ruleKey, updateDto.ruleValue());
        
        return BorrowingRuleDto.fromEntity(savedRule);
    }

    /**
     * Get rule value helpers for service usage
     */
    public Integer getIntegerRule(String ruleKey) {
        return borrowingRuleRepository.findByRuleKey(ruleKey)
                .map(BorrowingRule::getIntegerValue)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingRule", "ruleKey", ruleKey));
    }

    public BigDecimal getDecimalRule(String ruleKey) {
        return borrowingRuleRepository.findByRuleKey(ruleKey)
                .map(BorrowingRule::getDecimalValue)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingRule", "ruleKey", ruleKey));
    }

    public Boolean getBooleanRule(String ruleKey) {
        return borrowingRuleRepository.findByRuleKey(ruleKey)
                .map(BorrowingRule::getBooleanValue)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingRule", "ruleKey", ruleKey));
    }

    public String getStringRule(String ruleKey) {
        return borrowingRuleRepository.findByRuleKey(ruleKey)
                .map(BorrowingRule::getStringValue)
                .orElseThrow(() -> new ResourceNotFoundException("BorrowingRule", "ruleKey", ruleKey));
    }

    /**
     * Validate rule value based on type
     */
    private void validateRuleValue(String value, BorrowingRule.ValueType valueType) {
        try {
            switch (valueType) {
                case INTEGER:
                    Integer.valueOf(value);
                    break;
                case DECIMAL:
                    new BigDecimal(value);
                    break;
                case BOOLEAN:
                    Boolean.valueOf(value);
                    break;
                case STRING:
                    // String values are always valid
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported value type: " + valueType);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value for type " + valueType + ": " + value);
        }
    }
}