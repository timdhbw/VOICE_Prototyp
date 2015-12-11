package dhbw.ai12.speech.detection;

import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

public interface IMfccCsv {
	/**
	 * Interface fuer die Uebergabe von eingelesenen Mfcc-Daten aus dem CSV-file an die KI
	 * @return ArrayList<Vector13D> Liste mit 13dimensionalen Vektoren
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public ArrayList<Vector13D> leseGemittelteMfccDaten() throws NumberFormatException, IOException;
	
	/**
	 * Interface fuer die Uebergabe von Mfcc-Daten, ermittelt aus einem Wavefile
	 * @param filename Pfad zur wave-Datei
	 * @return filtered Mfcc frames
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public  double[] mfccFromWave(String filename) throws NumberFormatException, IOException, UnsupportedAudioFileException ;
}
