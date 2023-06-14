package pt.hmsk.logic;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import pt.hmsk.common.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tabs {
    private WebDriver driver;
    private Map<String, String> tabHandles = new HashMap<>();

    public Tabs(WebDriver driver) {
        this.driver = driver;
        tabHandles.put(Const.mainTab, driver.getWindowHandle());
    }
    
    public void registerTab(String tabId) {
        for (String handle : driver.getWindowHandles()) {
            if (!tabHandles.containsValue(handle)) {
                tabHandles.put(tabId, handle);
                System.out.println("Registered tabId:" + tabId + "; handle: " + handle);
            }
        }
    }

    public void switchTo(String tabId) {
        driver.switchTo().window(tabHandles.get(tabId));
        System.out.println("Switched to tabId: " + tabId + "; handle: " + tabHandles.get(tabId));
    }

    public void close() {
        driver.close();
    }
}