package edu.uchicago.umairmustafa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ImageShopMain extends Application {

     //public static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        //Root
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/imageshop.fxml"));

        //Scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");

        //Stage
        stage.setTitle("ImageShop");
        stage.setScene(scene);
        stage.show();

        Cc.getInstance().setMainStage(stage);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}