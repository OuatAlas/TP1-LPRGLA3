package com.security;

/**
 * Classe pour évaluer la force d'un mot de passe
 * Utilise des règles heuristiques et interagit avec Docker
 */
public class PasswordValidator {
    private final DockerValidator dockerValidator;

    public PasswordValidator() {
        this.dockerValidator = new DockerValidator();
    }

    /**
     * Évalue la force d'un mot de passe et retourne un score
     * @param password Le mot de passe à évaluer
     * @return Le niveau de force (Très faible, Faible, Moyen, Fort, Très fort)
     */
    public String evaluateStrength(String password) {
        // Validation locale avec critères heuristiques
        int score = calculateLocalScore(password);

        // Validation externe via Docker avec CrackLib ou zxcvbn
        try {
            String dockerScore = dockerValidator.validatePassword(password);
            // Combinaison des scores pour une évaluation plus précise
            score = Math.max(score, parseDockerScore(dockerScore));
        } catch (Exception e) {
            System.err.println("⚠️ Validation Docker indisponible, utilisation du score local");
        }

        return getStrengthLabel(score);
    }

    /**
     * Calcule un score local basé sur des critères heuristiques
     * Score de 0 à 100
     */
    private int calculateLocalScore(String password) {
        int score = 0;
        int length = password.length();

        // Critère 1 : Longueur (max 40 points)
        if (length >= 15) score += 40;
        else if (length >= 12) score += 30;
        else if (length >= 8) score += 20;
        else if (length >= 6) score += 10;

        // Critère 2 : Complexité (max 40 points)
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*");

        int complexityCount = 0;
        if (hasUpper) complexityCount++;
        if (hasLower) complexityCount++;
        if (hasDigit) complexityCount++;
        if (hasSymbol) complexityCount++;

        score += complexityCount * 10;

        // Critère 3 : Diversité des caractères (max 20 points)
        long uniqueChars = password.chars().distinct().count();
        if (uniqueChars >= 10) score += 20;
        else if (uniqueChars >= 6) score += 10;
        else if (uniqueChars >= 4) score += 5;

        // Pénalités pour patterns courants
        if (password.matches(".*(123|abc|qwerty|password).*")) score -= 20;
        if (password.matches("(.)\\1{2,}")) score -= 15; // Caractères répétitifs

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Parse le score retourné par Docker
     */
    private int parseDockerScore(String dockerOutput) {
        // Adaptez selon l'outil utilisé (CrackLib, zxcvbn, etc.)
        // Pour zxcvbn, on pourrait avoir une sortie en JSON
        try {
            // Exemple simplifié: l'outil retourne un nombre entre 0 et 4
            int score = Integer.parseInt(dockerOutput.trim());
            return score * 20; // Convertir sur 100
        } catch (NumberFormatException e) {
            // Si le format est différent, on retourne un score par défaut
            return 50;
        }
    }

    /**
     * Convertit un score numérique en libellé
     */
    private String getStrengthLabel(int score) {
        if (score >= 90) return "Très fort";
        if (score >= 70) return "Fort";
        if (score >= 50) return "Moyen";
        if (score >= 30) return "Faible";
        return "Très faible";
    }
}