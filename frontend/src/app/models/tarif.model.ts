export interface BaseTarif {
    id?: number;
    prestationId: number;
    anneeTarif: number;
    anneeDebutRevision?: number;
    delaiRevision?: number;
    tauxRevision?: number;
    actif: boolean;
}

export interface TarifEauElectricite extends BaseTarif {
    codePort: string;
    libelle: string;
    codeActivite: string;
    tarifDistributeur: number;
    tarifFacture: number;
}

export interface TarifOTDP extends BaseTarif {
    typeTerrain: string;
    natureActivite: string;
    categorie: string;
    uniteBase: string;
    montant: number;
}

export interface TarifAutorisation extends BaseTarif {
    libelle: string;
    montant: number;
}

export interface TarifConcession extends BaseTarif {
    typeContrat: string;
    montant: number;
}
