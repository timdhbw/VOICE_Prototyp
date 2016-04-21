package dhbw.ai13.autoencoding;

import dhbw.ai13.autoencoding.activationFunctions.Sigmoid;
import dhbw.ai13.autoencoding.activationFunctions.TanH;
import dhbw.ai13.autoencoding.framework.AudiofileHandler;
import dhbw.ai13.autoencoding.framework.AutoEncoder;

import java.io.File;

/**
 * Created by GomaTa on 13.02.2016.
 */
public class AutoEncoderTest {

    public static void main(String[] args) throws Exception {
        // Layer Info
        int numInOutLayer = 2000;
        int numMidLayer = numInOutLayer/4;

        // Init Autoencoder
        AutoEncoder autoencoder = new AutoEncoder(numInOutLayer);
        autoencoder.addLayer(1 ,numInOutLayer, new TanH(), true);
        autoencoder.addLayer(2 ,numMidLayer, new TanH(), true);
        autoencoder.addLayer(3 ,numInOutLayer, new TanH(), true);
        autoencoder.build();

        // Reading Trainingsdata
        //double[][] trainingsData = new TrainingsData().readFromFile("C:\\Users\\GomaTa\\Documents\\Training.csv");
        AudiofileHandler afh = new AudiofileHandler();
        File[] trainingsData = afh.getTrainingsData("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources\\tmp");

        // Train Autoencoder
        AutoencoderTrainer aet = new AutoencoderTrainer(autoencoder, 0.1, numInOutLayer);
        aet.train(trainingsData, 1, 1);
        aet.saveDataToFile("C:\\Users\\GomaTa\\Desktop\\autoencoder.txt");
        autoencoder.encode(new File("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources\\A_Eric_Zenker_02.wav"));

        /*
        double[] input1 = new double[]{1.0,0.0,0.0};
        System.out.print("[Input]\t");
        printData(input1);
        double[] output1 = ae.feedForward(input1);
        System.out.print("[Output]");
        printData(output1);

        System.out.println();

        double[] input2 = new double[]{0.0,1.0,0.0};
        System.out.print("[Input]\t");
        printData(input2);
        double[] output2 = ae.feedForward(input1);
        System.out.print("[Output]");
        printData(output2);

        System.out.println();

        double[] input3 = new double[]{0.0,0.0,1.0};
        System.out.print("[Input]\t");
        printData(input3);
        double[] output3 = ae.feedForward(input1);
        System.out.print("[Output]");
        printData(output3);
        */
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
