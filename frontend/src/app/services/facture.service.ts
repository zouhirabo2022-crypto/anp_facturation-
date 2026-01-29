import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Facture } from '../models/facture.model';
import { DashboardStats } from '../models/dashboard-stats.model';
import { ApiService } from './api.service';

@Injectable({
    providedIn: 'root'
})
export class FactureService {
    constructor(private api: ApiService) { }

    getAll(): Observable<Facture[]> {
        return this.api.get<Facture[]>('factures');
    }

    getDashboardStats(): Observable<DashboardStats> {
        return this.api.get<DashboardStats>('factures/stats');
    }

    getById(id: number): Observable<Facture> {
        return this.api.get<Facture>(`factures/${id}`);
    }

    create(facture: Facture): Observable<Facture> {
        return this.api.post<Facture>('factures', facture);
    }

    delete(id: number): Observable<any> {
        return this.api.delete(`factures/${id}`);
    }

    validate(id: number): Observable<Facture> {
        return this.api.post<Facture>(`factures/${id}/validate`, {});
    }

    retransmit(id: number): Observable<Facture> {
        return this.api.post<Facture>(`factures/${id}/retransmit`, {});
    }

    markAsPaid(id: number): Observable<Facture> {
        return this.api.post<Facture>(`factures/${id}/pay`, {});
    }

    downloadPdf(id: number): Observable<Blob> {
        return this.api.getBlob(`factures/${id}/pdf`);
    }

    getPreviewPdf(id: number): Observable<Blob> {
        return this.api.getBlob(`factures/${id}/preview`);
    }

    exportCsv(): Observable<string> {
        return this.api.getText('factures/export/csv');
    }

    lookupPrice(params: any): Observable<number> {
        return this.api.get('factures/lookup-price', params);
    }
}
