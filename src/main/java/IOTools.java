import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class IOTools {
    static void exportRosterToExcel(List<RosterDay> rosterDayList) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet();
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            // Header
            Row header = sheet.createRow(0);
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Datum");

            headerCell = header.createCell(1);
            headerCell.setCellValue("Besetzung Tagdienst");
            headerCell = header.createCell(3);
            headerCell.setCellValue("Besetzung Nachtdienst");

//             Content
            int index = 0;
            for (RosterDay rosterDay : rosterDayList) {
                Row row = sheet.createRow(++index);


                Cell cell = row.createCell(0);
                cell.setCellValue(rosterDay.getDay().getDayOfWeek().toString());

                if (!rosterDay.isWorkDay()) {
                    cell = row.createCell(1);
                    cell.setCellValue(rosterDay.getDayShift().person1().toString());
                    cell = row.createCell(2);
                    cell.setCellValue(rosterDay.getDayShift().person2().toString());
                }
                cell = row.createCell(3);
                cell.setCellValue(rosterDay.getNightShift().person1().toString());
                cell = row.createCell(4);
                cell.setCellValue(rosterDay.getNightShift().person2().toString());
            }

            wb.write(new FileOutputStream("text.xlsx"));
        }
    }
}
