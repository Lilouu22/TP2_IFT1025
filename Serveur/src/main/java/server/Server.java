package server;

import javafx.util.Pair;
import models.Course;
import models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Classe qui représente un server qui écoute sur un port spécifique et qui va avoir des géreurs d'événements
 * pour les actions prises par les clients quand ils se connectent. Cette classe va permettre aux clients
 * de s'inscrire à des cours et les inscrire dans un registre selon la session.
 */
public class Server {
    /**
     * Variable qui va permettre à une commande d'inscrire les clients aux cours
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * variable utilisée pour télécharger les cours sélectionnés selon la session
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * Socket servant à connecter le client au server
     */
    private final ServerSocket server;
    /**
     * Socket qui représente la communication entre le client
     * et le server une fois connecté  */
    private Socket client;
    /**
     * object InputStream va lire les requêtes du client
     */
    private ObjectInputStream objectInputStream;
    /**
     * Variable object OutputStream qui va transférer les output du server au client
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * liste de géreurs d'événements associés au server
     */
    private final ArrayList<EventHandler> handlers;

    /**
     * Ce constructeur initialise le serveur en ouvrant le port d'entrée spécifié en argument
     * en prenant 1 client dans la file d'attente du serveur
     *
     * @param  port  port de connexion vers le server
     * @throws IOException  exception si l'ouverture du port de connexion du serveur échoue
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    /**
     * Méthode pour rajouter un nouvel événements à liste de gestion d'événements
     *
     *  @param h le gestionnaire d'événement à ajouter
     */

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     *Méthode qui avertit tous les gestionnaires d'événements
     *
     * @param cmd commande à gérer
     * @param arg argument d'entrée à gérer
     */

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * lance le programme serveur en ouvrant le port d'entrée spécifié en argument et
     * attend pour de nouvelles connexions clients
     * Une fois un client connecté, créer un nouvel objet pour gérer la connexion
     *
     */

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Écoute pour une requête client et la traite.
     * @throws IOException si une erreur arrive lors de la lecture de la requête client
     * @throws ClassNotFoundException  exception si la classe objet n'est pas trouvée
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Traitement de la commande d'entrée et divise en commande et en argument
     * @param line variable d'entrée du traitement
     * @return une Paire d'objet contenant la commande et l'argument
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Ferme les entrées et sorties de la connexion client-server
     * Ferme la connexion client-server
     * @throws IOException si une erreur survient lors de la déconnexion
     */

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Gérer les événements selon la commande et les arguments rentrés
     * lors de l'enregistrement ou du chargement
     * @param cmd commande à exécuter
     * @param arg arguments pour utiliser la commande
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Méthode qui permet de télécharger les cours disponibles dans le fichier texte qui
     * concorde à la session choisie
     * Renvoie une liste de ces cours au client
     *
     * @param arg session pour y obtenir les cours disponibles
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
        ArrayList<Course> listeCours =new ArrayList<>();
        try {
            String cheminJar = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String cheminDossierJar = cheminJar.substring(1, cheminJar.lastIndexOf('/')+1);

            System.out.println(cheminDossierJar);
            FileReader fileReader = new FileReader(cheminDossierJar + "cours.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String ligne;
            while (( ligne = reader.readLine()) != null){
                String[] infoCours = ligne.split("\t");

                //filtre les cours par la session
                if (arg.equals(infoCours[2])){
                    System.out.println(infoCours[0]);
                    System.out.println(infoCours[1]);
                    System.out.println(infoCours[2]);
                    Course cours = new Course(infoCours[1], infoCours[0],infoCours[2]);
                    listeCours.add(cours);
                }
            }
            objectOutputStream.writeObject(listeCours);
            fileReader.close();
            reader.close();
        }catch(IOException | URISyntaxException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Cette méthode inscrit un étudiant à un cours en écrivant leur enregistrement dans un fichier texte
     *
     * Renvoie des erreurs si lors de la lecture ou écriture du fichier ou que la classe n'est pas trouvée
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode

        try{
            // Récupérer objet
            RegistrationForm registre = (RegistrationForm) objectInputStream.readObject();
            // Création objet pour créer fichier text
            String cheminJar = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String cheminDossierJar = cheminJar.substring(1, cheminJar.lastIndexOf('/')+1);
            PrintWriter ecrireFichier = new PrintWriter(new FileWriter(cheminDossierJar + "inscription.txt", true)); // à modf pour jar
            //Chaque ligne va correspondre à un registerform et va écrire directement en string
            ecrireFichier.println(registre.toString());
            ecrireFichier.close();

            objectOutputStream.writeObject("Succès : Inscription réussie de " + registre.getPrenom()+ " au cours " + registre.getCourse().getCode() + ""); //
        }catch(IOException | ClassNotFoundException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
    }
}

