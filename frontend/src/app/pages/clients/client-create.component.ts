import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/client.model';

@Component({
    selector: 'app-client-create',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './client-create.component.html',
    styleUrl: './client-create.component.css'
})
export class ClientCreateComponent implements OnInit {
    client: Client = {
        nom: '',
        prenom: '',
        email: '',
        telephone: '',
        adresse: ''
    };
    isEditMode = false;
    loading = false;
    errorMessage: string | null = null;

    constructor(
        private clientService: ClientService,
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEditMode = true;
            this.loading = true;
            this.clientService.getById(+id).subscribe({
                next: (data) => {
                    this.client = data;
                    this.loading = false;
                },
                error: (err) => {
                    this.errorMessage = 'Impossible de charger le client.';
                    this.loading = false;
                    console.error(err);
                }
            });
        }
    }

    saveClient(): void {
        if (!this.client.nom || !this.client.prenom) {
            this.errorMessage = 'Le nom et le prénom sont obligatoires';
            return;
        }

        this.loading = true;
        this.errorMessage = null;

        const request = this.isEditMode && this.client.id
            ? this.clientService.update(this.client.id, this.client)
            : this.clientService.create(this.client);

        request.subscribe({
            next: () => {
                // alert(this.isEditMode ? 'Client mis à jour !' : 'Client créé !');
                this.router.navigate(['/clients']);
            },
            error: (err) => {
                this.loading = false;
                this.errorMessage = 'Erreur lors de l\'enregistrement : ' + (err.error?.message || err.message);
            }
        });
    }

    cancel(): void {
        this.router.navigate(['/clients']);
    }
}
