package com.family.expensetracker.controller;

import com.family.expensetracker.dto.DashboardResponse;
import com.family.expensetracker.dto.ExpenseResponse;
import com.family.expensetracker.entity.Expense;
import com.family.expensetracker.entity.User;
import com.family.expensetracker.repository.ExpenseRepository;
import com.family.expensetracker.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Reports", description = "Aggregated spending data for the current user")
public class DashboardController {

    private final ExpenseRepository expenseRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ExpenseMapper mapper;

    @GetMapping
    @Operation(summary = "Get dashboard summary for the current user")
    public ResponseEntity<DashboardResponse> getDashboard() {
        User currentUser = currentUserProvider.getCurrentUser();
        List<Expense> expenses = expenseRepository.findByUserId(currentUser.getId());

        LocalDate now = LocalDate.now();

        BigDecimal totalThisMonth = expenses.stream()
                .filter(e -> e.getExpenseDate().getMonth() == now.getMonth() && e.getExpenseDate().getYear() == now.getYear())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalThisYear = expenses.stream()
                .filter(e -> e.getExpenseDate().getYear() == now.getYear())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAllTime = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long countThisMonth = expenses.stream()
                .filter(e -> e.getExpenseDate().getMonth() == now.getMonth() && e.getExpenseDate().getYear() == now.getYear())
                .count();

        Map<String, BigDecimal> categoryBreakdown = new LinkedHashMap<>();
        expenses.stream()
                .sorted(Comparator.comparing((Expense e) -> e.getCategory().getName()))
                .forEach(e -> categoryBreakdown.merge(e.getCategory().getName(), e.getAmount(), BigDecimal::add));

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, BigDecimal> monthlyTrend = new LinkedHashMap<>();
        expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate))
                .forEach(e -> monthlyTrend.merge(e.getExpenseDate().format(monthFormatter), e.getAmount(), BigDecimal::add));

        List<ExpenseResponse> recent = expenses.stream()
                .sorted((Expense a, Expense b) -> {
                    int c = b.getExpenseDate().compareTo(a.getExpenseDate());
                    return c != 0 ? c : b.getId().compareTo(a.getId());
                })
                .limit(5)
                .map(mapper::toResponse)
                .toList();

        DashboardResponse response = DashboardResponse.builder()
                .totalThisMonth(totalThisMonth)
                .totalThisYear(totalThisYear)
                .totalAllTime(totalAllTime)
                .expenseCountThisMonth(countThisMonth)
                .categoryBreakdown(categoryBreakdown)
                .monthlyTrend(monthlyTrend)
                .recentExpenses(recent)
                .build();

        return ResponseEntity.ok(response);
    }
}
