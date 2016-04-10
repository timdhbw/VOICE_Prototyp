package dhbw.ai13.autoencoding.framework;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class TrainingsData {
    private double[][] trainingsData;

    public double[][] readFromFile(String filename){
        String line;
        ArrayList<ArrayList<Double>> list = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            int lineNumber = 0;
            while((line = br.readLine()) != null){
                String[] elements = line.split(";");
                list.add(new ArrayList<>());
                for(int j = 0; elements.length > j; j++){
                    list.get(lineNumber).add(j,Double.valueOf(elements[j]));
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
        ArrayList<Double> tmpList = new ArrayList<>();
        trainingsData = new double[list.size()][];
        for(int i = 0; i < list.size(); i++){
            tmpList = list.get(i);
            trainingsData[i] = new double[tmpList.size()];
            for(int j = 0; j < tmpList.size(); j++){
                trainingsData[i][j] = tmpList.get(j);
            }
        }
        return trainingsData;
    }
}
