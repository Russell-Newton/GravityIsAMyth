package utilities;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class SceneController {

  private HashMap<String, AnchorPane> screenMap = new HashMap<>();
  private HashMap<String, Controller> controllerMap = new HashMap<>();
  private Scene main;
  private Controller currentController;

  public SceneController(Scene main) {
    this.main = main;
  }

  /**
   * @param name - key
   * @param pane - FXMLLoader.load(getClass().getResource("something.fxml"))
   */
  public void addScreen(String name, AnchorPane pane) {
    screenMap.put(name, pane);
  }

  public void removeScreen(String name) {
    screenMap.remove(name);
  }

  public void activate(String name) {
    main.setRoot(screenMap.get(name));
    currentController = controllerMap.get(name);
  }

  public AnchorPane getPanefromFXML(String fileName) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fileName));
    controllerMap.put(fileName, loader.getController());
    return loader.load();
  }

  public Controller getCurrentController() {
    return currentController;
  }
}
