package dhbw.ai13.autoencoding.framework.layer;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class Weight {
    private double value;


    public Weight(double value){
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
