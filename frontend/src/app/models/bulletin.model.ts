export interface LigneBulletin {
    id?: number;
    prestationId: number;
    quantite: number;
    typeTerrain?: string;
    natureActivite?: string;
    categorie?: string;
    codePort?: string;
    codeActivite?: string;
}

export interface Bulletin {
    id?: number;
    idBulletinMetier: string;
    clientId: number;
    clientNom?: string;
    periodeFacturation?: string;
    statut: string;
    dateReception: string;
    lignes: LigneBulletin[];
}
