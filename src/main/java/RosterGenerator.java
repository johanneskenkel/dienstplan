import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RosterGenerator {
    private final int hardCriterium;

    public RosterGenerator(int hardCriterium) throws IOException {
        this.hardCriterium = hardCriterium;
        List<Person> persons = getPersons();
        Map<Integer, List<RosterDay>> scoreRosterDayMap = new HashMap<>();
        int index;
        for (index = 0; index < 100000; index++) {
            List<RosterDay> rosterDays = getRosterDays(persons);
            int rosterScore = calculateRosterScore(rosterDays);
            scoreRosterDayMap.put(rosterScore, rosterDays);
        }
        scoreRosterDayMap.keySet().stream().sorted(Comparator.reverseOrder()).limit(7).forEach(System.out::println);
        Integer bestScore = scoreRosterDayMap.keySet().stream().sorted(Comparator.reverseOrder()).findFirst().orElseThrow();
        List<RosterDay> bestRoster = scoreRosterDayMap.get(bestScore);
        exportRosterToExcel(bestRoster);
    }

    private void exportRosterToExcel(List<RosterDay> rosterDayList) throws IOException {
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

    private static List<RosterDay> getRosterDays(List<Person> persons) {
        LocalDate startDate = LocalDate.of(2023, 11, 01);
        LocalDate endDate = LocalDate.of(2023, 11, 30);
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        List<LocalDate> days = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween + 1)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
        Random random = new Random();
        List<RosterDay> rosterDays = days.stream().map(day -> new RosterDay(
                day,
                persons.get(random.nextInt(persons.size())),
                persons.get(random.nextInt(persons.size())),
                persons.get(random.nextInt(persons.size())),
                persons.get(random.nextInt(persons.size())))).collect(Collectors.toList());
        return rosterDays;
    }

    private int calculateRosterScore(List<RosterDay> rosterDays) {
        int score = 0;
        for (RosterDay rosterDay : rosterDays) {
            if (!rosterDay.isWorkDay()) {
                Shift dayShift = rosterDay.getDayShift();
                score += evaluateHardCriteriumShiftExperienceLevel(dayShift);
                score += evaluateHardCriteriumDifferentPersons(dayShift);
            }
            Shift nightShift = rosterDay.getNightShift();
            score += evaluateHardCriteriumShiftExperienceLevel(nightShift);
            score += evaluateHardCriteriumDifferentPersons(nightShift);

        }
        return score;
    }

    private int evaluateHardCriteriumDifferentPersons(Shift shift) {
        if (shift.person1().equals(shift.person2())) {
            return hardCriterium;
        }
        return 0;
    }

    private double evaluateHardCriteriumShiftExperienceLevel(Shift shift) {
        return 1. / (1 + shift.person1().getLevel() + shift.person2().getLevel()) * hardCriterium;
    }

    private static List<Person> getPersons() {
        List<String> namen = List.of("Lukas", "Hannes");
        List<Person> personen = namen.stream().flatMap(name -> IntStream.range(0, 10).boxed()
                .map(k -> new Person(name + String.valueOf(k), k % 3))).collect(Collectors.toList());
        return personen;
    }
}
