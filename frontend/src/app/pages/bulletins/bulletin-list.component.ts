import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BulletinService } from '../../services/bulletin.service';
import { Bulletin } from '../../models/bulletin.model';

import { ClientService } from '../../services/client.service';
import { PrestationService } from '../../services/prestation.service';

@Component({
  selector: 'app-bulletin-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bulletin-list.component.html',
  styleUrls: ['./bulletin-list.component.css']
})
export class BulletinListComponent implements OnInit {
  bulletins: Bulletin[] = [];
  loading = false;
  error = '';
  successMessage = '';
  searchTerm: string = '';

  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;

  constructor(
    private bulletinService: BulletinService,
    private clientService: ClientService,
    private prestationService: PrestationService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadBulletins();
  }

  loadBulletins(): void {
    this.loading = true;
    this.error = '';
    this.successMessage = '';
    this.bulletinService.getPending().subscribe({
      next: (data) => {
        this.bulletins = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des bulletins';
        this.loading = false;
        console.error(err);
      }
    });
  }

  get filteredBulletins(): Bulletin[] {
    let result = this.bulletins;
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = this.bulletins.filter(b => 
        (b.idBulletinMetier && b.idBulletinMetier.toLowerCase().includes(term)) ||
        (b.clientNom && b.clientNom.toLowerCase().includes(term)) ||
        (b.periodeFacturation && b.periodeFacturation.toLowerCase().includes(term))
      );
    }
    return result;
  }

  get paginatedBulletins(): Bulletin[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredBulletins.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredBulletins.length / this.pageSize);
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

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.loading = true;
      this.error = '';
      this.successMessage = '';
      
      this.bulletinService.uploadCsv(file).subscribe({
        next: (report) => {
          this.loading = false;
          let msg = `Import terminé. Succès: ${report.success}, Échecs: ${report.failure}`;
          if (report.errors && report.errors.length > 0) {
            this.error = msg + '. Erreurs: ' + report.errors.join(', ');
          } else {
            this.successMessage = msg;
          }
          this.loadBulletins();
        },
        error: (err) => {
          this.loading = false;
          console.error(err);
          this.error = 'Erreur lors de l\'import CSV: ' + (err.error?.error || err.message);
        }
      });
    }
  }

  simulateImport(): void {
    // Keep existing for now or remove if deprecated
    this.error = "Fonctionnalité remplacée par l'import CSV.";
  }

  process(id: number): void {
    if (!confirm('Créer une facture pour ce bulletin ?')) return;
    
    this.loading = true;
    this.error = '';
    this.successMessage = '';

    this.bulletinService.process(id).subscribe({
      next: (facture) => {
        this.loading = false;
        this.successMessage = 'Facture créée avec succès !';
        setTimeout(() => {
          this.router.navigate(['/factures']);
        }, 1500);
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Erreur lors de la création de la facture: ' + (err.error?.message || err.message);
      }
    });
  }

  delete(id: number): void {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce bulletin ?')) return;

    this.loading = true;
    this.error = '';
    this.successMessage = '';

    this.bulletinService.delete(id).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Bulletin supprimé avec succès.';
        this.bulletins = this.bulletins.filter(b => b.id !== id);
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        this.error = 'Erreur lors de la suppression du bulletin: ' + (err.error?.message || err.message);
      }
    });
  }
}
