package pt.hmsk.common;

public interface Rgx {
    
    String pageCount = "^\\D*(\\d+)\\D*(\\d+).*$";
    String invoiceCapture = ".*?\\D?(\\d+)$";
}
