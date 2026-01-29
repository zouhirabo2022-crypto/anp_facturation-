import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PrestationService } from '../../services/prestation.service';
import { TarifOTDPService } from '../../services/tarif-otdp.service';
import { TarifEauElectriciteService } from '../../services/tarif-eau-elec.service';
import { TarifAutorisationService } from '../../services/tarif-autorisation.service';
import { TarifConcessionService } from '../../services/tarif-concession.service';
import { Prestation } from '../../models/prestation.model';
import { TarifOTDP, TarifEauElectricite, TarifAutorisation, TarifConcession } from '../../models/tarif.model';

@Component({
    selector: 'app-tariff-management',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './tariff-management.component.html',
    styleUrl: './tariff-management.component.css'
})
export class TariffManagementComponent implements OnInit {
    prestationId!: number;
    prestation?: Prestation;
    tarifsOTDP: TarifOTDP[] = [];
    tarifsEauElec: TarifEauElectricite[] = [];
    tarifsAutorisation: TarifAutorisation[] = [];
    tarifsConcession: TarifConcession[] = [];

    // UI State
    showModal: boolean = false;
    isEditMode: boolean = false;
    loading: boolean = false;
    errorMessage: string | null = null;
    globalError: string | null = null;
    successMessage: string | null = null;

