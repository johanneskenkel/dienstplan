import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

public class RosterGenerator {
    private final int hardCriterion;

    public RosterGenerator(int hardCriterion) throws IOException {
        this.hardCriterion = hardCriterion;
        List<Person> persons = getPersons();
        LocalDate startDate = LocalDate.of(2023, 11, 1);
        LocalDate endDate = LocalDate.of(2023, 11, 30);


        Map<Integer, List<RosterDay>> scoreRosterDaysMap = getScoreRosterDaysMap(persons, startDate, endDate);
        scoreRosterDaysMap.keySet().stream().sorted(Comparator.reverseOrder()).limit(7).forEach(System.out::println);
        Integer bestScore = scoreRosterDaysMap.keySet().stream().max(Comparator.naturalOrder()).orElseThrow();
        List<RosterDay> bestRoster = scoreRosterDaysMap.get(bestScore);
        IOTools.exportRosterToExcel(bestRoster);
    }

    private Map<Integer, List<RosterDay>> getScoreRosterDaysMap(List<Person> persons, LocalDate startDate, LocalDate endDate) {
        Map<Integer, List<RosterDay>> scoreRosterDayMap = new HashMap<>();
        int index;
        for (index = 0; index < 1000; index++) {
            List<RosterDay> rosterDays = getRosterDays(persons, startDate, endDate);
            int rosterScore = calculateRosterScore(rosterDays);
            System.out.println(rosterScore);
            scoreRosterDayMap.put(rosterScore, rosterDays);
        }
        return scoreRosterDayMap;
    }

    private static List<RosterDay> getRosterDays(List<Person> persons, LocalDate startDate, LocalDate endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        List<LocalDate> days = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween + 1)
                .mapToObj(startDate::plusDays)
                .toList();
        Random random = new Random();
        return days.stream().map(day -> {
            Person person1 = persons.get(random.nextInt(persons.size()));

            List<Person> tempListPerson2 = persons.stream()
                    .filter(p -> (p.getLevel() + person1.getLevel()) >= 2)
                    .filter(p -> !p.equals(person1))
                    .toList();
            Person person2 = tempListPerson2
                    .get(random.nextInt(tempListPerson2.size()));

            List<Person> tempListPerson3 = persons.stream()
                    .filter(p -> !p.equals(person1))
                    .filter(p -> !p.equals(person2))
                    .toList();
            Person person3 = tempListPerson3
                    .get(random.nextInt(tempListPerson3.size()));

            List<Person> tempListPerson4 = persons.stream()
                    .filter(p -> !p.equals(person1))
                    .filter(p -> !p.equals(person2))
                    .filter(p -> !p.equals(person3))
                    .filter(p -> (p.getLevel() + person3.getLevel()) >= 2).toList();
            Person person4 = tempListPerson4.get(random.nextInt(tempListPerson4.size()));

            return new RosterDay(
                    day,
                    person1,
                    person2,
                    person3,
                    person4);
        }).toList();
    }

    private int calculateRosterScore(List<RosterDay> rosterDays) {
        int score = 0;
        for (RosterDay rosterDay : rosterDays) {
            if (!rosterDay.isWorkDay()) {
                Shift dayShift = rosterDay.getDayShift();
                score += evaluateHardCriterionShiftExperienceLevel(dayShift);
                score += evaluateHardCriterionDifferentPersons(dayShift);
            }
            Shift nightShift = rosterDay.getNightShift();
            score += evaluateHardCriterionShiftExperienceLevel(nightShift);
            score += evaluateHardCriterionDifferentPersons(nightShift);

        }
        return score;
    }

    private int evaluateHardCriterionDifferentPersons(Shift shift) {
        if (shift.person1().equals(shift.person2())) {
            return hardCriterion;
        }
        return 0;
    }

    private int evaluateHardCriterionShiftExperienceLevel(Shift shift) {
        if ((shift.person1().getLevel() + shift.person2().getLevel())<2){
            return hardCriterion;
        }
        return 0;
    }

    private static List<Person> getPersons() {
        List<String> names = List.of("Lukas", "Hannes");
        return names.stream().flatMap(name -> IntStream.range(0, 10).boxed()
                .map(k -> new Person(name + k, k % 3))).toList();
    }
}
