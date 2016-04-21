package dhbw.ai13.autoencoding.activationFunctions;

public class Softsign implements ActivationFunction {

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
        return 1.0 / (1.0 + Math.pow(Math.abs(z),2));
    }

    @Override
    public double function(double z){
        return z / (1.0 + Math.abs(z));
    }
}
