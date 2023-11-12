import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class RosterDay {
    private final LocalDate day;
    private final Person person1;
    private final Person person2;
    private final Person person3;
    private final Person person4;
    private final boolean isWorkDay;
    private final Shift nightShift;
    private final Shift dayShift;

    @Override
    public String toString() {
        return "org.lujo.RosterDay{" +
                "day=" + day +
                ", isWorkDay=" + isWorkDay +
                ", nightShift=" + nightShift +
                ", dayShift=" + dayShift +
                '}';
    }

    public LocalDate getDay() {
        return day;
    }

    public boolean isWorkDay() {
        return isWorkDay;
    }

    public Shift getNightShift() {
        return nightShift;
    }

    public Shift getDayShift() {
        return dayShift;
    }

    public RosterDay(LocalDate day, Person person1, Person person2, Person person3, Person person4) {
        this.day = day;
        this.isWorkDay = List.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY)
                .contains(day.getDayOfWeek());
        this.person1 = person1;
        this.person2 = person2;
        this.person3 = person3;
        this.person4 = person4;

        nightShift = new Shift(person1,person2);
        dayShift = new Shift(person3,person4);
    }
}
