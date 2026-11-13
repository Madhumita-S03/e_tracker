package com.family.expensetracker.controller;

import com.family.expensetracker.dto.ExpenseResponse;
import com.family.expensetracker.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .notes(e.getNotes())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .paymentMethod(e.getPaymentMethod() == null ? "CASH" : e.getPaymentMethod().name())
                .categoryId(e.getCategory().getId())
                .categoryName(e.getCategory().getName())
                .categoryColor(e.getCategory().getColorCode())
                .userId(e.getUser().getId())
                .userFullName(e.getUser().getFullName())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
