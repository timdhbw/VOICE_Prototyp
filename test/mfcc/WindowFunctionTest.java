package mfcc;

import com.gomata.gchart.GChart;
import dhbw.ai13.mfcc.DFT;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by GomaTa on 16.05.2016.
 */
public class WindowFunctionTest {

    @Test
    public void testRoundMin() {
        DFT dft = new DFT();


        assertArrayEquals(new double[]{
                0.08,0.19,0.46
        }, dft.createHammingWindow(3),0.1);


    }

}
