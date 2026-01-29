export interface LigneFacture {
    id?: number;
    prestationId: number;
    prestationLibelle?: string;
    quantite: number;
    prixUnitaire?: number;
    tauxTva?: number;
    montantHt?: number;
    tauxTr?: number;
    montantTr?: number;
    montantTva?: number;
    montantTtc?: number;

    // Selection criteria
    typeTerrain?: string;
    natureActivite?: string;
    categorie?: string;
    codePort?: string;
    codeActivite?: string;
}

export enum StatutFacture {
    BROUILLON = 'BROUILLON',
    VALIDEE = 'VALIDEE',
    PAYEE = 'PAYEE',
    ANNULEE = 'ANNULEE'
}

export interface Facture {
    id?: number;
    numero?: string;
    date?: string;
    clientId: number;
    clientNom?: string;
    statut?: StatutFacture;
    montantHt?: number;
    montantTr?: number;
    montantTva?: number;
    montantTtc?: number;
    transmissionStatut?: string;
    lignes: LigneFacture[];
}
