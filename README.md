# Backend ANP Facturation

Ce projet est le backend de l'application de facturation ANP, implémentant les spécifications de gestion des prestations, tarifs et facturation pour les domaines OTDP, Autorisation, Concession, Eau et Électricité.

## État d'avancement (Analyse)

Le projet implémente la majorité des fonctionnalités spécifiées :

*   **Gestion des Prestations** : Création et paramétrage (TVA, etc.).
*   **Gestion des Tarifs** :
    *   **OTDP** : Gestion complète avec paramètres de révision.
    *   **Eau & Électricité** : Tarifs distributeur vs facturé, lien avec les ports.
    *   **Autorisations & Concessions** : Logique implémentée.
    *   **Révision Tarifaire** : Service de révision annuelle planifiée (`TarifRevisionService`) et endpoints de déclenchement manuel.
*   **Processus de Facturation** :
    *   Création de facture (Brouillon).
    *   Calcul automatique (HT, TR, TVA, TTC) selon les règles métiers.
    *   **Validation & Numérotation Officielle** : Numérotation séquentielle `FACT-AAAA-XXXX` implémentée.
    *   **Tableau de Bord** : API de statistiques globales (CA, état des factures) pour le dashboard.
    *   Génération PDF (avec iText/OpenPDF).
    *   Simulation de transmission aux systèmes externes (PREST, GRC, SI Finance).
*   **Sécurité & Audit** :
    *   Authentification JWT (Mock AD via `SecurityConfig` et `JwtAuthenticationFilter`).
    *   Audit des actions critiques en base de données.
    *   **Logging** : Configuration Logback pour logs JSON (compatible ELK/Graylog).

### Points d'attention / À faire :
1.  **Active Directory** : L'intégration LDAP est configurée mais nécessite un serveur LDAP réel. Actuellement simulée.
2.  **Intégration SI Finance** : Un stub local est utilisé pour simuler les taux fiscaux.

## Prérequis

*   Java 17
*   Maven
*   PostgreSQL (optionnel, H2 par défaut ou si profil dev)

## Installation et Démarrage

### 1. Compiler et Lancer

```bash
./mvnw clean install
./mvnw spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

### 2. Documentation API

*   **Swagger UI** : `http://localhost:8080/swagger-ui.html`

## Configuration Logging (ELK)

Les logs sont configurés pour sortir en format JSON sur la console dans les profils `prod` ou `docker`, facilitant l'ingestion par Logstash/Graylog.
Fichier de conf : `src/main/resources/logback-spring.xml`.

## Structure du Projet

*   `entity` : Modèle de données (Prestation, Tarif, Facture...).
*   `service` : Logique métier (Calculs, Révision, Validation).
*   `controller` : Endpoints REST.
*   `security` : Configuration JWT et LDAP.
