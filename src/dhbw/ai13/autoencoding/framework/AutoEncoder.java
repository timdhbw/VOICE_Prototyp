package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;

import java.util.ArrayList;
import java.util.Random;

public class AutoEncoder {
    //AutoEncoder
    private boolean buildStatus = false;
    //Layers
    private ArrayList<Layer> layers;
    private Layer inputLayer;
    private Layer outputLayer;
    private double learningRate;
    private int countLayers;
    private TrainingsError trainingsError;
    private int iterations;
    private AutoEncoderDataHandler dataHandler;
    private ActivationFunction activationFunction;
    private final boolean DEBUG = true;

    public AutoEncoder(){
        countLayers = 0;
        layers = new ArrayList<>();
        dataHandler = new AutoEncoderDataHandler();
    }

    public void build() throws AutoEncoderException {
        if(learningRate == 0.0){
            throw new AutoEncoderException("Learning error null");
        }
        if(activationFunction == null){
            throw new AutoEncoderException("Activation function null");
        }
        for(int i = 0; i < countLayers; i++){
            Layer layer = layers.get(i);
            layer.setActivationFunction(activationFunction);
            if(i == 0){
                layer.setNextLayer(layers.get(i+1));
            }else if(i == (countLayers-1)){
                layer.setPrevLayer(layers.get(i-1));
            }else{
                layer.setPrevLayer(layers.get(i-1));
                layer.setNextLayer(layers.get(i+1));
            }
        }
        for(int i = 0; i < countLayers; i++){
            layers.get(i).build();
        }
        inputLayer = layers.get(0);
        outputLayer = layers.get(countLayers-1);
        trainingsError = new TrainingsError();
        //dataHandler.readFromFile(this, "autoencoder.txt");
        buildStatus = true;
        if(DEBUG){System.out.println("[DEBUG] Built successfull.");}
    }

    public void train(double[][] trainingsData, double error, int maxIterations, int miniBatchSize) throws AutoEncoderException {
        if(buildStatus){
            if (trainingsData[0].length != inputLayer.getCountOut() || trainingsData[0].length != outputLayer.getCountOut()){
                throw new AutoEncoderException("Trainings data length does not match to input / output layer");
            }
            if (miniBatchSize > trainingsData.length){
                throw new AutoEncoderException("Mini Batch Size bigger than input or output data length");
            }
            iterations = 0;
            do {
                double[][] subset = shuffleTrainingsData(trainingsData, miniBatchSize);
                update_mini_batch(subset);
                iterations++;
                if(DEBUG && (iterations%100) == 0){ System.out.printf("[DEBUG] (%d/%d) - Error Rate: %f\n",iterations,maxIterations,trainingsError.getErrorRate());}
            }while(trainingsError.getErrorRate() > error && iterations < maxIterations);
            if(DEBUG) {System.out.printf("[DEBUG] Final error rate: %f\n", trainingsError.getErrorRate());}
            dataHandler.saveIntoFile(this, "autoencoder.txt");
        }else{
            throw new AutoEncoderException("AutoEncoder not built.");
        }
    }

    public double[][] shuffleTrainingsData(double[][] trainingsData, int length){
        double[][] subset = new double[length][];
        Random r = new Random();
        ArrayList<Integer> list = new ArrayList<>();
        int index;
        for(int i = 0; i < length; i++){
            index = r.nextInt(trainingsData.length);
            while(list.contains(new Integer(index))){
                index = r.nextInt(trainingsData.length);
            }
            list.add(index);
            subset[i] = trainingsData[index];
        }
        return subset;
    }

    public double[] feedForward(double [] xy) throws AutoEncoderException {
        if(buildStatus){
            for(int i = 0; i < countLayers; i++){
                layers.get(i).resetValues();
            }
            inputLayer.fill(xy);
            layers.get(countLayers-1).getActivations();
            return outputLayer.getActivations();
        }else{
            throw new AutoEncoderException("AutoEncoder not built.");
        }

    }

    public void update_mini_batch(double[][] xy) throws AutoEncoderException {
        double[][][] nablaW = initNablaW();
        double[][] nablaB = initNablaB();
        trainingsError.reset();
        for(int i = 0; i < xy.length; i++){ //for each input vector
            DeltaNabla dn = backprop(xy[i]);
            double[][][] deltaNablaW = dn.getDeltaNablaW();
            double[][] deltaNablaB = dn.getDeltaNablaB();
            nablaW = calculateNablaW(nablaW,deltaNablaW);
            nablaB = calculateNablaB(nablaB,deltaNablaB);
        }
        updateWeights(nablaW, xy.length);
        updateBias(nablaB, xy.length);
    }

    public void updateBias(double[][] nablaB, int dataLength){
        for(int i = countLayers-1; i > 0; i-- ){
            Layer layer = layers.get(i);
            double[] bias = layer.getBias();
            for(int k = 0; k < bias.length; k++){
                bias[k] = bias[k] - (learningRate/dataLength) * nablaB[i-1][k];
            }
        }
    }

    public double[][] calculateNablaB(double[][] m1, double[][] m2){
        double[][] m = new double[m1.length][];
        for(int l = 0; l < m.length; l++){ //loop for each layer
            m[l] = new double[m1[l].length];
            for(int k = 0; k < m[l].length; k++){ //loop for each node's bias
                m[l][k] = m1[l][k] + m2[l][k];
            }
        }
        return m;
    }

