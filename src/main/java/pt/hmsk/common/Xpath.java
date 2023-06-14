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
    String newWindowPopup = "//*/li[@class=\"rmItem rmLast\"]//span";
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
    String supplierDropDown = "//ul[@class='racList']/li";
    
    // Filters
    String stateFilterAll = "//*/input[@id=\"chk_F_001_stateAll\"]";
    String stateFilterProcessed = "//*/input[@id=\"chk_F_001_state11\"]";
    
    // Invoice Details
    String paymentInfoButton = "//li[3]//span[@class=\"rtsLink\"]";
    String inputDocEmsPag = "//div[@class=\"DocDetailData\"]//tr//input[@type=\"text\" " +
            "and contains(@name, \"PaymentIssuedReferenceTxt\")]";
    String inputDatEmsPag = "//div[@class=\"DocDetailData\"]//tr//input[@type=\"text\" " +
            "and contains(@name, \"PaymentIssuedDate\")]";
    String inputDatValPag = "//div[@class=\"DocDetailData\"]//tr//input[@type=\"text\" " +
            "and contains(@name, \"PaymentAmountDate\")]";
    String inputRefPag = "//div[@class=\"DocDetailData\"]//tr//input[@type=\"text\" " +
            "and contains(@name, \"PaymentReferenceTxt\")]";
    String actionSelect = "//div[contains(@id, \"BottomButtons\")]//select[contains(@id, \"Action\")]";
    String execute = "//div[contains(@id, \"BottomButtons\")]//input[contains(@id, " +
            "\"ExecuteButton\") and contains(@class, \"Button\")]";
    String goBack = "//div[contains(@id, \"BottomButtons\")]//input[contains(@id, " +
            "\"CancelButton\") and contains(@class, \"Button\")]";
    String confirmButton = "//table[@class=\"rwTable rwShadow\"]//span[@class=\"rwOuterSpan\"]";
    
}
