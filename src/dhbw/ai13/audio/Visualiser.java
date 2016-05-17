package dhbw.ai13.audio;

import dhbw.ai13.audio.AudioStreamReader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/**
 * Created by GomaTa on 09.05.2016.
 */
public class Visualiser {

    private double[] preEmphasisFilter(final double[] samples, double preemphasis) {
        final double[] newSignal = new double[samples.length];
        newSignal[0] = samples[0];
        IntStream.range(1, newSignal.length).parallel().forEach(i -> {
            newSignal[i] = samples[i] - (preemphasis * samples[i-1]);
        });
        return newSignal;
    }

    public static void main(String[] args){
        File audioFile = new File("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources\\A_Eric_Zenker_02.wav");
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioStreamReader reader = new AudioStreamReader(audioInputStream);
            double[] samples = reader.readSamples();
            saveSamplesToFile("C:\\Users\\GomaTa\\Documents\\Studium\\Studienarbeit\\MFCC\\preemphasis.csv", samples);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSamplesToFile(String filepath, double[] samples){
        try{
            List<String> lines = new LinkedList<>();
            StringBuilder sb = new StringBuilder();
            for(double v: samples){
                sb.append(String.format("%f;\n",v));
            }
            lines.add(sb.toString());
            Files.write(Paths.get(filepath),lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
