package ostryzhniuk.andriy.catering.clients.view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ostryzhniuk.andriy.catering.clients.view.dto.DtoClient;
import ostryzhniuk.andriy.catering.subsidiary.classes.XSSFInitializer;
import java.math.BigDecimal;
import java.util.*;

public class ExcelClientsReport<T extends DtoClient> {

    public void createTableClientsReport(TableView<T> tableView, ObservableList<T> dtoClientsList) {
        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_таблиці_клієнтів");
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        List<String> colNamesList = new LinkedList<>();
        tableView.getColumns().forEach(item -> colNamesList.add(item.getText()));
        xssfInitializer.createTitleHeaders(colNamesList, 0);

        for (T item : dtoClientsList) {
            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellValue(item.getName());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getAddress());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getTelephoneNumber());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getContactPerson());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getPercentageStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getDiscount().divide(new BigDecimal(100)))));

            cell = row.createCell(++columnCount);
            if (item.getEmail() == null || item.getSkype().isEmpty()) {
                cell.setCellValue("-");
            } else {
                cell.setCellValue(item.getEmail());
            }


            cell = row.createCell(++columnCount);
            if (item.getIcq() == null) {
                cell.setCellValue("-");
            } else {
                cell.setCellValue(item.getIcq());
            }

            cell = row.createCell(++columnCount);
            if (item.getSkype() == null || item.getSkype().isEmpty()) {
                cell.setCellValue("-");
            } else {
                cell.setCellValue(item.getSkype());
            }
        }

        xssfInitializer.setAutoSizeColumn(8);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();

    }

}
