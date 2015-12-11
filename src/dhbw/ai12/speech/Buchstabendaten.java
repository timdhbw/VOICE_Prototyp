package dhbw.ai12.speech;

/**
 * Buchstabendaten data structure for saving chars and file paths
 * Requested by the ARCHITECTS!!!!! See interface if you don't believe it will be used
 * @author Florian Volz
 */
public class Buchstabendaten {
	private char Buchstabe;
	private String DateiPfad;

	/**
	 * This little guy is something we call "constructor". 
	 * @param buchstabe
	 * @param dateiPfad
	 */
	public Buchstabendaten(char buchstabe, String dateiPfad) {
		super();
		Buchstabe = buchstabe;
		DateiPfad = dateiPfad;
	}
/**
 * That's a getter for the private char Buchstabe. It exposes its value to the public
 * @return char Buchstabe
 */
	public char getBuchstabe() {
		return Buchstabe;
	}
/**
 * That's a getter for the private String DateiPfad. It exposes its value to the public
 * @return String DateiPfad
 */
	public String getDateiPfad() {
		return DateiPfad;
	}
}
