package dhbw.ai13.autoencoding.framework.elements;

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

    public double[] calculate(double[] idealValues, double[] actualValues, double[] zValues){
        values = new double[idealValues.length];
        for(int j = 0; j < values.length; j++){
            values[j] = (actualValues[j] - idealValues[j]) * activationFunction.derivation(zValues[j]);
        }
        return values;
    }

    public double[] calculate(double[][] nextWeights, double[] nextDelta, double[] zValues){
        double[] delta = new double[zValues.length];
        for(int j = 0; j < zValues.length; j++){ // for each node of current layer
            double sum = 0.0;
            for(int k = 0; k < nextDelta.length; k++){ // for each node of next layer
                sum += nextWeights[k][j] * nextDelta[k];
            }
            delta[j] = sum * activationFunction.derivation(zValues[j]);
        }
        return delta;
    }
}
