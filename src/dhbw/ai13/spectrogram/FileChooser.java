package dhbw.ai13.spectrogram;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Ein einfacher Filechooser mit einem Filefilter, um eine .wav-Datei auszuw‰hlen.
 * Auﬂerdem eine Funktion um das gew‰hlte File weiterzugeben.
 * 
 * @author Rohmund, Tino
 *
 */
public class FileChooser {

	private String filename = "";
	private File file;
	
	
	public void choose (){
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("wave", "wav"));
		
		int rueckgabeWert = chooser.showOpenDialog(null);
		try
		{
			if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
			{
				setFilename(chooser.getSelectedFile().getAbsolutePath().toLowerCase());
				System.out.println(getFilename());
				file = new File(getFilename());
			}
		}				
			catch (Exception e)			
		{
				e.printStackTrace();
		}
	}
	
	public File getFile() {
		return file;
	}

	public String getFilename() {
		return filename;
	}

	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
