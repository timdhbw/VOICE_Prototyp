package dhbw.ai13.autoencoding.framework;

import com.musicg.dsp.WindowFunction;
import dhbw.ai13.audio.AudioStreamReader;
import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.layer.Layer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class AutoEncoder {
    //AutoEncoder
    private boolean isBuild = false;
    //Layers
    private ArrayList<Layer> layers = new ArrayList<>();
    private Layer inputLayer;
    private Layer encodeLayer;
    private Layer outputLayer;
    private double learningRate;
    private int countLayers = 0;

    private final boolean DEBUG = true;
    private int WINDOW_SAMPLE_SIZE = 2000;

    public void build() throws AutoEncoderException {
        for(int i = 0; i < countLayers; i++){
            Layer layer = layers.get(i);
            if(i > 0){
                Layer prevLayer = layers.get(i-1);
                layer.build(prevLayer, prevLayer.getCountNodes());
            }else{
                layer.build(null,0);
            }
        }
        // set shortcuts for layers
        inputLayer = layers.get(0);
        encodeLayer = layers.get((countLayers-1)/2);
        outputLayer = layers.get(countLayers-1);
        // build finished
        isBuild = true;
        if(DEBUG){System.out.println("[DEBUG] Built successfull.");}
    }


    public double[] feedForward(double [] inputValues) throws AutoEncoderException {
        if(isBuild){
            inputLayer.setValues(inputValues);
            for(int i = 1; i < countLayers; i++){
                layers.get(i).calculateActivations();
            }
            return outputLayer.getActivations();
        }else{
            throw new AutoEncoderException("AutoEncoder not built.");
        }
    }

    public double[] encode(File file) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if(isBuild) {
            double[] coefficients = new double[encodeLayer.getCountNodes()];
            double[][] windowedData = windowing(file, WINDOW_SAMPLE_SIZE, WINDOW_SAMPLE_SIZE / 2);
            final int n = windowedData.length;
            double[][] outputData = new double[n][];
            // calculate autoencoder values
            for (int i = 0; i < windowedData.length; i++) {
                inputLayer.setValues(windowedData[i]);
                outputData[i] = encodeLayer.calculateActivations();
            }
            //calculate average
            for(int i = 0; i < coefficients.length; i++){
                double coefficient = 0.0;
                for(int j = 0; j < n; j++){
                    coefficient += outputData[j][i];
                }
                coefficients[i] = coefficient / Double.valueOf(n);
            }
            return coefficients;

        }else{
            throw new AutoEncoderException("Autoencoder not built.");
        }
    }

    public void addLayer(int id, int countNodes, ActivationFunction activationFunction, boolean hasBias) {
        layers.add(new Layer(id, countNodes, activationFunction, hasBias));
        countLayers++;
    }

    public double[][]  windowing(File audioFile, final int windowSampleLength, final int windowSampleStep) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        //return automatically normalised double values
        double[] samples = reader.readSamples();
        final int sampleRate = (int) reader.getSampleRate();

        final int windowSampleCount = windowSampleLength;
        final int offsetSampleCount = windowSampleStep;
        final int windowCount = getWindowCount(samples,windowSampleCount,offsetSampleCount);

        final double[][] windowedSamples = new double[windowCount][windowSampleCount];

        WindowFunction windowFunction = new WindowFunction();
        windowFunction.setWindowType(3);
        double[] windowValues = windowFunction.generate(windowSampleCount);
        //System.out.println("WindowSampleCount:" + windowSampleCount);
        //System.out.println("HammingWindowLength:" + windowValues.length);
        IntStream.range(0, windowedSamples.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;
            // add the frame with the portion of the signal, weighted with a hamming window
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

    public Layer getOutputLayer() {
        return outputLayer;
    }

    public Layer getLayer(int index){
        return layers.get(index);
    }
}
