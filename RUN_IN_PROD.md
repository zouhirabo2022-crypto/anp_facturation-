# Guide de Lancement en Production (Docker Compose)

Ce guide explique comment déployer l'application **ANP Facturation** en mode production à l'aide de Docker Compose.

## 📋 Prérequis

- **Docker** et **Docker Compose** installés sur la machine hôte.
- **Git** pour cloner le projet.
- Une connexion Internet pour télécharger les images Docker et les dépendances.
- **Aucune installation de Java ou Node.js n'est requise sur l'hôte** (tout est géré par Docker).

## 🏗️ Architecture du Build

L'image Docker utilise des "Multi-stage builds" pour garantir une image finale légère et sécurisée :
1.  **Backend** : Compilation Maven avec Java 21 (Eclipse Temurin).
2.  **Frontend** : Build Angular Production (optimisation, minification).
3.  **Runtime** : Image finale combinant le JAR exécutable et le serveur Nginx (pour le Frontend).

## 🚀 Installation et Configuration

1.  **Cloner le dépôt** (si ce n'est pas déjà fait) :
    ```bash
    git clone <votre-repo-url>
    cd anp-facturation-backend
    ```

2.  **Configurer les variables d'environnement** :
    Assurez-vous d'avoir un fichier `.env` à la racine du projet. Copiez le fichier `.env.example` (s'il existe) ou créez-en un avec le contenu suivant :

    ```ini
    # Base de données PostgreSQL
    DB_USER=anp_prod_user
    DB_PASSWORD=anp_prod_password_secure
    DB_NAME=anp_facturation_prod

    # JWT (Authentification)
    JWT_SECRET=votre_secret_jwt_tres_long_et_securise_pour_la_production_min_256_bits
    JWT_EXPIRATION=86400000
    ```

    > ⚠️ **Important** : Changez les mots de passe et le secret JWT pour de vraies valeurs sécurisées avant le déploiement réel.

3.  **Certificats SSL** :
    L'environnement de production utilise HTTPS via Nginx. Des certificats auto-signés sont générés pour le test local.
    Pour une vraie production, remplacez les fichiers dans `frontend/certs/` (ou montez un volume) avec vos vrais certificats (`fullchain.pem`, `privkey.pem`).

## ▶️ Lancement

Lancez la stack complète (Base de données, Backend, Frontend/Nginx) avec la commande suivante :

```bash
docker compose -f docker-compose.prod.yml up -d --build
```

- `-d` : Mode détaché (en arrière-plan).
- `--build` : Force la reconstruction des images pour inclure les dernières modifications.

## ✅ Vérification

1.  **Accéder à l'application** :
    Ouvrez votre navigateur sur **[https://localhost](https://localhost)**.
    > Note : Comme le certificat est auto-signé, vous devrez accepter l'avertissement de sécurité du navigateur.

2.  **Se connecter** :
    Utilisez les identifiants administrateur (si les données de base ont été injectées par Flyway/Seed) :
    - **Utilisateur** : `admin`
    - **Mot de passe** : `admin123`

3.  **Vérifier le Healthcheck (Santé du système)** :
    Le backend expose un endpoint de santé :
    - URL : `http://localhost:8080/actuator/health` (Interne)
    - Via Docker : `docker inspect --format='{{json .State.Health}}' anp-backend`

4.  **Vérifier les logs** (en cas de problème) :
    ```bash
    docker compose -f docker-compose.prod.yml logs -f
    # Pour un service spécifique :
    docker compose -f docker-compose.prod.yml logs -f app
    ```

## 🛑 Arrêt et Maintenance

- **Arrêter la stack** :
    ```bash
    docker compose -f docker-compose.prod.yml down
    ```

- **Arrêter et supprimer les volumes (Attention : perte de données !)** :
    ```bash
    docker compose -f docker-compose.prod.yml down -v
    ```

## ☑️ Checklist "Run en Prod"

Avant d'ouvrir l'accès aux utilisateurs finaux :

- [ ] **Sécurité BDD** : Les mots de passe `DB_PASSWORD` dans le `.env` sont complexes.
- [ ] **Sécurité JWT** : Le `JWT_SECRET` est long, aléatoire et secret.
- [ ] **HTTPS** : Les certificats SSL auto-signés sont remplacés par des certificats valides (ex: Let's Encrypt).
- [ ] **Données** : La stratégie de sauvegarde du volume Docker `postgres_data_prod` est en place.
- [ ] **Réseau** : Seuls les ports 80 (Redirection HTTPS) et 443 sont exposés publiquement. Le port 8080 (API) ne doit pas être exposé directement (Nginx fait le proxy).
- [ ] **CORS** : Vérifier si `AllowedOriginPatterns` dans `SecurityConfigProd.java` doit être restreint au domaine de production exact au lieu de `*`.
- [ ] **Logs** : Configurer la rotation des logs Docker pour éviter de saturer le disque (fichier `daemon.json` ou configuration compose).
