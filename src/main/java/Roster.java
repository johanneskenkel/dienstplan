import java.io.IOException;

public class Roster {
    static int hardCriterium = -1;

    public static void main(String... args) throws IOException {
        new RosterGenerator(hardCriterium);
    }
}
