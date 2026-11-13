export type PaymentMethod = 'CASH' | 'CARD' | 'UPI' | 'BANK_TRANSFER' | 'OTHER';

export interface Expense {
  id: number;
  title: string;
  notes?: string;
  amount: number;
  expenseDate: string;
  paymentMethod: PaymentMethod;
  categoryId: number;
  categoryName: string;
  categoryColor?: string;
  userId: number;
  userFullName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseRequest {
  title: string;
  notes?: string;
  amount: number;
  expenseDate: string;
  paymentMethod: PaymentMethod;
  categoryId: number;
}

export interface ExpenseFilter {
  keyword?: string;
  categoryId?: number | null;
  startDate?: string | null;
  endDate?: string | null;
  minAmount?: number | null;
  maxAmount?: number | null;
  paymentMethod?: string | null;
}
