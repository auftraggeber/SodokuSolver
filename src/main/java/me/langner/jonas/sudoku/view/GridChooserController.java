package me.langner.jonas.sudoku.view;

import javafx.fxml.FXML;
import me.langner.jonas.sudoku.Sudoku;

import java.io.IOException;

public class GridChooserController extends Controller {


    @FXML
    private void handleSmallSudoku() {
        try {
            Controller c = getSudokuApp().showSceneFromRessource("SmallSudoku.fxml", "3x3 Sudoku");

            if (c != null && c instanceof SudokuController)
                ((SudokuController) c).setSize(3);
            else System.out.println(c.getClass());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @FXML
    private void handleMediumSudoku() {
        try {
            Controller c = getSudokuApp().showSceneFromRessource("MediumSudoku.fxml", "4x4 Sudoku");

            if (c != null && c instanceof SudokuController)
                ((SudokuController) c).setSize(4);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @FXML
    private void handleLargeSudoku() {
        try {
            Controller c = getSudokuApp().showSceneFromRessource("LargeSudoku.fxml", "5x5 Sudoku");

            if (c != null && c instanceof SudokuController)
                ((SudokuController) c).setSize(5);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
