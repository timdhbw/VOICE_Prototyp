package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;

import java.util.ArrayList;

/**
 * Created by GomaTa on 08.02.2016.
 */
public class Layer {
    private final int id;
    private final ArrayList<Node> nodes = new ArrayList<>();
    private final int countNodes;
    private Layer prevLayer;
    private int countNodesPrevLayer;
    private final ActivationFunction activationFunction;
    private final boolean hasBias;

    public Layer(int id, int countNodes, ActivationFunction activationFunction, boolean hasBias){
        this.id = id;
        this.countNodes = countNodes;
        this.activationFunction = activationFunction;
        this.hasBias = hasBias;
    }

    public void build(Layer prevLayer, int countNodesPrevLayer){
        // reference to previous layer
        this.prevLayer = prevLayer;
        this.countNodesPrevLayer = countNodesPrevLayer;
        // init nodes
        for(int i = 0;i < countNodes; i++) {
            this.nodes.add(new Node(i, countNodesPrevLayer, activationFunction, hasBias));
        }
    }

    public double[] calculateActivations() {
        if(prevLayer != null) {
            ArrayList<Node> prevNodes = prevLayer.getNodes();
            for (int i = 0; i < countNodes; i++) {
                nodes.get(i).calculateNewValue(prevNodes);
            }
        }
        return getActivations();
    }

    public double[] getActivations() {
        double[] values = new double[countNodes];
        for(int i = 0; i < countNodes; i++){
            values[i] = nodes.get(i).getActivationValue();
        }
        return values;
    }

    public double[] getZs() {
        double[] values = new double[countNodes];
        for(int i = 0; i < countNodes; i++){
            values[i] = nodes.get(i).getzValue();
        }
        return values;
    }

    public void setValues(double[] inputData){
        for(int i = 0; i < countNodes; i++){
            nodes.get(i).setActivationValue(inputData[i]);
        }
    }

    public double[][] getWeights(){
        double[][] weights = new double[countNodes][countNodesPrevLayer];
        for(int i = 0; i < countNodes; i++){
            weights[i] = nodes.get(i).getWeights();
        }
        return weights;
    }

    public int getCountNodesPrevLayer() {
        return countNodesPrevLayer;
    }

    public int getId() {
        return id;
    }

    public Layer getPrevLayer() {
        return prevLayer;
    }


    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public int getCountNodes() {
        return countNodes;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public double[] getBiases() {
        double[] biases = new double[countNodes];
        for(int i = 0; i < countNodes; i++){
            Node node = nodes.get(i);
            biases[i] = node.getBias();
        }
        return biases;
    }
}
