package dhbw.ai13.autoencoding.framework;

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

    public void setBiases(double[] values){
        for(int i = 0; i < countBiases; i++){
            biases.set(i, values[i]);
        }
    }

    public double getValue(int index){
        return biases.get(index);
    }
}
