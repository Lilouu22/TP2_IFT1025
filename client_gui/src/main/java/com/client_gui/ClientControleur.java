package com.client_gui;

import models.Course;
import models.RegistrationForm;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Cette classe représente la partie contrôleur de la méthode MVC
 * d'une application client qui interagit avec un server.
 * Elle fournit des méthodes pour permettre gérer les méthodes
 * d'enregistrement et de chargements.
 */

public class ClientControleur {

    /**
     * attribut qui représente l'enregistrement au cours
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * attribut qui représente le chargement de cours
     */
    public final static String LOAD_COMMAND = "CHARGER";

    private ClientApplication vue;

    /**
     *  Construire un nouvel objet contrôleur du client
     * @param vue la composante vue(graphique) de l'application
     */

    public ClientControleur(ClientApplication vue) {
        this.vue = vue;
    }

    /**
     * Envoie une requête au server pour télécharger la liste des cours pour la session choisie
     * @param session session pour laquelle les cours disposnibles vont être télécharger
     * @throws IOException erreur pendant réception ou envoie de données
     * @throws ClassNotFoundException erreur si la classe sérialisée n'est pas trouvée
     */


    public void runLoadCommand(String session) throws IOException, ClassNotFoundException {
        Socket clientSocket = new Socket("127.0.0.1",1337);
        System.out.println("Connexion Réussie!");


        ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());

        writer.writeObject(LOAD_COMMAND +" "+session);
        writer.flush();
        ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());

        ArrayList<Course> listeCours = (ArrayList<Course>) reader.readObject();
        System.out.println(listeCours.get(0));
        reader.close();
        writer.close();
        clientSocket.close();
        System.out.println("La liste des cours offerts pour la session"+" "+session);

        for(int i=0;i< listeCours.size(); i++) {
            Course cours = listeCours.get(i);
            vue.getTable().getItems().add(cours);

        }
    }

    /**
     *  Envoie une requête d'enregistrement au server
     * @throws IOException si erreur réception/envoie de données
     * @throws ClassNotFoundException si classe sérialisée non trouvée
     */

    public void runRegisterCommand() throws IOException, ClassNotFoundException {

        RegistrationForm registration = null;
        Socket clientSocket = new Socket("127.0.0.1",1337);
        System.out.println("Connexion Réussie!");

        ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream reader = new ObjectInputStream(clientSocket.getInputStream());
        writer.writeObject(REGISTER_COMMAND);
        String prenom = vue.getTextField1().getText();
        String nom = vue.getTextField2().getText();
        String courriel = vue.getTextField3().getText();
        String matricule = vue.getTextField4().getText();
        //Verification matricule
        int matriculeInt;
        try {
            matriculeInt = Integer.parseInt(matricule);
        }catch (NumberFormatException e){
            showErrorAlert("Matricule doit être composé de chiffres. Veuillez réessayer.");
            reader.close();
            writer.close();
            clientSocket.close();
            return;
        }
        if (matriculeInt / 100000 < 1 || matriculeInt /100000 >=10) {
            showErrorAlert("Matricule doit être composé de 6 chiffres. Veuillez réessayer. ");
            reader.close();
            writer.close();
            clientSocket.close();
            return;
        }


        Course cours = vue.getTable().getSelectionModel().getSelectedItem();
        if(isNull(cours)){
            showErrorAlert("Aucun cours n'a été sélectionné. Veuillez réessayer. ");
            reader.close();
            writer.close();
            clientSocket.close();
            return;
        }

        registration = new RegistrationForm(prenom, nom, courriel, matricule, cours);
        writer.writeObject(registration);
        String reponse = (String)reader.readObject();
        showSuccessAlert(reponse);
        reader.close();
        writer.close();
        clientSocket.close();
        clearInputFields();
    }

    /**
     * Méthode pour montrer dans la vue une erreur
     * @param message envoie message d'erreur à l'utilisateur
     */

    public void showErrorAlert(String message){
        Alert alert = vue.getAlerte();
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Méthode pour montrer graphiquement le type d'erreur
     * @param message information sur erreur
     */
    public void showSuccessAlert(String message){
        Alert alert = vue.getAlerte();
        alert.setAlertType(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Méthode pour vider les champs de réponse des utilisateurs
     */

    public void clearInputFields(){
        vue.getTextField1().clear();
        vue.getTextField2().clear();
        vue.getTextField3().clear();
        vue.getTextField4().clear();
    }
}