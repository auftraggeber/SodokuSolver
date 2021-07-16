package me.langner.jonas.sudoku.view;

import javafx.fxml.FXML;

import java.io.IOException;

public class GridChooserController extends Controller {


    @FXML
    private void handleSmallSudoku() {
        try {
            getSudokuApp().showSceneFromRessource("SmallSudoku.fxml", "3x3 Sudoku");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @FXML
    private void handleMediumSudoku() {

    }

    @FXML
    private void handleLargeSudoku() {

    }

}
