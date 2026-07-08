# Image pour le validateur de mots de passe avec CrackLib
FROM ubuntu:22.04

# Installation des dépendances
RUN apt-get update && apt-get install -y \
    cracklib-runtime \
    && rm -rf /var/lib/apt/lists/*

# Point d'entrée pour le validateur
ENTRYPOINT ["cracklib-check"]