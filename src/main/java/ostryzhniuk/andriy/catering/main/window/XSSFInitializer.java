package ostryzhniuk.andriy.catering.main.window;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

public class XSSFInitializer {

    private XSSFSheet sheet;
    private XSSFWorkbook workbook;
    private String fileName;
    private File excelFile;
    private CellStyle dollarStyle;
    private CellStyle percentageStyle;
    private CellStyle dateStyle;
    private CellStyle titleStyle;
    private CellStyle resultStyle;

    public XSSFInitializer(String fileName) {
        this.fileName = fileName;
        this.excelFile = new File(getDirectoryPath()+"\\" + fileName + ".xlsx");
    }

    public XSSFWorkbook createWorkbook(){
        if (excelFile.exists()) {
            try {
                return new XSSFWorkbook(new FileInputStream(excelFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new XSSFWorkbook();
    }

    public XSSFSheet getSheet(){
        if (sheet == null) {
            sheet = createSheet();
        }
        return sheet;
    }

    private XSSFSheet createSheet(){
        DateFormat curDateFormat = new SimpleDateFormat("dd.MM.yyyy - hh.mm.ss", new Locale("uk"));
        String curDate = curDateFormat.format(new java.util.Date());
        XSSFSheet newSheet = getWorkbook().createSheet("Звіт (" + curDate + ")");
        return newSheet;
    }

    public void write(){
        try {
            FileOutputStream outputStream = new FileOutputStream(excelFile);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFileInMicrosoftExcel(){
        try{
            Runtime.getRuntime().exec("cmd /c start " + getDirectoryPath()+"\\" + fileName + ".xlsx");
        }catch(IOException  e){
            e.printStackTrace();
        }
    }

    public void createTitleHeaders(List<String> colNamesList, Integer rowIndex){
        Row row = sheet.createRow(rowIndex);
        int columnCount = 0;

        for (String tableColumnName : colNamesList) {
            Cell cell = row.createCell(columnCount);
            cell.setCellStyle(getTitleStyle());
            cell.setCellValue(tableColumnName);
            columnCount++;
        }
    }

    public void setAutoSizeColumn(Integer numberOfColumns){
        IntStream.range(0, 7).forEach(i -> sheet.autoSizeColumn(i));
    }

    public File getExcelFile() {
        return excelFile;
    }

    public XSSFWorkbook getWorkbook() {
        if (workbook == null) {
            workbook = createWorkbook();
        }
        return workbook;
    }

    private String getDirectoryPath(){
        String path = "C:\\Users\\"+ System.getProperty("user.name") +"\\Documents\\Catering";
        File directory = new File(path);
        if (! directory.exists()) {
            directory.mkdir();
        }
        return path;
    }

    public CellStyle getDollarStyle (){
        if (dollarStyle == null) {
            dollarStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            dollarStyle.setDataFormat(dataFormat.getFormat("$#,#0.00"));
        }
        return dollarStyle;
    }

    public CellStyle getPercentageStyle(){
        if (percentageStyle == null) {
            percentageStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            percentageStyle.setDataFormat(dataFormat.getFormat("0.0%"));
        }
        return percentageStyle;
    }

    public CellStyle getDateStyle (){
        if (dateStyle == null) {
            dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));
        }
        return dateStyle;
    }

//    public java.util.Date parseDate(String dateStringValue){
//        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", new Locale("uk"));
//        java.util.Date date = null;
//        try {
//            date = dateFormat.parse(dateStringValue);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }

    public CellStyle getTitleStyle(){
        if (titleStyle == null) {
            titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
            titleStyle.setFont(getFontBold());
        }
        return titleStyle;
    }

    public CellStyle getResultStyle(){
        if (resultStyle == null) {
            resultStyle = workbook.createCellStyle();
            resultStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            resultStyle.setFont(getFontBold());
        }
        return resultStyle;
    }

    public Font getFontBold(){
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        return font;
    }

}
