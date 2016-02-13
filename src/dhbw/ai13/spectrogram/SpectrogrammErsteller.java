package dhbw.ai13.spectrogram;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

/**
 * 
 * @author Rohmund, Tino
 * 
 * Klasse, die die musicg-API nutzt, um aus einer .wav-Datei ein Spektrogramm zu erstellen.
 * Enthält ein zweidimensionales Array in dem die Daten des Spektrogramms gespeichert werden
 *
 */
public class SpectrogrammErsteller {
	
	private int fftSampleSize;
	private int overlapFactor;
	
	private double[][] spectrogramData;

	/**
	 * Konstruktor der Spektrogramm-Klasse. 
	 * 
	 * @param fftSampleSize Integer, der die Sample Size der Fast Fourier Transformation zur Erstellung des Spektrogramms enthält
	 * @param overlapFactor Integer, der den Overlap-Faktor der Transformation enthält
	 * @param url Qualifizierter Pfad der zu analysierenden .wav-Datei
	 */
	
	public SpectrogrammErsteller(int fftSampleSize, int overlapFactor, String url)
	{
		if(!url.contains("."))
		{
			System.out.println("Kein File angegeben");
		}
		else
		{
			setOverlapFactor(fftSampleSize);
			setFftSampleSize(fftSampleSize);
			
			Wave wave = new Wave(url);
			Spectrogram spectrogram = new Spectrogram(wave);
			
			setSpectrogramData(spectrogram.getAbsoluteSpectrogramData());
		}

	}
		

	private int getOverlapFactor() {
		return overlapFactor;
	}

	private void setOverlapFactor(int overlapFactor) {
		this.overlapFactor = overlapFactor;
	}
	private int getFftSampleSize() {
		return fftSampleSize;
	}

	private void setFftSampleSize(int fftSampleSize) {
		this.fftSampleSize = fftSampleSize;
	}
	
/**
 * 
 * @return zweidimensionaler Array mit den Intensitätswerten des Spektrogramm der analysierten .wav-Datei
 */
	public double[][] getSpectrogramData() {
		return spectrogramData;
	}

	private void setSpectrogramData(double[][] spectrogramData) {
		this.spectrogramData = spectrogramData;
	}
}
