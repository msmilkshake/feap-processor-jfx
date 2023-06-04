package pt.hmsk.browser;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pt.hmsk.common.Const;
import pt.hmsk.common.Rgx;
import pt.hmsk.common.Url;
import pt.hmsk.common.Xpath;

import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChromeController {
    
    static {
        System.setProperty("webdriver.chrome.driver", Const.chromedriverPath);
    }
    
    private static final long REFRESH_INTERVAL = 15 * 60 * 1000;
    
    private WebDriver driver;
    private JavascriptExecutor js;

    private ExecutorService executor;
    
    private Timer refreshTimer;
    private TimerTask refreshTask;
    
    public ChromeController() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(Const.chromeExecutablePath);
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        js = (JavascriptExecutor) driver;

        executor = Executors.newSingleThreadExecutor();
        
    }

    public void exec(Runnable task) {
        executor.execute(task);
    }
    
    private WebElement getElement(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    private List<WebElement> getElements(String xpath) {
        return driver.findElements(By.xpath(xpath));
    }
    
    public void loginTest(String username, String password) {
        driver.get(Url.loginPage);

        getElement(Xpath.loginReveal).click();
        
        getElement(Xpath.usernameInput).sendKeys(username);
        
        getElement(Xpath.passwordInput).sendKeys(password);
        
        getElement(Xpath.loginButton).click();
        
        new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.or(
                ExpectedConditions.urlContains(Url.listPageShort),
                ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath.loginErrorMessage))
        ));
        
        if (driver.getCurrentUrl().contains(Url.listPageShort)) {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath.listTable)));
            System.out.println("Login Success");
            Matcher m = Pattern.compile("^\\s*(?>\\S+)\\s+(\\S+).*\\s+(\\S+)\\s*$")
                    .matcher(getElement(Xpath.loggedUserName).getText().trim());
            m.matches();
            System.out.println("Logged person: " + m.group(1) + " " + m.group(2));
            startRefreshTask();
        } else {
            System.out.println("Login Failed");
        }
    }

    private void startRefreshTask() {
        cancelRefreshTask();
        refreshTask = new TimerTask() {
            @Override
            public void run() {
                driver.navigate().refresh();
                System.out.println("Page refreshed");
            }
        };
        refreshTimer = new Timer();
        refreshTimer.schedule(refreshTask, REFRESH_INTERVAL, REFRESH_INTERVAL);
    }

    private void cancelRefreshTask() {
        if (refreshTask != null) {
            refreshTask.cancel();
        }
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer.purge();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
    
    public void cleanup() {
        cancelRefreshTask();
        if (driver != null) {
            driver.quit();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
    
    public void clickResult(String number) {
        driver.get(Url.listPage);
        getElement(Xpath.openSearch).click();
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(Xpath.btnSearch))
        );
        
        // Search for the NIPC
        System.out.println("Search area loaded.");
        new Actions(driver).click(getElement(Xpath.supplierNumber)).build().perform();
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        new Actions(driver).sendKeys(number).build().perform();
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                driver -> driver.findElements(By.xpath("//ul[@class='racList']/li")).size() == 1);
        System.out.println("Supplier found.");
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath.firstProviderResult)));
        new Actions(driver).click(getElement(Xpath.firstProviderResult)).build().perform();
        System.out.println("Supplier clicked.");
        getElement(Xpath.btnSearch).click();
        
        // Searching...
        waitForResults();
        
        Matcher m = Pattern.compile(Rgx.pageCount).matcher(getElement(Xpath.pageCount).getText());
        m.matches();
        int pages = Integer.parseInt(m.group(2).trim());
        
        // Results displayed
        List<WebElement> rows =  getElements(Xpath.resultRow);
        for (WebElement el : rows) {
            System.out.println(el.getText());
        }

        for (int i = 1; i < pages; ++i) {
            getElement(Xpath.nextPage).click();
            new Actions(driver).pause(Duration.ofMillis(100)).build().perform();
            waitForResults();
            rows =  getElements(Xpath.resultRow);
            for (WebElement el : rows) {
                System.out.println(el.getText());
            }
        }
    }
    
    private void waitForResults() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath.loadingIcon)));
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.invisibilityOf(getElement(Xpath.loadingIcon)));
        } catch (Exception e) {
            System.out.println("Timed out. Maybe the search is finished.");
        }
    }
    
}
