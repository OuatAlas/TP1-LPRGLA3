package com.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Classe responsable de la communication avec le conteneur Docker
 * Utilise l'outil CrackLib pour la validation externe
 */
public class DockerValidator {
    private static final String DOCKER_IMAGE = "password-validator";
    private static final String CONTAINER_NAME = "cracklib-validator";

    /**
     * Valide un mot de passe via le conteneur Docker
     * Utilise l'outil CrackLib pour détecter les mots faibles
     */
    public String validatePassword(String password) throws IOException, InterruptedException {
        // Construction de la commande Docker
        String command = String.format(
                "docker run --rm %s cracklib-check -v '%s'",
                DOCKER_IMAGE,
                password
        );

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);

        Process process = processBuilder.start();

        // Lecture de la sortie du conteneur
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Lecture des erreurs
        StringBuilder error = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();

        // Analyse de la sortie de CrackLib
        if (exitCode == 0) {
            // CrackLib a trouvé un problème avec le mot de passe
            // Format: "password: OK" ou "password: message d'erreur"
            String result = output.toString();
            if (result.contains("OK")) {
                return "4"; // Très fort
            } else {
                return "2"; // Faible - CrackLib a détecté un pattern
            }
        } else {
            // Erreur d'exécution - utiliser le score local comme fallback
            System.err.println("⚠️ Erreur Docker: " + error.toString());
            return "3"; // Score moyen par défaut
        }
    }

    /**
     * Alternative: Utilisation de zxcvbn via un conteneur Node.js
     * Méthode plus précise mais nécessite une image différente
     */
    public String validateWithZxcvbn(String password) throws IOException, InterruptedException {
        String command = String.format(
                "docker run --rm zxcvbn-cli zxcvbn -j '%s'",
                password
        );

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            // Parse le JSON retourné par zxcvbn
            // On pourrait utiliser Jackson ou Gson ici pour une analyse plus fine
            return "4"; // Score maximal pour l'exemple
        }

        return "2"; // Score par défaut si erreur
    }
}