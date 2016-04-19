package dhbw.ai13.autoencoding.framework.layer;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class Bias {
    private double value;

    public Bias(double value){
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
