package demo;

public class ConsoleSpeaker implements Speaker {
    @Override
    public void announce(String message) {
        System.out.println(message);
    }
}
