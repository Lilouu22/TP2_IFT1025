package server;

/**
 * Cette classe permet le lancement d'une application server
 * Elle contient une méthode principale qui initialise le port de connexion du server
 * et qui va permet à des clients de se connecter
 */
public class ServerLauncher {
    /**
     * attribut pour spécifier le port de connexion par défaut
     */
    public final static int PORT = 1337;

    /**
     * Cette méthode initialise et démarre le server avec le port de
     * connexion spécifié et attrape les exceptions si la connexion échoue
     * @param args  arguments d'entrée
     */

    public static void main(String[] args) {
        /**
         * attribut de type Server
         */
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}