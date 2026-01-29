export interface Prestation {
    id?: number;
    code: string;
    libelle: string;
    unite?: string;
    tauxTva: number;
    tauxTr?: number;
    compteComptable?: string;
}
