
package org.openjfx.mainpack;

import org.openjfx.controllerpack.MainController;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class Main extends Application{


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainDesign.fxml"));

        stage.setTitle("EncryptionGUI");
        stage.setResizable(false);
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setStage(stage);
        Scene primaryScene = new Scene(root);
        stage.setScene(primaryScene);
        stage.show();
    }

}
