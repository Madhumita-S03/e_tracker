import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ExpenseService } from '../../services/expense.service';
import { CategoryService } from '../../services/category.service';
import { Expense, ExpenseFilter, ExpenseRequest } from '../../models/expense.model';
import { Category } from '../../models/category.model';
import { describeError } from '../../utils/http-error';

@Component({
  selector: 'app-expenses',
  templateUrl: './expenses.component.html',
  styleUrls: ['./expenses.component.css']
})
export class ExpensesComponent implements OnInit {
  expenses: Expense[] = [];
  categories: Category[] = [];
  loading = true;
  errorMessage = '';

  showForm = false;
  editingId: number | null = null;
  form: FormGroup;

  filterForm: FormGroup;

  paymentMethods = ['CASH', 'CARD', 'UPI', 'BANK_TRANSFER', 'OTHER'];

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private categoryService: CategoryService
  ) {
    this.form = this.fb.group({
      title: ['', Validators.required],
      notes: [''],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      expenseDate: ['', Validators.required],
      paymentMethod: ['CASH', Validators.required],
      categoryId: [null, Validators.required]
    });

    this.filterForm = this.fb.group({
      keyword: [''],
      categoryId: [null],
      startDate: [null],
      endDate: [null],
      minAmount: [null],
      maxAmount: [null],
      paymentMethod: [null]
    });
  }

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (cats) => (this.categories = cats),
      error: (err) => (this.errorMessage = describeError(err, 'Failed to load categories'))
    });
    this.loadExpenses();
  }

  loadExpenses(): void {
    this.loading = true;
    const filter: ExpenseFilter = this.filterForm.value;
    this.expenseService.getAll(filter).subscribe({
      next: (data) => {
        this.expenses = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = describeError(err, 'Failed to load expenses');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.loadExpenses();
  }

  resetFilters(): void {
    this.filterForm.reset();
    this.loadExpenses();
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ paymentMethod: 'CASH' });
    this.showForm = true;
  }

  openEditForm(expense: Expense): void {
    this.editingId = expense.id;
    this.form.setValue({
      title: expense.title,
      notes: expense.notes || '',
      amount: expense.amount,
      expenseDate: expense.expenseDate,
      paymentMethod: expense.paymentMethod,
      categoryId: expense.categoryId
    });
    this.showForm = true;
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.errorMessage = '';
    const request: ExpenseRequest = {
      ...this.form.value,
      amount: Number(this.form.value.amount),
      categoryId: Number(this.form.value.categoryId)
    };

    const obs = this.editingId
      ? this.expenseService.update(this.editingId, request)
      : this.expenseService.create(request);

    obs.subscribe({
      next: () => {
        this.showForm = false;
        this.editingId = null;
        this.loadExpenses();
      },
      error: (err) => {
        this.errorMessage = describeError(err, 'Failed to save expense');
      }
    });
  }

  deleteExpense(expense: Expense): void {
    if (!confirm(`Delete expense "${expense.title}"?`)) {
      return;
    }
    this.expenseService.delete(expense.id).subscribe({
      next: () => this.loadExpenses(),
      error: (err) => (this.errorMessage = describeError(err, 'Failed to delete expense'))
    });
  }
}
