package dhbw.ai12.speech;

import java.io.File;

import dhbw.ai12.speech.mfcc.FeatureExtractor;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A test application for dhbw.ai12.speech
 */
public class TestApp {

    public static void main(String[] args) throws Exception{
        FeatureExtractor extractor = new FeatureExtractor();

        long startTime = System.nanoTime();
        String filename="C:/Users/Stefan/Desktop/waves/A_Christian_Eichmann_9.wav";
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
        double[][] mfccFrames = extractor.getMfcc(audioInputStream, 0.050, 0.025, 512, 0.0, 1.0,true);
        long endTime = System.nanoTime();

        for (int j = 0; j < mfccFrames[0].length; j++) {
            System.out.print(j +"\t");
            for (int i = 0; i < mfccFrames.length; i++) {
                System.out.print(Double.toString(mfccFrames[i][j]).replace('.', ',') + "\t");
            }
            System.out.println();
        }

        System.out.println();
        System.out.printf("Finished in %.2f ms", (endTime - startTime) / 1000000.0);
    }



}
