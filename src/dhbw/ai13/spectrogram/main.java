package dhbw.ai13.spectrogram;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileChooser chooser = new FileChooser();
		
		final int fftSampleSize = 1024;
		final int overlapFactor = 0;
		
		SpectrogrammErsteller creater = new SpectrogrammErsteller(fftSampleSize, overlapFactor, chooser.getFilename());
		
		final double[][] spectrogramDaten = creater.getSpectrogramData();
	}

}
