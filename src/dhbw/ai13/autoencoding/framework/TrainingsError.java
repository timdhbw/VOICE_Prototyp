package dhbw.ai13.autoencoding.framework;

/**
 * Created by GomaTa on 26.03.2016.
 */
public class TrainingsError {

    private double caluclateMSE(double[] idealOutput, double[] actualOutput){
        int n = idealOutput.length;
        double tmpError = 0.0;
        for(int i = 0; i < n; i++){
            tmpError += (idealOutput[i]-actualOutput[i])*(idealOutput[i]-actualOutput[i]);
        }
        return tmpError / ((double) n);
    }


    public double calculateError(double[] idealOutput, double[] actualOutput) {
        return caluclateMSE(idealOutput, actualOutput);
    }
}
