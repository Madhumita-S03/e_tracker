import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Expense, ExpenseFilter, ExpenseRequest } from '../models/expense.model';

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  private baseUrl = `${environment.apiUrl}/expenses`;

  constructor(private http: HttpClient) {}

  getAll(filter?: ExpenseFilter): Observable<Expense[]> {
    let params = new HttpParams();
    if (filter) {
      if (filter.keyword) params = params.set('keyword', filter.keyword);
      if (filter.categoryId) params = params.set('categoryId', filter.categoryId);
      if (filter.startDate) params = params.set('startDate', filter.startDate);
      if (filter.endDate) params = params.set('endDate', filter.endDate);
      if (filter.minAmount != null) params = params.set('minAmount', filter.minAmount);
      if (filter.maxAmount != null) params = params.set('maxAmount', filter.maxAmount);
      if (filter.paymentMethod) params = params.set('paymentMethod', filter.paymentMethod);
    }
    return this.http.get<Expense[]>(this.baseUrl, { params });
  }

  create(request: ExpenseRequest): Observable<Expense> {
    return this.http.post<Expense>(this.baseUrl, request);
  }

  update(id: number, request: ExpenseRequest): Observable<Expense> {
    return this.http.put<Expense>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
