package com.sekhmet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.sekhmet.db.DB; // ✅ import your DB class
import com.sekhmet.bootstrap.DatabaseBootstrap;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // ✅ Ensure database structure exists
        DB.init();

        // ✅ Ensure default admin user exists
        DatabaseBootstrap.ensureDefaultAdmin();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/flat-theme.css").toExternalForm());
        primaryStage.setTitle("Hospital Management System FX (JavaFX 21)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
