package dhbw.ai13.autoencoding.activationFunctions;

public interface ActivationFunction{
    double[] function(double[] z);

    double function(double z);

    double[] derivation(double[] z);

    double derivation(double z);
}
