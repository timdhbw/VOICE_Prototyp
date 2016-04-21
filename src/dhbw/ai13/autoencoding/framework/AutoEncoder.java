package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.ActivationFunction;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.elements.Layer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;

public class AutoEncoder {
    //AutoEncoder
    private boolean isBuild = false;
    //Layers
    private ArrayList<Layer> layers = new ArrayList<>();
    private Layer inputLayer;
    private Layer encodeLayer;
    private Layer outputLayer;
    private int countLayers = 0;

    private final boolean DEBUG = true;
    private final int windowSampleSize;

    public AutoEncoder(int windowSize){
        this.windowSampleSize = windowSize;
    }

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

    public double[] encode(double[] input) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if(isBuild) {
            inputLayer.setValues(input);
            return encodeLayer.calculateActivations();

        }else{
            throw new AutoEncoderException("Autoencoder not built.");
        }
    }

    public void addLayer(int id, int countNodes, ActivationFunction activationFunction) {
        layers.add(new Layer(id, countNodes, activationFunction));
        countLayers++;
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
