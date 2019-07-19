package demo;

public interface Speaker {
    default void announce(String message) {
        System.out.println(message);
    }
}
