package devs.traffic;

import java.util.Random;

/*
 * This car generator would try to generate the car flows as it is between the 7 am to 12 pm. 
 * From rush-hour traffic to normal traffic
 * The rush hour frequency is considered between [5, 25] 
 * The normal traffic is considered betwen [30, 60]    
 * And the car passing time is between [20, 40]
 */
public class ExpCarGenerator extends CarGenerator {
	private final int MAX_FREQENCY_RUSH = 2;
	private final int MIN_FREQENCY_RUSH = 1;
	private final int MAX_FREQUENCY_NORMAL = 40;
	private final int MIN_FREQENCY_NORMAL = 20 ;
	private final int MAX_CAR_TRAVEL = 20;
	private final int MIN_CAR_TRAVEL = 5;
	private double rushTime = 5000;
	private double cycletime = 16000;
	public ExpCarGenerator() {
	}

	public ExpCarGenerator(String name, int period, Random rand) {
		super(name, period, rand);
	}

	public ExpCarGenerator(CarGenerator carGen) {
		super(carGen);
		accumulateTime = ((ExpCarGenerator) carGen).accumulateTime;
		lastEventTime = ((ExpCarGenerator) carGen).lastEventTime;
		rushTime = ((ExpCarGenerator) carGen).rushTime;
		cycletime = ((ExpCarGenerator) carGen).cycletime;
	}
	
	public ExpCarGenerator(String name, int period, Random rand, double rushTime, double cycletime) {
		super(name, period, rand);
		this.rushTime = rushTime;
		this.cycletime = cycletime;
	}
		
	/**
	 * Use the overridden method to record the accumulated time
	 */
	@Override
    protected double getNextGenerationTime() {
		double realTime = accumulateTime ;			//consider one time unit is 10 seconds in real time
		realTime = realTime %  (int)cycletime ;				
		
		if (realTime <= rushTime && realTime >= 0 ) {
			lastEventTime = r.nextInt(MAX_FREQENCY_RUSH-MIN_FREQENCY_RUSH) + MIN_FREQENCY_RUSH ; 
		}
		else {
			// normal hours
			lastEventTime = r.nextInt(MAX_FREQUENCY_NORMAL-MIN_FREQENCY_NORMAL) + MIN_FREQENCY_NORMAL;
		}	
		accumulateTime += lastEventTime;
		return lastEventTime;
	}	
	
	@Override
	protected int getNextCarProcessingTime() {
		return r.nextInt(MAX_CAR_TRAVEL - MIN_CAR_TRAVEL)  + MIN_CAR_TRAVEL;  	
    }
	
	@Override
	protected double getVehicleSpeed() {
		return r.nextInt(MAX_CAR_TRAVEL - MIN_CAR_TRAVEL)  + MIN_CAR_TRAVEL;
	}
}
 