package dhbw.ai13.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;

/**
 * Represents a StreamReader for AudioInputStreams, that can be used to read the Samples of an audio file.
 * @implNote This reader only reads the first audio channel, even if there are more
 * @author David S.
 * @author Felix H.
 * @author Eric Zenker
 */
public class AudioStreamReader {
    private AudioInputStream stream;
    private AudioFormat audioFormat;

    /**
     * Creates a new AudioStreamReader from the given AudioInputStream
     * @param inputStream The input stream, that is going to be read
     */
    public AudioStreamReader(AudioInputStream inputStream) {
        stream = inputStream;
        audioFormat = inputStream.getFormat();
    }

    public void close() throws IOException {
        stream.close();
    }

    /**
     * Returns the format of the audio in the AudioInputStream
     * @implNote Remember that this reader only reads the first channel, even if there are more
     */
    public AudioFormat getAudioFormat()
    {
        return audioFormat;
    }

    /**
     * Returns the sample rate of the audio in the AudioInputStream
     */
    public float getSampleRate(){
        return audioFormat.getSampleRate();
    }

    /**
     * Returns the number of samples in the AudioInputStream
     */
    public int getFrameCount(){
        try {
            //Check if audio file consists of complete samples only
            if (stream.getFrameLength() != (stream.available() / audioFormat.getFrameSize())){
                throw new IOException("audio file contains incomplete sample");
            }
            return stream.available() / audioFormat.getFrameSize();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Reads all samples in the AudioInputStream as normalized doubles from -1 to 1.
     * This function only returns the samples of one channel, if there are multiple channels, the first one is returned
     * @return An array of doubles that represent the audio samples, within the normalized value range of -1 to 1
     * @throws IOException
     */
    public double[] readSamples() throws IOException {
        double[][]samples = readSamples(1);
        return samples[0];
    }

    /**
     * Reads all samples in the AudioInputStream as normalized doubles from -1 to 1.
     * This function reads the samples of multiple channels
     * @param  n Number of channels which should be read
     * @return An array of doubles that represent the audio samples, within the normalized value range of -1 to 1
     * @throws IOException
     */
    public double[][] readSamples(int n) throws IOException {
        int numChannels = audioFormat.getChannels();
        if(n > numChannels){
            //TODO Exception
            return null;
        }
        int frameCount = getFrameCount();
        //int sampleCount = frameCount * numChannels;
        int numSamples = frameCount;
        System.out.println("Samples pro Channel: " + numSamples);
        double[][] samples = new double[n][numSamples];

        byte[] frame = new byte[audioFormat.getFrameSize()];
        int iSamples = 0;
        int channelIndex;
        int sampleIndex;
        int frameIterator = 0;
        while (stream.read(frame) > 0) {
            for(int i = 0; i < n; i++){
                iSamples = (frameIterator*numChannels)+i;
                channelIndex = iSamples%numChannels;
                sampleIndex = iSamples/numChannels;
                if (audioFormat.getFrameSize() == 2) { //sample size 8bit
                    samples[channelIndex][sampleIndex] = frame[0] / 128.0;
                } else if (audioFormat.getFrameSize() == 4) { //sample size 16bit
                    if (audioFormat.isBigEndian()) { //big endian (high byte, low byte)
                        samples[channelIndex][sampleIndex] = (  (frame[0] << 8) | (frame[1] & 0xFF) ) / 32768.0;
                    }else{ // little endian (low byte, high byte)
                        samples[channelIndex][sampleIndex] = (  (frame[0] & 0xFF) | (frame[1] << 8) ) / 32768.0;
                    }
                } else if (audioFormat.getFrameSize() == 6) { //sample size 24bit
                    if (audioFormat.isBigEndian()) { //big endian (high byte, low byte)
                        samples[channelIndex][sampleIndex] = (   (frame[0] & 0xFF) | ((frame[1] & 0xFF) << 8) |  (frame[2] << 16) ) / 8388606.0;
                    }else{ // little endian (low byte, high byte)
                        samples[channelIndex][sampleIndex] = (   (frame[0] << 16) | ((frame[1] & 0xFF) << 8) |  (frame[2] & 0xFF) ) / 8388606.0;
                    }
                }
            }
            frameIterator++;
        }
        return samples;
    }

}
