package ostryzhniuk.andriy.catering.debtors.view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ostryzhniuk.andriy.catering.debtors.view.dto.DtoDebtors;
import ostryzhniuk.andriy.catering.subsidiary.classes.XSSFInitializer;
import java.math.BigDecimal;
import java.util.*;

public class ExcelDebtorsReport<T extends DtoDebtors> {

    public void createTableDebtorsReport(TableView<T> tableView, ObservableList<T> dtoDebtorsList){
        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_таблиці_боржників");
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        List<String> colNamesList = new LinkedList<>();
        tableView.getColumns().forEach(item -> colNamesList.add(item.getText()));
        xssfInitializer.createTitleHeaders(colNamesList, 0);

        for (T item : dtoDebtorsList) {
            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellValue(item.getClientName());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getTelephoneNumber());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getContactPerson());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getOrderId());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDateStyle());
            cell.setCellValue(item.getDate());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getCost())));

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getPercentageStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getDiscount().divide(new BigDecimal(100)))));

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getBill())));

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getPaid())));

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getDebt())));
        }

        xssfInitializer.setAutoSizeColumn(10);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();

    }

}
