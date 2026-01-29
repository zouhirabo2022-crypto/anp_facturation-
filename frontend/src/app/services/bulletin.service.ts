import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Bulletin } from '../models/bulletin.model';
import { Facture } from '../models/facture.model';

@Injectable({
    providedIn: 'root'
})
export class BulletinService {
    constructor(private api: ApiService) { }

    getPending(): Observable<Bulletin[]> {
        return this.api.get<Bulletin[]>('bulletins/pending');
    }

    import(bulletin: Bulletin): Observable<Bulletin> {
        return this.api.post<Bulletin>('bulletins/import', bulletin);
    }

    process(id: number): Observable<Facture> {
        return this.api.post<Facture>(`bulletins/${id}/process`, {});
    }

    uploadCsv(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);
        return this.api.post<any>('bulletins/import/csv', formData);
    }

    delete(id: number): Observable<void> {
        return this.api.delete(`bulletins/${id}`);
    }
}
