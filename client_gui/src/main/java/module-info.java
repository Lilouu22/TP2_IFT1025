module com.client_gui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.client_gui to javafx.fxml;
    opens com.client_gui.models to javafx.base;
    exports com.client_gui;
}