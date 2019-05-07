package utilities;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Run extends Application {

  private static Scene scene = new Scene(new AnchorPane());
  public static SceneController sceneController = new SceneController(scene);

  public static void main(String[] args) {
    launch(args);
  }

  public static Controller getCurrentController() {
    return sceneController.getCurrentController();
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    addScreens();
    sceneController.activate("Direction Control");

    primaryStage.setScene(scene);
    primaryStage.setTitle("Gravity is a Myth");
    primaryStage.setResizable(false);
    primaryStage.show();
  }

  private void addScreens() throws IOException {
    sceneController.addScreen("Direction Control",
        sceneController.getPanefromFXML("/fxml/DirectionController.fxml"));

    sceneController.addScreen("Source Control",
        sceneController.getPanefromFXML("/fxml/SourceController.fxml"));
  }
}
