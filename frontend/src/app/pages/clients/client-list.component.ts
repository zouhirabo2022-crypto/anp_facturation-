import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';

@Component({
    selector: 'app-client-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './client-list.component.html',
    styleUrl: './client-list.component.css'
})
export class ClientListComponent implements OnInit {
    clients: Client[] = [];
    searchTerm: string = '';
    error: string = '';
    successMessage: string = '';
    
    // Pagination
    currentPage: number = 1;
    pageSize: number = 10;

    constructor(private clientService: ClientService) { }

    ngOnInit(): void {
        this.clientService.getAll().subscribe(data => {
            this.clients = data;
        });
    }

    get filteredClients(): Client[] {
        let result = this.clients;
        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            result = this.clients.filter(c => 
                c.nom.toLowerCase().includes(term) ||
                (c.prenom && c.prenom.toLowerCase().includes(term)) ||
                (c.email && c.email.toLowerCase().includes(term)) ||
                (c.ice && c.ice.toLowerCase().includes(term)) ||
                (c.rc && c.rc.toLowerCase().includes(term))
            );
        }
        return result;
    }

    get paginatedClients(): Client[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.filteredClients.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredClients.length / this.pageSize);
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

    deleteClient(id: number | undefined): void {
        if (id && confirm('Êtes-vous sûr de vouloir supprimer ce client ?')) {
            this.error = '';
            this.successMessage = '';
            
            this.clientService.delete(id).subscribe({
                next: () => {
                    this.successMessage = 'Client supprimé avec succès';
                    this.clients = this.clients.filter(c => c.id !== id);
                },
                error: (err) => {
                    console.error('Erreur lors de la suppression', err);
                    this.error = err?.status === 403
                        ? 'Suppression refusée: droits insuffisants.'
                        : err?.status === 409
                            ? 'Suppression impossible: le client est référencé par d’autres données.'
                            : 'La suppression a échoué.';
                }
            });
        }
    }
}
