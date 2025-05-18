import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="home-container">
      <h1>Welcome to the Application!</h1>
      <p>You have successfully logged in.</p>
      <button (click)="logout()" class="btn btn-danger">Logout</button>
    </div>
  `,
  styles: [`
    .home-container {
      text-align: center;
      margin-top: 50px;
    }
    .btn-danger {
      background-color: #dc3545;
      color: white;
      padding: 10px 15px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
    }
  `]
})
export class HomeComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
