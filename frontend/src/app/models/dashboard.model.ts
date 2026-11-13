import { Expense } from './expense.model';

export interface DashboardData {
  totalThisMonth: number;
  totalThisYear: number;
  totalAllTime: number;
  expenseCountThisMonth: number;
  categoryBreakdown: { [key: string]: number };
  monthlyTrend: { [key: string]: number };
  recentExpenses: Expense[];
}
