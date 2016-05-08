package ostryzhniuk.andriy.catering.menu.view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ostryzhniuk.andriy.catering.subsidiary.classes.XSSFInitializer;
import ostryzhniuk.andriy.catering.menu.view.dto.DtoMenu;
import java.util.*;

public class ExcelMenuReport<T extends DtoMenu> {

    public void createTableMenuReport(TableView<T> tableView, ObservableList<T> dtoMenuList){
        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_таблиці_меню");
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        List<String> colNamesList = new LinkedList<>();
        tableView.getColumns().forEach(item -> colNamesList.add(item.getText()));
        xssfInitializer.createTitleHeaders(colNamesList, 0);

        for (T item : dtoMenuList) {
            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellValue(item.getDishesTypeName());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getName());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getPrice())));

            cell = row.createCell(++columnCount);
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getMass())));

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getIngredients());
        }

        xssfInitializer.setAutoSizeColumn(5);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();

    }

}
