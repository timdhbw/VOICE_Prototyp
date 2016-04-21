package dhbw.ai13.autoencoding.framework;

import com.musicg.dsp.WindowFunction;
import dhbw.ai13.audio.AudioStreamReader;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.elements.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by GomaTa on 18.04.2016.
 */
public class SmallAutoencoderTrainer {

    private final int countLayers;
    private boolean DEBUG = true;

    private final AutoEncoder autoencoder;
    private final double learningRate;
    private TrainingsError trainingsError;
    private AutoEncoderDataHandler dataHandler = new AutoEncoderDataHandler();
    private boolean showresult;

    public SmallAutoencoderTrainer(AutoEncoder autoencoder, double learningRate) {
        this.autoencoder = autoencoder;
        this.learningRate = learningRate;
        this.trainingsError = new TrainingsError();
        this.countLayers = autoencoder.getCountLayers();
    }


    public void train(double[][] trainingsData, int epochCount) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if (autoencoder.isBuild()) {
            SGD(trainingsData, epochCount);
        } else {
            throw new AutoEncoderException("Autoencoder not built.");
        }
    }

    public void train(double[][] trainingsData, int epochCount, boolean showresult) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        this.showresult = showresult;
        if (autoencoder.isBuild()) {
            SGD(trainingsData, epochCount);
        } else {
            throw new AutoEncoderException("Autoencoder not built.");
        }
    }


    private void SGD(double[][] trainingsData, int epochCount) throws AutoEncoderException {
        int epoch = 1;
        Random r = new Random();
        do {
            int index = r.nextInt(trainingsData.length);
            double[] subset = trainingsData[index];
            update(subset);
            if (DEBUG) {
                System.out.printf("[DEBUG] (%d/%d) - Error: %f\n", epoch, epochCount, trainingsError.calculateError(subset,autoencoder.getOutputLayer().getActivations()));
            }
            epoch++;
        } while (epoch <= epochCount);
    }

    private void update(double[] subset) throws AutoEncoderException {
        NablaBiases[] nablaB = new NablaBiases[countLayers-1];
        NablaWeights[] nablaW = new NablaWeights[countLayers-1];
        for(int i = 1; i < countLayers; i++){
            Layer layer = autoencoder.getLayer(i);
            Layer prevLayer = autoencoder.getLayer(i-1);
            nablaB[i-1] = new NablaBiases(layer.getCountNodes());
            nablaW[i-1] = new NablaWeights(layer.getCountNodes(),prevLayer.getCountNodes());
        }
        backpropagation(subset, nablaW, nablaB);
        updateBiases(nablaB);
        updateWeights(nablaW);

    }

    public void updateBiases(NablaBiases[] nablaB){
        for(int l = 1; l < countLayers; l++ ){
            Layer layer = autoencoder.getLayer(l);
            ArrayList<Node> nodes = layer.getNodes();
            NablaBiases nablaBLayer = nablaB[l-1];
            for(int j = 0; j < nodes.size(); j++){
                Node node = nodes.get(j);
                double oldValue = node.getBias();
                node.setBias(oldValue - (learningRate * nablaBLayer.getValue(j)));
            }
        }
    }

    public void updateWeights(NablaWeights[] nablaW){
        for(int l = 1; l < countLayers; l++ ){
            Layer layer = autoencoder.getLayer(l);
            ArrayList<Node> nodes = layer.getNodes();
            NablaWeights nablaWLayer= nablaW[l-1];
            for (int j = 0; j < nodes.size(); j++) {
                Node node = nodes.get(j);
                ArrayList<Double> nablaWNode = nablaWLayer.getWeightsOfNode(j);
                double[] oldWeights = node.getWeights();
                for (int k = 0; k < oldWeights.length; k++) {
                    node.updateWeight(k, oldWeights[k] - (learningRate * nablaWNode.get(k)));
                }
            }
        }
    }

    public void backpropagation(double[] idealValues, NablaWeights[] nablaW, NablaBiases[] nablaB) throws AutoEncoderException {
        //feed forward
        autoencoder.feedForward(idealValues);
        if(showresult) {
            Layer outputLayer = autoencoder.getOutputLayer();
            for (int i = 0; i < outputLayer.getCountNodes(); i++) {
                System.out.printf("%f|%f\n",idealValues[i],outputLayer.getNodes().get(i).getActivationValue());
            }
        }
        Layer prevLayer, currentLayer, nextLayer;
        Delta deltaClass;

        currentLayer = autoencoder.getOutputLayer();
        prevLayer = autoencoder.getLayer(countLayers-2);
        // calculate delta for output layer
        deltaClass = new Delta(currentLayer.getActivationFunction());
        double[] delta = deltaClass.calculate(idealValues,currentLayer.getActivations(), currentLayer.getZs());

        nablaW[countLayers-2].setWeights(delta,prevLayer.getActivations());
        nablaB[countLayers-2].setBiases(delta);

        //calculate nabla w and b for other layers
        for(int i = countLayers-2; i > 0; i--){
            currentLayer = autoencoder.getLayer(i);
            nextLayer = autoencoder.getLayer(i+1);
            prevLayer = autoencoder.getLayer(i-1);

            // calculate deltas
            deltaClass = new Delta(currentLayer.getActivationFunction());
            delta = deltaClass.calculate(nextLayer.getWeights(),delta, currentLayer.getZs());

            // calculate delta nabla weights and biases
            nablaW[i-1].setWeights(delta,prevLayer.getActivations());
            nablaB[i-1].setBiases(delta);
        }
    }

}
