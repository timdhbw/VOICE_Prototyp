package dhbw.ai13.mfcc;

import com.gomata.gchart.GChart;
import dhbw.ai13.audio.AudioStreamReader;
import org.apache.commons.math3.util.FastMath;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by GomaTa on 04.05.2016.
 */
public class DFT {

    private double[][] applyDft(final double[][] framedSignal, final int frameSampleCount, final int dftLength){
        // apply the discrete fourier transform algorithm to each frame
        final double[][] framedSpectrum = new double[framedSignal.length][dftLength];
        System.out.printf("[NumWindows] %d\n",framedSignal.length);
        System.out.printf("[Window-Length] %d\n",framedSignal[0].length);
        System.out.printf("[DFT-Length] %d\n",dftLength);
        Thread[] worker = new Thread[framedSpectrum.length];
        for (int i = 0; i < worker.length; i++) {
            int index = i;
            worker[i] = new Thread() {
                private int i = index;

                @Override
                public void run() {
                    final double[] spectrum = framedSpectrum[i];
                    final double[] signal = framedSignal[i];

                    // this is the discrete fourier transform
                    IntStream.range(0, spectrum.length).forEach(k -> {
                        double real = 0.0;
                        double imaginary = 0.0;
                        for (int n = 0; n < frameSampleCount; n++) {
                            //k = index des spektrums
                            //n = index der Sample
                            // value = delta

                            double value = -2 * FastMath.PI * (k + 1) * (n + 1) / frameSampleCount;
                            real += signal[n] * FastMath.cos(value);
                            imaginary += signal[n] * FastMath.sin(value);
                        }
                        //spectrum[k] = 1.0 / frameSampleCount * (real*real);
                        //spectrum[k] = 1.0 / frameSampleCount * (real*real + imaginary*imaginary);
                        //spectrum[k] = 1.0 / frameSampleCount * FastMath.sqrt(real*real  + imaginary*imaginary);
                        spectrum[k] = (1.0 / frameSampleCount) * FastMath.sqrt((real*real) + (imaginary*imaginary));
                            //System.out.printf("[%d] realAmount: %f\n", k, real);
                            //System.out.printf("[%d] imagAmount: %f\n", k, imaginary);
                            //System.out.printf("[%d] amplitude: %f\n", k, spectrum[k]);
                    });
                }
            };
            worker[i].start();
        }
        for (Thread thread : worker) {
            try {
                thread.join();
            } catch (InterruptedException e) {

            }
        }
        return framedSpectrum;
    }

    private int getFrameCount(final double[] signal, final int windowLengthSamples, final int windowStepSamples) {
        int framesCount = 1;
        for (int i = signal.length - windowLengthSamples; i > 0; i-= windowStepSamples) {
            framesCount++;
        }
        return framesCount;
    }

    private double[] createHammingWindow(final int windowLength) {
        // calculate discrete points of the hamming window
        return IntStream.range(0, windowLength).parallel().mapToDouble(
                i -> 0.54 - 0.46 * FastMath.cos((2*FastMath.PI*i)/(windowLength-1))
        ).toArray();
    }

    private double[][] windowding(final double[] signal, final int sampleRate, final double windowLength, final double windowStep){
        final int frameSampleCount = (int) (windowLength * sampleRate);
        final int offsetSampleCount = (int) (windowStep * sampleRate);
        final int framesCount = getFrameCount(signal, frameSampleCount, offsetSampleCount);
        final double[] hammingWindow = createHammingWindow(frameSampleCount);

        // setWeights each frame with it's overlapping portion of the signal
        final double[][] framedSignal = new double[framesCount][frameSampleCount];
        IntStream.range(0, framedSignal.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;

            // setWeights the frame with the portion of the signal, weighted with a hamming window
            IntStream.range(0, framedSignal[i].length).parallel().forEach(j -> {
                if (startOffset + j < signal.length)
                    framedSignal[i][j] = signal[startOffset + j] * hammingWindow[j];
            });
        });
        return framedSignal;
    }

    private double[][] windowdingSampleValue(final double[] signal, final int sampleRate, final int frameSampleCount, final int offsetSampleCount){
        final int framesCount = getFrameCount(signal, frameSampleCount, offsetSampleCount);
        final double[] hammingWindow = createHammingWindow(frameSampleCount);

        // setWeights each frame with it's overlapping portion of the signal
        final double[][] framedSignal = new double[framesCount][frameSampleCount];
        IntStream.range(0, framedSignal.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;

            // setWeights the frame with the portion of the signal, weighted with a hamming window
            IntStream.range(0, framedSignal[i].length).parallel().forEach(j -> {
                if (startOffset + j < signal.length)
                    framedSignal[i][j] = signal[startOffset + j] * hammingWindow[j];
            });
        });
        return framedSignal;
    }

    public static void main(String[] args) {
        File audioFile = new File("C:\\Users\\GomaTa\\Documents\\Studium\\Studienarbeit\\MFCC\\test.wav");
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            AudioStreamReader reader = new AudioStreamReader(audioInputStream);
            double[] samples = reader.readSamples();
            GChart g = new GChart();
            g.setCaptionYAxis(true);
            g.setCaptionXAxis(true);
            g.setImageHeight(1000);
            g.setGap(50.0);
            //g.setImageWidth(2000);
            DFT dft = new DFT();
            double dftLength = 512.0;
            double[][] windowedSamples = dft.windowding(samples, (int) reader.getSampleRate(), 0.05, 0.025);
            double[][] windowedSpectrum = dft.applyDft(windowedSamples, windowedSamples[0].length, (int) dftLength);
            //double[][] windowedSpectrum = dft.applyDft(new double[][]{samples}, samples.length, 512);
            double[] spectrum = new double[(int) dftLength];
            for(int i = 0; i < spectrum.length; i++){
                double sum = 0.0;
                for(int j = 0; j < windowedSpectrum.length; j++){
                    sum += windowedSpectrum[j][i];
                }
                spectrum[i] = sum / Double.valueOf(windowedSpectrum.length);
            }
            //saveWindows("C:\\Users\\GomaTa\\Documents\\Studium\\Studienarbeit\\MFCC\\windows.csv", windowedSpectrum);
            //saveSamplesToFile("C:\\Users\\GomaTa\\Documents\\Studium\\Studienarbeit\\MFCC\\dft.csv", reader.getSampleRate(), dftLength, spectrum);
            double[] xAxis = new double[(int) dftLength];
            for(int i = 0; i < xAxis.length; i++){
                xAxis[i] = (i+1)*(reader.getSampleRate()/dftLength);
            }
            g.draw("C:\\Users\\GomaTa\\Documents\\Studium\\Studienarbeit\\MFCC\\dft.png",spectrum,xAxis);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveWindows(String filepath, double[][] samples){
        try {
            List<String> lines = new LinkedList<>();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < samples.length; i++) {
                for(int j = 0; j < samples[i].length; j++) {
                    sb.append(String.format("%.2f;", samples[i][j]));
                }
                sb.append("\n");
            }
            lines.add(sb.toString());
            Files.write(Paths.get(filepath), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSamplesToFile(String filepath, double sampleRate, double dftLength, double[] samples) {
        try {
            List<String> lines = new LinkedList<>();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < samples.length; i++) {
                double freq = ((i+1)/dftLength)*(sampleRate*0.5);
                sb.append(String.format("%.2f;%f;\n", freq, samples[i]));
            }
            lines.add(sb.toString());
            Files.write(Paths.get(filepath), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
