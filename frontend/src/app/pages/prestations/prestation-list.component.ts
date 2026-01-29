import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PrestationService } from '../../services/prestation.service';
import { Prestation } from '../../models/prestation.model';

@Component({
    selector: 'app-prestation-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './prestation-list.component.html',
    styleUrl: './prestation-list.component.css'
})
export class PrestationListComponent implements OnInit {
    prestations: Prestation[] = [];
    searchTerm: string = '';
    error: string = '';
    successMessage: string = '';

    // Pagination
    currentPage: number = 1;
    pageSize: number = 10;

    constructor(private prestationService: PrestationService) { }

    ngOnInit(): void {
        this.prestationService.getAll().subscribe(data => {
            this.prestations = data;
        });
    }

    get filteredPrestations(): Prestation[] {
        let result = this.prestations;
        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            result = this.prestations.filter(p => 
                p.code.toLowerCase().includes(term) ||
                p.libelle.toLowerCase().includes(term) ||
                (p.unite && p.unite.toLowerCase().includes(term))
            );
        }
        return result;
    }

    get paginatedPrestations(): Prestation[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.filteredPrestations.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredPrestations.length / this.pageSize);
    }

    get pages(): number[] {
        const total = this.totalPages;
        let start = Math.max(1, this.currentPage - 2);
        let end = Math.min(total, start + 4);
        
        if (end - start < 4) {
            start = Math.max(1, end - 4);
        }
        
        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    }

    setPage(page: number): void {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
        }
    }

    onSearch(): void {
        this.currentPage = 1; // Reset to first page on search
    }

    deletePrestation(id: number | undefined): void {
        if (id && confirm('Êtes-vous sûr de vouloir supprimer cette prestation ?')) {
            this.error = '';
            this.successMessage = '';
            
            this.prestationService.delete(id).subscribe({
                next: () => {
                    this.successMessage = 'Prestation supprimée avec succès';
                    this.prestations = this.prestations.filter(p => p.id !== id);
                },
                error: (err) => {
                    console.error('Erreur lors de la suppression', err);
                    let msg = 'La suppression a échoué.';
                    if (err.error && err.error.message) {
                        msg = err.error.message;
                    } else if (err.status === 403) {
                        msg = 'Suppression refusée: droits insuffisants.';
                    } else if (err.status === 409) {
                        msg = 'Suppression impossible: la prestation est utilisée dans des factures.';
                    }
                    this.error = msg;
                }
            });
        }
    }
}
