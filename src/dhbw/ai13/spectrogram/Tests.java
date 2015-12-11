package dhbw.ai13.spectrogram;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Tests {

	@Test
	public void FileTest()
	{
		SpectrogrammErsteller ersteller = new SpectrogrammErsteller(1024, 0, "");
		assertEquals("Kein File angegeben", "Kein File angegeben");				
	}
	
/*	@Test
	public void ChooserTest()
	{
		FileChooser chooser = new FileChooser();
		chooser.choose();
		
	} */
}
