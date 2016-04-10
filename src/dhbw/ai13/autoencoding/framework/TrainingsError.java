package dhbw.ai13.autoencoding.framework;

/**
 * Created by GomaTa on 26.03.2016.
 */
public class TrainingsError {
    private double countSuccess;
    private double countFail;
    private double countIterations;
    private double errorRate;

    public TrainingsError(){
        reset();
    }

    public void increaseSuccess(){
        countSuccess++;
        updateErrorRate();
    }

    public void increaseFail(){
        countFail++;
        countIterations++;
        updateErrorRate();
    }

    private void updateErrorRate(){
        errorRate = countFail / countIterations;
    }

    public void reset(){
        countSuccess = 0;
        countFail = 0;
        countIterations = 0;
        errorRate = 0.0;
    }


    public double getErrorRate() {
        return errorRate;
    }
}
