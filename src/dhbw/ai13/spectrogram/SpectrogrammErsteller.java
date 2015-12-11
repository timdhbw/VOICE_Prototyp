package dhbw.ai13.spectrogram;
import com.musicg.wave.Wave;
import com.musicg.wave.extension.Spectrogram;

public class SpectrogrammErsteller {
	
	int fftSampleSize;
	int overlapFactor;
	
	double[][] spectrogramData;

	
	public SpectrogrammErsteller(int fftSampleSize, int overlapFactor, String file)
	{
		if(!file.contains("."))
		{
			System.out.println("Kein File angegeben");
		}
		else
		{
			setOverlapFactor(fftSampleSize);
			setFftSampleSize(fftSampleSize);
			
			Wave wave = new Wave(file);
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

	public double[][] getSpectrogramData() {
		return spectrogramData;
	}

	private void setSpectrogramData(double[][] spectrogramData) {
		this.spectrogramData = spectrogramData;
	}
}
