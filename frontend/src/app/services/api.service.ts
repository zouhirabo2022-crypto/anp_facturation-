import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private baseUrl = 'http://localhost:8080/api';

    constructor(private http: HttpClient) { }

    get<T>(path: string, params?: any): Observable<T> {
        return this.http.get<T>(`${this.baseUrl}/${path}`, { params });
    }

    post<T>(path: string, body: any): Observable<T> {
        return this.http.post<T>(`${this.baseUrl}/${path}`, body);
    }

    put<T>(path: string, body: any): Observable<T> {
        return this.http.put<T>(`${this.baseUrl}/${path}`, body);
    }

    delete(path: string): Observable<any> {
        return this.http.delete(`${this.baseUrl}/${path}`);
    }

    getBlob(path: string): Observable<Blob> {
        return this.http.get(`${this.baseUrl}/${path}`, { responseType: 'blob' });
    }

    getText(path: string): Observable<string> {
        return this.http.get(`${this.baseUrl}/${path}`, { responseType: 'text' });
    }
}
