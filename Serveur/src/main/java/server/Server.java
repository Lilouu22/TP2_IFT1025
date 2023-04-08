package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server {
    /**
     * Constante de classe Server de type string servant à spécifier la commande pour executer la méthode handleRegistraton()
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constanste de la classe Server de type string servant à spécifer la commande pour handleLoadCourses(arg)
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * Variable de type ServerSocket servant à connecter le client au server dans la méthode run()
     */
    private final ServerSocket server;
    /**
     * Variable de type Socket qui représente le client une fois connecté au server
     */
    private Socket client;
    /**
     * Variable de type objectInputStream qui va prendre les requêtes du client pour les transférer au server
     */
    private ObjectInputStream objectInputStream;
    /**
     * Variable de type objectOutputStream qui va transférer les output du server au client
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * à compléter
     */
    private final ArrayList<EventHandler> handlers;

    /**
     * Ce constructeur initialise le serveur en ouvrant le port d'entrée spécifié en argument
     * en permettant 1 client dans la file d'attente du serveur
     * et créer la liste de 'EventHandlers' en lui ajoutant la méthode 'handleEvents()'.
     *
     * @param  port  un integer qui spécifie le port d'entrée pour se connecter au server
     * @throws IOException  exception si l'ouverture du port de connexion du serveur échoue
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Ce constructeur initialise le serveur en ouvrant le port d'entrée spécifie en argument
     * et créer la liste de 'EventHandlers' en lui ajoutant la méthode 'handleEvents()'.
     *
     * @param  client  attente d'une connexion d'un client
     * @throws Exception  si l'ouverture du port de connexion du serveur échoue
     * @return
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

    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        // TODO: implémenter cette méthode
        ArrayList<Course> listeCours =new ArrayList<Course>();
        try {
            FileReader fileReader = new FileReader("src/main/java/server/data/cours.txt");
            BufferedReader reader = new BufferedReader(fileReader);

            String ligne;
            while (( ligne = reader.readLine()) != null){
                String[] infoCours = ligne.split("\t");

                //filtre les cours par la session
                if (arg.equals(infoCours[2])){
                    Course cours = new Course(infoCours[1], infoCours[0],infoCours[2]);
                    listeCours.add(cours);
                }
                objectOutputStream.writeObject(listeCours);
                fileReader.close();
                reader.close();
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        // TODO: implémenter cette méthode
        try{
            // Récupérer objet
            RegistrationForm registre = (RegistrationForm) objectInputStream.readObject();
            // Création objet pour créer fichier texte
            PrintWriter ecrireFichier = new PrintWriter("src/main/java/server/data/inscription.txt"); // à modf pour jar
            //Chaque ligne va correspondre à un registerform et va écrire directement en string
            ecrireFichier.println(registre.toString());
            ecrireFichier.close();

            objectOutputStream.writeUTF("Succès : Mission traitement du registre accomplie"); //
        }catch(IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

