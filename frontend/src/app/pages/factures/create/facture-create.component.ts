import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ClientService } from '../../../services/client.service';
import { PrestationService } from '../../../services/prestation.service';
import { FactureService } from '../../../services/facture.service';
import { Client } from '../../../models/client.model';
import { Prestation } from '../../../models/prestation.model';
import { LigneFacture, Facture } from '../../../models/facture.model';

@Component({
    selector: 'app-facture-create',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './facture-create.component.html',
    styleUrl: './facture-create.component.css'
})
export class FactureCreateComponent implements OnInit {
    clients: Client[] = [];
    prestations: Prestation[] = [];
    loading: boolean = false;
    errorMessage: string | null = null;

    newFacture: Facture = {
        clientId: 0,
        lignes: []
    };

    constructor(
        private clientService: ClientService,
        private prestationService: PrestationService,
        private factureService: FactureService,
        public router: Router
    ) { }

    ngOnInit(): void {
        this.clientService.getAll().subscribe(data => this.clients = data);
        this.prestationService.getAll().subscribe(data => this.prestations = data);
        this.addLine();
    }

    addLine(): void {
        this.newFacture.lignes.push({
            prestationId: 0,
            quantite: 1,
            prixUnitaire: 0,
            tauxTva: 20,
            tauxTr: 0,
            montantHt: 0,
            montantTr: 0,
            montantTva: 0,
            montantTtc: 0
        });
    }

    removeLine(index: number): void {
        this.newFacture.lignes.splice(index, 1);
        this.calculateTotals();
    }

    onPrestationChange(index: number): void {
        const ligne = this.newFacture.lignes[index];
        const selectedP = this.prestations.find(p => p.id == ligne.prestationId);
        if (selectedP) {
            ligne.tauxTva = selectedP.tauxTva;
            ligne.tauxTr = selectedP.tauxTr || 0;
            ligne.prestationLibelle = selectedP.libelle;
            this.onCriteriaChange(index);
        }
    }

    onCriteriaChange(index: number): void {
        const ligne = this.newFacture.lignes[index];
        if (!ligne.prestationId) return;

        const params = {
            prestationId: ligne.prestationId,
            typeTerrain: ligne.typeTerrain,
            natureActivite: ligne.natureActivite,
            categorie: ligne.categorie,
            codePort: ligne.codePort,
            codeActivite: ligne.codeActivite
        };

        this.factureService.lookupPrice(params).subscribe({
            next: (price) => {
                if (price) {
                    ligne.prixUnitaire = price;
                    this.calculateLine(index);
                }
            },
            error: (err) => console.error('Price lookup failed', err)
        });
    }

    calculateLine(index: number): void {
        const ligne = this.newFacture.lignes[index];
        // 1. HT = Quantité * Prix
        ligne.montantHt = (ligne.quantite || 0) * (ligne.prixUnitaire || 0);
        // 2. TR = HT * tauxTR
        ligne.montantTr = ligne.montantHt * ((ligne.tauxTr || 0) / 100);
        // 3. TVA = (HT + TR) * tauxTVA
        ligne.montantTva = (ligne.montantHt + (ligne.montantTr || 0)) * ((ligne.tauxTva || 0) / 100);
        // 4. TTC = HT + TR + TVA
        ligne.montantTtc = ligne.montantHt + (ligne.montantTr || 0) + (ligne.montantTva || 0);

        this.calculateTotals();
    }

    calculateTotals(): void {
        this.newFacture.montantHt = this.newFacture.lignes.reduce((sum, l) => sum + (l.montantHt || 0), 0);
        this.newFacture.montantTr = this.newFacture.lignes.reduce((sum, l) => sum + (l.montantTr || 0), 0);
        this.newFacture.montantTva = this.newFacture.lignes.reduce((sum, l) => sum + (l.montantTva || 0), 0);
        this.newFacture.montantTtc = this.newFacture.lignes.reduce((sum, l) => sum + (l.montantTtc || 0), 0);
    }

    saveFacture(): void {
        this.errorMessage = null;
        if (this.newFacture.clientId === 0) {
            this.errorMessage = 'Veuillez sélectionner un client';
            return;
        }
        if (this.newFacture.lignes.length === 0) {
            this.errorMessage = 'Veuillez ajouter au moins une ligne';
            return;
        }

        this.loading = true;
        this.factureService.create(this.newFacture).subscribe({
            next: () => {
                this.loading = false;
                this.router.navigate(['/factures']);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = 'Erreur lors de la création de la facture : ' + (err.error?.message || err.message);
                console.error(err);
            }
        });
    }
}
