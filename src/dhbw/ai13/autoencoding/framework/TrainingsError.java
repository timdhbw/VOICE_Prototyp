package dhbw.ai13.autoencoding.framework;

/**
 * Created by GomaTa on 26.03.2016.
 */
public class TrainingsError {

    // C = 1/2 * SUM[ (y_j - a_j)^2 ]
    private double calculateMSE(double[] idealOutput, double[] actualOutput){
        double tmpError = 0.0;
        for(int i = 0; i < idealOutput.length; i++){
            tmpError += Math.pow(idealOutput[i]-actualOutput[i],2);
        }
        return 0.5 * tmpError;
    }


    public double calculateError(double[] idealOutput, double[] actualOutput) {
        return calculateMSE(idealOutput, actualOutput);
    }
}
