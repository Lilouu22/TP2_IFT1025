package server;

/**
 * Cette interface sert à gérer les événements du server à l'aide d'une seule méthode qui accepter 2 paramètres entrants:
 * une commande et un argument et qui vont exécuter les actions nécessaires pour gérer l'événement
 */
@FunctionalInterface
public interface EventHandler {
    /**
     Gère les futurs événements possibles en ajustant les actions à faire
     @param cmd commande pour gérer l'événement entrant
     @param arg argument pour gérer l'événement entrant
     */
    void handle(String cmd, String arg);
}
