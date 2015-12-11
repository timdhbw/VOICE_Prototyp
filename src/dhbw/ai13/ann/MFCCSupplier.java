package dhbw.ai13.ann;

import dhbw.ai13.mfcc.MFCC;
import dhbw.ai13.speech.detection.Vector13D;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MFCCSupplier {

    /**
     * The function computeMFCOfFile computes mfcc data from a file.
     * @param path Path of the file.
     */
    public static Vector13D computeMFCCOfFile(String path) throws IOException, UnsupportedAudioFileException {
        //System.out.println(path);
        MFCC mfcc = new MFCC();
        File f = new File(path);
        double[] mfccValue = mfcc.computeMFCC(f,13);
        String fileName = f.getName(), vocal = "UNKNOWN", person = "UNKNOWN";
        if (fileName.matches("(A|E|I|O|U)_.*_.*_.*wav")) {
            vocal = fileName.substring(0, 1);
            person = fileName.substring(2, fileName.length());
        }
        return new Vector13D(mfccValue, vocal, person);
    }

    /**
     * The function computeMFCOfFolder computes mfcc data from a folder.
     * @param path Path of the folder.
     */
    public static ArrayList<Vector13D> computeMFCCsOfFolder(String path) {
        try {
            File dir = new File(path);
            ArrayList<Vector13D> output = new ArrayList<>();
            fileLoop: for (File p : dir.listFiles()) {
                if (!p.isDirectory() && p.getName().matches("(A|E|I|O|U)_.*_.*_.*wav")) {
                    Vector13D v = computeMFCCOfFile(p.toString());
                    output.add(v);
                }
            }
            return output;
        } catch (NumberFormatException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    public static void main(String[] args){
        ArrayList<Vector13D> o = MFCCSupplier.computeMFCCsOfFolder("C:\\Users\\GomaTa\\Documents\\VOICE_Prototyp\\resources");
        System.out.println(o);
    }
    */

}
