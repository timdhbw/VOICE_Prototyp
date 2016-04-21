package dhbw.ai13.autoencoding.framework;

import dhbw.ai13.autoencoding.activationFunctions.Logistic;
import dhbw.ai13.autoencoding.activationFunctions.Softsign;
import dhbw.ai13.autoencoding.exceptions.AutoEncoderException;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/**
 * Created by GomaTa on 21.04.2016.
 */
public class SmallAutoencoder {
    private final int numInOutLayer;
    private final AutoEncoder autoencoder;
    private final int numMidLayer;

    public SmallAutoencoder() throws AutoEncoderException {
        // Layer Info
        numInOutLayer = 4;
        numMidLayer = 2;

        // Init Autoencoder
        autoencoder = new AutoEncoder(numInOutLayer);
        autoencoder.addLayer(1 ,numInOutLayer, new Logistic());
        autoencoder.addLayer(2 ,numMidLayer, new Logistic());
        autoencoder.addLayer(3 ,numInOutLayer, new Logistic());
        autoencoder.build();
    }

    public AutoEncoder getAutoencoder() {
        return autoencoder;
    }

    public double[] encode(double[] data) throws IOException, UnsupportedAudioFileException, AutoEncoderException {
        return autoencoder.encode(data);
    }
}
