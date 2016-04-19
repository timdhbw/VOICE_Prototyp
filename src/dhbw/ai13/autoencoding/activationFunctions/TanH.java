package dhbw.ai13.autoencoding.activationFunctions;

public class TanH implements ActivationFunction {

    @Override
    public double[] function(double[] z) {
        double[] f = new double[z.length];
        for(int i = 0; i < f.length; i++){
            f[i] = function(z[i]);
        }
        return f;
    }

    @Override
    public double[] derivation(double[] z){
        double[] d = new double[z.length];
        for(int i = 0; i < d.length; i++){
            d[i] = derivation(z[i]);
        }
        return d;
    }

    @Override
    public double derivation(double z) {
        return 1.0D - function(z) * function(z);
    }

    @Override
    public double function(double z){
        return Math.tanh(z);
    }
}
