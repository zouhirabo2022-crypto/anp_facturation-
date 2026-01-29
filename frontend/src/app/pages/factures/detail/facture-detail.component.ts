import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FactureService } from '../../../services/facture.service';
import { Facture } from '../../../models/facture.model';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-facture-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './facture-detail.component.html',
  styleUrl: './facture-detail.component.css'
})
export class FactureDetailComponent implements OnInit {
  facture: Facture | null = null;
  loading = true;
  error: string | null = null;
  pdfPreviewUrl: SafeResourceUrl | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private factureService: FactureService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadFacture(+id);
    } else {
      this.error = 'Identifiant de facture manquant';
      this.loading = false;
    }
  }

  loadFacture(id: number): void {
    this.loading = true;
    this.factureService.getById(id).subscribe({
      next: (data) => {
        this.facture = data;
        this.loading = false;
        // Load PDF preview if not draft (or even if draft if backend supports it)
        this.loadPdfPreview(id);
      },
      error: (err) => {
        console.error('Error loading facture', err);
        this.error = 'Impossible de charger la facture';
        this.loading = false;
      }
    });
  }

  loadPdfPreview(id: number): void {
      this.factureService.getPreviewPdf(id).subscribe({
          next: (blob) => {
              const url = window.URL.createObjectURL(blob);
              this.pdfPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
          },
          error: (err) => console.error('Error loading PDF preview', err)
      });
  }

  validate(): void {
    if (!this.facture?.id) return;
    if (confirm('Êtes-vous sûr de vouloir valider cette facture ? Elle ne pourra plus être modifiée.')) {
      this.factureService.validate(this.facture.id).subscribe({
        next: (updated) => {
          this.facture = updated;
          alert('Facture validée avec succès');
        },
        error: (err) => alert('Erreur lors de la validation')
      });
    }
  }

  pay(): void {
    if (!this.facture?.id) return;
    if (confirm('Confirmer le paiement de cette facture ?')) {
      this.factureService.markAsPaid(this.facture.id).subscribe({
        next: (updated) => {
          this.facture = updated;
          alert('Facture marquée comme payée');
        },
        error: (err) => alert('Erreur lors du paiement')
      });
    }
  }

  retransmit(): void {
      if (!this.facture?.id) return;
      if (confirm('Forcer la retransmission aux systèmes externes ?')) {
          this.factureService.retransmit(this.facture.id).subscribe({
              next: (updated) => {
                  this.facture = updated;
                  alert('Retransmission effectuée');
              },
              error: (err) => alert('Erreur lors de la retransmission')
          });
      }
  }

  downloadPdf(): void {
    if (!this.facture?.id) return;
    this.factureService.downloadPdf(this.facture.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Facture_${this.facture?.numero || this.facture?.id}.pdf`;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error downloading PDF', err)
    });
  }
  
  deleteFacture(): void {
      if (!this.facture?.id) return;
      if (confirm('Êtes-vous sûr de vouloir supprimer cette facture ?')) {
          this.factureService.delete(this.facture.id).subscribe({
              next: () => {
                  this.router.navigate(['/factures']);
              },
              error: (err) => alert('Erreur lors de la suppression')
          });
      }
  }
}
