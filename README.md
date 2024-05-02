# Gestion Intérim Mobile App

## Description
Ce projet consiste en le développement d'une application mobile pour la gestion des offres et des candidatures dans le domaine de l'intérim. L'application vise à faciliter le processus de recherche d'emploi pour les candidats et la gestion des offres d'emploi pour les employeurs.

## Fonctionnalités

### Utilisateurs Anonymes
- Affichage des annonces/offres d'emploi
- Recherche d'annonces/offres d'emploi
- Authentification/Inscription

### Candidats Inscrits/Connectés
En plus des fonctionnalités offertes aux utilisateurs anonymes, les candidats inscrits/connectés peuvent :
- Candidater pour une offre d'emploi
- Consulter et gérer leurs candidatures en cours
- Consulter les candidatures acceptées
- Afficher l'itinéraire vers le lieu de travail

### Employeurs
En plus des fonctionnalités offertes aux utilisateurs anonymes, les employeurs peuvent :
- Déposer une offre d'emploi
- Consulter et gérer leurs offres d'emploi
- Consulter les candidatures reçues pour leurs offres
- Accepter, refuser ou répondre aux candidatures
- Contacter les candidats

## Comment Faire Fonctionner le Projet

1. Cloner le repository du projet depuis GitHub.
2. Ouvrir le projet dans Android Studio.
3. Vérifier que les dépendances nécessaires sont correctement téléchargées et configurées.
4. Configurer Firebase :
   - Créer un projet sur Firebase.
   - Ajouter l'application Android au projet Firebase et suivre les instructions pour configurer le fichier `google-services.json`.
   - Activer les services Firebase nécessaires tels que Firebase Realtime Database, Firebase Authentication, etc.
5. Configurer la base de données :
   - Créer la structure de la base de données en suivant le modèle défini dans le code.
   - Remplir la base de données avec les données nécessaires pour les annonces/offres d'emploi, les utilisateurs, etc.
6. Compiler et exécuter l'application sur un émulateur Android ou un appareil physique.
7. Tester toutes les fonctionnalités de l'application pour vérifier qu'elles fonctionnent correctement.
8. Effectuer des ajustements ou des corrections au besoin et répéter les étapes 6 et 7 si nécessaire.
9. Une fois satisfait du fonctionnement de l'application, finaliser et documenter le projet selon les directives fournies.

