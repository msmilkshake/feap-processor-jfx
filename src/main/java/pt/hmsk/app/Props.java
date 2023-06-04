package pt.hmsk.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Props {
    
    public static final String REMEMBER = "remember";
    public static final String AUTO_LOGIN = "autoLogin";
    public static final String USR = "usr";
    public static final String PWD = "pwd";
    
    public static Properties props;

    public static boolean remember = false;
    public static boolean autoLogin = false;
    public static String usr = "";
    public static String pwd = "";
    
    public static void loadProps() {
        File propsFile = new File("config.properties");
        if (!propsFile.exists()) {
            saveProps();
        } else {
            try (FileInputStream input = new FileInputStream("config.properties")) {
                props = new Properties();
                props.load(input);
                remember = Boolean.parseBoolean(props.getProperty(REMEMBER));
                autoLogin = Boolean.parseBoolean(props.getProperty(AUTO_LOGIN));
                usr = props.getProperty(USR);
                pwd = props.getProperty(PWD);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveProps() {
        try (FileOutputStream output = new FileOutputStream("config.properties")) {
            props = new Properties();
            props.setProperty(REMEMBER, String.valueOf(remember));
            props.setProperty(AUTO_LOGIN, String.valueOf(autoLogin));
            props.setProperty(USR, usr);
            props.setProperty(PWD, pwd);
            props.store(output, "Props");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
