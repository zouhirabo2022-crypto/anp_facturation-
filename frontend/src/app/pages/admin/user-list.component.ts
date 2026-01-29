import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
    selector: 'app-user-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './user-list.component.html',
    styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit {
    users: User[] = [];
    loading = false;
    showModal = false;
    isEditing = false;
    errorMessage: string | null = null;
    successMessage: string = '';

    // Search and Pagination
    searchTerm: string = '';
    currentPage: number = 1;
    pageSize: number = 10;

    currentUser: User = {
        username: '',
        password: '',
        enabled: true,
        roles: []
    };

    availableRoles = ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF', 'CONSULTATION'];

    constructor(private userService: UserService) { }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.loading = true;
        this.errorMessage = null;
        this.userService.getAll().subscribe({
            next: (data: User[]) => {
                this.users = data;
                this.loading = false;
            },
            error: (err: any) => {
                this.errorMessage = 'Erreur lors du chargement des utilisateurs: ' + err.message;
                this.loading = false;
            }
        });
    }

    get filteredUsers(): User[] {
        let result = this.users;
        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            result = this.users.filter(u => 
                u.username.toLowerCase().includes(term) ||
                u.roles.some(r => r.toLowerCase().includes(term))
            );
        }
        return result;
    }

    get paginatedUsers(): User[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.filteredUsers.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredUsers.length / this.pageSize);
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
        this.currentPage = 1;
    }

    openCreateModal(): void {
        this.isEditing = false;
        this.currentUser = { username: '', password: '', enabled: true, roles: [] };
        this.showModal = true;
        this.errorMessage = null;
    }

    openEditModal(user: User): void {
        this.isEditing = true;
        this.currentUser = { ...user, password: '' };
        this.showModal = true;
        this.errorMessage = null;
    }

    closeModal(): void {
        this.showModal = false;
        this.errorMessage = null;
    }

    saveUser(): void {
        this.loading = true;
        this.errorMessage = null;

        if (this.isEditing) {
            this.userService.update(this.currentUser.username, this.currentUser).subscribe({
                next: () => {
                    this.closeModal();
                    this.loadUsers();
                },
                error: (err: any) => {
                    this.errorMessage = 'Erreur lors de la modification: ' + err.message;
                    this.loading = false;
                }
            });
        } else {
            this.userService.create(this.currentUser).subscribe({
                next: () => {
                    this.closeModal();
                    this.loadUsers();
                },
                error: (err: any) => {
                    this.errorMessage = 'Erreur lors de la création: ' + err.message;
                    this.loading = false;
                }
            });
        }
    }

    toggleUserStatus(user: User): void {
        if (confirm(`Voulez-vous vraiment ${user.enabled ? 'désactiver' : 'activer'} cet utilisateur ?`)) {
            this.errorMessage = null;
            this.successMessage = '';
            
            this.userService.toggleStatus(user.username).subscribe({
                next: () => {
                    this.successMessage = `Utilisateur ${user.enabled ? 'désactivé' : 'activé'} avec succès`;
                    this.loadUsers();
                },
                error: (err: any) => this.errorMessage = 'Erreur: ' + err.message
            });
        }
    }

    deleteUser(username: string): void {
        if (confirm('Êtes-vous sûr de vouloir supprimer cet utilisateur ?')) {
            this.errorMessage = null;
            this.successMessage = '';
            
            this.userService.delete(username).subscribe({
                next: () => {
                    this.successMessage = 'Utilisateur supprimé avec succès';
                    this.loadUsers();
                },
                error: (err: any) => this.errorMessage = 'Erreur lors de la suppression: ' + err.message
            });
        }
    }

    toggleRole(role: string): void {
        const index = this.currentUser.roles.indexOf(role);
        if (index > -1) {
            this.currentUser.roles.splice(index, 1);
        } else {
            this.currentUser.roles.push(role);
        }
    }

    hasRole(role: string): boolean {
        return this.currentUser.roles.includes(role);
    }
}
