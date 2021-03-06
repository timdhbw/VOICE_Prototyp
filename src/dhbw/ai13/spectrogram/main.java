package dhbw.ai13.spectrogram;

import java.io.File;

public class main {

	public static void main(String[] args) {

		/*
		 * Tinos Modul:
		 * .wav-Datei ausw�hlen
		 * Spektrogramm daraus erstellen
		 * Daten des Spektrogramms in zweidimensionalem Array speichern
		 */
		
		FileChooser chooser = new FileChooser();
		chooser.choose();
		
		// Die beiden Werte noch als User-Eingabe/Auswahl programmieren
		final int fftSampleSize = 1024;
		final int overlapFactor = 0;
		
		SpectrogrammErsteller creater = new SpectrogrammErsteller(fftSampleSize, overlapFactor, chooser.getFilename());
		
		final double[][] spectrogramDaten = creater.getSpectrogramData();
		datenAusgeben(spectrogramDaten);
		
		File data = chooser.getFile();
		
		
		/*
		 * Tims Modul:
		 * 
		 */
		
		//TODO
		
		/*
		 * Erics Modul:
		 * 
		 */
		
		//TODO
		
		/*
		 * Nicos Modul:
		 * 
		 */
		
		//TODO
		
	}
	
	/**
	 * Gibt die Werte eines zweidimensionalen Arrays in der Form [i][j] = Wert aus.
	 * Hier nur n�tig um die Spektrogrammwerte sichtbar auf der Konsole anzeigen zu lassen.
	 * 
	 * @param data ein zweidimensionales Array mit einem primitiven Datentyp
	 */
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
