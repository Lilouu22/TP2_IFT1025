import javax.xml.transform.OutputKeys;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * une première fonctionnalité qui permet au client de récupérer la liste des
 * cours disponibles pour une session donnée. Le client envoie une requête charger
 * au serveur. Le serveur doit récupérer la liste des cours du fichier cours.txt et
 * l’envoie au client. Le client récupère les cours et les affiche
 *
 *  une deuxième fonctionnalité qui permet au client de faire une demande
 * d’inscription à un cours. Le client envoie une requête inscription au serveur. Les
 * informations suivantes sont données nécessaires (voir le format du fichier
 * inscription.txt ci-dessus) en arguments. Le choix du cours doit être valide c.à.d le
 * code du cours doit être présent dans la liste des cours disponibles dans la session en
 * question. Le serveur ajoute la ligne correspondante au fichier inscription.txt et
 * envoie un message de réussite au client. Le client affiche ce message (ou celui de
 * l’échec en cas d’exception)
 * SCANNER
 */

public class Main {
    /**
     * Constante de classe Server de type string servant à spécifier la commande pour executer la méthode handleRegistraton()
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constanste de la classe Server de type string servant à spécifer la commande pour handleLoadCourses(arg)
     */
    public final static String LOAD_COMMAND = "CHARGER";
    public static void main(String[] args)  {
        try{

            Socket clientSocket = new Socket("127.0.0.1",1337);
            System.out.println("Connexion Réussie!");
            ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
            writer.writeObject(LOAD_COMMAND);
            writer.flush();

            String line;
// TODO: Reussir a lire ce que le serveur envoie!
            while ((line = reader.readObject().toString()) != null) {
                System.out.println("Reçu : " + line);
            }

            reader.close();
            writer.close();
            clientSocket.close();
        }catch( IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}