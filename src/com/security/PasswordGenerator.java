package com.security;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe responsable de la génération de mots de passe sécurisés
 * Utilise SecureRandom pour une cryptographie forte
 */
public class PasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final SecureRandom random = new SecureRandom();

    /**
     * Génère un mot de passe selon les paramètres spécifiés
     * @param length Longueur souhaitée
     * @param includeUppercase Inclure des majuscules
     * @param includeLowercase Inclure des minuscules
     * @param includeDigits Inclure des chiffres
     * @param includeSymbols Inclure des symboles
     * @return Le mot de passe généré
     */
    public String generatePassword(int length, boolean includeUppercase,
                                   boolean includeLowercase, boolean includeDigits,
                                   boolean includeSymbols) {
        StringBuilder characterPool = new StringBuilder();
        StringBuilder password = new StringBuilder();

        // Construction du pool de caractères selon les options
        if (includeUppercase) characterPool.append(UPPERCASE);
        if (includeLowercase) characterPool.append(LOWERCASE);
        if (includeDigits) characterPool.append(DIGITS);
        if (includeSymbols) characterPool.append(SYMBOLS);

        // Validation : au moins un type de caractère sélectionné
        if (characterPool.length() == 0) {
            throw new IllegalArgumentException("Au moins un type de caractère doit être sélectionné");
        }

        // Garantir au moins un caractère de chaque type sélectionné
        if (includeUppercase) password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        if (includeLowercase) password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        if (includeDigits) password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        if (includeSymbols) password.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // Remplir le reste avec des caractères aléatoires du pool
        for (int i = password.length(); i < length; i++) {
            password.append(characterPool.charAt(random.nextInt(characterPool.length())));
        }

        // Mélanger les caractères pour éviter que les premiers soient prévisibles
        return shuffleString(password.toString());
    }

    /**
     * Mélange les caractères d'une chaîne pour plus de randomisation
     */
    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }

        StringBuilder shuffled = new StringBuilder(input.length());
        while (!characters.isEmpty()) {
            int index = random.nextInt(characters.size());
            shuffled.append(characters.remove(index));
        }

        return shuffled.toString();
    }

    /**
     * Génère une liste de mots de passe en mode rafale
     */
    public List<String> generateBatch(int count, int length, boolean includeUppercase,
                                      boolean includeLowercase, boolean includeDigits,
                                      boolean includeSymbols) {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            passwords.add(generatePassword(length, includeUppercase, includeLowercase,
                    includeDigits, includeSymbols));
        }
        return passwords;
    }
}