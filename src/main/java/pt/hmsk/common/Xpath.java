package pt.hmsk.common;

public interface Xpath {
    
    // Login
    String loginReveal = "//*[@id='mainlogin']/div/h4[2]";
    String usernameInput = "//*[@id=\"login\"]";
    String passwordInput = "//*[@id=\"password\"]";
    String loginButton = "//*[@id=\"loginbutton\"]";
    String loginErrorMessage = "//*[@id=\"ValidationSummary1\"]//*[@class=\"input-notification error\"]";
    
    // ListView
    String listTable = "//*[@class=\"Monitor\"]";
    String loggedUserName = "//*/li[@class=\"UserMenuZone\"]";
    String nextPage = "//*/input[@class=\"rgPageNext\"]";
    String pageCount = "//*[@class=\"rgWrap rgInfoPart\"]";
    String newWindowPopup = "//*/li[@class=\"rmItem rmLast\"]/a";
    String resultRow = "//*/td[@class=\"gridItemsStyle gridItemsStyleRefNumber\"]";
    
    // Search
    String openSearch = "//*/a[@class=\"aCritPesq\"]";
    String supplierNumber = "//*[@class=\"racTokenList\"]";
    String btnSearch = "//*/input[@id=\"btn_F_001_Search\"]";
    String btnResetFilters = "//*/input[@id=\"btn_F_001_CleanFilters\"]";
    String dateReceptionFrom = "//*/input[@id=\"dt_F_001_ReceptionDateFrom\"]";
    String supplierResultList = "//*/ul[@class=\"racList\"]";
    String firstProviderResult = "//ul[@class='racList']/li[1]";
    String loadingIcon = "//*[@id=\"divLoadingUProgress\"]//*/img";
}
