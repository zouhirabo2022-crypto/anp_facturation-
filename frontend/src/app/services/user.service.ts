import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { User } from '../models/user.model';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private endpoint = 'users';

    constructor(private api: ApiService) { }

    getAll(): Observable<User[]> {
        return this.api.get<User[]>(this.endpoint);
    }

    getByUsername(username: string): Observable<User> {
        return this.api.get<User>(`${this.endpoint}/${username}`);
    }

    create(user: User): Observable<User> {
        return this.api.post<User>(this.endpoint, user);
    }

    update(username: string, user: User): Observable<User> {
        return this.api.put<User>(`${this.endpoint}/${username}`, user);
    }

    delete(username: string): Observable<any> {
        return this.api.delete(`${this.endpoint}/${username}`);
    }

    toggleStatus(username: string): Observable<any> {
        return this.api.post(`${this.endpoint}/${username}/toggle`, {});
    }
}
