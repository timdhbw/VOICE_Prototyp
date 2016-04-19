package dhbw.ai13.autoencoding;

import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.AutoEncoder;
import dhbw.ai13.autoencoding.framework.AutoEncoderDataHandler;
import dhbw.ai13.autoencoding.framework.TrainingsError;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static sun.security.krb5.internal.Krb5.DEBUG;

/**
 * Created by GomaTa on 18.04.2016.
 */
public class AutoencoderTrainer {

    private final AutoEncoder autoencoder;

    private boolean DEBUG = true;
    private int WINDOW_SAMPLE_SIZE = 2000;
    private AutoEncoderDataHandler dataHandler = new AutoEncoderDataHandler();
    private TrainingsError trainingsError;

    public AutoencoderTrainer(AutoEncoder autoencoder) {
        this.autoencoder = autoencoder;
        this.trainingsError = autoencoder.getTrainingsError();
    }

    public void train(File[] trainingsData, int epochCount, int miniBatchSize) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if (autoencoder.isBuild()) {
            double[][][] trainingsSampleData = new double[trainingsData.length][][];
            for (int i = 0; i < trainingsData.length; i++) {
                trainingsSampleData[i] = autoencoder.windowing(trainingsData[i], WINDOW_SAMPLE_SIZE, WINDOW_SAMPLE_SIZE / 2);
            }
            int epoch = 1;
            do {
                double[][] subset = shuffleTrainingsData(trainingsSampleData, miniBatchSize);
                autoencoder.update_mini_batch(subset);
                if (DEBUG) {
                    System.out.printf("[DEBUG] (%d/%d) - Error Rate: %f\n", epoch, epochCount, trainingsError.getErrorRate());
                }
                epoch++;
            } while (epoch <= epochCount);
            if (DEBUG) {
                System.out.printf("[DEBUG] Final error rate: %f\n", trainingsError.getErrorRate());
            }
        } else {
            throw new AutoEncoderException("AutoEncoder not built.");
        }
    }

    public double[][] shuffleTrainingsData(double[][][] trainingsSampleData, int length){
        double[][] subset = new double[length][];
        Random r = new Random();
        ArrayList<Integer> index1List = new ArrayList<>();
        ArrayList<Integer> index2List = new ArrayList<>();
        int index1;
        int index2;
        for(int i = 0; i < length; i++){
            index1 = r.nextInt(trainingsSampleData.length);
            index2 = r.nextInt(trainingsSampleData[index1].length);
            while(index1List.contains(new Integer(index1)) && index2List.contains(new Integer(index2))){
                index1 = r.nextInt(trainingsSampleData.length);
                index2 = r.nextInt(trainingsSampleData[index1].length);
            }
            index1List.add(index1);
            index2List.add(index2);
            subset[i] = trainingsSampleData[index1][index2];
        }
        return subset;
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

    public void saveDataToFile(String filepath){
        dataHandler.saveIntoFile(autoencoder, filepath);
    }

    public void readDataFromFile(String filepath){
        dataHandler.readFromFile(autoencoder, filepath);
    }
}
