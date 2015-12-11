package dhbw.ai12.speech;
/**
 * interface zur Kommunikation mit der GUI
 * @author Florian Volz
 */
public interface IMfccGUI {
/**
 * Recognize a character
 * 
 * @param pathFile
 * @return
 */
	public char recognizeChar(String pathFile);

	/**
	 * Recognize a person
	 * 
	 * @param charData
	 * @return
	 */
	public String recognizePerson(Buchstabendaten charData[]);
	
}
