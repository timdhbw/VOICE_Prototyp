package dhbw.ai13.autoencoding;

import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.AutoEncoder;
import dhbw.ai13.autoencoding.framework.AutoEncoderDataHandler;
import dhbw.ai13.autoencoding.framework.TrainingsError;
import dhbw.ai13.autoencoding.framework.layer.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by GomaTa on 18.04.2016.
 */
public class AutoencoderTrainer {

    private final int countLayers;
    private boolean DEBUG = true;
    private final int WINDOW_SAMPLE_SIZE = 2000;

    private final AutoEncoder autoencoder;
    private final double learningRate;
    private TrainingsError trainingsError;
    private AutoEncoderDataHandler dataHandler = new AutoEncoderDataHandler();

    public AutoencoderTrainer(AutoEncoder autoencoder, double learningRate) {
        this.autoencoder = autoencoder;
        this.learningRate = learningRate;
        this.trainingsError = new TrainingsError();
        this.countLayers = autoencoder.getCountLayers();
    }

    public void train(File[] trainingsData, int epochCount, int miniBatchSize) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if (autoencoder.isBuild()) {
            double[][][] trainingsSampledData = new double[trainingsData.length][][];
            for (int i = 0; i < trainingsData.length; i++) {
                trainingsSampledData[i] = autoencoder.windowing(trainingsData[i], WINDOW_SAMPLE_SIZE, WINDOW_SAMPLE_SIZE / 2);
            }
            SGD(trainingsSampledData, epochCount, miniBatchSize);
        } else {
            throw new AutoEncoderException("Autoencoder not built.");
        }
    }

    private void SGD(double[][][] trainingsData, int epochCount, int miniBatchSize) throws AutoEncoderException {
        int epoch = 1;
        do {
            double[][] subset = shuffleTrainingsData(trainingsData, miniBatchSize);
            updateMiniBatch(subset);
            if (DEBUG) {
                System.out.printf("[DEBUG] (%d/%d) - Error: %f\n", epoch, epochCount, trainingsError.calculateError(subset[subset.length-1],autoencoder.getOutputLayer().getActivations()));
            }
            epoch++;
        } while (epoch <= epochCount);
    }

    private double[][] shuffleTrainingsData(double[][][] trainingsSampleData, int length){
        double[][] subset = new double[length][];
        Random r = new Random();
        HashMap<String,Boolean> indexList = new HashMap<>();
        int index1, index2;
        for(int i = 0; i < length; i++){
            index1 = r.nextInt(trainingsSampleData.length);
            index2 = r.nextInt(trainingsSampleData[index1].length);
            while(indexList.get(index1+"_"+index2) != null){
                index1 = r.nextInt(trainingsSampleData.length);
                index2 = r.nextInt(trainingsSampleData[index1].length);
            }
            indexList.put(index1+"_"+index2,true);
            subset[i] = trainingsSampleData[index1][index2];
        }
        return subset;
    }

    private void updateMiniBatch(double[][] subset) throws AutoEncoderException {
        NablaBiases[] nablaB = new NablaBiases[countLayers-1];
        NablaWeights[] nablaW = new NablaWeights[countLayers-1];
        //init nabla biases and weights
        for(int i = 1; i < countLayers; i++){
            Layer layer = autoencoder.getLayer(i);
            Layer prevLayer = autoencoder.getLayer(i-1);
            nablaB[i-1] = new NablaBiases(layer.getCountNodes());
            nablaW[i-1] = new NablaWeights(layer.getCountNodes(),prevLayer.getCountNodes());
        }
        // backpropagation for each subset
        for(int i = 0; i < subset.length; i++){
            backpropagation(subset[i], nablaW, nablaB);
        }
        updateBiases(nablaB,subset.length);
        updateWeights(nablaW,subset.length);

    }

    public void updateBiases(NablaBiases[] nablaB, int dataLength){
        for(int i = 1; i < countLayers; i++ ){
            Layer layer = autoencoder.getLayer(i);
            ArrayList<Node> nodes = layer.getNodes();
            NablaBiases nablaBLayer = nablaB[i-1];
            for(int k = 0; k < nodes.size(); k++){
                Node node = nodes.get(k);
                double oldValue = node.getBias();
                node.setBias(oldValue - ((learningRate/Double.valueOf(dataLength)) * nablaBLayer.getValue(k)));
            }
        }
    }

    public void updateWeights(NablaWeights[] nablaW, int dataLength){
        for(int i = 1; i < countLayers; i++ ){
            Layer layer = autoencoder.getLayer(i);
            ArrayList<Node> nodes = layer.getNodes();
            NablaWeights nablaWLayer= nablaW[i-1];
            for (int k = 0; k < nodes.size(); k++) {
                Node node = nodes.get(k);
                ArrayList<Double> nablaWNode = nablaWLayer.getWeightsOfNode(k);
                double[] oldWeights = node.getWeights();
                for (int j = 0; j < oldWeights.length; j++) {
                    node.updateWeight(j, oldWeights[j] - ((learningRate/Double.valueOf(dataLength)) * nablaWNode.get(j)));
                }
            }
        }
    }

    public void backpropagation(double[] idealValues, NablaWeights[] nablaW, NablaBiases[] nablaB) throws AutoEncoderException {
        //feed forward
        autoencoder.feedForward(idealValues);

        Layer prevLayer, currentLayer, nextLayer;
        Delta deltaClass;
        double[] delta;

        //calculate delta for output layer
        currentLayer = autoencoder.getOutputLayer();
        prevLayer = autoencoder.getLayer(countLayers-2);
        deltaClass = new Delta(currentLayer.getActivationFunction());
        delta = deltaClass.calculate(idealValues,currentLayer.getActivations());
        nablaW[countLayers-2].add(delta,prevLayer.getActivations());
        nablaB[countLayers-2].add(delta);

        //calculate nabla w and b for other layers
        for(int i = countLayers-2; i > 0; i--){
            currentLayer = autoencoder.getLayer(i);
            nextLayer = autoencoder.getLayer(i+1);
            prevLayer = autoencoder.getLayer(i-1);

            // calculate deltas
            deltaClass = new Delta(currentLayer.getActivationFunction());
            delta = deltaClass.calculate(nextLayer.getWeights(),delta, currentLayer.getZs());

            // calculate delta nabla weights and biases
            nablaW[i-1].add(delta,prevLayer.getActivations());
            nablaB[i-1].add(delta);
        }
    }


    /*
    public void saveDataToFile(String filepath){
        dataHandler.saveIntoFile(autoencoder, filepath);
    }

    public void readDataFromFile(String filepath){
        dataHandler.readFromFile(autoencoder, filepath);
    }
    */
}
