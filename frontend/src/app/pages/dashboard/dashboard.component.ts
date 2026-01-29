import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { FactureService } from '../../services/facture.service';
import { TariffService } from '../../services/tariff.service';
import { AuthService } from '../../services/auth.service';
import { Facture } from '../../models/facture.model';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
    factures: Facture[] = [];
    totalHt = 0;
    totalTr = 0;
    totalTva = 0;
    totalTtc = 0;
    statusCounts: { [key: string]: number } = { 'BROUILLON': 0, 'VALIDEE': 0, 'PAYEE': 0 };
    statusTotals: { [key: string]: number } = { 'BROUILLON': 0, 'VALIDEE': 0, 'PAYEE': 0 };
    filteredFactures: Facture[] = [];
    filterClient = '';
    filterStatus = '';
    filterDateStart = '';
    filterDateEnd = '';
    isAdmin$: Observable<boolean> | undefined;
    errorMessage: string | null = null;

    // Modal state
    showValidationModal = false;
    pdfPreviewUrl: SafeResourceUrl | null = null;
    selectedFactureId: number | null = null;
    isLoadingPreview = false;

    constructor(
        private factureService: FactureService,
        private tariffService: TariffService,
        public authService: AuthService,
        private sanitizer: DomSanitizer,
        private router: Router
    ) {
        this.isAdmin$ = this.authService.hasRole('ADMIN_SYSTEME');
    }

    ngOnInit(): void {
        this.loadStats();
        this.loadFactures();
    }

    loadStats(): void {
        this.factureService.getDashboardStats().subscribe({
            next: (stats: any) => {
                this.totalHt = stats.totalHt;
                this.totalTr = stats.totalTr;
                this.totalTva = stats.totalTva;
                this.totalTtc = stats.totalTtc;

                this.statusCounts = {
                    'BROUILLON': stats.countBrouillon,
                    'VALIDEE': stats.countValidee,
                    'PAYEE': stats.countPayee
                };
                
                this.statusTotals = {
                    'BROUILLON': stats.amountBrouillon,
                    'VALIDEE': stats.amountValidee,
                    'PAYEE': stats.amountPayee
                };
            },
            error: (err) => console.error('Error loading stats', err)
        });
    }

    loadFactures(): void {
        this.factureService.getAll().subscribe({
            next: (data) => {
                this.factures = data;
                this.applyFilters();
                // this.calculateTotals(); // No longer needed as we use server-side stats
                this.errorMessage = null;
            },
            error: (err) => {
                console.error('Error loading factures:', err);
                this.errorMessage = 'Impossible de charger les factures. Veuillez réessayer.';
                if (err.status === 401 || err.status === 403) {
                     this.router.navigate(['/login']);
                }
            }
        });
    }

    applyFilters(): void {
        this.filteredFactures = this.factures.filter(f => {
            const matchClient = !this.filterClient || f.clientNom?.toLowerCase().includes(this.filterClient.toLowerCase());
            const matchStatus = !this.filterStatus || f.statut === this.filterStatus;

            let matchDate = true;
            if (f.date) {
                const fDate = new Date(f.date).getTime();
                if (this.filterDateStart) {
                    matchDate = matchDate && fDate >= new Date(this.filterDateStart).getTime();
                }
                if (this.filterDateEnd) {
                    matchDate = matchDate && fDate <= new Date(this.filterDateEnd).getTime();
                }
            }

            return matchClient && matchStatus && matchDate;
        });
    }

    calculateTotals(): void {
        this.totalHt = this.factures.reduce((acc, f) => acc + (f.montantHt || 0), 0);
        this.totalTr = this.factures.reduce((acc, f) => acc + (f.montantTr || 0), 0);
        this.totalTva = this.factures.reduce((acc, f) => acc + (f.montantTva || 0), 0);
        this.totalTtc = this.factures.reduce((acc, f) => acc + (f.montantTtc || 0), 0);

        // Reset and Calculate status breakdown
        this.statusCounts = { 'BROUILLON': 0, 'VALIDEE': 0, 'PAYEE': 0 };
        this.statusTotals = { 'BROUILLON': 0, 'VALIDEE': 0, 'PAYEE': 0 };

        this.factures.forEach(f => {
            const s = f.statut || 'BROUILLON';
            this.statusCounts[s] = (this.statusCounts[s] || 0) + 1;
            this.statusTotals[s] = (this.statusTotals[s] || 0) + (f.montantTtc || 0);
        });
    }

    downloadFacturePdf(id: number | undefined): void {
        if (!id) return;
        this.factureService.downloadPdf(id).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = `facture-${id}.pdf`;
                link.click();
                window.URL.revokeObjectURL(url);
            },
            error: (err) => alert('Erreur lors du téléchargement : ' + err.message)
        });
    }

    validateFacture(id: number | undefined): void {
        if (!id) return;
        this.selectedFactureId = id;
        this.showValidationModal = true;
        this.isLoadingPreview = true;
        this.pdfPreviewUrl = null;

        this.factureService.getPreviewPdf(id).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                this.pdfPreviewUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
                this.isLoadingPreview = false;
            },
            error: (err) => {
                console.error('Error fetching PDF preview:', err);
                this.isLoadingPreview = false;
                alert('Impossible de charger l\'aperçu PDF');
                this.closeModal();
            }
        });
    }

    confirmValidation(): void {
        if (!this.selectedFactureId) return;

        this.factureService.validate(this.selectedFactureId).subscribe({
            next: () => {
                this.closeModal();
                this.ngOnInit(); // Refresh list
                alert('Facture validée avec succès !');
            },
            error: (err) => alert('Erreur lors de la validation : ' + err.message)
        });
    }

    closeModal(): void {
        this.showValidationModal = false;
        this.selectedFactureId = null;
        this.pdfPreviewUrl = null;
    }

    triggerRevision(): void {
        const year = new Date().getFullYear();
        if (confirm(`Voulez-vous déclencher la révision annuelle des tarifs pour l'année ${year} ?`)) {
            this.tariffService.reviseOTDP(year).subscribe({
                next: () => {
                    this.tariffService.reviseEauElec(year).subscribe({
                        next: () => alert('Révision terminée avec succès'),
                        error: (err) => alert('Erreur révision Eau/Elec: ' + err.message)
                    });
                },
                error: (err) => alert('Erreur révision OTDP: ' + err.message)
            });
        }
    }

    retransmitFacture(id: number | undefined): void {
        if (!id) return;
        this.factureService.retransmit(id).subscribe({
            next: () => {
                alert('Demande de retransmission envoyée');
                this.ngOnInit();
            },
            error: (err) => alert('Erreur retransmission: ' + err.message)
        });
    }

    payFacture(id: number | undefined): void {
        if (!id) return;
        if (confirm('Confirmer le paiement de cette facture ?')) {
            this.factureService.markAsPaid(id).subscribe({
                next: () => {
                    this.ngOnInit();
                },
                error: (err) => alert('Erreur paiement: ' + err.message)
            });
        }
    }

    deleteFacture(id: number | undefined): void {
        if (!id) return;
        if (!confirm('Êtes-vous sûr de vouloir supprimer cette facture ? Cette action est irréversible.')) return;

        this.factureService.delete(id).subscribe({
            next: () => {
                this.factures = this.factures.filter(f => f.id !== id);
                this.applyFilters();
                this.calculateTotals();
            },
            error: (err) => {
                console.error(err);
                if (err.status === 403) {
                    alert('Suppression refusée: Seul l\'administrateur peut supprimer des factures.');
                } else if (err.error && err.error.message) {
                    alert(err.error.message);
                } else {
                    alert('Erreur lors de la suppression de la facture.');
                }
            }
        });
    }

    exportToCsv(): void {
        this.factureService.exportCsv().subscribe({
            next: (csv) => {
                const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'export-factures.csv');
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            },
            error: (err) => alert('Erreur lors de l\'export : ' + err.message)
        });
    }
}
