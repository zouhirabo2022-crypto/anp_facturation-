import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { TokenService } from './token.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const tokenService = inject(TokenService);

    if (req.headers.has('Skip-Auth')) {
        return next(req);
    }

    const accessToken = tokenService.getAccessToken();
    const basic = authService.getCredentials();

    let authReq: HttpRequest<any> = req;
    if (accessToken) {
        authReq = req.clone({
            setHeaders: { Authorization: `Bearer ${accessToken}` }
        });
    } else if (basic) {
        authReq = req.clone({
            setHeaders: { Authorization: `Basic ${basic}` }
        });
    }

    return next(authReq).pipe(
        catchError((error: any) => {
            if (error instanceof HttpErrorResponse && error.status === 401) {
                const refresh = tokenService.getRefreshToken();
                if (refresh) {
                    const refreshReq = authReq.clone({
                        url: '/api/auth/refresh',
                        method: 'POST',
                        setHeaders: { 'Skip-Auth': 'true' },
                        body: { refreshToken: refresh }
                    });
                    return next(refreshReq).pipe(
                        switchMap((resp: any) => {
                            const newAccess = resp?.accessToken;
                            if (newAccess) {
                                tokenService.setAccessToken(newAccess);
                                const retried = req.clone({
                                    setHeaders: { Authorization: `Bearer ${newAccess}` }
                                });
                                return next(retried);
                            }
                            return throwError(() => error);
                        })
                    );
                }
            }
            return throwError(() => error);
        })
    );
};
