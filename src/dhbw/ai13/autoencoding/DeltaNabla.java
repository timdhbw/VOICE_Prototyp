package dhbw.ai13.autoencoding;

/**
 * Created by GomaTa on 28.02.2016.
 */
public class DeltaNabla {
    double[][][] nabla_w;
    double[][] nalba_b;

    public DeltaNabla(double[][][] nabla_w, double[][] nalba_b) {
        this.nabla_w = nabla_w;
        this.nalba_b = nalba_b;
    }

    public double[][][] getNabla_w() {
        return nabla_w;
    }

    public double[][] getNalba_b() {
        return nalba_b;
    }
}
