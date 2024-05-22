package DDDS_2024_MultiCarModel;

import genDevs.modeling.*;
import simView.ViewableAtomic;

import java.util.Random;

public class multiModel_Generator_E2W extends ViewableAtomic
{
    private final int CAR_TRAVEL_TIME = 20;
    protected  double nextCarArriveTime_Lambda = model_configData.generator_westMoving_lambda; // 1 car per 6 seconds 
    protected int count=0;
    protected Random r;
    protected double lastEventTime = 0;
    protected double accumulateTime = 0; 			// Keep track of the time of car model
    int roadIdx = -1;

    public String getSettingString()
    {
        return "MeanInterval=" + nextCarArriveTime_Lambda;
    }

    public multiModel_Generator_E2W()
    {
        this("carGenr_E2W", 1/7, new Random(model_configData.randSeed_westMoving));
    }

    public double getGentime() {
        return nextCarArriveTime_Lambda;
    }

    public Random getR() {
        return r;
    }

    public multiModel_Generator_E2W(String name, double lambda,Random rand)
    {
        super(name);
        addOutport("out");
        lambda =  nextCarArriveTime_Lambda ;
        System.out.println("lambda: " + lambda);
        r = rand;
    }

    public multiModel_Generator_E2W(multiModel_Generator_E2W carGen){
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
        return getPoissonNextTime(nextCarArriveTime_Lambda);
    }
    private double getPoissonNextTime(double lambda) {
	double u = r.nextDouble();
	double nextTime = -Math.log(u) / lambda;
	if(nextTime < 1.05)  nextTime = 1.05;
	return nextTime;
}

    protected int getNextCarProcessingTime() {
        return CAR_TRAVEL_TIME;
    }
    protected double getVehicleSpeed() {
        return 10;
    }

    protected Vehicle getNextVehicle() {
        return new Vehicle("veh_E2W_" + this.count);
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

