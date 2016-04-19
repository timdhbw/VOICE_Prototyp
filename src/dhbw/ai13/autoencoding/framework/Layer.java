package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;

/**
 * Created by GomaTa on 08.02.2016.
 */
public class Layer {
    private int index;
    private Layer prevLayer;
    private Layer nextLayer;
    private int countIn;
    private int countOut;
    private int countNodes;
    private double[][] weights;
    private double[] z;
    private double[] activations;
    private double[] bias;
    private ActivationFunction activationFunction;

    public Layer(int i){
        index = i;
        countIn = 0;
        countOut = 0;
    }

    public void build(){
        resetValues();
        //init bias
        bias = new double[countNodes];
        // init weights
        if(prevLayer != null){
            weights = new double[countNodes][prevLayer.getCountNodes()];
            for(int i = 0; i < weights.length; i++){
                for(int j = 0; j < weights[0].length; j++){
                    weights[i][j] = Math.random();
                }
            }
            for(int i = 0; i < bias.length; i++){
                bias[i] = 0.0;
            }
        }
    }

    public void resetValues(){
        z = new double[countNodes];
        activations = null;
    }

    public Layer in(int n){
        countIn = n;
        return this;
    }

    public Layer out(int n){
        countOut = n;
        countNodes = n;
        return this;
    }

    public double[] getActivations(){
        if(activations == null){
            activations = new double[countNodes];
            if(prevLayer != null){
                for(int i = 0; i < activations.length; i++){ //loop for each node
                    double sum = 0.0;
                    double[] prevActivations = prevLayer.getActivations();
                    for(int j = 0; j < weights[0].length; j++){ //loop for each prev node
                        try {
                            sum += weights[i][j] * prevActivations[j];
                        }catch (ArrayIndexOutOfBoundsException e){
                            System.out.println(e);
                        }

                    }
                    z[i] = sum + bias[i];
                    activations[i] = activationFunction.function(z[i]);
                }
            }
        }
        return activations;
    }

    public void fill(double[] inputData){
        activations = inputData;
    }

    public double[] getBias() {
        return bias;
    }

    public double[][] getWeights() {
        return weights;
    }

    public double[] getZ() {
        return z;
    }

    public int getCountIn() {
        return countIn;
    }

    public int getCountOut() {
        return countOut;
    }

    public int getCountNodes() {
        return countNodes;
    }

    public void setBias(int index, double value){
        bias[index] = value;
    }

    public void setWeight(int nodeIndex, int weightIndex, double value){
        weights[nodeIndex][weightIndex] = value;
    }

    public void setPrevLayer(Layer prevLayer) {
        this.prevLayer = prevLayer;
    }

    public void setNextLayer(Layer nextLayer) {
        this.nextLayer = nextLayer;
    }

    public void setActivationFunction(ActivationFunction activationFunction){
        this.activationFunction = activationFunction;
    }

    public String toString(){
        String str = "[Layer" + index + "] in:" + countIn + ", out:" + countOut + "\n";
        if(prevLayer != null) {
            for (int i = 0; i < countNodes; i += 100) {
                str += "--[Node" + (i + 1) + "] weights{";
                for (int j = 0; j < countIn; j += 100) {
                    str += weights[i][j] + ";";
                }
                str += "} bias{" + bias[i] + "}\n";
            }
        }
        return str;
    }
}
