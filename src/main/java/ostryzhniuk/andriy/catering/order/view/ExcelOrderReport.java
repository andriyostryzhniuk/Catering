package ostryzhniuk.andriy.catering.order.view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import ostryzhniuk.andriy.catering.commands.ClientCommandTypes;
import ostryzhniuk.andriy.catering.main.window.XSSFInitializer;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrder;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrderReport;
import ostryzhniuk.andriy.catering.order.view.dto.DtoOrderingForReport;
import ostryzhniuk.andriy.catering.ordering.view.dto.DtoOrdering;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static ostryzhniuk.andriy.catering.client.Client.sendARequestToTheServer;

public class ExcelOrderReport<T extends DtoOrder> {

    public void createTableOrderReport(TableView<T> tableView, ObservableList<T> dtoOrdersList){
        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_таблиці_замовлень");
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        List<String> colNamesList = new LinkedList<>();
        tableView.getColumns().forEach(item -> colNamesList.add(item.getText()));
        xssfInitializer.createTitleHeaders(colNamesList, 0);

        for (T item : dtoOrdersList) {
            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellValue(item.getId());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDateStyle());
            cell.setCellValue(item.getDate());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getClient());

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
        }

        xssfInitializer.setAutoSizeColumn(7);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();

    }

    public void createOrderReport(Date date){
        List<Object> objectList = new LinkedList<>();
        objectList.add(new java.sql.Date(date.getTime()));

        List<DtoOrderReport> dtoOrderReportList = Collections.synchronizedList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_ORDER_REPORT, objectList));

        DateFormat dateFormat = new SimpleDateFormat("(dd.MM.yyyy)", new Locale("uk"));
        String formatDate = dateFormat.format(date);

        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_замовлень"+formatDate);
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        for (DtoOrderReport item : dtoOrderReportList) {
            List<String> colNamesList = new LinkedList<>();
            colNamesList.add("Дата");
            colNamesList.add("Клієнт");
            colNamesList.add("Адреса");
            colNamesList.add("Замовлення №");
            colNamesList.add("До оплати");
            colNamesList.add("Борг");
            xssfInitializer.createTitleHeaders(colNamesList, rowCount);

            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellStyle(xssfInitializer.getDateStyle());
            cell.setCellValue(item.getDate());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getClient());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getAddress());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getOrderId());

            cell = row.createCell(++columnCount);
            cell.setCellStyle(xssfInitializer.getDollarStyle());
            cell.setCellValue(Double.parseDouble(String.valueOf(item.getBill())));

            cell = row.createCell(++columnCount);
            if (item.getDebt().compareTo(new BigDecimal(0)) == 0) {
                cell.setCellValue("-");
            } else {
                cell.setCellStyle(xssfInitializer.getDollarStyle());
                cell.setCellValue(Double.parseDouble(String.valueOf(item.getDebt())));
            }
            ++rowCount;

            colNamesList = new LinkedList<>();
            colNamesList.add("Назва страви");
            colNamesList.add("Кількість порцій");
            xssfInitializer.createTitleHeaders(colNamesList, ++rowCount);

            objectList = new LinkedList<>();
            objectList.add(item.getOrderId());
            List<DtoOrdering> dtoOrderingList = Collections.synchronizedList(
                    sendARequestToTheServer(ClientCommandTypes.SELECT_ORDERING, objectList));

            if (dtoOrderingList.isEmpty()) {
                columnCount = 0;
                row = sheet.createRow(++rowCount);

                cell = row.createCell(columnCount);
                cell.setCellValue("-");

                cell = row.createCell(++columnCount);
                cell.setCellValue("-");
            } else{
                for (DtoOrdering orderingItem : dtoOrderingList) {
                    columnCount = 0;
                    row = sheet.createRow(++rowCount);

                    cell = row.createCell(columnCount);
                    cell.setCellValue(orderingItem.getDishesName());

                    cell = row.createCell(++columnCount);
                    cell.setCellValue(orderingItem.getNumberOfServings());
                }
            }
            rowCount = rowCount + 3;
        }

        xssfInitializer.setAutoSizeColumn(6);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();

    }

    public void createMenuReport(Date date){
        List<Object> objectList = new LinkedList<>();
        objectList.add(new java.sql.Date(date.getTime()));

        List<DtoOrderingForReport> dtoOrderingForReportList = Collections.synchronizedList(
                sendARequestToTheServer(ClientCommandTypes.SELECT_MEALS_A_DAY, objectList));

        DateFormat dateFormat = new SimpleDateFormat("(dd.MM.yyyy)", new Locale("uk"));
        String formatDate = dateFormat.format(date);

        XSSFInitializer xssfInitializer = new XSSFInitializer("Звіт_кількість_страв"+formatDate);
        XSSFSheet sheet = xssfInitializer.getSheet();

        Row row;
        Cell cell;
        int columnCount;
        int rowCount = 0;

        List<String> colNamesList = new LinkedList<>();
        colNamesList.add("Тип страви");
        colNamesList.add("Назва страви");
        colNamesList.add("Кількість порцій");
        xssfInitializer.createTitleHeaders(colNamesList, rowCount);

        Integer sumOfServings = 0;

        for (DtoOrderingForReport item : dtoOrderingForReportList) {
            row = sheet.createRow(++rowCount);
            columnCount = 0;

            cell = row.createCell(columnCount);
            cell.setCellValue(item.getDishesType());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getDishesName());

            cell = row.createCell(++columnCount);
            cell.setCellValue(item.getNumberOfServings());

            sumOfServings = sumOfServings + item.getNumberOfServings();
        }

        row = sheet.createRow(++rowCount);

        cell = row.createCell(1);
        cell.setCellStyle(xssfInitializer.getResultStyle());
        cell.setCellValue("Всього:");

        cell = row.createCell(2);
        cell.setCellStyle(xssfInitializer.getResultStyle());
        cell.setCellValue(sumOfServings);

        rowCount = rowCount + 2;
        row = sheet.createRow(rowCount);
        cell = row.createCell(2);
        cell.setCellStyle(xssfInitializer.getResultStyle());
        cell.setCellValue("Дата");

        row = sheet.createRow(++rowCount);
        cell = row.createCell(2);
        cell.setCellStyle(xssfInitializer.getDateStyle());
        cell.setCellValue(date);

        xssfInitializer.setAutoSizeColumn(3);

        xssfInitializer.write();
        xssfInitializer.openFileInMicrosoftExcel();
    }
}
