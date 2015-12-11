package dhbw.ai13.spectrogram;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser {

	String filename = "";

	
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
			}
		}				
			catch (Exception e)			
		{
				e.printStackTrace();
		}
	}

	public String getFilename() {
		return filename;
	}

	private void setFilename(String filename) {
		this.filename = filename;
	}
	
	
}
