import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, LoginData, SignupData } from '../models/user.model';
import { API_ENDPOINTS } from '../constants/api.constants';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUser: User | null = null;

  // Set up default headers for all requests
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }),
    withCredentials: false // Set to true if your API requires cookies
  };

  constructor(private http: HttpClient) {}

  login(loginData: LoginData): Observable<User> {
    return this.http.post<User>(API_ENDPOINTS.LOGIN, loginData, this.httpOptions);
  }

  signup(signupData: SignupData): Observable<User> {
    return this.http.post<User>(API_ENDPOINTS.SIGNUP, signupData, this.httpOptions);
  }

  setCurrentUser(user: User): void {
    this.currentUser = user;
    localStorage.setItem('token', user.token);
    localStorage.setItem('user', JSON.stringify(user));
  }

  logout(): void {
    this.currentUser = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
