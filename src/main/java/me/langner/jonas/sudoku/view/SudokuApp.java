package me.langner.jonas.sudoku.view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import me.langner.jonas.sudoku.Field;
import me.langner.jonas.sudoku.Sudoku;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SudokuApp extends Application {

    private Stage primaryStage;
    private static final String PACKAGE_PATHS = "src/main/java/me/langner/jonas/sudoku/view/";

    public static void main(String[] args) {
        launch(args);
    }

    public static FXMLLoader getLoader(String ressource) throws MalformedURLException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(SudokuApp.class.getResource(ressource));

            loader	.getLocation()
                    .getContent();

            return loader;
        } catch (Exception ex) {
            try {
                URL url = new File(PACKAGE_PATHS + ressource).toURI()
                        .toURL();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(url);

                return loader;
            } catch (IOException ex2) {
                throw ex2;
            }
        }
    }

    public Map<Scene, Controller> getSceneFromRessource(String ressource) throws IOException {
        FXMLLoader loader = getLoader(ressource);

        Pane pane = loader.load();
        Map<Scene, Controller> map = new HashMap<>();

        Scene scene = new Scene(pane);

        if (loader.getController() != null && loader.getController() instanceof Controller) {
            ((Controller)loader.getController()).setSudokuApp(this);
            map.put(scene, loader.getController());
        }else if (loader.getController() != null)
            map.put(scene, loader.getController());
        else
            map.put(scene, null);



        return map;
    }

    public Controller showSceneFromRessource(String ressource, String title) throws IOException {
        Map<Scene, Controller> map = getSceneFromRessource(ressource);
        Scene scene = map.keySet().stream().findFirst().get();
        Controller c = map.get(scene);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();


        return map.get(scene);
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        showSceneFromRessource("GridChooser.fxml", "SudokuSolver");
    }
}