    // New Tariff Forms
    newOTDP: Partial<TarifOTDP> = { actif: true, anneeTarif: new Date().getFullYear() };
    newEauElec: Partial<TarifEauElectricite> = { actif: true, anneeTarif: new Date().getFullYear() };
    newAutorisation: Partial<TarifAutorisation> = { actif: true, anneeTarif: new Date().getFullYear() };
    newConcession: Partial<TarifConcession> = { actif: true, anneeTarif: new Date().getFullYear() };

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private prestationService: PrestationService,
        private otdpService: TarifOTDPService,
        private eauElecService: TarifEauElectriciteService,
        private autorisationService: TarifAutorisationService,
        private concessionService: TarifConcessionService
    ) { }

    ngOnInit(): void {
        this.prestationId = Number(this.route.snapshot.paramMap.get('id'));
        this.loadDetails();
    }

    loadDetails(): void {
        this.prestationService.getById(this.prestationId).subscribe(p => {
            this.prestation = p;
            if (p.code.includes('EAU') || p.code.includes('ELEC')) {
                this.loadEauElec();
            } else if (p.code.includes('AUT')) {
                this.loadAutorisation();
            } else if (p.code.includes('CONC')) {
                this.loadConcession();
            } else {
                this.loadOTDP();
            }
        });
    }

    loadOTDP(): void {
        this.otdpService.getByPrestation(this.prestationId).subscribe(data => this.tarifsOTDP = data);
    }

    loadEauElec(): void {
        this.eauElecService.getByPrestation(this.prestationId).subscribe(data => this.tarifsEauElec = data);
    }

    loadAutorisation(): void {
        this.autorisationService.getByPrestation(this.prestationId).subscribe(data => this.tarifsAutorisation = data);
    }

    loadConcession(): void {
        this.concessionService.getByPrestation(this.prestationId).subscribe(data => this.tarifsConcession = data);
    }

    openModal(tarif?: any): void {
        this.showModal = true;
        this.errorMessage = null;
        
        if (tarif) {
            this.isEditMode = true;
            if (this.isOTDP()) {
                this.newOTDP = { ...tarif };
            } else if (this.isEauElec()) {
                this.newEauElec = { ...tarif };
            } else if (this.isAutorisation()) {
                this.newAutorisation = { ...tarif };
            } else if (this.isConcession()) {
                this.newConcession = { ...tarif };
            }
        } else {
            this.isEditMode = false;
            this.resetForms();
        }
    }

    resetForms(): void {
        const defaultYear = new Date().getFullYear();
        this.newOTDP = { actif: true, anneeTarif: defaultYear };
        this.newEauElec = { actif: true, anneeTarif: defaultYear };
        this.newAutorisation = { actif: true, anneeTarif: defaultYear };
        this.newConcession = { actif: true, anneeTarif: defaultYear };
    }

    closeModal(): void {
        this.showModal = false;
        this.errorMessage = null;
        this.resetForms();
    }

    isOTDP(): boolean {
        return !!this.prestation && !this.isEauElec() && !this.isAutorisation() && !this.isConcession();
    }

    isEauElec(): boolean {
        return !!this.prestation && (this.prestation.code.includes('EAU') || this.prestation.code.includes('ELEC'));
    }

    isAutorisation(): boolean {
        return !!this.prestation && this.prestation.code.includes('AUT');
    }

    isConcession(): boolean {
        return !!this.prestation && this.prestation.code.includes('CONC');
    }

    saveOTDP(): void {
        this.loading = true;
        this.errorMessage = null;
        const tarif = { ...this.newOTDP, prestationId: this.prestationId } as TarifOTDP;
        
        const request = this.isEditMode && tarif.id 
            ? this.otdpService.update(tarif.id, tarif)
            : this.otdpService.create(tarif);

        request.subscribe({
            next: () => {
                this.loadOTDP();
                this.closeModal();
                this.loading = false;
                this.successMessage = tarif.id ? 'Tarif mis à jour' : 'Tarif créé avec succès';
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = "Erreur lors de l'enregistrement du tarif. Vérifiez les données.";
                console.error(err);
            }
        });
    }

    saveEauElec(): void {
        this.loading = true;
        this.errorMessage = null;
        const tarif = { ...this.newEauElec, prestationId: this.prestationId } as TarifEauElectricite;
        
        const request = this.isEditMode && tarif.id 
            ? this.eauElecService.update(tarif.id, tarif)
            : this.eauElecService.create(tarif);

        request.subscribe({
            next: () => {
                this.loadEauElec();
                this.closeModal();
                this.loading = false;
                this.successMessage = this.isEditMode ? 'Tarif mis à jour' : 'Tarif créé avec succès';
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = "Erreur lors de l'enregistrement du tarif. Vérifiez les données.";
                console.error(err);
            }
        });
    }

    saveAutorisation(): void {
        this.loading = true;
        this.errorMessage = null;
        const tarif = { ...this.newAutorisation, prestationId: this.prestationId } as TarifAutorisation;
        
        const request = this.isEditMode && tarif.id 
            ? this.autorisationService.update(tarif.id, tarif)
            : this.autorisationService.create(tarif);

        request.subscribe({
            next: () => {
                this.loadAutorisation();
                this.closeModal();
                this.loading = false;
                this.successMessage = this.isEditMode ? 'Tarif mis à jour' : 'Tarif créé avec succès';
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = "Erreur lors de l'enregistrement du tarif. Vérifiez les données.";
                console.error(err);
            }
        });
    }

    saveConcession(): void {
        this.loading = true;
        this.errorMessage = null;
        const tarif = { ...this.newConcession, prestationId: this.prestationId } as TarifConcession;
        
        const request = this.isEditMode && tarif.id 
            ? this.concessionService.update(tarif.id, tarif)
            : this.concessionService.create(tarif);

        request.subscribe({
            next: () => {
                this.loadConcession();
                this.closeModal();
                this.loading = false;
                this.successMessage = this.isEditMode ? 'Tarif mis à jour' : 'Tarif créé avec succès';
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = "Erreur lors de l'enregistrement du tarif. Vérifiez les données.";
                console.error(err);
            }
        });
    }

    deleteOTDP(id: number | undefined): void {
        if (id && confirm('Supprimer ce tarif ?')) {
            this.globalError = null;
            this.successMessage = null;
            this.otdpService.delete(id).subscribe({
                next: () => {
                    this.loadOTDP();
                    this.successMessage = 'Tarif supprimé avec succès';
                },
                error: (err) => {
                    this.globalError = 'Erreur lors de la suppression du tarif';
                    console.error(err);
                }
            });
        }
    }

    deleteEauElec(id: number | undefined): void {
        if (id && confirm('Supprimer ce tarif ?')) {
            this.globalError = null;
            this.successMessage = null;
            this.eauElecService.delete(id).subscribe({
                next: () => {
                    this.loadEauElec();
                    this.successMessage = 'Tarif supprimé avec succès';
                },
                error: (err) => {
                    this.globalError = 'Erreur lors de la suppression du tarif';
                    console.error(err);
                }
            });
        }
    }

    deleteAutorisation(id: number | undefined): void {
        if (id && confirm('Supprimer ce tarif ?')) {
            this.globalError = null;
            this.successMessage = null;
            this.autorisationService.delete(id).subscribe({
                next: () => {
                    this.loadAutorisation();
                    this.successMessage = 'Tarif supprimé avec succès';
                },
                error: (err) => {
                    this.globalError = 'Erreur lors de la suppression du tarif';
                    console.error(err);
                }
            });
        }
    }

    deleteConcession(id: number | undefined): void {
        if (id && confirm('Supprimer ce tarif ?')) {
            this.globalError = null;
            this.successMessage = null;
            this.concessionService.delete(id).subscribe({
                next: () => {
                    this.loadConcession();
                    this.successMessage = 'Tarif supprimé avec succès';
                },
                error: (err) => {
                    this.globalError = 'Erreur lors de la suppression du tarif';
                    console.error(err);
                }
            });
        }
    }

    back(): void {
        this.router.navigate(['/prestations']);
    }
}
