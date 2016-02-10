package dhbw.ai13.bayesClassificator.naiveBayes;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


/*
 * Diese Klasse ist zum "cutten" einer Wav Datei, es wird der urspruengliche Audio File,
 *  sowie die Anfangs- und Endzeit angegeben.
 * Ausgeben wird ein neuer Audio File
 */
 
public class WaveCutter {
	public File cutAudioFile(File file) throws UnsupportedAudioFileException, IOException{
		File audFile;
		
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		stream.
		
		return audFile;
	}
}
