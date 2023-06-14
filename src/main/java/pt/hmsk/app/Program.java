package pt.hmsk.app;

import pt.hmsk.gui.MainWindow;
import pt.hmsk.gui.PreRun;
import pt.hmsk.gui.RunningPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Program extends Application {
    
    public static Stage primaryStage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Program.primaryStage = primaryStage;
        primaryStage.setTitle("FE-AP - Preenchimento de Pagamentos");
        
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("MainWindow.fxml"));
        MainWindow.pane = loader.load();

        loader = new FXMLLoader(PreRun.class.getResource("PreRun.fxml"));
        PreRun.pane = loader.load();

        loader = new FXMLLoader(RunningPane.class.getResource("RunningPane.fxml"));
        RunningPane.pane = loader.load();
        
        
        MainWindow.getInstance().getRunContentPane().getChildren().add(PreRun.pane);
                
        Scene scene = new Scene(MainWindow.pane);
        
        primaryStage.setResizable(false);
        primaryStage.setX(10);
        primaryStage.setY(10);
        primaryStage.setScene(scene);
        
        MainWindow.getInstance().setCloseHook();
        
        primaryStage.show();
    }
}
