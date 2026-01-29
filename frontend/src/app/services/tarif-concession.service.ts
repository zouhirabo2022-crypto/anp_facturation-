import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { TarifConcession } from '../models/tarif.model';

@Injectable({
    providedIn: 'root'
})
export class TarifConcessionService {
    constructor(private api: ApiService) { }

    getByPrestation(prestationId: number): Observable<TarifConcession[]> {
        return this.api.get<TarifConcession[]>(`tarifs-concession/prestation/${prestationId}`);
    }

    create(tarif: TarifConcession): Observable<TarifConcession> {
        return this.api.post<TarifConcession>('tarifs-concession', tarif);
    }

    update(id: number, tarif: TarifConcession): Observable<TarifConcession> {
        return this.api.put<TarifConcession>(`tarifs-concession/${id}`, tarif);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`tarifs-concession/${id}`);
    }
}
