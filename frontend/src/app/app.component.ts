import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';
import { map, Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'family-expense-tracker-frontend';

  showNav$: Observable<boolean> = this.authService.currentUser$.pipe(map((user) => !!user));

  constructor(private authService: AuthService) {}
}