    public void updateWeights(double[][][] nablaW, int dataLength){
        for(int i = countLayers-1; i > 0; i--) {
            Layer layer = layers.get(i);
            double[][] weights = layer.getWeights();
            for (int k = 0; k < weights.length; k++) {
                for (int j = 0; j < weights[0].length; j++) {
                    weights[k][j] = weights[k][j] - (learningRate / dataLength) * nablaW[i-1][k][j];
                }
            }
        }
    }

    public double[][][] calculateNablaW(double[][][] m1, double[][][] m2){
        double[][][] matrix = new double[m1.length][][];
        for(int i = 0; i < matrix.length; i++){ //loop for each layer
            matrix[i] = new double[m1[i].length][];
            for(int j = 0; j < matrix[i].length; j++){ //loop for each node's weights
                matrix[i][j] = new double[m1[i][j].length];
                for(int k = 0; k < matrix[i][j].length; k++){ //loop for each weight of a node
                    matrix[i][j][k] = m1[i][j][k] + m2[i][j][k];
                }
            }
        }
        return matrix;
    }

    public DeltaNabla backprop(double[] xy) throws AutoEncoderException {
        //feed forward
        feedForward(xy);
        updateTrainingsError(xy);
        //delta nabla weight and bias
        double[][][] deltaNablaW = new double[countLayers][][];
        double[][]deltaNablaB = new double[countLayers][];
        //calculate delta for output layer
        double[] delta = costFunction(xy,outputLayer.getActivations());
        //calculate nabla w and b for output layer
        Layer prevLayer = layers.get(countLayers-2);
        deltaNablaW[countLayers-2] = calculateDeltaNablaW(prevLayer.getActivations(),delta);
        deltaNablaB[countLayers-2] = delta;
        //calculate nabla w and b for other layers
        for(int i = countLayers-2; i > 0; i--){
            Layer currentLayer = layers.get(i);
            Layer nextLayer = layers.get(i+1);
            prevLayer = layers.get(i-1);
            double[] sp = activationFunction.derivation(currentLayer.getZ());
            delta = calculateDelta(nextLayer.getWeights(),delta,sp);
            deltaNablaW[i-1] = calculateDeltaNablaW(prevLayer.getActivations(),delta);
            deltaNablaB[i-1] = delta;
        }
        return new DeltaNabla(deltaNablaW,deltaNablaB);
    }

    private void updateTrainingsError(double[] xy){
        double[] output = outputLayer.getActivations();
        boolean error = false;
        int i = 0;
        while(i < xy.length && !error){
            if(output[i] != xy[i]){
                error = true;
            }
            i++;
        }
        if(error){
            trainingsError.increaseFail();
        }else{
            trainingsError.increaseSuccess();
        }
    }

    public double[][] initNablaB(){
        double[][] nablaB = new double[countLayers-1][];
        for(int i = 1; i <= nablaB.length; i++){
            Layer layer = layers.get(i);
            nablaB[i-1] = new double[layer.getCountNodes()];
        }
        return nablaB;
    }

    public double[][][] initNablaW(){
        double[][][] nablaW = new double[countLayers-1][][];
        for(int i = 1; i <= nablaW.length; i++){
            Layer layer = layers.get(i);
            double[][] weights = layer.getWeights();
            nablaW[i-1] = new double[weights.length][weights[0].length];
            for(int k = 0; k < nablaW[i-1].length; k++){
                for(int j = 0; j < nablaW[i-1][k].length; j++){
                    nablaW[i-1][k][j] = 0.0;
                }
            }
        }
        return nablaW;
    }

    public double[][] calculateDeltaNablaW(double[] a, double[] d){
        double[][] deltaNablaW = new double[d.length][a.length];
        for(int j = 0; j < d.length; j++){ //for each node of layer
            for(int k = 0; k < a.length; k++){ //for each node's wheight
                //deltaNablaW[j][k] = delta nalba for node
                // a[k] = activation value of node of current layer
                // d[j] = delta of node of next layer
                deltaNablaW[j][k] = a[k] * d[j];
            }
        }
        return deltaNablaW;
    }

    public double[] calculateDelta(double[][] nextWeights, double[] nextDelta, double[] sp){
        double[] delta = new double[nextWeights[0].length];
        for(int i = 0; i < sp.length; i++){
            double sum = 0.0;
            for(int j = 0; j < nextDelta.length; j++){
                sum += nextWeights[j][i] * nextDelta[j];
            }
            delta[i] = sum * sp[i];
        }
        return delta;
    }

    public double[] costFunction(double[] expectedOutputValue,double[] realOutputValue){
        double[] cost = new double[expectedOutputValue.length];
        for(int i = 0; i < cost.length; i++){
            cost[i] = realOutputValue[i]-expectedOutputValue[i];
        }
        return cost;
    }

    public void setActivationFunction(ActivationFunction activationFunction){
        this.activationFunction = activationFunction;
    }


    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public Layer addLayer(int i) {
        layers.add(new Layer(i));
        countLayers++;
        return layers.get(countLayers-1);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        StringBuilder sbLayer = new StringBuilder();
        StringBuilder sbNodes = new StringBuilder();
        for(int i = 0; i < countLayers; i++){
            sbNodes.append(layers.get(i));
        }
        sb.append(sbLayer.toString()+"\n");
        sb.append(sbNodes.toString());
        return sb.toString();
    }

    public ArrayList<Layer> getLayers() {
        return layers;
    }

    public int getCountLayers() {
        return countLayers;
    }

    public boolean isDEBUG() {
        return DEBUG;
    }
}
