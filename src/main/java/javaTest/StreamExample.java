//package javaTest;
//
//import java.util.*;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//class User {
//    private int id;
//    private String name;
//    private int amount; // 통장 잔고
//    private int age;
//
//    // constructor, getter, setter, toString()...
//    public User(int id, String name, int amount, int age) {
//        this.id = id;
//        this.name = name;
//        this.amount = amount;
//        this.age = age;
//    }
//
//    public int getId() { return id; }
//    public String getName() { return name; }
//    public int getAmount() { return amount; }
//    public int getAge() { return age; }
//
//    @Override
//    public String toString() {
//        return "User{id=" + id + ", name=" + name + ", amount=" + amount + ", age=" + age + "}";
//    }
//}
//
//public class StreamExample2 {Iterable
//
//    public static Predicate<User> isUnder19() {
//        return u -> u.getAge() < 19;
//    }
//
//    public static Predicate<User> hasOver1000() {
//        return u -> u.getAmount() > 1000;
//    }
//
//    public static void print(List<User> filtered) {
//        // StringBuilder에 결과를 합쳐서 한 번에 출력
//        StringBuilder sb = new StringBuilder("Filtered Users:\n");
//        for (User u : filtered) {
//            sb.append(u.toString()).append("\n");
//        }
//
//        LinkedList<>
//
//        System.out.println(sb.toString());
//    }
//
//    public static void main(String[] args) {
//        List<User> users = Arrays.asList(
//                new User(1, "Alice", 500, 17),
//                new User(2, "Bob", 2000, 15),
//                new User(3, "Charlie", 1200, 20),
//                new User(4, "Dave", 3000, 18)
//        );
//
//        // 필터링
//        List<User> filtered = users.stream()
//                .filter(isUnder19().and(hasOver1000()))
//                .toList();
//    }
//}
//
