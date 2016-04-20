package dhbw.ai13.autoencoding.framework.layer;

import java.util.ArrayList;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class NablaBiases {
    private ArrayList<Double> biases = new ArrayList<>();
    private int countBiases;

    public NablaBiases(int countBiases){
        this.countBiases = countBiases;
        init();
    }

    private void init(){
        for(int i = 0; i < countBiases; i++){
            biases.add(0.0);
        }
    }

    public void addValues(NablaBiases nablaB){
        double newBiases;
        for(int i = 0; i < biases.size(); i++){
            newBiases = biases.get(i) + nablaB.getValue(i);
            biases.set(i, newBiases);
        }
    }

    public void add(double[] values){
        for(int i = 0; i < countBiases; i++){
            double oldValue = biases.get(i);
            biases.set(i, oldValue + values[i]);
        }
    }

    public double getValue(int index){
        return biases.get(index);
    }
}
