package com.family.expensetracker.controller;

import com.family.expensetracker.dto.ExpenseRequest;
import com.family.expensetracker.dto.ExpenseResponse;
import com.family.expensetracker.entity.Category;
import com.family.expensetracker.entity.Expense;
import com.family.expensetracker.entity.User;
import com.family.expensetracker.exception.AccessDeniedCustomException;
import com.family.expensetracker.exception.BadRequestException;
import com.family.expensetracker.exception.ResourceNotFoundException;
import com.family.expensetracker.repository.CategoryRepository;
import com.family.expensetracker.repository.ExpenseRepository;
import com.family.expensetracker.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Manage personal family member expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUserProvider currentUserProvider;
    private final ExpenseMapper mapper;

    @GetMapping
    @Operation(summary = "List / search / filter the current user's expenses")
    public ResponseEntity<List<ExpenseResponse>> getAll(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(name = "paymentMethod", required = false) String paymentMethod
    ) {
        final User currentUser = currentUserProvider.getCurrentUser();

        Specification<Expense> spec = (root, query, cb) -> cb.equal(root.get("user").get("id"), currentUser.getId());

        if (keyword != null && !keyword.isBlank()) {
            final String like = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(cb.coalesce(root.<String>get("notes"), "")), like)
            ));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDate>get("expenseDate"), startDate));
        }
        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.<LocalDate>get("expenseDate"), endDate));
        }
        if (minAmount != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("amount"), minAmount));
        }
        if (maxAmount != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("amount"), maxAmount));
        }
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            final Expense.PaymentMethod pm = parsePaymentMethod(paymentMethod);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("paymentMethod"), pm));
        }

        List<ExpenseResponse> result = expenseRepository
                .findAll(spec, Sort.by(Sort.Direction.DESC, "expenseDate").and(Sort.by(Sort.Direction.DESC, "id")))
                .stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single expense by id (must belong to current user)")
    public ResponseEntity<ExpenseResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(mapper.toResponse(getOwnedExpenseOrThrow(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new expense for the current user")
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Expense expense = Expense.builder()
                .title(request.getTitle().trim())
                .notes(request.getNotes())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .category(category)
                .user(currentUser)
                .build();

        Expense saved = expenseRepository.saveAndFlush(expense);
        return ResponseEntity.status(201).body(mapper.toResponse(saved));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an expense (must belong to current user)")
    public ResponseEntity<ExpenseResponse> update(@PathVariable("id") Long id, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = getOwnedExpenseOrThrow(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        expense.setTitle(request.getTitle().trim());
        expense.setNotes(request.getNotes());
        expense.setAmount(request.getAmount());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setPaymentMethod(parsePaymentMethod(request.getPaymentMethod()));
        expense.setCategory(category);

        Expense updated = expenseRepository.saveAndFlush(expense);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an expense (must belong to current user)")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        Expense expense = getOwnedExpenseOrThrow(id);
        expenseRepository.delete(expense);
        return ResponseEntity.noContent().build();
    }

    private Expense getOwnedExpenseOrThrow(Long id) {
        User currentUser = currentUserProvider.getCurrentUser();
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedCustomException("You are not allowed to access another family member's expense");
        }
        return expense;
    }

    private Expense.PaymentMethod parsePaymentMethod(String value) {
        if (value == null || value.isBlank()) {
            return Expense.PaymentMethod.CASH;
        }
        try {
            return Expense.PaymentMethod.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid payment method: " + value);
        }
    }
}
