package devs.traffic;

import genDevs.modeling.*;
import simView.ViewableAtomic;

import java.util.Random;

public class CarGenerator extends ViewableAtomic
{
	private final int CAR_TRAVEL_TIME = 20;
    protected int int_gen_time;
    protected int count=0;
    protected Random r;
    protected double lastEventTime = 0;
	protected double accumulateTime = 0; 			// Keep track of the time of car model
	int roadIdx = -1;
	
    public String getSettingString()
    {
    	return "MeanInterval=" + int_gen_time;
    }
    
    public CarGenerator() 
    {
        this("carGenr", 7, new Random());
    }

    public int getGentime() {
		return int_gen_time;
	}

	public Random getR() {
		return r;
	}

	public CarGenerator(String name, int period,Random rand)
    {
        super(name);
        addOutport("out");
        int_gen_time = period ;
        r = rand;
    }

	public CarGenerator(CarGenerator carGen){
		this(carGen.getName(),carGen.getGentime(),new Random());
		count = carGen.count;
	}

    public void initialize(){
        holdIn("active", getNextGenerationTime());
    }

    public void deltext(double e, message x) 
    {
        Continue(e);
        for (int i=0; i< x.getLength();i++)
        {
            if (messageOnPort(x, "in", i)) 
            { 
                //the stop message from transducer
                passivate();
            }
        } 	
    }

    public void deltint( ) 
    {
        if(phaseIs("active"))
        {
            count = count +1;
            double nextTime = getNextGenerationTime(); 
            holdIn("active", nextTime);
        } 
        else 
            passivate();
    }

    public message out( ) 
    {
        message m = new message();
        content con = makeContent("out", getNextVehicle());  
        m.add(con);   
        return m;
    }
 
    protected double getNextGenerationTime() {
		return r.nextInt(int_gen_time);
	}
 
    protected int getNextCarProcessingTime() {
    	return CAR_TRAVEL_TIME;
    }
    protected double getVehicleSpeed() {
		return 10;
	}
    
    protected Vehicle getNextVehicle() {
    	return new Vehicle("veh" + this.count);
    }
    
    public String toString() {
    	return "Car generator Simulate Time:" + this.getSimulationTime() + "\t vehicle generated:" + count;
    }
    
    public void setRoadIndex(int idx) {
    	this.roadIdx = idx;
    }
    public int getRoadIndex(){
    	return roadIdx;
    }
}

