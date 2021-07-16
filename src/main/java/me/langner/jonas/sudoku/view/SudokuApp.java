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

    public Scene getSceneFromRessource(String ressource) throws IOException {
        FXMLLoader loader = getLoader(ressource);

        Pane pane = loader.load();

        if (loader.getController() != null && loader.getController() instanceof Controller) {
            ((Controller)loader.getController()).setSudokuApp(this);
        }

        return new Scene(pane);
    }

    public void showSceneFromRessource(String ressource, String title) throws IOException {
        Scene scene = getSceneFromRessource(ressource);

        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        showSceneFromRessource("GridChooser.fxml", "SudokuSolver");
    }
}
