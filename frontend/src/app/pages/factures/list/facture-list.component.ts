import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FactureService } from '../../../services/facture.service';
import { Facture, StatutFacture } from '../../../models/facture.model';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

declare var bootstrap: any;

@Component({
  selector: 'app-facture-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './facture-list.component.html',
  styleUrls: ['./facture-list.component.css']
})
export class FactureListComponent implements OnInit {
  factures: Facture[] = [];
  loading = false;
  error = '';
  successMessage = '';
  searchTerm: string = '';
  
  // PDF Preview
  pdfPreviewUrl: SafeResourceUrl | null = null;

  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;

  constructor(
    private factureService: FactureService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.loadFactures();
  }

  loadFactures(): void {
    this.loading = true;
    this.factureService.getAll().subscribe({
      next: (data) => {
        this.factures = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des factures';
        this.loading = false;
        console.error(err);
      }
    });
  }

  get filteredFactures(): Facture[] {
    let result = this.factures;
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = this.factures.filter(f => 
        (f.numero && f.numero.toLowerCase().includes(term)) ||
        (f.clientNom && f.clientNom.toLowerCase().includes(term)) ||
        f.statut?.toLowerCase().includes(term)
      );
    }
    return result;
  }

  get paginatedFactures(): Facture[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredFactures.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredFactures.length / this.pageSize);
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

  validate(id: number): void {
    if (!confirm('Voulez-vous vraiment valider cette facture ?')) return;
    
    this.error = '';
    this.successMessage = '';
    
    this.factureService.validate(id).subscribe({
      next: () => {
        this.successMessage = 'Facture validée avec succès';
        this.loadFactures();
      },
      error: (err) => {
        this.error = 'Erreur lors de la validation';
        console.error(err);
      }
    });
  }

  pay(id: number): void {
    if (!confirm('Voulez-vous marquer cette facture comme payée ?')) return;

    this.factureService.markAsPaid(id).subscribe({
      next: () => {
        this.successMessage = 'Facture marquée comme payée';
        this.loadFactures();
      },
      error: (err) => {
        this.error = 'Erreur lors du paiement';
        console.error(err);
      }
    });
  }

  deleteFacture(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer cette facture ?')) return;

    this.factureService.delete(id).subscribe({
      next: () => {
        this.successMessage = 'Facture supprimée avec succès';
        this.loadFactures();
      },
      error: (err) => {
        this.error = 'Erreur lors de la suppression';
        console.error(err);
      }
    });
  }

  previewPdf(id: number): void {
    this.loading = true;
    this.factureService.getPreviewPdf(id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this.pdfPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.loading = false;
        
        // Show Modal
        const modalElement = document.getElementById('previewModal');
        if (modalElement) {
            const modal = new bootstrap.Modal(modalElement);
            modal.show();
        }
      },
      error: (err) => {
        console.error('PDF Preview failed', err);
        this.error = 'Impossible de charger l\'aperçu PDF';
        this.loading = false;
      }
    });
  }

  downloadPdf(id: number): void {
    this.factureService.downloadPdf(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `facture-${id}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('PDF Download failed', err);
        this.error = 'Impossible de télécharger le PDF';
      }
    });
  }
}
