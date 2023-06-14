package pt.hmsk.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;


public class RunningPane {

    public static RunningPane instance;
    public static Parent pane;

    @FXML
    public TextArea logArea;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TableView<String> table;
    @FXML
    public ProgressBar glowPB;

    public static RunningPane getInstance() {
        return instance;
    }

    public RunningPane() {
        instance = this;
    }

    @FXML
    public void initialize() {
        logArea.setText("");
        progressBar.setProgress(0);
    }

    public void updateProgressBar(double progress) {
        glowPB.setVisible(true);
        progressBar.setProgress(progress);
    }

    public void finishProgressBar() {
        glowPB.setVisible(false);
        progressBar.setProgress(1);
    }
    
    public void logProgress(String message) {
        logArea.appendText(message + "\n");
    }

}
