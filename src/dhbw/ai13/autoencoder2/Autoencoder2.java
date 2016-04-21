package dhbw.ai13.autoencoder2;

import com.musicg.dsp.WindowFunction;
import dhbw.ai13.audio.AudioStreamReader;
import dhbw.ai13.speech.detection.Vector13D;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static dhbw.ai13.ann.ArrayUtil.getMaxIndex;
import static dhbw.ai13.ann.Configuration.DEBUG;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class Autoencoder2 {
    static protected final double TRAIN_ERROR = 0.0001;
    static protected final int SAMPLE_NEURONS = 2000;
    static protected final int ENCODE_NEURONS = 500;
    static protected final int COEFFICIENTS_TO_USE = 26;
    private HashMap<Integer, String> indexToSpeakerMap;
    private BasicNetwork net;
    private final boolean DEBUG = true;

    public Autoencoder2(File audiofile) throws IOException, UnsupportedAudioFileException {
        long start = System.nanoTime();

        // prepare datasets
        double[][] inOutput =  windowing(audiofile, SAMPLE_NEURONS,SAMPLE_NEURONS/2);
        int epoch = 1;
        MLDataSet data;
        do {
            // Set up network
            net = new BasicNetwork();
            net.addLayer(new BasicLayer(new ActivationTANH(), true, SAMPLE_NEURONS));
            net.addLayer(new BasicLayer(new ActivationTANH(), true, ENCODE_NEURONS));
            net.addLayer(new BasicLayer(new ActivationSoftMax(), true, SAMPLE_NEURONS));
            net.getStructure().finalizeStructure();
            net.reset();
            // Training
            data = new BasicMLDataSet(inOutput, inOutput);
            MLTrain train = new ResilientPropagation(net, data);
            epoch = 1;
            do {
                train.iteration();
                if (DEBUG)
                    System.out.println("Epoch #" + epoch + " Error:" + train.getError());
                epoch++;
            } while (train.getError() > TRAIN_ERROR && epoch < 200);
            if (epoch >= 200)
                System.out.println("Emergency break");
            train.finishTraining();
        } while (epoch >= 200);

        long time = System.nanoTime() - start;

        // Debug messages: Compute training error
        if (DEBUG) {
            System.out.println("Neural Network Results:");
            int right = 0, wrong = 0;
            for (MLDataPair pair : data) {
                final MLData out = net.compute(pair.getInput());
                String suggestion = indexToSpeakerMap.get(getMaxIndex(out.getData()));
                String ideal = indexToSpeakerMap.get(getMaxIndex(pair.getIdealArray()));
                if (suggestion.equals(ideal))
                    right++;
                else {
                    wrong++;
                    System.out.println("Wrong:  " + suggestion + " / " + ideal);
                }
            }
            System.out.println(indexToSpeakerMap);
            System.out.println("Training Error: " + (wrong) + "/" + (wrong + right) + " (" + (100.0 * wrong / (wrong + right) + "%)"));
            System.out.println("Training SpeakerNet took: " + time + " ns");
        }
        Encog.getInstance().shutdown();
    }

    public double[][]  windowing(File audioFile, final int windowSampleLength, final int windowSampleStep) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        //return automatically normalised double values
        double[] samples = reader.readSamples();
        final int sampleRate = (int) reader.getSampleRate();

        final int windowSampleCount = windowSampleLength;
        final int offsetSampleCount = windowSampleStep;
        //final int windowSampleCount = (int) (sampleRate * windowLength);
        //final int offsetSampleCount = (int) (sampleRate * windowStep);
        final int windowCount = getWindowCount(samples,windowSampleCount,offsetSampleCount);

        final double[][] windowedSamples = new double[windowCount][windowSampleCount];

        WindowFunction windowFunction = new WindowFunction();
        windowFunction.setWindowType(3);
        double[] windowValues = windowFunction.generate(windowSampleCount);
        //System.out.println("WindowSampleCount:" + windowSampleCount);
        //System.out.println("HammingWindowLength:" + windowValues.length);
        IntStream.range(0, windowedSamples.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;
            // setWeights the frame with the portion of the signal, weighted with a hamming window
            IntStream.range(0, windowedSamples[i].length).parallel().forEach(j -> {
                if (startOffset + j < samples.length)
                    windowedSamples[i][j] = samples[startOffset + j] * windowValues[j];
            });
        });
        return windowedSamples;
    }

    private int getWindowCount(final double[] samples, final int windowSampleCount, final int offsetSampleCount) {
        int windowCount = 1;
        for (int i = samples.length - windowSampleCount; i > 0; i-= offsetSampleCount) {
            windowCount++;
        }
        return windowCount;
    }

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        File file = new File("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources\\A_Eric_Zenker_02.wav");
        Autoencoder2 ae2 = new Autoencoder2(file);
    }
}
