public class Person {
    private final String name;
    private final int level;

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Person(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", level=" + level +
                '}';
    }
}
