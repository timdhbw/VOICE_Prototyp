package dhbw.ai13.autoencoding.framework.layer;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class Delta {
    private final ActivationFunction activationFunction;
    private double[] values;

    public  Delta(ActivationFunction activationFunction){
        this.activationFunction = activationFunction;
    }

    public double[] calculate(double[] idealValues, double[] actualValues){
        values = new double[idealValues.length];
        for(int i = 0; i < values.length; i++){
            values[i] = actualValues[i] - idealValues[i];
        }
        return values;
    }

    public double[] calculate(double[][] nextWeights, double[] nextDelta, double[] zValues){
        double[] delta = new double[nextWeights[0].length];
        for(int i = 0; i < zValues.length; i++){
            double sum = 0.0;
            for(int j = 0; j < nextDelta.length; j++){
                sum += nextWeights[j][i] * nextDelta[j];
            }
            delta[i] = sum * activationFunction.derivation(zValues[i]);;
        }
        return delta;
    }
}
