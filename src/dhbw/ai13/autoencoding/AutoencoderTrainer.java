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

    private int WINDOW_SAMPLE_SIZE = 2000;
    private AutoEncoderDataHandler dataHandler = new AutoEncoderDataHandler();
    private TrainingsError trainingsError;

    public AutoencoderTrainer(AutoEncoder autoencoder) {
        this.autoencoder = autoencoder;
        this.trainingsError = autoencoder.getTrainingsError();
    }

    public void train(File[] trainingsData, double error, int miniBatchSize) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        if (autoencoder.isBuild()) {
            double[][][] trainingsSampleData = new double[trainingsData.length][][];
            for (int i = 0; i < trainingsData.length; i++) {
                //trainingsSampleData[i] = windowing(trainingsData[i], 0.05, 0.025);
                trainingsSampleData[i] = autoencoder.windowing(trainingsData[i], WINDOW_SAMPLE_SIZE, WINDOW_SAMPLE_SIZE / 2);
            }
            int iterations = 0;
            int maxIterations = 1000;
            do {
                double[][] subset = shuffleTrainingsData(trainingsSampleData, miniBatchSize);
                autoencoder.update_mini_batch(subset);
                iterations++;
                if (DEBUG && (iterations % 100) == 0) {
                    System.out.printf("[DEBUG] (%d/%d) - Error Rate: %f\n", iterations, maxIterations, trainingsError.getErrorRate());
                }
            } while (trainingsError.getErrorRate() > error && iterations < maxIterations);
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
        ArrayList<Integer> indexList = new ArrayList<>();
        int index;
        for(int i = 0; i < length; i++){
            index = r.nextInt(trainingsSampleData.length);
            while(indexList.contains(new Integer(index))){
                index = r.nextInt(trainingsSampleData.length);
            }
            indexList.add(index);
            subset[i] = trainingsSampleData[index][0];
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
