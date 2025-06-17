package com.aaron212.onlinelibrarymanagement.backend.controller;

import com.aaron212.onlinelibrarymanagement.backend.model.Borrow;
import com.aaron212.onlinelibrarymanagement.backend.service.FeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/fees")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    // -------------------- 计算逾期罚款 --------------------
    @PostMapping("/overdue/{borrowId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Borrow> calculateOverdueFine(@PathVariable Long borrowId) {

        Borrow result = feeService.calculateOverdueFine(borrowId);
        return result != null
                ? ResponseEntity.ok(result)
                : ResponseEntity.notFound().build();
    }

    // -------------------- 计算赔书费用 --------------------
    @PostMapping("/compensate/{borrowId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<Borrow> calculateCompensation(@PathVariable Long borrowId) {

        Borrow result = feeService.calculateBookCompensation(borrowId);
        return result != null
                ? ResponseEntity.ok(result)
                : ResponseEntity.notFound().build();
    }

    // -------------------- 查询用户待缴费用 --------------------
    @GetMapping("/user/unpaid")
    public ResponseEntity<List<Borrow>> getUnpaidFees(Principal principal) {
        // 从认证信息获取用户ID
        Long userId = 1L; // 实际需从principal获取
        List<Borrow> fees = feeService.getUnpaidFeesByUser(userId);
        return ResponseEntity.ok(fees);
    }
}
