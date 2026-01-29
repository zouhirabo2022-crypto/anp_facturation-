import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserListComponent } from './user-list.component';
import { UserService } from '../../services/user.service';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { describe, it, expect, beforeEach, vi } from 'vitest';

describe('UserListComponent', () => {
    let component: UserListComponent;
    let fixture: ComponentFixture<UserListComponent>;
    let mockUserService: any;

    const mockUsers = [
        { username: 'admin', enabled: true, roles: ['ADMIN_SYSTEME'] },
        { username: 'user1', enabled: true, roles: ['CONSULTATION'] },
        { username: 'disabled', enabled: false, roles: [] }
    ];

    beforeEach(async () => {
        mockUserService = {
            getAll: vi.fn(),
            create: vi.fn(),
            update: vi.fn(),
            delete: vi.fn(),
            toggleStatus: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [UserListComponent, CommonModule, FormsModule], // Standalone component
            providers: [
                { provide: UserService, useValue: mockUserService }
            ]
        }).compileComponents();

        mockUserService.getAll.mockReturnValue(of(mockUsers));
        mockUserService.create.mockReturnValue(of(mockUsers[0]));
        mockUserService.update.mockReturnValue(of(mockUsers[0]));
        mockUserService.delete.mockReturnValue(of({}));
        mockUserService.toggleStatus.mockReturnValue(of({}));
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load users on init', () => {
        expect(mockUserService.getAll).toHaveBeenCalled();
        expect(component.users.length).toBe(3);
    });

    it('should filter users by search term', () => {
        component.searchTerm = 'admin';
        expect(component.filteredUsers.length).toBe(1);
        expect(component.filteredUsers[0].username).toBe('admin');

        component.searchTerm = 'CONSULTATION';
        expect(component.filteredUsers.length).toBe(1);
        expect(component.filteredUsers[0].username).toBe('user1');
    });

    it('should paginate users', () => {
        // Setup for pagination
        const manyUsers = Array.from({ length: 25 }, (_, i) => ({
            username: `user${i}`, enabled: true, roles: []
        }));
        component.users = manyUsers;
        component.pageSize = 10;
        component.currentPage = 1;

        expect(component.paginatedUsers.length).toBe(10);
        expect(component.totalPages).toBe(3);

        component.setPage(2);
        expect(component.currentPage).toBe(2);
        expect(component.paginatedUsers[0].username).toBe('user10');
    });

    it('should open create modal', () => {
        component.openCreateModal();
        expect(component.showModal).toBe(true);
        expect(component.isEditing).toBe(false);
        expect(component.currentUser.username).toBe('');
    });

    it('should open edit modal', () => {
        const userToEdit = mockUsers[0];
        component.openEditModal(userToEdit);
        expect(component.isEditing).toBe(true);
        expect(component.currentUser.username).toBe('admin');
    });

    it('should call create service on save logic when not editing', () => {
        component.openCreateModal();
        component.currentUser = { username: 'newuser', password: 'pwd', enabled: true, roles: [] };
        component.saveUser();
        expect(mockUserService.create).toHaveBeenCalled();
    });

    it('should call update service on save logic when editing', () => {
        component.openEditModal(mockUsers[0]);
        component.saveUser();
        expect(mockUserService.update).toHaveBeenCalledWith('admin', expect.any(Object));
    });

    it('should delete user after confirmation', () => {
        // mock window.confirm
        // Vitest doesn't have spyOn(window, 'confirm') directly if window is not standard, but jsdom handles it.
        // Use vi.spyOn
        const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);

        component.deleteUser('user1');
        expect(mockUserService.delete).toHaveBeenCalledWith('user1');

        confirmSpy.mockRestore();
    });

    it('should toggle role', () => {
        component.currentUser.roles = [];
        component.toggleRole('ADMIN');
        expect(component.currentUser.roles).toContain('ADMIN');

        component.toggleRole('ADMIN');
        expect(component.currentUser.roles).not.toContain('ADMIN');
    });
});
