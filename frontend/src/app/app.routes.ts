import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ClientListComponent } from './pages/clients/client-list.component';
import { ClientCreateComponent } from './pages/clients/client-create.component';
import { PrestationListComponent } from './pages/prestations/prestation-list.component';
import { PrestationCreateComponent } from './pages/prestations/prestation-create.component';
import { FactureCreateComponent } from './pages/factures/create/facture-create.component';
import { FactureListComponent } from './pages/factures/list/facture-list.component';
import { FactureDetailComponent } from './pages/factures/detail/facture-detail.component';
import { BulletinListComponent } from './pages/bulletins/bulletin-list.component';
import { AuditLogsComponent } from './pages/audit/audit-logs.component';
import { TariffManagementComponent } from './pages/prestations/tariff-management.component';
import { UserListComponent } from './pages/admin/user-list.component';
import { authGuard } from './auth.guard';
import { roleGuard } from './role.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    {
        path: '',
        canActivate: [authGuard],
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: DashboardComponent },
            { path: 'clients', component: ClientListComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF', 'CONSULTATION'] } },
            { path: 'clients/new', component: ClientCreateComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF'] } },
            { path: 'clients/:id/edit', component: ClientCreateComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF'] } },
            { path: 'prestations', component: PrestationListComponent },
            { path: 'prestations/new', component: PrestationCreateComponent },
            { path: 'prestations/:id/edit', component: PrestationCreateComponent },
            { path: 'prestations/:id/tarifs', component: TariffManagementComponent },
            { path: 'bulletins', component: BulletinListComponent },
            { path: 'factures', component: FactureListComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF', 'CONSULTATION'] } },
            { path: 'factures/create', component: FactureCreateComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF'] } },
            { path: 'factures/:id', component: FactureDetailComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME', 'GESTIONNAIRE_PARAM', 'GESTIONNAIRE_TARIF', 'CONSULTATION'] } },
            { path: 'audit', component: AuditLogsComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME'] } },
            { path: 'users', component: UserListComponent, canActivate: [roleGuard], data: { roles: ['ADMIN_SYSTEME'] } }
        ]
    },
    { path: '**', redirectTo: 'dashboard' }
];
