package dhbw.ai13.mfcc;

import dhbw.ai13.audio.AudioStreamReader;
import org.apache.commons.math3.util.FastMath;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class MFCC {
    // The default length in seconds of one window for which the mfcc is calculated
    double DEFAULT_WINDOW_LENGTH = 0.050;
    // The default offset in seconds between two overlapping windows
    private static final double DEFAULT_WINDOW_STEP = 0.025;
    // The length of the dft to compute
    private static final int DEFAULT_DFT_SIZE = 512;
    // The number of mfcc coefficients
    private static final int DEFAULT_MFCC_COEFFICIENTS = 13;
    // The number of mel filter banks to compute for mfcc
    private static final int DEFAULT_MEL_FILTER_COUNT = DEFAULT_MFCC_COEFFICIENTS *2;
    // The default preemphasis filter strength. Valid values are smaller than 1
    private static final double DEFAULT_PREEMPHASIS = 0.95;
    // The default liftering amount.
    private static final double DEFAULT_LIFTERING = 22.0;
    // Whether to append derivation by default
    private static final boolean DEFAULT_APPENDDERIVATION = false;
    // MFCC filter
    private static final double FILTER_SILENCE = -400;
    private static final double FILTER_NOISE = 60;

    public double[] computeMFCC(File audioFile, int n) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        double[] samples = reader.readSamples();
        int sampleRate = (int) reader.getSampleRate();
        double[][] windowMFCC = getMfcc(samples, sampleRate, DEFAULT_WINDOW_LENGTH, DEFAULT_WINDOW_STEP, DEFAULT_DFT_SIZE, DEFAULT_PREEMPHASIS, n, DEFAULT_LIFTERING, DEFAULT_APPENDDERIVATION);
        double[] mfcc = averageFrames(windowMFCC);
        double[] result = new double[n];
        for(int i = 0; i < n;i++){
            result[i] = mfcc[i+1];
        }
        return result;
    }

    public double[] computeMFCC(File audioFile, int n, final double windowLength, final double windowStep, final int dftLength, final double preEmphasis, final double liferingAmount, final boolean appendDerivation) throws IOException, UnsupportedAudioFileException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        AudioStreamReader reader = new AudioStreamReader(audioInputStream);
        double[] samples = reader.readSamples();
        int sampleRate = (int) reader.getSampleRate();
        double[][] windowMFCC = getMfcc(samples, sampleRate, windowLength, windowStep, dftLength, preEmphasis, n, liferingAmount, appendDerivation);
        double[] mfcc = averageFrames(windowMFCC);
        double[] result = new double[n];
        for(int i = 0; i < n;i++){
            result[i] = mfcc[i+1];
        }
        return result;
    }




    /**
     * Returns the mel frequency cepstral coefficients of a given signal with the specified sampleRate and windowing
     * @param signal An array of normalized samples between -1 and 1, that represent a audio signal
     * @param sampleRate The rate at with the audio signal has been sampled in samples per second
     * @param windowLength The length in seconds of one window for which the mfcc is calculated
     * @param windowStep The offset in seconds between two overlapping windows
     * @param dftLength The length of the discrete fourier transform to compute for mfcc calculation
     * @param preEmphasis The strength of the preEmphasis filter applied on the given signal. Valid values are smaller than 1
     * @param liferingAmount The amount of liftering that is applied on the mfcc coefficients
     * @param appendDerivation Whether to append the first derivation of the mfcc coefficients (derived over several frames) to the results
     * @return An array that contains the resulting coefficients
     */
    public double[][] getMfcc(final double[] signal, final int sampleRate, final double windowLength, final double windowStep, final int dftLength, final double preEmphasis, final int countMFCCCoefficients, final double liferingAmount, final boolean appendDerivation) {
        if (signal.length < 1){ throw new IllegalArgumentException("signal");}
        final int frameSampleCount = (int) (windowLength * sampleRate);
        // Apply preemphasis on signal
        final double[] newSignal = applyPreempasisFilter(signal, preEmphasis);
        // Frame the signal into short pieces
        final double[][] frames = createFramedSignal(newSignal, sampleRate, windowLength, windowStep);
        // Apply the discrete fourier transform to get a spectrum
        final double[][] spectrum = applyDft(frames, frameSampleCount, dftLength);
        // Apply a mel filterbank on the spectrum and calculate the amount of energy in each filter
        final double[][] energies = calculateEnergies(spectrum, countMFCCCoefficients, sampleRate, dftLength);
        // Apply a discrete cosine transform to get the mel frequency cepstral coefficients
        final int melFilterCount = 2*countMFCCCoefficients;
        double[][] mfcc = applyDct(energies, melFilterCount, 0);
        // Apply cepstral liftering on the result
        mfcc = applyPostProcessing(mfcc, liferingAmount);

        final double[][] result;
        if (appendDerivation) {
            result = calculateDeltas(mfcc);
        }
        else {
            result = mfcc;
        }
        return result;
    }

    /**
     * Calculates the number of frames for a given signal with the specified windowing
     * @param signal An array of normalized samples between -1 and 1, that represent a audio signal
     * @param windowLengthSamples The length in samples of one window for which the mfcc is calculated
     * @param windowStepSamples The offset in samples between two overlapping windows
     */
    private int getFrameCount(final double[] signal, final int windowLengthSamples, final int windowStepSamples)
    {
        int framesCount = 1;
        for (int i = signal.length - windowLengthSamples; i > 0; i-= windowStepSamples) {
            framesCount++;
        }

        return framesCount;
    }

    /**
     * Applies a preempasis filter to the specified {@code signal}.
     * The preempasis filter is calculated using the following formula:
     * <code>
     *   signal[i] = signal[i] - (strength * signal[i-1])
     * </code>
     * where the {@code strength} is the {@code preemphasis}-Value.
     * For the signal at index {@code 0} the filter is not applied.
     * @param signal The signal to be processed
     * @param preemphasis The strength of the preemphasis filter
     * @return The signal after applying the preempasis filter
     */
    private double[] applyPreempasisFilter(final double[] signal, double preemphasis)
    {
        final double[] newSignal = new double[signal.length];
        newSignal[0] = signal[0];
        IntStream.range(1, newSignal.length).parallel().forEach(i -> {
            newSignal[i] = signal[i] - (preemphasis * signal[i-1]);
        });
        return newSignal;
    }

    /**
     * Frames a audio signal into small portions that are weighted with a hamming window with the specified windowing parameters
     * @param signal An array of normalized samples between -1 and 1, that represent a audio signal
     * @param sampleRate The rate at with the audio signal has been sampled in samples per second
     * @param windowLength The length in seconds of one window for which the mfcc is calculated
     * @param windowStep The offset in seconds between two overlapping windows
     */
    private double[][] createFramedSignal(final double[] signal, final int sampleRate, final double windowLength, final double windowStep)
    {
        final int frameSampleCount = (int) (windowLength * sampleRate);
        final int offsetSampleCount = (int) (windowStep * sampleRate);
        final int framesCount = getFrameCount(signal, frameSampleCount, offsetSampleCount);
        final double[] hammingWindow = createHammingWindow(frameSampleCount);

        // fill each frame with it's overlapping portion of the signal
        final double[][] framedSignal = new double[framesCount][frameSampleCount];
        IntStream.range(0, framedSignal.length).parallel().forEach(i -> {
            final int startOffset = i * offsetSampleCount;

            // fill the frame with the portion of the signal, weighted with a hamming window
            IntStream.range(0, framedSignal[i].length).parallel().forEach(j -> {
                if (startOffset + j < signal.length)
                    framedSignal[i][j] = signal[startOffset + j] * hammingWindow[j];
            });
        });
        return framedSignal;
    }

    /**
     * Applies a discrete fourier transform onto a framed audio signal
     * @param framedSignal Frames of an audio signal
     * @param frameSampleCount The number of samples in each frame
     * @param dftLength The length of the result vector of the discrete fourier transform
     */
    private double[][] applyDft(final double[][] framedSignal, final int frameSampleCount, final int dftLength){
        // apply the discrete fourier transform algorithm to each frame
        final double[][] framedSpectrum = new double[framedSignal.length][dftLength];
        //System.out.printf("[NumWindows] %d\n",framedSignal.length);
        //System.out.printf("[Window-Length] %d\n",framedSignal[0].length);
        //System.out.printf("[DFT-Length] %d\n",dftLength);
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
                        double real = 0;
                        double imaginary = 0;
                        for (int n = 0; n < frameSampleCount; n++) {
                            //k = index des spektrums
                            //n = index der Sample
                            // value = delta

                            double value = -2 * FastMath.PI * (k + 1) * (n + 1) / frameSampleCount;
                            real += signal[n] * FastMath.cos(value);
                            imaginary += signal[n] * FastMath.sin(value);
                        }
                        //double realAmount = real;
                        //double imaginaryAmount = -imaginary;
                        //spectrum[k] = realAmount;
                        spectrum[k] = 1.0 / frameSampleCount * FastMath.sqrt(real*real + imaginary*imaginary);
                        //spectrum[k] = 1.0 / frameSampleCount * (real*real+imaginary*imaginary);
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
        /*
        try {
            int i = 0;
            for(double[] spectrum: framedSpectrum){
                File f = new File("window"+i+".txt");
                FileWriter fos = new FileWriter(f,false);
                for(double v: spectrum){
                    fos.write(v+System.getProperty("line.separator"));
                }
                fos.close();
                i++;
            }
        } catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
        */
        return framedSpectrum;
    }

    /**
     * Calculates a specified number of energies in a framed audio frequency spectrum using the same number of filter banks
     * @param framedSpectrum The frequency spectrum for each frame
     * @param filterCount The number of filter banks to compute
     * @param sampleRate The sample rate of the original audio signal
     * @param filterLength The length of each filter bank (You typically want to compute the length of your previous dft)
     */
    private double[][] calculateEnergies(final double[][] framedSpectrum, int filterCount, final int sampleRate, final int filterLength) {
        // create as many triangular filterbanks as requested
        final double[][] filterbanks = createMelFilterbanks(filterCount, 300, 8000, sampleRate, filterLength);

        // calculate the amount of energy in each filter bank for each frame
        final double[][] framedEnergies = new double[framedSpectrum.length][filterbanks.length];
        IntStream.range(0, framedEnergies.length).parallel().forEach(i -> { // for each frame
            final double[] energies = framedEnergies[i];
            final double[] spectrum = framedSpectrum[i];

            IntStream.range(0, energies.length).parallel().forEach(j -> { // for each filterbank
                double energy = 0;
                for (int k = 0; k < filterbanks[j].length; k++) {
                    energy += spectrum[k] * filterbanks[j][k];
                }
                energies[j] = FastMath.log(energy);
            });
        });
        return framedEnergies;
    }

    /**
     * Applies a discrete cosine transform onto framed values
     *
     * Warning: This function will drop values if {@code numDropFirst > 0}.
     * This may be required, because the first value of the mfcc does not contain useful values
     *
     * @param framedEnergies Frames of values (In MFCC these are the energies in each filter bank for each frame)
     * @param dctLength The length of the result vector of the discrete cosine transform
     * @param numDropFirst The amount of beginning-coefficients to be dropped from each dct
     */
    private double[][] applyDct(final double[][] framedEnergies, final int dctLength, final int numDropFirst){
        // apply the discrete cosine transform algorithm to each frame
        final double[][] framedDct = new double[framedEnergies.length][dctLength-numDropFirst];
        IntStream.range(0, framedEnergies.length).parallel().forEach(i -> {
            double[] dct = framedDct[i];
            final double[] energies = framedEnergies[i];
            final int N = energies.length;

            // this is the discrete cosine transform
            IntStream.range(0, dct.length).parallel().forEach(j -> {
                final int k = j + numDropFirst;
                double sum = 0;
                for (int n = 0; n < N; n++) {
                    sum += energies[n] * FastMath.cos((FastMath.PI/N)*(n+0.5)*k);
                }
                dct[j] = sum;
            });
        });
        return framedDct;
    }


    /**
     * Executes post processing on the calculated mfcc.
     *
     * During post processing each mfcc-coefficient is weighted using a weight-value.
     * The weight-value is depends on the index {@code i} of the coefficient inside the mfcc.
     * The weight-value is calculated using the following formula:
     * <code>
     *     weight = 1 + (amount / 2) * sin(PI * i / amount)
     * </code>
     * where
     * {@code amount} is the {@code amount} passed as a parameter,
     * {@code sin} is computed using {@link org.apache.commons.math3.util.FastMath#sin} and
     * {@code PI} is the value of {@link org.apache.commons.math3.util.FastMath#PI}.
     *
     * @param framedMfcc The current mfcc
     * @param amount The amount used in calculating the weight
     * @return The mfcc after being weighted
     */
    private double[][] applyPostProcessing(final double[][] framedMfcc, final double amount) {
        final double[][] framedLifter = new double[framedMfcc.length][framedMfcc[0].length];
        IntStream.range(0, framedLifter.length).parallel().forEach(i -> {
            final double[] lifter = framedLifter[i];
            final double[] mfcc = framedMfcc[i];

            for (int n = 0; n < lifter.length; n++) {
                final double lift = 1 + (amount / 2) * FastMath.sin(FastMath.PI * n / amount);
                lifter[n] = lift * mfcc[n];
            }
        });

        return framedLifter;
    }

    /**
     * Calculates the derivation of the passed mfcc and appends it to the return value.
     *
     * The result is a two-dimensional array
     * where the length of the first index is equal to the length of the first index of {@code framedMfcc}
     * and the length of the second index is equal to the two times the length of the second index of {@code framedMfcc}.
     * @param framedMfcc the current mfcc
     * @return the current mfcc with the derivation appended to it
     */
    private double[][] calculateDeltas(final double[][] framedMfcc) {
        final int N = 2;
        final double[][] framedResults = new double[framedMfcc.length][framedMfcc[0].length*2];
        final int maxDeltaIndex = framedResults.length - N;
        IntStream.range(0, framedResults.length).parallel().forEach(t -> {
            final double[] results = framedResults[t];
            final double[] mfcc = framedMfcc[t];
            final int mfccLength = mfcc.length;

            for (int i = 0; i < mfccLength; i++) {
                results[i] = mfcc[i];

                if (t >= N && t < maxDeltaIndex)
                {
                    double sum1 = 0;
                    for (int n = 1; n <= N; n++) {
                        sum1 += n * (framedMfcc[t + n][i] - framedMfcc[t - n][i]);
                    }

                    double sum2 = 0;
                    for (int n = 1; n <= N; n++) {
                        sum2 += n * n;
                    }

                    results[i + mfccLength] = sum1 / (2 * sum2);
                }
            }
        });
        return framedResults;
    }

    /**
     * Creates amount filter banks with filterLength discrete points.
     * All filter banks have a triganular shape that are equally large on the mel scala
     * The range the filter banks cover is determined by the lower and upper frequency
     * <pre>Example with 3 banks:
     * #   o   o       o
     * #  + + + ++   ++ ++
     * # +   +   ++++     ++
     * # +  ++   +++        ++
     * #o  o  oo+  +o         +o
     * ##########################</pre>
     * @param amount The total number of filter banks to create. The more banks are created, the smaller the frequency range of one filter gets.
     * @param lowerFrequency The lowest frequency the filter banks have to cover
     * @param upperFrequency The highest frequency the filter banks have to cover
     * @param sampleRate The sampleRate of the signal to create the filter bank for
     * @param filterLength The number of discrete points you want to calculate for each filter bank. (You typically want to compute the length of your dft)
     */
    private double[][] createMelFilterbanks(final int amount, final double lowerFrequency, final double upperFrequency, final int sampleRate, final int filterLength) {
        final double lowerMel = toMel(lowerFrequency);
        final double upperMel = toMel(upperFrequency);

        // calculate start, peak and end point of the triangular filter banks
        // the points are placed linear *on the mel scala*
        final double[] point = new double[amount + 2];
        final double difference = (upperMel - lowerMel) / (amount + 1);
        for (int i = 0; i < point.length; i++) {
            point[i] = FastMath.floor((filterLength + 1) * toFrequency(lowerMel + i * difference) / sampleRate);
        }

        // calculate the filter banks itself
        final double[][] filterbanks = new double[amount][filterLength];
        IntStream.range(0, filterbanks.length).parallel().forEach(m -> {
            // calculate discrete points of the filter bank
            for (int k = 0; k < filterbanks[m].length; k++) {
                if (k < point[m+1-1]) {
                    filterbanks[m][k] = 0;
                } else if (k <= point[m+1]) {
                    filterbanks[m][k] = (k - point[m+1-1]) / (point[m+1] - point[m+1-1]);
                } else if (k <= point[m+1+1]) {
                    filterbanks[m][k] = (point[m+1+1] - k) / (point[m+1+1] - point[m+1]);
                } else {
                    filterbanks[m][k] = 0;
                }
            }
        });

        return filterbanks;
    }

    /**
     * Creates windowLength discrete points of a hamming window.
     * Use these as a factor to apply a hamming window on a discrete signal.
     * <pre>Example:
     * #        +++
     * #     +++   +++
     * #   ++         ++
     * #  ++           ++
     * #++               ++
     * #####################</pre>
     * @param windowLength The length of the window / The number of discrete points
     */
    private double[] createHammingWindow(final int windowLength) {
        // calculate discrete points of the hamming window
        return IntStream.range(0, windowLength).parallel().mapToDouble(
                i -> 0.54 - 0.46 * FastMath.cos((2*FastMath.PI*i)/(windowLength-1))
        ).toArray();
    }

    /**
     * Converts a frequency from Hertz to mel
     * @param hz The frequency in Hertz
     */
    private double toMel(final double hz) {
        // Hz -> mel
        return 1125 * FastMath.log(1 + hz/700);
    }

    /**
     * Converts a frequency from mel to Hertz
     * @param mel The frequency in mel
     */
    private double toFrequency(final double mel) {
        // mel -> Hz
        return 700 * (FastMath.exp(mel/1125) - 1);
    }

    public double[] filterAndShrinkFrames(double[][] mfccFrames) {
        double[] avgMfcc = new double[mfccFrames[0].length];
        int cnt = 0;

        for (int j = 1; j < mfccFrames.length; j++) {
            double difference = 0;

            for (int i = 0; i < 13; i++) {
                difference += Math.abs(mfccFrames[j][i] - mfccFrames[j - 1][i]);
            }

            if (difference < FILTER_NOISE && mfccFrames[j][0] > FILTER_SILENCE) {
                for (int i = 0; i < mfccFrames[j].length; i++) {
                    avgMfcc[i] += mfccFrames[j][i];
                }
                cnt++;
            }
        }
        for (int i = 0; i < avgMfcc.length; i++) {
            avgMfcc[i] = avgMfcc[i] / cnt;
        }
        return avgMfcc;
    }

    public double[] averageFrames(double[][] mfccFrames) {
        double[] avgMfcc = new double[mfccFrames[0].length];
        int cnt = 0;
        for (int j = 1; j < mfccFrames.length; j++) {
            for (int i = 0; i < mfccFrames[j].length; i++) {
                avgMfcc[i] += mfccFrames[j][i];
            }
            cnt++;
        }
        for (int i = 0; i < avgMfcc.length; i++) {
            avgMfcc[i] = avgMfcc[i] / cnt;
        }
        return avgMfcc;
    }
}

