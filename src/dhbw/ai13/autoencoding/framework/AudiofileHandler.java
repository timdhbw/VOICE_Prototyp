package dhbw.ai13.autoencoding.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GomaTa on 10.04.2016.
 */
public class AudiofileHandler {

    public File[] getTrainingsData(String dir) throws IOException {
        final File folder = new File(dir);
        ArrayList<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.getName().endsWith(".wav")) {
                //System.out.println(fileEntry.getName());
                files.add(fileEntry);
            }
        }
        File[] filesArray = new File[files.size()];
        for(int i = 0; i < files.size(); i++){
            filesArray[i] = files.get(i);
        }
        System.out.println("Read all WAV-files successfully.");
        return filesArray;
    }
}
