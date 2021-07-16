package me.langner.jonas.sudoku.view;

import me.langner.jonas.sudoku.Sudoku;

public class Controller {

    private SudokuApp sudokuApp;

    public void setSudokuApp(SudokuApp sudokuApp) {
        this.sudokuApp = sudokuApp;
    }

    public SudokuApp getSudokuApp() {
        return sudokuApp;
    }
}
