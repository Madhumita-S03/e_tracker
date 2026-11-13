import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { FamilyMember } from '../../models/user.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  members: FamilyMember[] = [];
  loading = true;
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.authService.getMembers().subscribe({
      next: (members) => {
        this.members = members;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not reach the backend. Make sure the Spring Boot server is running on port 8080.';
        this.loading = false;
      }
    });
  }

  selectMember(member: FamilyMember): void {
    this.router.navigate(['/login'], { queryParams: { username: member.username } });
  }
}
