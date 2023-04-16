//VUE
package com.client_gui;

import models.Course;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.stage.Stage;
import javafx.scene.text.*;

import javafx.scene.control.Button;

import java.io.IOException;

/**
 * Cette classe représente la partie VUE d'un modèle MVC pour une application client qui permet aux utilisateurs
 * de s'enregistrer à des cours en se connectant à un serveur qui fournit la liste.
 * Cette classe extend une classe application qui contient des éléments GUI telles que TableView qui permettent
 * de visualiser les cours disponibles.
 * Les TextFields sont pour que l'utilisateur rentre ces informations
 * Les ComboBox servent à sélectionner la session d'inscription voulue.
 * Les boutons servent à charger et soumettre l'enregistrement.
 * Un objet contrôleur permet de gérer la communication avec le server et les events handlers.
 * La session sélectionnée et l'enregistrement au cours sont enregistrés dans des variables d'instanciations.
 *  La classe contient des getteurs pour accéder aux informations rentrées par l'utilisateur.
 */

public class ClientApplication extends Application {
    private ClientControleur controleur;
    private String sessionSelectione = "hiver";
    private TableView<Course> table;
    private TextField textField1;
    private TextField textField2;
    private TextField textField3;
    private TextField textField4;

    private Alert alerte;

    /**
     * Retourne un objet d'Alerte utilisé pour envoyer des messages d'erreurs dans le GUI
     * @return @return objet Alerte
     */

    public Alert getAlerte() {
        return alerte;
    }
    /**
     * Initialisation des composants GUI et met en place les boutons
     * @param stage objet Stage qui represente la principale fenêtre de l'application
     * @throws IOException  si une erreur survient durant le chargement du fichier FXML pour le GUI
     */

    @Override
    public void start(Stage stage) throws IOException {
        //Titre du stage
        stage.setTitle("Inscription UdeM");

        //Creation du controleur
        controleur = new ClientControleur(this);

        //Initialisation de l'alerte
        alerte = new Alert(Alert.AlertType.NONE);

        //Boutons initialisés
        Button bouton = new Button();
        bouton.setText("Charger");
        Button bouton2 =  new Button();
        bouton2.setText("Envoyer");

        //Combo box
        String session[] = { "Automne", "Hiver", "Ete"};
        ComboBox comboBox = new ComboBox(FXCollections.observableArrayList(session));
        comboBox.setValue("Hiver");

        // bouton.setOnAction(this);
        //bouton2.setOnAction(this);// bouton pour handle ce bouton dans cette classe
        // anonymous inner class ou e -> sout
        GridPane layout  = new GridPane();
        GridPane gridInfoUser = new GridPane();

        //Formulaire inscription à droite
        Label titleForm= new Label(" Formulaire D'inscription ");
        titleForm.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        // Initialisation des textbox et label pour infos user
        Label label1 = new Label(" Prénom ");
        textField1 = new TextField();

        Label label2 = new Label(" Nom ");
        textField2 = new TextField();

        Label label3 = new Label(" Email ");
        textField3 = new TextField();
        Label label4 = new Label(" Matricule ");
        textField4 = new TextField();

        gridInfoUser.add(label1,0,0);
        gridInfoUser.add(textField1,1,0);
        gridInfoUser.add(label2,0,1);
        gridInfoUser.add(textField2,1,1);
        gridInfoUser.add(label3,0,2);
        gridInfoUser.add(textField3,1,2);
        gridInfoUser.add(label4,0,3);
        gridInfoUser.add(textField4,1,3);
        gridInfoUser.setHgap(10);
        gridInfoUser.setVgap(10);
        //gridInfoUser.add(bouton2,1,4);

        //Divise en 2 la scene
        VBox vb1 = new VBox();
        vb1.setSpacing(10);
        vb1.setAlignment(Pos.TOP_CENTER);
        vb1.getChildren().addAll(titleForm,gridInfoUser,bouton2);

        //partie gauche
        HBox hbox = new HBox();
        Label titleList= new Label(" Liste des cours");
        titleList.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        hbox.getChildren().add(titleList);
        hbox.setAlignment(Pos.TOP_CENTER);

        table = new TableView<Course>();
        table.setEditable(true);
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> coursCol = new TableColumn("Cours");
        coursCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().addAll(codeCol, coursCol);
        HBox hbox2 = new HBox();
        hbox2.getChildren().addAll(comboBox,bouton);
        hbox2.setSpacing(10);


        VBox vb2= new VBox();
        vb2.getChildren().addAll(hbox,table,hbox2);
        layout.add(vb1,1,0);
        layout.add(vb2,0,0);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setHgap(10);

        Scene scene = new Scene(layout,500,500);
        stage.setScene(scene);
        stage.show();

        bouton.setOnAction((actionEvent)-> {
            try{
                table.getItems().clear();
                controleur.runLoadCommand((String) comboBox.getValue());
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });

        bouton2.setOnAction(actionEvent -> {
            try {
                controleur.runRegisterCommand();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     *  Lance l'application
     * @param args paramètre d'entrée de commande
     */

    public static void main(String[] args) {
        launch();
    }

    /**
     *  Méthode utilisée pour montrer graphiquement les cours disponibles
     * @return objet TableView pour afficher graphiquement les cours
     */

    public TableView<Course> getTable() {
        return table;
    }

    /**
     *  Méthode utilisée pour obtenir le prénom de l'utilisateur
     * @return TextField Object utilisée pour le prénom de l'utilisateur
     */

    public TextField getTextField1() {
        return textField1;
    }

    /**
     * Méthode utilisée pour obtenir le nom de l'utilisateur
     * @return TextField Object utilisée pour le nom de l'utilisateur
     */

    public TextField getTextField2() {
        return textField2;
    }

    /**
     * Méthode utilisée pour obtenir l'email de l'utilisateur
     * @return TextField Object utilisée pour l'email de l'utilisateur
     */
    public TextField getTextField3() {
        return textField3;
    }

    /**
     * Méthode utilisée pour obtenir le matricule de l'utilisateur
     * @return TextField Object utilisée pour le matricule de l'utilisateur
     */
    public TextField getTextField4() {
        return textField4;
    }
}