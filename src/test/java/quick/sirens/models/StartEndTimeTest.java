package quick.sirens.models;



import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import sirens.models.StartEndTime;

public class StartEndTimeTest {
    static StartEndTime startEndTime;

    @Before
    public void init() {
        startEndTime = new StartEndTime(0, 10, 5);
    }

    @Test
    public void getStart() {
        assertEquals(0, startEndTime.getStart());
    }

    @Test
    public void getEnd() {
        assertEquals(10, startEndTime.getEnd());
    }
    
    @Test
    public void getNetworkingStart() {
    	assertEquals(5, startEndTime.getNetworkingStart());
    }

}
