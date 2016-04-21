package dhbw.ai13.autoencoding;

import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;
import dhbw.ai13.autoencoding.framework.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Created by GomaTa on 13.02.2016.
 */
public class AutoencoderTest {

    public static void main(String[] args) throws Exception {
        //testAudioAutoencoder();
        testSmallAutoencoder();
    }

    private static void testSmallAutoencoder() throws AutoEncoderException, IOException, UnsupportedAudioFileException {
        SmallAutoencoder sae = new SmallAutoencoder();
        AutoEncoder autoencoder = sae.getAutoencoder();

        double[][] trainingsData = new double[4][];
        trainingsData[0] = new double[]{1.0,0.0,0.0,0.0};
        trainingsData[1] = new double[]{0.0,1.0,0.0,0.0};
        trainingsData[2] = new double[]{0.0,0.0,1.0,0.0};
        trainingsData[3] = new double[]{0.0,0.0,0.0,1.0};
        SmallAutoencoderTrainer sat = new SmallAutoencoderTrainer(autoencoder, 0.5);
        sat.train(trainingsData, 1000000);
        System.out.println();
        sat.train(trainingsData, 1, true);
        System.out.println();
        sat.train(trainingsData, 1, true);
        System.out.println();
        sat.train(trainingsData, 1, true);
        System.out.println();
        sat.train(trainingsData, 1, true);

    }

    public static void testAudioAutoencoder() throws AutoEncoderException, IOException, UnsupportedAudioFileException {
        AudioAutoencoder aae = new AudioAutoencoder();
        AutoEncoder autoencoder = aae.getAutoencoder();
        int numInOutLayer = aae.getNumInOutLayer();

        // Reading Trainingsdata
        AudiofileHandler afh = new AudiofileHandler();
        File[] trainingsData = afh.getTrainingsData("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources");

        // Train Autoencoder
        AudioAutoencoderTrainer aet = new AudioAutoencoderTrainer(autoencoder, 0.1, numInOutLayer);
        aet.train(trainingsData, 2000, 1);
        aet.train(trainingsData, 1, 1, true);
        //aet.saveDataToFile("C:\\Users\\GomaTa\\Desktop\\autoencoder.txt");
        //autoencoder.encode(new File("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources\\A_Eric_Zenker_02.wav"));

    }

    public static void printData(double[] data){
        System.out.print("{");
        for(int i = 0; i < data.length; i++){
            System.out.printf( "%.3f; ", data[i]);
        }
        System.out.println("}");
    }


    public static void printData(double[][] data, int index){
        System.out.print("{");
        double[] trainingsData = data[1];
        for(int i = 0; i < trainingsData.length; i++){
            System.out.printf( "%.3f; ", trainingsData[i]);
        }
        System.out.println("}");
    }
}
