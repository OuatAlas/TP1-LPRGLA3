package com.security;

import java.util.List;
import java.util.Scanner;

/**
 * Classe responsable de l'interface en ligne de commande
 * Gère les arguments et l'interaction utilisateur
 */
public class CLIHandler {
    private final PasswordGenerator generator;
    private final PasswordValidator validator;
    private final Scanner scanner;

    public CLIHandler() {
        this.generator = new PasswordGenerator();
        this.validator = new PasswordValidator();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Mode interactif avec prompt utilisateur
     */
    public void interactiveMode() {
        System.out.println("\n=== Mode Interactif ===");

        // Configuration des paramètres
        int length = askLength();
        boolean includeUppercase = askYesNo("Inclure des majuscules ? (o/n)");
        boolean includeLowercase = askYesNo("Inclure des minuscules ? (o/n)");
        boolean includeDigits = askYesNo("Inclure des chiffres ? (o/n)");
        boolean includeSymbols = askYesNo("Inclure des symboles ? (o/n)");

        // Mode rafale ou simple
        if (askYesNo("Générer plusieurs mots de passe ? (o/n)")) {
            int count = askCount();
            generateBatch(count, length, includeUppercase, includeLowercase, includeDigits, includeSymbols);
        } else {
            generateSingle(length, includeUppercase, includeLowercase, includeDigits, includeSymbols);
        }
    }

    /**
     * Traitement des arguments en ligne de commande
     */
    public void processArguments(String[] args) {
        int length = 12; // Valeur par défaut
        boolean includeUppercase = true;
        boolean includeLowercase = true;
        boolean includeDigits = true;
        boolean includeSymbols = false;
        int count = 1;

        // Parsing des arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-l", "--length":
                    if (i + 1 < args.length) {
                        try {
                            length = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("❌ Longueur invalide, utilisation de la valeur par défaut");
                        }
                    }
                    break;
                case "-u", "--uppercase":
                    includeUppercase = true;
                    break;
                case "-L", "--lowercase":
                    includeLowercase = true;
                    break;
                case "-d", "--digits":
                    includeDigits = true;
                    break;
                case "-s", "--symbols":
                    includeSymbols = true;
                    break;
                case "-c", "--count":
                    if (i + 1 < args.length) {
                        try {
                            count = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("❌ Nombre invalide, utilisation de la valeur par défaut");
                        }
                    }
                    break;
                case "-h", "--help":
                    showHelp();
                    return;
                default:
                    System.err.println("❌ Argument inconnu: " + args[i]);
                    showHelp();
                    return;
            }
        }

        // Génération selon les paramètres
        if (count > 1) {
            generateBatch(count, length, includeUppercase, includeLowercase, includeDigits, includeSymbols);
        } else {
            generateSingle(length, includeUppercase, includeLowercase, includeDigits, includeSymbols);
        }
    }

    /**
     * Génère un seul mot de passe
     */
    private void generateSingle(int length, boolean includeUppercase, boolean includeLowercase,
                                boolean includeDigits, boolean includeSymbols) {
        try {
            String password = generator.generatePassword(length, includeUppercase,
                    includeLowercase, includeDigits, includeSymbols);
            String strength = validator.evaluateStrength(password);

            System.out.println("\n=== Résultat ===");
            System.out.println("🔐 Mot de passe: " + password);
            System.out.println("📊 Force: " + strength);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    /**
     * Génère plusieurs mots de passe
     */
    private void generateBatch(int count, int length, boolean includeUppercase, boolean includeLowercase,
                               boolean includeDigits, boolean includeSymbols) {
        try {
            List<String> passwords = generator.generateBatch(count, length, includeUppercase,
                    includeLowercase, includeDigits, includeSymbols);

            System.out.println("\n=== Résultats (Mode Rafale) ===");
            for (int i = 0; i < passwords.size(); i++) {
                String password = passwords.get(i);
                String strength = validator.evaluateStrength(password);
                System.out.printf("%d. 🔐 %s 📊 %s%n", i + 1, password, strength);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    /**
     * Demande la longueur du mot de passe
     */
    private int askLength() {
        System.out.print("Longueur du mot de passe (8-32) [12]: ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return 12;
            int length = Integer.parseInt(input);
            return Math.min(32, Math.max(8, length)); // Bornage entre 8 et 32
        } catch (NumberFormatException e) {
            return 12;
        }
    }

    /**
     * Demande un nombre pour le mode rafale
     */
    private int askCount() {
        System.out.print("Nombre de mots de passe à générer (1-100) [5]: ");
        try {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) return 5;
            int count = Integer.parseInt(input);
            return Math.min(100, Math.max(1, count)); // Bornage entre 1 et 100
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    /**
     * Demande une réponse oui/non
     */
    private boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("o") || input.equals("oui")) return true;
            if (input.equals("n") || input.equals("non")) return false;
            System.out.println("Veuillez répondre par o/n ou oui/non");
        }
    }

    /**
     * Affiche l'aide
     */
    private void showHelp() {
        System.out.println("""
            \n=== Password Security CLI Tool ===
            
            Usage: java -jar password-cli.jar [OPTIONS]
            
            Options:
              -l, --length <n>      Longueur du mot de passe (8-32)
              -u, --uppercase       Inclure des majuscules
              -L, --lowercase       Inclure des minuscules
              -d, --digits          Inclure des chiffres
              -s, --symbols         Inclure des symboles
              -c, --count <n>       Nombre de mots de passe à générer (1-100)
              -h, --help            Affiche cette aide
            
            Exemples:
              java -jar password-cli.jar -l 16 -u -L -d -s
              java -jar password-cli.jar -l 12 -u -L -d -c 10
            
            Mode interactif (sans arguments):
              java -jar password-cli.jar
            """);
    }
}