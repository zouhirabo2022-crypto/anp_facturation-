import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { TarifOTDP } from '../models/tarif.model';

@Injectable({
    providedIn: 'root'
})
export class TarifOTDPService {
    constructor(private api: ApiService) { }

    getByPrestation(prestationId: number): Observable<TarifOTDP[]> {
        return this.api.get<TarifOTDP[]>(`tarifs-otdp/prestation/${prestationId}`);
    }

    create(tarif: TarifOTDP): Observable<TarifOTDP> {
        return this.api.post<TarifOTDP>('tarifs-otdp', tarif);
    }

    update(id: number, tarif: TarifOTDP): Observable<TarifOTDP> {
        return this.api.put<TarifOTDP>(`tarifs-otdp/${id}`, tarif);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`tarifs-otdp/${id}`);
    }
}
