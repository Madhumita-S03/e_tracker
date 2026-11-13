import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ExpenseService } from '../../services/expense.service';
import { CategoryService } from '../../services/category.service';
import { Expense, ExpenseFilter } from '../../models/expense.model';
import { Category } from '../../models/category.model';
import { describeError } from '../../utils/http-error';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  expenses: Expense[] = [];
  categories: Category[] = [];
  loading = true;
  errorMessage = '';
  filterForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private categoryService: CategoryService
  ) {
    this.filterForm = this.fb.group({
      startDate: [null],
      endDate: [null],
      categoryId: [null]
    });
  }

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (cats) => (this.categories = cats),
      error: (err) => (this.errorMessage = describeError(err, 'Failed to load categories'))
    });
    this.runReport();
  }

  runReport(): void {
    this.loading = true;
    this.errorMessage = '';
    const filter: ExpenseFilter = this.filterForm.value;
    this.expenseService.getAll(filter).subscribe({
      next: (data) => {
        this.expenses = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = describeError(err, 'Failed to load report');
        this.loading = false;
      }
    });
  }

  get totalAmount(): number {
    return this.expenses.reduce((sum, e) => sum + Number(e.amount), 0);
  }

  get byCategory(): { name: string; value: number; color: string }[] {
    const map = new Map<string, { value: number; color: string }>();
    for (const e of this.expenses) {
      const existing = map.get(e.categoryName) || { value: 0, color: e.categoryColor || '#6B7280' };
      existing.value += Number(e.amount);
      map.set(e.categoryName, existing);
    }
    return Array.from(map.entries())
      .map(([name, v]) => ({ name, value: v.value, color: v.color }))
      .sort((a, b) => b.value - a.value);
  }

  get byPaymentMethod(): { method: string; value: number }[] {
    const map = new Map<string, number>();
    for (const e of this.expenses) {
      map.set(e.paymentMethod, (map.get(e.paymentMethod) || 0) + Number(e.amount));
    }
    return Array.from(map.entries()).map(([method, value]) => ({ method, value }));
  }

  exportCsv(): void {
    const header = ['Title', 'Category', 'Date', 'Payment Method', 'Amount', 'Notes'];
    const rows = this.expenses.map((e) => [
      e.title,
      e.categoryName,
      e.expenseDate,
      e.paymentMethod,
      e.amount.toString(),
      (e.notes || '').replace(/,/g, ';')
    ]);
    const csvContent = [header, ...rows].map((r) => r.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'expense-report.csv';
    link.click();
    URL.revokeObjectURL(url);
  }
}
