import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditService, AuditLog } from '../../services/audit.service';

import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-audit-logs',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './audit-logs.component.html',
    styleUrl: './audit-logs.component.css'
})
export class AuditLogsComponent implements OnInit {
    logs: AuditLog[] = [];
    loading = true;
    errorMessage: string | null = null;

    // Search and Pagination
    searchTerm: string = '';
    currentPage: number = 1;
    pageSize: number = 10;

    constructor(private auditService: AuditService) { }

    ngOnInit(): void {
        this.loadLogs();
    }

    loadLogs(): void {
        this.loading = true;
        this.errorMessage = null;
        this.auditService.getAll().subscribe({
            next: (data) => {
                this.logs = data.sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime());
                this.loading = false;
            },
            error: (err) => {
                console.error('Error fetching audit logs', err);
                this.errorMessage = 'Erreur lors du chargement des journaux d\'audit.';
                this.loading = false;
            }
        });
    }

    get filteredLogs(): AuditLog[] {
        let result = this.logs;
        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            result = this.logs.filter(log => 
                log.username.toLowerCase().includes(term) ||
                log.action.toLowerCase().includes(term) ||
                (log.details && log.details.toLowerCase().includes(term))
            );
        }
        return result;
    }

    get paginatedLogs(): AuditLog[] {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        return this.filteredLogs.slice(startIndex, startIndex + this.pageSize);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredLogs.length / this.pageSize);
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
}
