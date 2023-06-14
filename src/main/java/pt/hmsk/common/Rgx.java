package pt.hmsk.common;

public interface Rgx {
    
    String pageCount = "^\\D*(\\d+)\\D*(\\d+).*?(\\d+)$";
    String invoiceCapture = ".*?\\D?(\\d+)$";
}
