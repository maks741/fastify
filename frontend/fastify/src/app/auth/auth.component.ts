import { Component } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>{{ isLoginMode ? 'Login' : 'Sign Up' }}</h2>
        <form #authForm="ngForm" (ngSubmit)="onSubmit(authForm)">
          <div class="form-group" *ngIf="!isLoginMode">
            <label for="username">Username</label>
            <input
              type="text"
              id="username"
              class="form-control"
              ngModel
              name="username"
              required
            />
          </div>
          <div class="form-group">
            <label for="email">E-Mail</label>
            <input
              type="email"
              id="email"
              class="form-control"
              ngModel
              name="email"
              required
              email
            />
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <input
              type="password"
              id="password"
              class="form-control"
              ngModel
              name="password"
              required
              minlength="6"
            />
          </div>
          <div *ngIf="error" class="error-message">
            {{ error }}
          </div>
          <div class="button-group">
            <button
              type="submit"
              class="btn btn-primary"
              [disabled]="!authForm.valid || isLoading">
              {{ isLoading ? 'Loading...' : (isLoginMode ? 'Login' : 'Sign Up') }}
            </button>
            <button
              type="button"
              class="btn btn-secondary"
              (click)="onSwitchMode()">
              Switch to {{ isLoginMode ? 'Sign Up' : 'Login' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .auth-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background-color: #f5f5f5;
    }

    .auth-card {
      width: 100%;
      max-width: 400px;
      padding: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
      background-color: white;
    }

    h2 {
      text-align: center;
      margin-bottom: 20px;
    }

    .form-group {
      margin-bottom: 15px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
    }

    .form-control {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 16px;
    }

    .button-group {
      display: flex;
      justify-content: space-between;
      margin-top: 20px;
    }

    .btn {
      padding: 10px 15px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 16px;
    }

    .btn-primary {
      background-color: #007bff;
      color: white;
    }

    .btn-primary:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }

    .btn-secondary {
      background-color: #6c757d;
      color: white;
    }

    .error-message {
      color: #dc3545;
      margin-top: 10px;
      text-align: center;
    }
  `]
})
export class AuthComponent {
  isLoginMode = true;
  isLoading = false;
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onSwitchMode() {
    this.isLoginMode = !this.isLoginMode;
  }

  onSubmit(form: NgForm) {
    if (!form.valid) {
      return;
    }

    const email = form.value.email;
    const password = form.value.password;

    this.isLoading = true;
    this.error = null;

    if (this.isLoginMode) {
      this.authService.login({ email, password }).subscribe({
        next: (responseData) => {
          this.isLoading = false;
          this.authService.setCurrentUser(responseData);
          this.router.navigate(['/home']);
        },
        error: (error) => {
          this.isLoading = false;
          this.error = 'An error occurred!';
          console.error(error);
        }
      });
    } else {
      const username = form.value.username;
      this.authService.signup({ username, email, password }).subscribe({
        next: (responseData) => {
          this.isLoading = false;
          this.authService.setCurrentUser(responseData);
          this.router.navigate(['/home']);
        },
        error: (error) => {
          this.isLoading = false;
          this.error = 'An error occurred!';
          console.error(error);
        }
      });
    }

    form.reset();
  }
}
