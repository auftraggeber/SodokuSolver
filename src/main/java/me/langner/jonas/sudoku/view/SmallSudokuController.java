package me.langner.jonas.sudoku.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import me.langner.jonas.sudoku.Field;
import me.langner.jonas.sudoku.Sudoku;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SmallSudokuController {

    private static final int SIZE = 3, MIN_INTERVAL = 200, MAX_INTERVAL = 3500;

    @FXML
    private GridPane superGridPane;
    @FXML
    private Slider speedSlider;
    @FXML
    private Button startButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button solutionButton;
    @FXML
    private Label timeLabel;

    private volatile Sudoku sudoku;
    private List<Field> solutions;
    private Timer timer = new Timer();


    @FXML
    private void initialize(){
        speedSlider.setMax(MAX_INTERVAL);
        speedSlider.setMin(MIN_INTERVAL);
    }

    /**
     * Ermittelt die eingegebenen Integer und packt sie in eine Matrix, welche leicht zu lesen ist.
     * @return Die Eingabematrix.
     */
    private int[][] getGridInput() {
        int[][] ints = new int[SIZE*SIZE][SIZE*SIZE];

        for (int i = 0; i < 9; i++) {
            int rowIndex = i / 3;
            int columnIndex = i % 3;
            GridPane pane = (GridPane) superGridPane.getChildren().get(i);

            for (int x = 0; x < SIZE; x++) {

                for (int y = 0; y < SIZE; y++) {
                    int content = getTextFieldInput(pane,x,y);

                    /* füllen */
                    ints[(rowIndex*3) + y][(columnIndex*3)+x] = content;
                }
            }
        }

        return ints;
    }

    /**
     * Ermittelt den eingegebenen Wert.
     * @param pane Das GridPane, welches eine TextArea beinhaltet.
     * @param locX X-Koordinate des Feldes.
     * @param locY Y-Koordinate des Feldes.
     * @return Inhalt des Feldes (oder -1 bei keinem Inhalt).
     */
    private int getTextFieldInput(GridPane pane, int locX, int locY) {
        Node node = pane.getChildren().get(locX + (SIZE * locY));

        if (node != null && node instanceof TextArea) {
            TextArea area = (TextArea) node;

            String content = getNumberText(area.getText());

            try {
                return Integer.parseInt(content);
            }
            catch (RuntimeException ex){
                return -1;
            }
        }
        return -1;
    }

    /**
     * Gibt die Ziffern in einem Text aus.
     * @param text Der ursprüngliche Text.
     * @return Text aus Ziffern.
     */
    private String getNumberText(String text) {
        if (text == null)
            return "";

        return text.replaceAll("/[^0-9]/g","");
    }

    /**
     * Verhindert, dass man Inputs abändert, die fest definiert sind.
     */
    private void lockStickyInputs() {
        for (Node node : superGridPane.getChildren()) {
            if (!(node instanceof GridPane))
                continue;

            GridPane pane = (GridPane) node;

            for (Node textNode : pane.getChildren()) {

                if (textNode instanceof TextArea){
                    TextArea area = (TextArea) textNode;

                    if (!getNumberText(area.getText()).equals("")) {
                        //area.setDisable(true);
                        area.setStyle("-fx-font-weight: 1000; -fx-background-color: cyan");
                        area.setDisable(true);
                    }
                }
            }
        }
    }

    /**
     * Gibt an, ob alles gelöst werden soll;
     * @param recursive Gibt an, ob es automatisch den nächsten Schritt aufrufen soll.
     * @param sleep Gibt an, ob die Methode kurz warten soll, bis der nächste Schritt aufgerufen wird.
     */
    private void fillSolution(boolean recursive, boolean sleep) {
        if (solutions.isEmpty())
            return;

        Field currentSolution = solutions.get(0);
        solutions.remove(0);

        int x = currentSolution.getPosX();
        int y = currentSolution.getPosY();

        int gridX = x / SIZE;
        int gridY = y / SIZE;

        int textX = x % SIZE;
        int textY = y % SIZE;

        try {
            GridPane pane = (GridPane) superGridPane.getChildren().get(gridX + (gridY * 3));
            TextArea area = (TextArea) pane.getChildren().get(textX + (textY * SIZE));

            area.setText(String.valueOf(currentSolution.getValue()));

            String color = (currentSolution.getPossible().size() >= 2) ? "orange" : "green";

            if (currentSolution.getPossible().size() >= 4)
                color = "red";

            area.setStyle("-fx-background-color: " + color);
        }
        catch (ClassCastException ex){

        }

        if (!recursive)
            return;

        if (!sleep) {
            fillSolution(true, false);
            return;
        }

        final double interval = (MAX_INTERVAL + MIN_INTERVAL) - speedSlider.getValue();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fillSolution(true, true);
            }
        }, (long) interval);

    }

    private synchronized boolean isSolving() {
        return sudoku == null;
    }

    /**
     * Setzt die Lösung des Sudokus.
     * @param s Das gelöste Sudoku.
     */
    private void setSolution(Sudoku s) {
        sudoku = s;
        solutions = s.getSolutionPath();
    }

    /**
     * Löst das Sudoku.
     * @param run Das was nach dem Lösen ausgeführt werden soll.
     */
    private void solve(Runnable run) {
        startButton.setDisable(true);
        nextButton.setDisable(true);
        solutionButton.setDisable(true);

        if (sudoku != null) {
            run.run();
            return;
        }

        String startText = startButton.getText();
        String nextText = nextButton.getText();
        String solutionText = solutionButton.getText();

        lockStickyInputs();
        startButton.setText("Berechne...");
        nextButton.setText("Berechne...");
        solutionButton.setText("Berechne...");

        ExecutorService service = Executors.newFixedThreadPool(3);

        service.submit(() -> {

            Sudoku s = new Sudoku(SIZE, getGridInput());

            Platform.runLater(() -> {
                setSolution(s);

                startButton.setText(startText);
                nextButton.setText(nextText);
                solutionButton.setText(solutionText);

                run.run();
            });


        });

        service.submit(() -> {
            long start = System.currentTimeMillis();

            while (isSolving()) {
                System.out.println("HI");
                long now = System.currentTimeMillis();

                int diff = (int) ((int) now - start);

                if (timeLabel != null)
                    Platform.runLater(() -> timeLabel.setText(diff + "ms"));

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (timeLabel != null && sudoku != null)
                Platform.runLater(() -> timeLabel.setText(sudoku.getTimeDifference() + "ms"));
        });



    }


    /*

    EVENTS

     */


    @FXML
    private void handleStart() {
        solve(() -> {
            solutionButton.setDisable(false);

            fillSolution(true, true);
        });

    }

    @FXML
    private void handleNext() {
        solve(() -> {
            startButton.setDisable(false);
            nextButton.setDisable(false);
            solutionButton.setDisable(false);
            fillSolution(false, false);
        });


    }

    @FXML
    private void handleSolution() {
        solve(() -> {
            fillSolution(true,false);
        });

    }

    @FXML
    private void handleReset() {
        for (Node superChildren : superGridPane.getChildren()) {
            if (!(superChildren instanceof GridPane))
                continue;

            GridPane pane = (GridPane) superChildren;

            for (Node textNode : pane.getChildren()) {

                if (textNode instanceof TextArea) {
                    TextArea area = (TextArea) textNode;
                    area.setText("");
                    area.setStyle("");
                    area.setDisable(false);
                }
            }
        }

        sudoku = null;
        solutions = new ArrayList<>();
        startButton.setDisable(false);
        nextButton.setDisable(false);
        solutionButton.setDisable(false);
        timeLabel.setText("");
    }

    @FXML
    private void handleSoftReset() {
        for (Node superChildren : superGridPane.getChildren()) {
            if (!(superChildren instanceof GridPane))
                continue;

            GridPane pane = (GridPane) superChildren;

            for (Node textNode : pane.getChildren()) {

                if (textNode instanceof TextArea) {
                    TextArea area = (TextArea) textNode;

                    if (!area.isDisable())
                        area.setText("");
                    area.setStyle("");
                    area.setDisable(false);
                }
            }
        }

        sudoku = null;
        solutions = new ArrayList<>();
        startButton.setDisable(false);
        nextButton.setDisable(false);
        solutionButton.setDisable(false);
        timeLabel.setText("");
    }
}
