package dhbw.ai13.ann;

import java.io.IOException;
import java.util.ArrayList;

import dhbw.ai13.ann.SpeakerNet;
import dhbw.ai13.speech.detection.Vector13D;

public class PrototypeTest {

	private ArrayList<Vector13D> mfccs;
	private SpeakerNet speakerNet;
	
	public PrototypeTest(){
		mfccs = new ArrayList<>();
	}
	public void init() throws NumberFormatException, IOException {
		if (mfccs.size() == 0){
//			ArrayList<Vector13D> m = [Daten zum Trainieren]
//			if (!m.equals(mfccs)) {
//			mfccs = m;
//			speakerNet = new SpeakerNet(mfccs);		
		}
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
