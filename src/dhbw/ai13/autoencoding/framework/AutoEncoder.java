package dhbw.ai13.autoencoding.framework;

import com.musicg.dsp.WindowFunction;
import dhbw.ai13.audio.AudioStreamReader;
import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

public class AutoEncoder {
    //AutoEncoder
    private boolean isBuild = false;
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
    private int WINDOW_SAMPLE_SIZE = 2000;


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
        isBuild = true;
        if(DEBUG){System.out.println("[DEBUG] Built successfull.");}
    }

    public double[] encode(File file, int countCoefficients) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        double[] coefficients = new double[countCoefficients];
        if (isBuild) {
            double[][] windowedData = windowing(file, WINDOW_SAMPLE_SIZE, WINDOW_SAMPLE_SIZE / 2);
            double[][] outputData = new double[windowedData.length][];
            for(int i = 0; i < windowedData.length; i++){
                resetLayers();
                inputLayer.fill(windowedData[i]);
                outputData[i] = layers.get(countLayers-2).getActivations();
                System.out.println("test");
            }
        }
        return null;
    }

    public void resetLayers(){
        for(int i = 0; i < countLayers; i++){
            layers.get(i).resetValues();
        }
    }


    public double[] feedForward(double [] xy) throws AutoEncoderException {
        if(isBuild){
            inputLayer.fill(xy);
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
                    nablaW[i-1][k][j] = Math.random();
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

    public double[][]  windowing(File audioFile, final int windowSampleLength, final int windowSampleStep) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        //return automatically normalised double values
        double[] samples = reader.readSamples();
        final int sampleRate = (int) reader.getSampleRate();

        final int windowSampleCount = windowSampleLength;
        final int offsetSampleCount = windowSampleStep;
        //final int windowSampleCount = (int) (sampleRate * windowLength);
        //final int offsetSampleCount = (int) (sampleRate * windowStep);
        final int windowCount = getWindowCount(samples,windowSampleCount,offsetSampleCount);

        final double[][] windowedSamples = new double[windowCount][windowSampleCount];

        WindowFunction windowFunction = new WindowFunction();
        windowFunction.setWindowType(3);
        double[] windowValues = windowFunction.generate(windowSampleCount);
        //System.out.println("WindowSampleCount:" + windowSampleCount);
        //System.out.println("HammingWindowLength:" + windowValues.length);
        IntStream.range(0, windowedSamples.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;
            // fill the frame with the portion of the signal, weighted with a hamming window
            IntStream.range(0, windowedSamples[i].length).parallel().forEach(j -> {
                if (startOffset + j < samples.length)
                    windowedSamples[i][j] = samples[startOffset + j] * windowValues[j];
            });
        });
        return windowedSamples;
    }

    private int getWindowCount(final double[] samples, final int windowSampleCount, final int offsetSampleCount) {
        int windowCount = 1;
        for (int i = samples.length - windowSampleCount; i > 0; i-= offsetSampleCount) {
            windowCount++;
        }
        return windowCount;
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

    public boolean isBuild() {
        return isBuild;
    }

    public TrainingsError getTrainingsError() {
        return trainingsError;
    }
}
