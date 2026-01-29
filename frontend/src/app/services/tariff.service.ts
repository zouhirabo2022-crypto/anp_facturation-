import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class TariffService {
    constructor(private api: ApiService) { }

    reviseOTDP(year: number): Observable<any> {
        return this.api.post(`tariffs/revision/otdp?year=${year}`, {});
    }

    reviseEauElec(year: number): Observable<any> {
        return this.api.post(`tariffs/revision/eau-elec?year=${year}`, {});
    }
}
