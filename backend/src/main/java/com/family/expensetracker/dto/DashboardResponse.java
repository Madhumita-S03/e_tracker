package com.family.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    private BigDecimal totalThisMonth;
    private BigDecimal totalThisYear;
    private BigDecimal totalAllTime;
    private long expenseCountThisMonth;
    private Map<String, BigDecimal> categoryBreakdown;
    private Map<String, BigDecimal> monthlyTrend;
    private List<ExpenseResponse> recentExpenses;
}
