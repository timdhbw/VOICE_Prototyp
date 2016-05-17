package dhbw.ai13.audio;

/**
 * Created by GomaTa on 16.05.2016.
 */
public class WindowFunction {
    private static WindowFunction ourInstance = new WindowFunction();

    public static WindowFunction getInstance() {
        return ourInstance;
    }

    private WindowFunction() {
    }
}
