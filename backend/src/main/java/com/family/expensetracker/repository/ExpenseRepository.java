package com.family.expensetracker.repository;

import com.family.expensetracker.entity.Expense;
import com.family.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByUser(User user);
    List<Expense> findByUserId(Long userId);
    boolean existsByCategoryId(Long categoryId);
}
