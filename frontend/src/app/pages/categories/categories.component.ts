import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategoryService } from '../../services/category.service';
import { Category, CategoryRequest } from '../../models/category.model';
import { describeError } from '../../utils/http-error';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  loading = true;
  errorMessage = '';

  showForm = false;
  editingId: number | null = null;
  form: FormGroup;

  constructor(private fb: FormBuilder, private categoryService: CategoryService) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      colorCode: ['#4F46E5'],
      icon: ['']
    });
  }

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loading = true;
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = describeError(err, 'Failed to load categories');
        this.loading = false;
      }
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ colorCode: '#4F46E5' });
    this.showForm = true;
  }

  openEditForm(category: Category): void {
    this.editingId = category.id;
    this.form.setValue({
      name: category.name,
      description: category.description || '',
      colorCode: category.colorCode || '#4F46E5',
      icon: category.icon || ''
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
    const request: CategoryRequest = this.form.value;
    const obs = this.editingId
      ? this.categoryService.update(this.editingId, request)
      : this.categoryService.create(request);

    obs.subscribe({
      next: () => {
        this.showForm = false;
        this.editingId = null;
        this.loadCategories();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to save category.';
      }
    });
  }

  deleteCategory(category: Category): void {
    if (category.defaultCategory) {
      return;
    }
    if (!confirm(`Delete category "${category.name}"?`)) {
      return;
    }
    this.categoryService.delete(category.id).subscribe({
      next: () => this.loadCategories(),
      error: (err) => (this.errorMessage = err?.error?.message || 'Failed to delete category.')
    });
  }
}
