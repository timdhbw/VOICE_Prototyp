package dhbw.ai13.autoencoding.framework.elements;

import java.util.ArrayList;

/**
 * Created by GomaTa on 19.04.2016.
 */
public class NablaWeights {
    private final int countWeights;
    private final int countNodes;
    private ArrayList<ArrayList<Double>> weights = new ArrayList<>();

    public NablaWeights(int countNodes, int countWeights){
        this.countNodes = countNodes;
        this.countWeights = countWeights;
        init();
    }

    private void init(){
        for(int i = 0; i < countNodes; i++){
            ArrayList<Double> tmpWeights = new ArrayList<>();
            for(int j = 0; j < countWeights; j++){
                tmpWeights.add(0.0);
            }
            weights.add(tmpWeights);
        }
    }

    public void setWeights(double[] delta, double[] prevActivationValues){
        for(int j = 0; j < delta.length; j++){ //for each node of layer
            for(int k = 0; k < prevActivationValues.length; k++){ //for each node's wheight
                //deltaNablaW[j][k] = delta nalba for node
                // a[k] = activation value of node of current layer
                // d[j] = delta of node of next layer
                ArrayList<Double> node = weights.get(j);
                node.set(k, (prevActivationValues[k] * delta[j]));
            }
        }
    }

    public ArrayList<Double> getWeightsOfNode(int index){
        return weights.get(index);
    }
}
