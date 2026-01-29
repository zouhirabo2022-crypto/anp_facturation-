import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PrestationService } from '../../services/prestation.service';
import { Prestation } from '../../models/prestation.model';

@Component({
    selector: 'app-prestation-create',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './prestation-create.component.html',
    styleUrl: './prestation-create.component.css'
})
export class PrestationCreateComponent implements OnInit {
    prestation: Prestation = {
        code: '',
        libelle: '',
        unite: '',
        tauxTva: 20,
        tauxTr: 0,
        compteComptable: ''
    };
    isEditMode = false;
    loading = false;
    errorMessage: string | null = null;

    constructor(
        private prestationService: PrestationService,
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEditMode = true;
            this.loading = true;
            this.prestationService.getById(+id).subscribe({
                next: (data) => {
                    this.prestation = data;
                    this.loading = false;
                },
                error: (err) => {
                    this.errorMessage = 'Impossible de charger la prestation.';
                    this.loading = false;
                    console.error(err);
                }
            });
        }
    }

    savePrestation(): void {
        if (!this.prestation.code || !this.prestation.libelle) {
            this.errorMessage = 'Le code et le libellé sont obligatoires';
            return;
        }

        this.loading = true;
        this.errorMessage = null;

        const request = this.isEditMode && this.prestation.id
            ? this.prestationService.update(this.prestation.id, this.prestation)
            : this.prestationService.create(this.prestation);

        request.subscribe({
            next: () => {
                this.router.navigate(['/prestations']);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = 'Erreur lors de l\'enregistrement : ' + (err.error?.message || err.message);
            }
        });
    }

    cancel(): void {
        this.router.navigate(['/prestations']);
    }
}
