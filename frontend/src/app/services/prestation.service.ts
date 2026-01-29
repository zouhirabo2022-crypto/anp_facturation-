import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Prestation } from '../models/prestation.model';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class PrestationService {
    constructor(private api: ApiService) { }

    getAll(): Observable<Prestation[]> {
        return this.api.get<Prestation[]>('prestations');
    }

    getById(id: number): Observable<Prestation> {
        return this.api.get<Prestation>(`prestations/${id}`);
    }

    create(prestation: Prestation): Observable<Prestation> {
        return this.api.post<Prestation>('prestations', prestation);
    }

    update(id: number, prestation: Prestation): Observable<Prestation> {
        return this.api.put<Prestation>(`prestations/${id}`, prestation);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`prestations/${id}`);
    }
}
