public record Shift(Person person1, Person person2) {
    @Override
    public String toString() {
        return "org.lujo.Shift{" +
                "person1=" + person1 +
                ", person2=" + person2 +
                '}';
    }
}
