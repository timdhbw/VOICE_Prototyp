package dhbw.ai13.ann;

import dhbw.ai13.mfcc.MFCC;
import dhbw.ai13.speech.detection.Vector13D;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MFCCSupplier {

    /**
     * The function computeMFCOfFile computes mfcc data from a file.
     * @param path Path of the file.
     */
    public static Vector13D computeMFCCOfFile(String path) throws IOException, UnsupportedAudioFileException {
        System.out.println(path);
        MFCC mfcc = new MFCC();
        File f = new File(path);
        double[] mfccValue = mfcc.computeMFCC(f,26);
        String fileName = f.getName(), vocal = "UNKNOWN", person = "UNKNOWN";
        Pattern p = Pattern.compile("(A|E|I|O|U)_(.+)_(.+)_.*wav");
        Matcher m = p.matcher(fileName);
        if (m.matches()) {
            vocal = m.group(1);
            person = m.group(2) + " " + m.group(3);
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

    public static void saveMFFCsOfFolderToFile(String pathToWAV, String pathToFile) throws NumberFormatException, IOException {
        File f = new File(pathToFile);
        FileWriter fw = new FileWriter(f);
        ArrayList<Vector13D> mfccs = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();
        if (mfccs.size() == 0){
            ArrayList<Vector13D> m = MFCCSupplier.computeMFCCsOfFolder(pathToWAV);
            for(int i = 0; i < m.size(); i++){
                StringBuilder line = new StringBuilder();
                Vector13D v = m.get(i);
                double[] a = v.getVector();
                line.append(v.getUser() + "$");
                line.append(v.getVocal() + "$");
                for(int j = 0; j < a.length; j++){
                    line.append(String.format("%f;",a[j]));
                }
                lines.add(line.toString());
            }
        }
        for(int i = 0; i < lines.size(); i++){
            fw.write(lines.get(i) + System.getProperty("line.separator"));
            fw.flush();
        }
        fw.close();
    }

}
