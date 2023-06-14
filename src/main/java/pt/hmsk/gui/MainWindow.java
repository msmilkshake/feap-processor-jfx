package pt.hmsk.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pt.hmsk.app.Program;
import pt.hmsk.app.Props;
import pt.hmsk.browser.ChromeController;
import pt.hmsk.logic.ProgramLogic;
import pt.hmsk.logic.State;
import pt.hmsk.security.Security;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainWindow {

    public static MainWindow instance;
    public static Parent pane;


    public static MainWindow getInstance() {
        return instance;
    }

    @FXML
    private BorderPane root;
    @FXML
    private VBox runContentPane;
    @FXML
    private Label lblSession;
    @FXML
    private Label lblLoadedFile;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private CheckBox cbAutoLogin;
    @FXML
    private CheckBox cbRemember;
    @FXML
    private Button btnLogin;
    @FXML
    public TextField txtFilePicker;
    @FXML
    public Button btnLoadFile;
    @FXML
    public ImageView spinner;

    private ChromeController controller;
    private ProgramLogic logic = new ProgramLogic();
    private ExecutorService executor;
    private String filepath = "";

    public MainWindow() {
        instance = this;
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            controller = new ChromeController();
            if (Props.autoLogin) {
                btnLoginOnClicked();
            }
        });
    }

    @FXML
    public void initialize() throws Exception {

        populateLoginForm();
        setVisibilities();
        setListeners();

        System.out.println(root.getScene());
    }

    private void setListeners() {
        txtUsername.textProperty().addListener((ob, ov, nv) -> {
            try {
                Props.usr = Security.getEncryptedString(txtUsername.getText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (cbRemember.isSelected()) {
                Props.saveProps();
            }
        });

        txtPassword.textProperty().addListener((ob, ov, nv) -> {
            try {
                Props.pwd = Security.getEncryptedString(txtPassword.getText());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (cbRemember.isSelected()) {
                Props.saveProps();
            }
        });
    }

    public void setCloseHook() {
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setOnCloseRequest(event -> {
            event.consume();

            if (controller.getDriver() != null) {
                controller.exec(() -> controller.cleanup());
                System.out.println("Driver terminated gracefully");
            }

            if (Props.remember) {
                try {
                    Props.usr = Security.getEncryptedString(txtUsername.getText());
                    Props.pwd = Security.getEncryptedString(txtPassword.getText());
                    Props.saveProps();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            Platform.runLater(stage::close);
            System.exit(0);
        });
    }

    public VBox getRunContentPane() {
        return runContentPane;
    }

    public void test() {
        WebDriverWait wait = new WebDriverWait(controller.getDriver(), Duration.ofMinutes(1));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("ul.racList > li"), 1));
        System.out.println("Target NIPC Found");
    }

    public void startRunning() throws IOException {
        instance.runContentPane.getChildren().remove(0);

        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("RunningPane.fxml"));
        Parent runningParent = loader.load();
    }

    private void setVisibilities() throws Exception {
        lblSession.setVisible(
                logic.getState() == State.LOGGED_OUT || logic.getState() == State.LOGGED_IN);
        switch (logic.getState()) {
            case LOGGED_OUT:
                lblSession.setVisible(true);
                lblSession.setText("Sem sessão iniciada");
                spinner.setVisible(false);
                txtUsername.setDisable(false);
                txtPassword.setDisable(false);
                btnLogin.setDisable(false);
                break;
            case LOGGING_IN:
                lblSession.setVisible(false);
                spinner.setVisible(true);
                txtUsername.setDisable(true);
                txtPassword.setDisable(true);
                btnLogin.setDisable(true);
        }
    }

    private void populateLoginForm() throws Exception {
        if (Props.remember) {
            try {
                txtUsername.setText(Security.getDecryptedString(Props.usr));
                txtPassword.setText(Security.getDecryptedString(Props.pwd));
            } catch (Exception e) {
                txtUsername.setText("");
                txtPassword.setText("");
            }
            cbRemember.setSelected(true);
            cbAutoLogin.setDisable(false);
        } else {
            cbAutoLogin.setDisable(true);
        }
        cbAutoLogin.setSelected(Props.autoLogin);

        cbRemember.setOnAction((event) -> {
            if (cbRemember.isSelected()) {
                Props.remember = true;
                cbAutoLogin.setDisable(false);
            } else {
                cbAutoLogin.setSelected(false);
                cbAutoLogin.setDisable(true);
                Props.remember = false;
                Props.autoLogin = false;
                Props.usr = "";
                Props.pwd = "";
            }
            Props.saveProps();
        });

        cbAutoLogin.setOnAction((event) -> {
            if (cbAutoLogin.isSelected()) {
                Props.autoLogin = true;
            } else {
                Props.autoLogin = false;
            }
            Props.saveProps();
        });
    }

    public void btnLoginOnClicked() {
        controller.exec(() -> {
            btnLogin.setDisable(true);
            lblSession.setVisible(false);
            spinner.setVisible(true);
            controller.loginTest(txtUsername.getText(), txtPassword.getText());
        });
    }

    public void cbRememberOnClicked() {

    }

    public void cbAutoLoginOnClicked() {

    }

    public static ChromeController getController() {
        return instance.controller;
    }

    public void txtFilePickerOnClicked(MouseEvent mouseEvent) {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        java.io.File desktopDirectory = fileSystemView.getHomeDirectory();

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(desktopDirectory);
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel - xlsx", "*.xlsx")
        );
        File selectedFile = chooser.showOpenDialog(Program.primaryStage);
        String filename = txtFilePicker.getText();
        if (selectedFile != null) {
            filepath = selectedFile.getAbsolutePath();
            filename = selectedFile.getName();
            PreRun.getInstance().userAddedFile(true);
        }
        txtFilePicker.setText(filename);
    }

    public String getFilepath() {
        return filepath;
    }

    public void setSession(String message) {
        Platform.runLater(() -> {
            spinner.setVisible(false);
            lblSession.setVisible(true);
            lblSession.setText(message);
            PreRun.getInstance().userLoggedIn(true);
        });
    }
}
