import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardData } from '../../models/dashboard.model';
import { describeError } from '../../utils/http-error';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  data: DashboardData | null = null;
  loading = true;
  errorMessage = '';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getDashboard().subscribe({
      next: (data) => {
        this.data = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = describeError(err, 'Failed to load dashboard data');
        this.loading = false;
      }
    });
  }

  get categoryEntries(): { name: string; value: number }[] {
    if (!this.data) return [];
    return Object.entries(this.data.categoryBreakdown).map(([name, value]) => ({ name, value }));
  }

  get maxCategoryValue(): number {
    return Math.max(1, ...this.categoryEntries.map((c) => c.value));
  }

  get monthlyEntries(): { month: string; value: number }[] {
    if (!this.data) return [];
    return Object.entries(this.data.monthlyTrend).map(([month, value]) => ({ month, value }));
  }

  get maxMonthlyValue(): number {
    return Math.max(1, ...this.monthlyEntries.map((m) => m.value));
  }
}
