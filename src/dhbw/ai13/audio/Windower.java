package dhbw.ai13.audio;

/**
 * Created by GomaTa on 16.05.2016.
 */
public class Windower {
    private static Windower ourInstance = new Windower();

    public static Windower getInstance() {
        return ourInstance;
    }

    private Windower() {
    }
}
