package dhbw.ai13.ann;

import java.io.IOException;
import java.util.ArrayList;

import dhbw.ai13.ann.SpeakerNet;
import dhbw.ai13.speech.detection.Vector13D;

public class PrototypeTest {

	private ArrayList<Vector13D> mfccs;
	private SpeakerNet speakerNet;
	
	public ArrayList<Vector13D> getMfccs() {
		return mfccs;
	}
	public void setMfccs(ArrayList<Vector13D> mfccs) {
		this.mfccs = mfccs;
	}
	public SpeakerNet getSpeakerNet() {
		return speakerNet;
	}
	public void setSpeakerNet(SpeakerNet speakerNet) {
		this.speakerNet = speakerNet;
	}
	public PrototypeTest(){
		mfccs = new ArrayList<>();
	}
	public void init() throws NumberFormatException, IOException {
		if (mfccs.size() == 0){
			ArrayList<Vector13D> m = MFCCSupplier.computeMFCCsOfFolder("C:/Users/Nico/workspace/VOICE_Prototyp/resources");
			if (!m.equals(mfccs)) {
			mfccs = m;
			speakerNet = new SpeakerNet(mfccs);		
			} 
		}
	}
	
	public static void main(String[] args) {
		PrototypeTest net = new PrototypeTest();
		try {
			net.init();
			System.out.println("INIT DONE");
			Vector13D v = MFCCSupplier.computeMFCCOfFile("C:/Users/Nico/workspace/VOICE_Prototyp/resources/A_Nico_Becker_01.wav");
			System.out.println("FILE READ");
			System.out.println(net.getSpeakerNet().identify(v.getVector()));
			System.out.println("SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
