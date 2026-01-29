import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { TarifAutorisation } from '../models/tarif.model';

@Injectable({
    providedIn: 'root'
})
export class TarifAutorisationService {
    constructor(private api: ApiService) { }

    getByPrestation(prestationId: number): Observable<TarifAutorisation[]> {
        return this.api.get<TarifAutorisation[]>(`tarifs-autorisation/prestation/${prestationId}`);
    }

    create(tarif: TarifAutorisation): Observable<TarifAutorisation> {
        return this.api.post<TarifAutorisation>('tarifs-autorisation', tarif);
    }

    update(id: number, tarif: TarifAutorisation): Observable<TarifAutorisation> {
        return this.api.put<TarifAutorisation>(`tarifs-autorisation/${id}`, tarif);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`tarifs-autorisation/${id}`);
    }
}
