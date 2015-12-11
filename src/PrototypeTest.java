import java.io.IOException;
import java.util.ArrayList;

import dhbw.ai13.ann.SpeakerNet;
import dhbw.ai13.ann.VowelNet;
import dhbw.ai13.speech.detection.Vector13D;

public class PrototypeTest {

	private ArrayList<Vector13D> mfccs;
	private SpeakerNet speakerNet;
	
	public PrototypeTest(){
		mfccs = new ArrayList<>();
	}
	public void init() throws NumberFormatException, IOException {
		
	}
	
	public static void main(String[] args) {
		PrototypeTest net = new PrototypeTest();
		try {
			net.init();
		} catch (Exception e) {
			// TODO: handle exception
		} 
	}

}
