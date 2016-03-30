package dhbw.ai13.autoencoding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by GomaTa on 08.02.2016.
 */
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

    public AutoEncoder(){
        countLayers = 0;
        layers = new ArrayList<>();
    }

    public void build(){
        for(int i = 0; i < countLayers; i++){
            Layer layer = layers.get(i);
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
        buildStatus = true;
        readData();
    }

    public void train(ArrayList<ArrayList<Double>> inputData, ArrayList<ArrayList<Double>> outputData, double error, int miniBatchSize) throws Exception {
        if(buildStatus){
            if (inputData.get(0).size() != inputLayer.getCountOut() || outputData.get(0).size() != outputLayer.getCountOut()){
                throw new Exception("Input or output data length does not match to input layer");
            }
            if (miniBatchSize > inputData.size()  || miniBatchSize > outputData.size()){
                throw new Exception("Mini Batch Size bigger than input or output data length");
            }
            iterations = 0;
            int maxIterations = 100;
            do {
                ArrayList<ArrayList<ArrayList<Double>>> subset = shuffleTrainingsData(inputData, outputData, miniBatchSize);
                ArrayList<ArrayList<Double>> x = subset.get(0);
                ArrayList<ArrayList<Double>> y = subset.get(1);
                update_mini_batch(x,y);
                iterations++;
                if((iterations%100000) == 0){
                    System.out.printf("[%d/%d] error rate: %f\n",iterations,maxIterations,trainingsError.getErrorRate());
                }
            }while(trainingsError.getErrorRate() > error && iterations < maxIterations);
            saveData();
        }
    }

    private void saveData(){
        JSONObject jsonNetwork = new JSONObject();
        JSONObject jsonLayers = new JSONObject();
        jsonNetwork.append("layers", jsonLayers);
        for(int i = 1; i < countLayers; i++){ //for each layer
            JSONObject jsonLayer = new JSONObject();
            JSONObject jsonWeights = new JSONObject();
            JSONObject jsonBias = new JSONObject();
            Layer layer = layers.get(i);
            double[] bias = layer.getBias();
            double[][] weights = layer.getWeights();
            for(int j = 0; j < layer.getCountNodes(); j++ ){ //for each node
                jsonBias.append(String.valueOf(j),bias[j]);
                for(int k = 0; k < layer.getCountOut(); k++){
                    jsonWeights.append(String.valueOf(k),weights[j]);
                }
            }
            jsonLayer.append("bias", jsonBias);
            jsonLayer.append("weights", jsonWeights);
            jsonLayers.append(String.valueOf(i),jsonLayer);
        }
        String line = jsonNetwork.toString();
        try {
            File outputFile = new File(this.getClass().getClassLoader().getResource("autoencoder.txt").getFile());
            if(!outputFile.exists()){
                outputFile.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write(line);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readData(){;
        try {
            File inputFile = new File(this.getClass().getClassLoader().getResource("autoencoder.txt").getFile());
            if(inputFile.exists()){
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                String line = br.readLine();
                if(line != null) {
                    br.close();
                    JSONObject jsonNetwork = new JSONObject(line);
                    JSONObject jsonLayers = jsonNetwork.getJSONArray("layers").getJSONObject(0);
                    for (int i = 0; i < jsonLayers.length(); i++) { // for each Layer
                        JSONObject jsonLayer = jsonLayers.getJSONArray(String.valueOf(i + 1)).getJSONObject(0);
                        for (int k = 0; k < jsonLayer.length(); k++) {
                            JSONObject jsonBias = jsonLayer.getJSONArray("bias").getJSONObject(0);
                            for (int j = 0; j < jsonBias.length(); j++) {
                                double value = jsonBias.getJSONArray(String.valueOf(j)).getDouble(0);
                            }
                        }
                            JSONObject jsonWeights = jsonLayer.getJSONArray("weights").getJSONObject(0);
                            for(int j = 0; j < jsonWeights.length(); j++){ //for each node
                                JSONArray jsonWeightsOfLayer = jsonWeights.getJSONArray(String.valueOf(j));
                                for(int l = 0; l < jsonWeightsOfLayer.length(); l++){
                                    JSONArray jsonWeightsOfNode = jsonWeightsOfLayer.getJSONArray(j);
                                    for(int m = 0; m < jsonWeights.length(); m++){

                                    }
                                    System.out.println("test");
                                }
                            }
                            System.out.println("test");
                        
                    }
                }
                System.out.println("test");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<ArrayList<Double>>> shuffleTrainingsData(ArrayList<ArrayList<Double>> inputData, ArrayList<ArrayList<Double>> outputData, int length){
        ArrayList<ArrayList<Double>> subsetInputData = new ArrayList<>();
        ArrayList<ArrayList<Double>> subsetOutputData = new ArrayList<>();
        Random r = new Random();
        ArrayList<Integer> list = new ArrayList<>();
        int index;
        for(int i = 0; i < length; i++){
            index = r.nextInt(inputData.size());
            while(list.contains(new Integer(index))){
                index = r.nextInt(inputData.size());
            }
            list.add(index);
            subsetInputData.add(inputData.get(index));
            subsetOutputData.add(outputData.get(index));
        }
        ArrayList<ArrayList<ArrayList<Double>>> subset = new ArrayList<>();
        subset.add(subsetInputData);
        subset.add(subsetOutputData);
        return subset;
    }

    public void feedForward(ArrayList<Double> x) {
        if(buildStatus){
            // Fill first layer
            resetLayerValues();
            layers.get(0).fill(x);
            // Compute output
            layers.get(countLayers-1).getActivations();
        }
    }

    public void resetLayerValues(){
        for(int i = 0; i < countLayers; i++){
            layers.get(i).init_z_and_activations();
        }
    }

    public void update_mini_batch(ArrayList<ArrayList<Double>> x, ArrayList<ArrayList<Double>> y){
        double[][][] nablaW = initNablaW();
        double[][] nablaB = initNablaB();
        trainingsError.reset();
        for(int i = 0; i < x.size(); i++){ //for each input vector
            DeltaNabla dn = backprop(x.get(i), y.get(i));
            double[][][] deltaNablaW = dn.getDeltaNablaW();
            double[][] deltaNablaB = dn.getDeltaNablaB();
            nablaW = calculateNablaW(nablaW,deltaNablaW);
            nablaB = calculateNablaB(nablaB,deltaNablaB);
        }
        updateWeights(nablaW, x.size());
        updateBias(nablaB, x.size());
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
        double[][][] m = new double[m1.length][][];
        for(int i = 0; i < m.length; i++){ //loop for each layer
            m[i] = new double[m1[i].length][];
            for(int j = 0; j < m[i].length; j++){ //loop for each node's weights
                m[i][j] = new double[m1[i][j].length];
                for(int k = 0; k < m[i][j].length; k++){ //loop for each weight of a node
                    m[i][j][k] = m1[i][j][k] + m2[i][j][k];
                }
            }
        }
        return m;
    }

    public DeltaNabla backprop(ArrayList<Double> x, ArrayList<Double> y){
        //feed forward
        feedForward(x);
        updateTrainingsError(y);
        //delta nabla weight and bias
        double[][][] deltaNablaW = new double[countLayers][][];
        double[][]deltaNablaB = new double[countLayers][];
        //calculate delta for output layer
        double[] delta = costFunction(y,outputLayer.getActivations());
        //calculate nabla w and b for output layer
        Layer prevLayer = layers.get(countLayers-2);
        deltaNablaW[countLayers-2] = calculateDeltaNablaW(prevLayer.getActivations(),delta);
        deltaNablaB[countLayers-2] = delta;
        //calculate nabla w and b for other layers
        for(int i = countLayers-2; i > 0; i--){
            Layer currentLayer = layers.get(i);
            Layer nextLayer = layers.get(i+1);
            prevLayer = layers.get(i-1);
            double[] sp = sigmoid_prime(currentLayer.getZ());
            delta = calculateDelta(nextLayer.getWeights(),delta,sp);
            deltaNablaW[i-1] = calculateDeltaNablaW(prevLayer.getActivations(),delta);
            deltaNablaB[i-1] = delta;
        }
        return new DeltaNabla(deltaNablaW,deltaNablaB);
    }

    private void updateTrainingsError(ArrayList<Double> y){
        double[] output = outputLayer.getActivations();
        boolean error = false;
        int i = 0;
        while(i < y.size() && !error){
            if(output[i] != y.get(i)){
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

    public ArrayList<Double> getOutput(){
        ArrayList<Double> output = new ArrayList<>();
        double[] activations = outputLayer.getActivations();
        for(int i = 0; i < activations.length; i++){
            output.add(activations[i]);
        }
        return output;
    }

    public double[] costFunction(ArrayList<Double> expectedOutputValue,double[] realOutputValue){
        double[] cost = new double[expectedOutputValue.size()];
        for(int i = 0; i < cost.length; i++){
            cost[i] = realOutputValue[i]-expectedOutputValue.get(i);
        }
        return cost;
    }

    public double[] sigmoid_prime(double[] z){
        double[] sp = new double[z.length];
        for(int i = 0; i < sp.length; i++){
            sp[i] = sigmoid(z[i])*(1-sigmoid(z[i]));
        }
        return sp;
    }

    public double sigmoid(double z){
        return 1.0/(1.0 + Math.exp(-z));
    }

    public void setLearningRate(double lR) {
        learningRate = lR;
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
}
