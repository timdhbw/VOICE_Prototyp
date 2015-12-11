package dhbw.ai13.spectrogram;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileChooser chooser = new FileChooser();
		chooser.choose();
		
		final int fftSampleSize = 1024;
		final int overlapFactor = 0;
		
		SpectrogrammErsteller creater = new SpectrogrammErsteller(fftSampleSize, overlapFactor, chooser.getFilename());
		
		final double[][] spectrogramDaten = creater.getSpectrogramData();
		datenAusgeben(spectrogramDaten);
		
	}
	
	private static void datenAusgeben (double[][] data){
		
		for(int i = 0; i < data.length; i++)
		{
			for(int j = 0; j < data[i].length; j++)
			{
				System.out.println("[i"+ i + ",j" + j + "] = " + data[i][j]);
			}
			
		}
	}

}
