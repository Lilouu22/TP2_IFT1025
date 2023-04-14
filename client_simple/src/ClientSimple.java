import models.Course;
import models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.util.Objects.isNull;

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

public class ClientSimple {
    /**
     * Constante de classe Server de type string servant à spécifier la commande pour executer la méthode handleRegistraton()
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constanste de la classe Server de type string servant à spécifer la commande pour handleLoadCourses(arg)
     */

    public final static String LOAD_COMMAND = "CHARGER";
    public static void main(String[] args)  {
        /**
         *
         */
        System.out.println("***Bienvenue au portail d'inscription de listeCours de l'UDEM");
        try{
            Scanner scanner = new Scanner(System.in);
            ArrayList<Course> listeCours = runLoadCommand();

            do{
                try{
                    System.out.print("Choix: ");
                    int choix = scanner.nextInt();
                    if (choix == 1) {
                        listeCours  = runLoadCommand();
                    }else if(choix == 2){
                        String reponse = runRegisterCommand(listeCours);
                        System.out.println(reponse);
                        break;
                    }else{
                        System.out.println("Choix invalide, sélectionnez option 1 ou  2");
                    }
            }catch(InputMismatchException e){
                System.out.println("Erreur: Format d'input invalide, doit être 1, 2");
                scanner.next();
            }
            }while(true);
        }catch( IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Course> runLoadCommand() throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1",1337);
        System.out.println("Connexion Réussie!");
        Scanner scanner = new Scanner(System.in);


        String session = "";

        while(session.equals("")){
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des listeCours :" +
                    "\n1.Autommne" +
                    "\n2.Hiver" +
                    "\n3.Été"
            );
            System.out.print("Choix: ");

            try{
                int choix = scanner.nextInt();

                switch (choix) {
                    case 1:
                        session = "Automne";
                        break;
                    case 2:
                        session = "Hiver";
                        break;
                    case 3:
                        session = "Ete";
                        break;
                    default:
                        System.out.println("Choix de session invalide");

                }
            }catch(InputMismatchException e){
                System.out.println("Erreur: Format d'input invalide, doit être 1, 2, ou 3");
                scanner.next();
            }
        }

        ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());

        writer.writeObject(LOAD_COMMAND +" "+session);
        writer.flush();
        ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());

        ArrayList<Course> listeCours = (ArrayList<Course>) reader.readObject();
        reader.close();
        writer.close();
        clientSocket.close();
        System.out.println("La liste des cours offerts pour la session"+" "+session);

        for(int i=0;i< listeCours.size(); i++){
            Course cours = listeCours.get(i);
            int  numero = i+1;
            System.out.println(numero+". "+cours.getCode() +" "+cours.getName());
        }

        System.out.println("Faites votre choix" +
                "\n1.Consulter les cours offerts à une autre session" +
                "\n2.Inscription à un cours"
        );
        return listeCours;
    }

    public static String runRegisterCommand(ArrayList<Course> listeCours) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        RegistrationForm registration = null;
        Socket clientSocket = new Socket("127.0.0.1",1337);
        System.out.println("Connexion Réussie!");

        ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
        writer.writeObject(REGISTER_COMMAND);
        System.out.println("Veuillez saisir votre prenom :");
        String prenom = scanner.next();
        System.out.println("Veuillez saisir votre nom :");
        String nom = scanner.next();
        System.out.println("Veuillez saisir votre courriel Udem:");
        String courriel = scanner.next();
        String matricule = null;
        //Verification matricule
        while(isNull(matricule)) {
            System.out.println("Veuillez saisir votre matricule:");
            int matriculeInt;
            try{
                 matriculeInt = scanner.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Matricule doit être composé de chiffres. Veuillez réessayer.");
                scanner.next();
                continue;
            }
            if (matriculeInt / 100000 < 1 || matriculeInt /100000 >=10) {
                System.out.println("Matricule doit être composé de 6 chiffres. Veuillez réessayer. ");
                continue;
            }
            matricule = String.valueOf(matriculeInt);
        }
        //Verification code cours
        while(isNull(registration)){
            System.out.println("Veuillez saisir votre code du cours :");
            String codeCours = scanner.next();
            for (Course cours :
                    listeCours) {
                if (cours.getCode().equalsIgnoreCase(codeCours)) {
                    registration = new RegistrationForm(prenom, nom, courriel, matricule, cours);
                    break;
                }
            }
            if (isNull(registration)) {
                System.out.println("Erreur: code de cours inscrit inexistant. Veuillez réessayer. ");
            }
        }
        writer.writeObject(registration);
        String reponse = (String)reader.readObject();
        reader.close();
        writer.close();
        clientSocket.close();
        return reponse;
    }
}