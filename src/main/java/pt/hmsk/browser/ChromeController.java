package pt.hmsk.browser;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pt.hmsk.app.App;
import pt.hmsk.common.*;
import pt.hmsk.gui.MainWindow;
import pt.hmsk.gui.RunningPane;
import pt.hmsk.logic.Excel;
import pt.hmsk.logic.Tabs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.*;
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
    private Tabs tabs;
    private WebDriverWait wait;
    private WebDriverWait tinyWait;
    private WebDriverWait longWait;

    private ExecutorService executor;

    private Timer refreshTimer;
    private TimerTask refreshTask;

    private Map<String, String[]> dataMap;

    public ChromeController() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary(Const.chromeExecutablePath);
        if (App.isHeadless) {
            options.addArguments("--headless");
        }
        options.addArguments("--window-size=1000,650");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));

        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        tinyWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        longWait = new WebDriverWait(driver, Duration.ofSeconds(180));
        executor = Executors.newSingleThreadExecutor();

        tabs = new Tabs(driver);
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

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains(Url.listPageShort),
                ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath.loginErrorMessage))
        ));

        if (driver.getCurrentUrl().contains(Url.listPageShort)) {
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(Xpath.listTable)));
            System.out.println("Login Success");
            Matcher m = Pattern.compile("^\\s*(?>\\S+)\\s+(\\S+).*\\s+(\\S+)\\s*$")
                    .matcher(getElement(Xpath.loggedUserName).getText().trim());
            m.matches();
            String name = m.group(1) + " " + m.group(2);
            System.out.println("Logged person: " + name);
            MainWindow.getInstance().setSession("Sessão iniciada com: " + name);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            startRefreshTask();
        } else {
            MainWindow.getInstance().setSession("Falha no Login. Por favor, reinicie a Aplicação.");
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Login Failed");
            MainWindow.getInstance().unlockLoginButton();
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
            refreshTask = null;
        }
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer.purge();
            refreshTimer = null;
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

        try {
            // Assuming the PowerShell script is named 'killChromiumProcesses.ps1' and located in the same directory
            String filePath = new File("cleanup.ps1").getAbsolutePath();

            // Create ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", filePath);

            // Start the process
            Process process = pb.start();

            // Get the input stream
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read the output from the command
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Wait for the process to finish and get the exit value
            int exitValue = process.waitFor();
            System.out.println("Exit value is: " + exitValue);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clickResult(String number) {
        dataMap = Excel.getDataMap(MainWindow.instance.getFilepath());

        RunningPane.instance.logProgress("A pesquisar pelo fornecedor com NIPC: " + number);
        RunningPane.instance.logProgress("A abrir a página de pesquisa...");
        RunningPane.instance.updateProgressBar(0.025);
        cancelRefreshTask();
        driver.get(Url.listPage);
        getElement(Xpath.openSearch).click();
        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.xpath(Xpath.btnSearch))
        );

        System.out.println("Search area loaded.");
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        RunningPane.instance.logProgress("Página de pesquisa aberta.");

        RunningPane.instance.logProgress("A configurar os filtros de pesquisa...");
        // Reset filters
        getElement(Xpath.btnResetFilters).click();
        waitForSpinner();
        System.out.println("Filters resetted.");

        // State Selector
        getElement(Xpath.creditNote).click();
        new Actions(driver).pause(Duration.ofMillis(20)).build().perform();
        getElement(Xpath.debitNote).click();
        new Actions(driver).pause(Duration.ofMillis(20)).build().perform();
        getElement(Xpath.stateFilterAll).click();
        new Actions(driver).pause(Duration.ofMillis(20)).build().perform();
        getElement(Xpath.stateFilterAll).click();
        new Actions(driver).pause(Duration.ofMillis(20)).build().perform();
        getElement(Xpath.stateFilterProcessed).click();
        System.out.println("State Set.");

        // Search for the NIPC
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        new Actions(driver).click(getElement(Xpath.supplierNumber)).build().perform();
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        new Actions(driver).sendKeys(number).build().perform();
        new Actions(driver).pause(Duration.ofMillis(250)).build().perform();
        wait.until(driver ->
                driver.findElements(By.xpath(Xpath.supplierDropDown)).size() == 1);
        System.out.println("Supplier found.");
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(Xpath.firstProviderResult)));
        new Actions(driver).click(getElement(Xpath.firstProviderResult)).build().perform();
        System.out.println("Supplier clicked.");
        getElement(Xpath.btnSearch).click();
        RunningPane.instance.logProgress("Filtros de pesquisa configurados.");

        RunningPane.instance.logProgress("A pesquisar pelas faturas...");
        // Searching...
        waitForSpinner();

        Matcher m = Pattern.compile(Rgx.pageCount).matcher(getElement(Xpath.pageCount).getText());
        m.matches();
        int pages = Integer.parseInt(m.group(2).trim());
        int totalRecords = Integer.parseInt(m.group(3).trim());
        RunningPane.instance.logProgress("Pesquisa concluida. Foram encontradas " + totalRecords +
                " faturas para processamento.");

        // Results displayed
        for (int record = 0, offset = 0; record < totalRecords; ++record) {
            if (offset > 19) {
                System.out.println("Changing page...");
                offset = 0;
                getElement(Xpath.nextPage).click();
                waitForSpinner();
            }
            RunningPane.instance.logProgress("Processamento #" + record + "...");
            new Actions(driver).pause(Duration.ofMillis(250)).build().perform();

            List<WebElement> rows = getElements(Xpath.resultRow);
            
            if (rows.isEmpty()) {
                RunningPane.instance.logProgress("Não existem faturas para processar.");
                break;
            }
            
            WebElement el = rows.get(offset);
            String invoiceName = el.getText();

            Matcher m2 = Pattern.compile(Rgx.invoiceCapture).matcher(invoiceName);
            m2.matches();
            String invoiceNumber = m2.group(1);

            RunningPane.instance.logProgress("A verificar a fatura: " + invoiceName + "...");
            if (!dataMap.containsKey(invoiceNumber)) {
                RunningPane.instance.logProgress("A fatura " + invoiceName + " não tem dados associados no mapa Excel.");
                ++offset;
                new Actions(driver).pause(Duration.ofMillis(1000)).build().perform();
                continue;
            }
            System.out.println("Processing invoice: " + invoiceName);
            System.out.println("Double clicking invoice.");
            new Actions(driver).doubleClick(el).build().perform();
            System.out.println("Double click done.");
            RunningPane.instance.logProgress("A abrir os detalhes da fatura: " + invoiceName + "... (Pode demorar)");

            waitForSpinner();

            longWait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(Xpath.paymentInfoButton)));
            RunningPane.instance.logProgress("Fatura " + invoiceName + "aberta.");
            String[] fillData = dataMap.get(invoiceNumber);

            RunningPane.instance.logProgress("A preencher os dados: \n" +
                    " - Doc. Emissão Pagamento: " + fillData[0] + "\n" +
                    " - Data Emissão Pagamento: " + fillData[1] + "\n" +
                    " - Data valor pagamento: " + fillData[2] + "\n" +
                    " - Referência pagamento: " + fillData[3] + "\n");
            System.out.println("Invoice Details loaded and ready.");

            getElement(Xpath.paymentInfoButton).click();
            System.out.println("Clicked the Payment Info tab.");
            new Actions(driver).pause(Duration.ofMillis(100)).build().perform();

            wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath(Xpath.inputDocEmsPag)));
            
            getElement(Xpath.inputDocEmsPag).sendKeys(fillData[0]);
            getElement(Xpath.inputDatEmsPag).sendKeys(fillData[1]);
            getElement(Xpath.inputDatValPag).sendKeys(fillData[2]);
            getElement(Xpath.inputRefPag).sendKeys(fillData[3]);

            new Actions(driver).pause(Duration.ofMillis(250)).build().perform();

            WebElement selectElement = getElement(Xpath.actionSelect);
            Select select = new Select(selectElement);

            List<WebElement> options = select.getOptions();

            // Iterate through the options to find a match
            System.out.println("Checking if execute options are valid.");
            boolean optionFound = false;
            for (WebElement option : options) {
                if (option.getAttribute("value").equals("25") &&
                        option.getText().equals("Emitir Pagamento")) {
                    optionFound = true;
                    break;
                }
            }

            if (!optionFound) {
                System.out.println("The option was not valid. Continuing to the next iteration.");
                RunningPane.instance.logProgress("A opção Emitir Pagamento não tem " +
                        "o ID esperado. Reportar ao Filipe.");
                ++offset;
                new Actions(driver).pause(Duration.ofMillis(1000)).build().perform();
                continue;
            } else {
                System.out.println("Option is valid and was found.");
                System.out.println("Selecting the correct option.");
                select.selectByValue("25");
            }
            new Actions(driver).pause(Duration.ofMillis(100)).build().perform();
            System.out.println("Clicking execute.");
            getElement(Xpath.execute).click();

            System.out.println("Clicking the OK Popup");
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(Xpath.confirmButton)));
            getElement(Xpath.confirmButton).click();
            new Actions(driver).pause(Duration.ofMillis(250)).build().perform();

            waitForSpinner();

            System.out.println("Execute done.");

            // Wait - Going back
            longWait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(Xpath.pageCount)));

            new Actions(driver).pause(Duration.ofMillis(100)).build().perform();

            double progress = 1.0 * record / totalRecords * 0.95 + 0.025;

            RunningPane.instance.updateProgressBar(progress);
            RunningPane.instance.logProgress("Fatura: " + invoiceName + " processada.");

            RunningPane.instance.logProgress("Processamento #" + record + " completo.");
        }
        RunningPane.instance.finishProgressBar();
        startRefreshTask();
    }

    private void waitForSpinner() {
        System.out.println("Spinner wait started.");
        try {
            tinyWait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath(Xpath.loadingIcon)));
            try {
                longWait.until(ExpectedConditions
                        .invisibilityOf(getElement(Xpath.loadingIcon)));
            } catch (Exception e) {
                System.out.println("Timed out. Maybe the search is finished.");
            }
        } catch (Exception e) {
            System.out.println("Timed out. The spinner wasn't found.");
        }
        System.out.println("Spinner wait ended.");
    }

}
