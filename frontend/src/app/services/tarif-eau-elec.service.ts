import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { TarifEauElectricite } from '../models/tarif.model';

@Injectable({
    providedIn: 'root'
})
export class TarifEauElectriciteService {
    constructor(private api: ApiService) { }

    getByPrestation(prestationId: number): Observable<TarifEauElectricite[]> {
        return this.api.get<TarifEauElectricite[]>(`tarifs-eau-electricite/prestation/${prestationId}`);
    }

    create(tarif: TarifEauElectricite): Observable<TarifEauElectricite> {
        return this.api.post<TarifEauElectricite>('tarifs-eau-electricite', tarif);
    }

    update(id: number, tarif: TarifEauElectricite): Observable<TarifEauElectricite> {
        return this.api.put<TarifEauElectricite>(`tarifs-eau-electricite/${id}`, tarif);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`tarifs-eau-electricite/${id}`);
    }
}
