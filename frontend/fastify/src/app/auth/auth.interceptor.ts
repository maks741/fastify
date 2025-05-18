import { HttpInterceptorFn, HttpHeaders } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { DEBUG_HTTP } from '../constants/api.constants';
import { finalize, tap } from 'rxjs/operators';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Start with the original request headers or create new headers
  let headers = req.headers || new HttpHeaders();

  // Add CORS headers
  headers = headers.set('Content-Type', 'application/json');
  headers = headers.set('Accept', 'application/json');

  // Add authorization token if available
  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  // Clone the request with the updated headers
  const modifiedReq = req.clone({
    headers: headers,
    withCredentials: false // Set to true if your API requires cookies
  });

  if (DEBUG_HTTP) {
    console.log('🚀 HTTP Request:', {
      url: modifiedReq.url,
      method: modifiedReq.method,
      headers: modifiedReq.headers,
      body: modifiedReq.body
    });
  }

  return next(modifiedReq).pipe(
    tap({
      next: (event) => {
        if (DEBUG_HTTP) {
          console.log('✅ HTTP Response:', event);
        }
      },
      error: (error) => {
        if (DEBUG_HTTP) {
          console.error('❌ HTTP Error:', error);
        }
      }
    }),
    finalize(() => {
      if (DEBUG_HTTP) {
        console.log('🏁 HTTP Request completed');
      }
    })
  );
};
