package dhbw.ai13.autoencoding.framework.elements;

/**
 * Created by GomaTa on 28.02.2016.
 */
public class DeltaNabla {
    double[][][] deltaNablaW;
    double[][] deltaNablaB;

    public DeltaNabla(double[][][] dnW, double[][] dnB) {
        this.deltaNablaW = dnW;
        this.deltaNablaB = dnB;
    }

    public double[][][] getDeltaNablaW() {
        return deltaNablaW;
    }

    public double[][] getDeltaNablaB() {
        return deltaNablaB;
    }
}
