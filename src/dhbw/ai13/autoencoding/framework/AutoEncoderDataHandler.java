package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.framework.layer.Layer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;

/**
 * Created by GomaTa on 27.03.2016.
 */

public class AutoEncoderDataHandler {

/*
    public void readFromFile(AutoEncoder autoEncoder, String filename) {
        try {
            URL fileURL = this.getClass().getClassLoader().getResource("autoencoder.txt");
            if (fileURL != null) {
                File inputFile = new File(fileURL.getFile());
                if (inputFile.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(inputFile));
                    String line = br.readLine();
                    if (line != null) {
                        br.close();
                        JSONObject jsonNetwork = new JSONObject(line);
                        JSONObject jsonLayers = jsonNetwork.getJSONArray("layers").getJSONObject(0);
                        for (int i = 0; i < jsonLayers.length(); i++) { // for each Layer
                            Layer layer = autoEncoder.getLayers().get(i + 1);
                            JSONObject jsonLayer = jsonLayers.getJSONArray(String.valueOf(i + 1)).getJSONObject(0);
                            for (int k = 0; k < jsonLayer.length(); k++) {
                                JSONObject jsonBias = jsonLayer.getJSONArray("bias").getJSONObject(0);
                                for (int j = 0; j < jsonBias.length(); j++) {
                                    double value = jsonBias.getJSONArray(String.valueOf(j)).getDouble(0);
                                    layer.setBias(j, value);
                                }
                            }
                            JSONObject jsonWeights = jsonLayer.getJSONArray("weights").getJSONObject(0);
                            for (int j = 0; j < jsonWeights.length(); j++) { //for each layer
                                JSONArray jsonWeightsOfNode = jsonWeights.getJSONArray(String.valueOf(j)).getJSONArray(j);
                                for (int l = 0; l < jsonWeightsOfNode.length(); l++) {
                                    double value = jsonWeightsOfNode.getDouble(l);
                                    layer.setWeight(j, l, value);
                                }
                            }
                        }
                    }
                }
            }
            if (autoEncoder.isDEBUG()) {
                System.out.println("[DEBUG] Read data from " + filename + " successfully.");
            }
        }catch (JSONException e){
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIntoFile(AutoEncoder autoEncoder, String filename){
        try {
            //URL fileURL = this.getClass().getClassLoader().getResource(filename);
            //if(fileURL != null) {
                File outputFile = new File(filename);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
                bw.write(writeJSON(autoEncoder));
                bw.flush();
                bw.close();
                if(autoEncoder.isDEBUG()){
                    System.out.println("[DEBUG] Saved data into " + filename + " successfully.");
                }
            /*}else{
                if(autoEncoder.isDEBUG()){
                    System.out.println("[DEBUG] File " + filename + " does not exist.");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String writeJSON(AutoEncoder autoEncoder){
        JSONObject jsonNetwork = new JSONObject();
        JSONObject jsonLayers = new JSONObject();
        jsonNetwork.append("layers", jsonLayers);
        for(int i = 1; i < autoEncoder.getCountLayers(); i++){ //for each layer
            JSONObject jsonLayer = new JSONObject();
            JSONObject jsonWeights = new JSONObject();
            JSONObject jsonBias = new JSONObject();
            Layer layer = autoEncoder.getLayers().get(i);
            double[] bias = layer.getBiases();
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
        return jsonNetwork.toString();
    }
    */
}