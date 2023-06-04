package pt.hmsk.gui;

import javafx.scene.Parent;

public class RunningPane {

    public static RunningPane instance;
    public static Parent pane;


    public static RunningPane getInstance() {
        return instance;
    }
    
    public RunningPane() {
        instance = this;
    }
    
}
