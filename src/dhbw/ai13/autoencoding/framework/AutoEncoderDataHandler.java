package dhbw.ai13.autoencoding.framework;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by GomaTa on 27.03.2016.
 */

public class AutoEncoderDataHandler {


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
                            Layer layer = autoEncoder.getLayer(i + 1);
                            JSONObject jsonLayer = jsonLayers.getJSONArray(String.valueOf(i + 1)).getJSONObject(0);
                            for (int k = 0; k < jsonLayer.length(); k++) {
                                JSONObject jsonBias = jsonLayer.getJSONArray("bias").getJSONObject(0);
                                for (int j = 0; j < jsonBias.length(); j++) {
                                    double value = jsonBias.getJSONArray(String.valueOf(j)).getDouble(0);
                                    layer.getNodes().get(j).setBias(value);
                                }
                            }
                            JSONObject jsonWeights = jsonLayer.getJSONArray("weights").getJSONObject(0);
                            for (int j = 0; j < jsonWeights.length(); j++) { //for each layer
                                JSONArray jsonWeightsOfNode = jsonWeights.getJSONArray(String.valueOf(j)).getJSONArray(j);
                                for (int l = 0; l < jsonWeightsOfNode.length(); l++) {
                                    double value = jsonWeightsOfNode.getDouble(l);
                                    layer.getNodes().get(j).updateWeight(l, value);
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

    public void saveIntoFile(AutoEncoder autoEncoder, String filepath){
        try {
            try{
                Files.createFile(Paths.get(filepath));
            }catch (FileAlreadyExistsException e){}
            File outputFile = new File(filepath);
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
            String[] lines = writeCSV(autoEncoder);
            for(String line: lines){
                pw.write(line);
            }
            pw.flush();
            pw.close();
            if(autoEncoder.isDEBUG()){
                System.out.println("[DEBUG] Saved data into " + filepath + " successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] writeCSV(AutoEncoder autoencoder){
        int countLayers = autoencoder.getCountLayers();
        String[] lines = new String[countLayers];
        for(int i = 1; i < countLayers; i++){ //for each layer
            Layer layer = autoencoder.getLayer(i);
            int countNodes = layer.getCountNodes();
            double[] bias = layer.getBiases();
            double[][] weights = layer.getWeights();
            String tmp = "";
            for(int j = 0; j < countNodes; j++ ){ //for each node
                int countPrevNodes = weights[j].length;
                tmp += (Double.toString(bias[j]));
                tmp += "b"; //separator between baises and weights of a node
                for(int k = 0; k < countPrevNodes; k++){
                    tmp += Double.toString(weights[j][k]);
                    if(k < (countPrevNodes-1)){
                        tmp += "w";
                    }
                }
                if(j < (countNodes-1)){
                    tmp += "n";
                }
            }
            lines[i] = tmp;
            System.out.println(lines[1].substring(0,500));
        }
        return lines;
    }

    private String writeJSON(AutoEncoder autoEncoder){
        JSONObject jsonNetwork = new JSONObject();
        JSONObject jsonLayers = new JSONObject();
        jsonNetwork.append("layers", jsonLayers);
        for(int i = 1; i < autoEncoder.getCountLayers(); i++){ //for each layer
            JSONObject jsonLayer = new JSONObject();
            JSONObject jsonWeights = new JSONObject();
            JSONObject jsonBias = new JSONObject();
            Layer layer = autoEncoder.getLayer(i);
            double[] bias = layer.getBiases();
            double[][] weights = layer.getWeights();
            for(int j = 0; j < layer.getCountNodes(); j++ ){ //for each node
                jsonBias.append(String.valueOf(j),bias[j]);
                for(int k = 0; k < layer.getCountNodes(); k++){
                    jsonWeights.append(String.valueOf(k),weights[j]);
                }
            }
            jsonLayer.append("bias", jsonBias);
            jsonLayer.append("weights", jsonWeights);
            jsonLayers.append(String.valueOf(i),jsonLayer);
        }
        return jsonNetwork.toString();
    }
}