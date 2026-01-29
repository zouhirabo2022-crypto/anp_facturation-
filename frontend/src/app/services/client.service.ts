import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Client } from '../models/client.model';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class ClientService {
    constructor(private api: ApiService) { }

    getAll(): Observable<Client[]> {
        return this.api.get<Client[]>('clients');
    }

    getById(id: number): Observable<Client> {
        return this.api.get<Client>(`clients/${id}`);
    }

    create(client: Client): Observable<Client> {
        return this.api.post<Client>('clients', client);
    }

    update(id: number, client: Client): Observable<Client> {
        return this.api.put<Client>(`clients/${id}`, client);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`clients/${id}`);
    }
}
