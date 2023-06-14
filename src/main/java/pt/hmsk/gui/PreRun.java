package pt.hmsk.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import pt.hmsk.browser.ChromeController;

public class PreRun {

    public static PreRun instance;
    public static Parent pane;

    @FXML
    private Button btnStart;
    @FXML
    private TextField txtNIPC;
    
    private boolean isLoggedIn = false;
    private boolean hasExcelFile = false;

    public static PreRun getInstance() {
        return instance;
    }
    
    public PreRun() {
        instance = this;
    }
    
    public void userLoggedIn(boolean val) {
        isLoggedIn = val;
        enableInputs();
    }
    public void userAddedFile(boolean val) {
        hasExcelFile = val;
        enableInputs();
    }
    
    public void enableInputs() {
        if (isLoggedIn && hasExcelFile) {
            txtNIPC.setDisable(false);
            btnStart.setDisable(false);
        }
    }
    
    public void btnStartOnClicked() {
        MainWindow.instance.getRunContentPane().getChildren().remove(0);
        MainWindow.instance.getRunContentPane().getChildren().add(RunningPane.pane);

        RunningPane.pane.getScene().getWindow().sizeToScene();

        for (int i = 0; i < ((AnchorPane) (MainWindow.instance.getRunContentPane().getChildren().get(0))).getChildren().size(); ++i) {
            System.out.println(((AnchorPane) (MainWindow.instance.getRunContentPane().getChildren().get(0))).getChildren().get(i));
        }
        
        String nipc = txtNIPC.getText();
        ChromeController c = MainWindow.getController();
        c.exec(() -> c.clickResult(nipc));
    }
}
