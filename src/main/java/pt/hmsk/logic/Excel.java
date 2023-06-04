package pt.hmsk.logic;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import pt.hmsk.common.Rgx;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Excel {

    public static Map<String, String[]> getDataMap(String filePath) {
        Map<String, String[]> dataMap = new HashMap<>();

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet != null) {
                for (Row row : sheet) {
                    Cell cellC = row.getCell(CellReference.convertColStringToIndex("C"));
                    Cell cellE = row.getCell(CellReference.convertColStringToIndex("E"));
                    Cell cellF = row.getCell(CellReference.convertColStringToIndex("F"));
                    Cell cellG = row.getCell(CellReference.convertColStringToIndex("G"));
                    Cell cellH = row.getCell(CellReference.convertColStringToIndex("H"));

                    if (cellC != null
                            && !cellC.getStringCellValue().trim().isEmpty()
                            && cellE != null
                            && cellF != null
                            && cellG != null) {
                        String invoice = cellC.getStringCellValue().trim();
                        Matcher m = Pattern.compile(Rgx.invoiceCapture).matcher(invoice);
                        m.matches();
                        String key = m.group(1);
                        String[] values = new String[]{
                                cellH.getStringCellValue().trim(),
                                cellE.getStringCellValue().trim().replaceAll("/", "-"),
                                cellF.getStringCellValue().trim().replaceAll("/", "-"),
                                cellG.getStringCellValue().trim()
                        };

                        dataMap.put(key, values);
                    }
                }
            }

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataMap;
    }
}
