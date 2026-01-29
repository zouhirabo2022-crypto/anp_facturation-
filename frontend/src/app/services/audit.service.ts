import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface AuditLog {
    id?: number;
    action: string;
    details: string;
    timestamp: string;
    username: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuditService {
    constructor(private api: ApiService) { }

    getAll(): Observable<AuditLog[]> {
        return this.api.get<AuditLog[]>('audit');
    }
}
