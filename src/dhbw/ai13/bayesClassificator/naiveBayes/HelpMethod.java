package dhbw.ai13.bayesClassificator.naiveBayes;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/*
 * Diese Klasse ist zum "cutten" einer Wav Datei, es wird der urspruengliche Audio File,
 *  sowie die Anfangs- und Endzeit (in Millisekunden)angegeben.
 * Ausgeben wird ein neuer Audio File.
 * Auﬂerdem gibt es eine Methode die den Eingangsstream verkleinert
 */

public class HelpMethod {

	public static void copyAudio(String sourceFileName, String destinationFileName, int startMilliSecond,
			int milliSecondsToCopy, int multiplication) {
		startMilliSecond = startMilliSecond*multiplication;
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		try {
			// open the file
			File file = new File(sourceFileName);
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			
			// get the format
			AudioFormat format = fileFormat.getFormat();
			inputStream = AudioSystem.getAudioInputStream(file);
			
			// get the bytes per second to get time information
			int frameRate = (int) format.getFrameRate();
			int bytesPerSecond = format.getFrameSize() * (frameRate / 1000);
			
			// skip the input stream till start time
			inputStream.skip(startMilliSecond * bytesPerSecond);
			
			// get number of the frames of the new file (in milli seconds)
			long framesOfAudioToCopy = milliSecondsToCopy * (frameRate / 1000);
			
			// start the audio stream of the output file
			shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
			
			// create the output file
			File destinationFile = new File(destinationFileName);
			AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (Exception e) {
					System.out.println(e);
				}
			if (shortenedStream != null)
				try {
					shortenedStream.close();
				} catch (Exception e) {
					System.out.println(e);
				}
		}
	}
	
	/*
	 * methode to convert the stream
	 * the frequency is split by the frequencySplit and the intensity ist split by intensitySplit
	 * the result ist stream[time][frequency/frequencySplit] = intensity/intensitySplit
	 */
	public static double[][] convertStream(double[][] stream, int maxIntensity, int intensitySplit, int frequencySplit){
		double[][] nStream = new double[stream.length][stream[0].length/frequencySplit];
		int time = 0;
		double help = 0;
		for(int i=0;i<stream.length;i++){
			for(int j=0;j<nStream[0].length;j++){
				help = help + stream[i][j];
				time = time +1;
				if(time==frequencySplit){
					help = help/frequencySplit;
					//to be sure that Intensity is not to high
					if(help/intensitySplit < maxIntensity){
						nStream[i][j/frequencySplit] = help/intensitySplit;
					}else{
						nStream[i][j/frequencySplit] = maxIntensity-1;
					}
					time=0;
					//System.out.println("[" + i + "]" + "[" + j + "] = " +  help/1000);
					help=0;
				}
			}	
		}
		return nStream;
	}
	
	//normalization of the stream (Vektor per timeStep)
	public static double[][] streamNormalizer(double [][] stream){
		//Werte durch die der Array geteilt wird, fuer jeden timeStep eins
		double [] divider = new double[stream.length];
		
		//divider wird aufgefuellt
		for(int i=0;i<stream.length;i++){
			double dividerHelp = 0;
			for(int j=0;j<stream[i].length;j++){
				dividerHelp = dividerHelp + stream[i][j];
			}
			divider[i] = dividerHelp;
		}
		
		//normalisation des Streams
		for(int i=0;i<stream.length;i++){
			for(int j=0;j<stream[i].length;j++){
				stream[i][j] = stream[i][j]/divider[i];
			}
		}
		return stream;
	}
	
}
