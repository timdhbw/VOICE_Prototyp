package dhbw.ai13.autoencoding.framework;

import com.musicg.dsp.WindowFunction;
import dhbw.ai13.audio.AudioStreamReader;
import dhbw.ai13.autoencoding.activationFunctions.TanH;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

/**
 * Created by GomaTa on 21.04.2016.
 */
public class AudioAutoencoder {
    private final int numInOutLayer;
    private final AutoEncoder autoencoder;
    private final int numMidLayer;

    public AudioAutoencoder() throws AutoEncoderException {
        // Layer Info
        numInOutLayer = 2000;
        numMidLayer = 500;

        // Init Autoencoder
        autoencoder = new AutoEncoder(numInOutLayer);
        autoencoder.addLayer(1 ,numInOutLayer, new TanH());
        autoencoder.addLayer(2 ,numMidLayer, new TanH());
        autoencoder.addLayer(3 ,numInOutLayer, new TanH());
        autoencoder.build();
    }

    public AutoEncoder getAutoencoder() {
        return autoencoder;
    }

    public int getNumInOutLayer() {
        return numInOutLayer;
    }

    public double[] encode(File audiofile) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        double[] coefficients = new double[numMidLayer];
        double[][] windowedData = windowing(audiofile, numInOutLayer, numInOutLayer / 2);
        final int n = windowedData.length;
        double[][] outputData = new double[n][];
        // calculate autoencoder values
        for (int i = 0; i < windowedData.length; i++) {
            outputData[i] = autoencoder.encode(windowedData[i]);
        }
        //calculate average
        for(int i = 0; i < coefficients.length; i++){
            double coefficient = 0.0;
            for(int j = 0; j < n; j++){
                coefficient += outputData[j][i];
            }
            coefficients[i] = coefficient / Double.valueOf(n);
        }
        return coefficients;
    }

    private double[][]  windowing(File audioFile, final int windowSampleLength, final int windowSampleStep) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        //return automatically normalised double values
        double[] samples = reader.readSamples();
        final int sampleRate = (int) reader.getSampleRate();

        final int windowSampleCount = windowSampleLength;
        final int offsetSampleCount = windowSampleStep;
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
}
