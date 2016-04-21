package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;

import java.util.ArrayList;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class Node {
    private final int id;
    private final ActivationFunction activationFunction;
    private double bias;
    private final boolean hasBias;
    private ArrayList<Double> weights = new ArrayList<>();
    private final int countWeights;
    private double activationValue;
    private double zValue;

    public Node(int id, int countWeights, ActivationFunction activationFunction, boolean hasBias){
        this.id = id;
        this.countWeights = countWeights;
        this.activationFunction = activationFunction;
        this.hasBias = hasBias;
        init();
    }

    private void init(){
        if(hasBias){
            bias = 0.0;
        }
        for(int i = 0; i < countWeights; i++){
            weights.add(0.001*Math.random());
        }
    }

    public int getId() {
        return id;
    }

    public double getBias() {
        return bias;
    }

    public double[] getWeights() {
        double[] weights = new double[countWeights];
        for(int i = 0; i < countWeights; i++){
            weights[i] = this.weights.get(i);
        }
        return weights;
    }

    public int getCountWeights() {
        return countWeights;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public void updateWeight(int index, double value){
        weights.set(index,value);
    }

    public void calculateNewValue(ArrayList<Node> prevNodes){
        double newValue = 0.0;
        for(int j = 0; j < prevNodes.size(); j++){
            double preValue = prevNodes.get(j).getActivationValue();
            newValue += weights.get(j) * preValue;
        }
        if(hasBias){
            newValue += bias;
        }
        this.zValue = newValue;
        this.activationValue = activationFunction.function(newValue);
    }

    public double getzValue() {
        return zValue;
    }

    public double getActivationValue() {
        return activationValue;
    }

    public void setActivationValue(double activationValue) {
        this.activationValue = activationValue;
    }
}
