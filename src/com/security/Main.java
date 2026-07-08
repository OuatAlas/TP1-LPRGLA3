package com.security;

import java.util.Scanner;

/**
 * Point d'entrée principal de l'application CLI
 * Gère les arguments en ligne de commande et l'interaction utilisateur
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Password Security CLI Tool ===");

        CLIHandler cli = new CLIHandler();

        // Vérification des arguments en ligne de commande
        if (args.length > 0) {
            // Mode batch avec arguments
            cli.processArguments(args);
        } else {
            // Mode interactif
            cli.interactiveMode();
        }
    }
}