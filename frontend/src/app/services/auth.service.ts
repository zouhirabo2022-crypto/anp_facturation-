import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, of, tap, throwError } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private baseUrl = '/api';
    private loggedIn = new BehaviorSubject<boolean>(false);
    private roles$ = new BehaviorSubject<string[]>([]);

    constructor(private http: HttpClient, private tokenService: TokenService) {
        this.loggedIn.next(this.hasToken());
        this.roles$.next(this.getStoredRoles());
    }

    login(username: string, password: string, provider: 'local' | 'ldap' = 'local', domain?: string): Observable<any> {
        const body: any = { username, password, provider };
        if (provider === 'ldap' && domain) body.domain = domain;

        return this.http.post(`${this.baseUrl}/auth/login`, body).pipe(
            tap((resp: any) => {
                const access = resp?.accessToken;
                const refresh = resp?.refreshToken;
                if (access) this.tokenService.setAccessToken(access);
                if (refresh) this.tokenService.setRefreshToken(refresh);
                this.updateRolesFromToken();
                this.loggedIn.next(true);
            }),
            catchError(err => {
                if (err?.status === 404 || err?.status === 501) {
                    return this.loginBasic(username, password);
                }
                return throwError(() => err);
            })
        );
    }

    private loginBasic(username: string, password: string): Observable<any> {
        const credentials = btoa(`${username}:${password}`);
        const headers = new HttpHeaders({
            Authorization: `Basic ${credentials}`
        });
        return this.http.get(`${this.baseUrl}/factures`, { headers }).pipe(
            tap(() => {
                localStorage.setItem('auth_credentials', credentials);
                this.loggedIn.next(true);
                this.roles$.next([]); // roles unknown in Basic mode
                localStorage.setItem('user_roles', JSON.stringify([]));
            })
        );
    }

    logout(): void {
        localStorage.removeItem('auth_credentials');
        this.tokenService.clearTokens();
        this.loggedIn.next(false);
        this.roles$.next([]);
        localStorage.removeItem('user_roles');
    }

    getCredentials(): string | null {
        return localStorage.getItem('auth_credentials');
    }

    isLoggedIn(): Observable<boolean> {
        return this.loggedIn.asObservable();
    }

    private hasToken(): boolean {
        return !!localStorage.getItem('auth_credentials') || !!this.tokenService.getAccessToken();
    }

    getAccessToken(): string | null {
        return this.tokenService.getAccessToken();
    }

    getRefreshToken(): string | null {
        return this.tokenService.getRefreshToken();
    }

    getRoles(): Observable<string[]> {
        return this.roles$.asObservable();
    }

    hasRole(role: string): Observable<boolean> {
        return this.roles$.pipe(map(rs => rs.includes(role)));
    }

    hasAnyRole(roles: string[]): Observable<boolean> {
        return this.roles$.pipe(map(rs => roles.some(r => rs.includes(r))));
    }

    private updateRolesFromToken(): void {
        const payload = this.tokenService.decodePayload(this.tokenService.getAccessToken());
        let roles = (payload?.roles || payload?.authorities || []) as string[];
        if (Array.isArray(roles)) {
            // Normalize roles by removing 'ROLE_' prefix if present
            roles = roles.map(r => r.startsWith('ROLE_') ? r.substring(5) : r);
            this.roles$.next(roles);
            localStorage.setItem('user_roles', JSON.stringify(roles));
        } else {
            this.roles$.next([]);
            localStorage.setItem('user_roles', JSON.stringify([]));
        }
    }

    private getStoredRoles(): string[] {
        try {
            const raw = localStorage.getItem('user_roles');
            return raw ? JSON.parse(raw) : [];
        } catch {
            return [];
        }
    }
}
