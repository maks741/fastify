import { Component } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  isLoginMode = true;
  isLoading = false;
  error: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  onSwitchMode() {
    this.isLoginMode = !this.isLoginMode;
    this.error = null; // Clear any errors when switching modes
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
          this.error = error?.error?.message || 'Authentication failed. Please check your credentials.';
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
          this.error = error?.error?.message || 'Signup failed. Please try again.';
          console.error(error);
        }
      });
    }
  }
}
