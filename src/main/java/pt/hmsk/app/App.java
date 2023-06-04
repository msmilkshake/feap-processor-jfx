package pt.hmsk.app;

import javafx.application.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException {
        
        init();
        Application.launch(Program.class, args);
    }

    private static void init() {
        Props.loadProps();
    }
}
