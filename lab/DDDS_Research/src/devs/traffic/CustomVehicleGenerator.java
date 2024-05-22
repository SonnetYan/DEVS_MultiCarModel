package devs.traffic;

import java.util.Random;

public class CustomVehicleGenerator extends CarGenerator {
	private double MAX_FREQENCY_RUSH = 8;
	private double MIN_FREQENCY_RUSH = 2;	
	private int MAX_CAR_TRAVEL = 20;
	private int MIN_CAR_TRAVEL = 8;
	private double rushTime = 5000;
	private double cycletime = 16000;
	public CustomVehicleGenerator() {
	}

	public CustomVehicleGenerator(String name, int period, Random rand) {
		super(name, period, rand);
	}

	public CustomVehicleGenerator(CarGenerator carGen) {
		super(carGen);
		accumulateTime = carGen.accumulateTime;
		lastEventTime = carGen.lastEventTime;
		rushTime = ((CustomVehicleGenerator) carGen).rushTime;
		cycletime = ((CustomVehicleGenerator) carGen).cycletime;
		this.setMAX_FREQENCY_RUSH(((CustomVehicleGenerator) carGen).getMAX_FREQENCY_RUSH());
		this.setMIN_FREQENCY_RUSH(((CustomVehicleGenerator) carGen).getMIN_FREQENCY_RUSH());	
	}
	
	public CustomVehicleGenerator(String name, int period, Random rand, double rushTime, double cycletime) {
		super(name, period, rand);
		this.rushTime = rushTime;
		this.cycletime = cycletime;
	}
	
	
	/**
	 * Use the overridden method to record the accumulated time
	 */
	@Override
    protected double getNextGenerationTime() {
		double realTime = accumulateTime ;
		if (realTime < this.int_gen_time) {
			lastEventTime = (int) (int_gen_time - realTime+1);
		}
		else if (realTime >= rushTime && realTime <= rushTime + cycletime ) {
			lastEventTime = r.nextDouble() *(MAX_FREQENCY_RUSH-MIN_FREQENCY_RUSH) + MIN_FREQENCY_RUSH;
		}
		else {
			// normal hours
			//lastEventTime = r.nextInt(MAX_FREQUENCY_NORMAL-MIN_FREQENCY_NORMAL) + MIN_FREQENCY_NORMAL;
			lastEventTime = 100000;
			//System.out.println("Normal Traffic generated at time " + accumulateTime + " next event " + lastEventTime);
		}	
		accumulateTime += lastEventTime;
		return lastEventTime;
	}	
	
	@Override
	protected double getVehicleSpeed() {
		double realTime = accumulateTime ;			//consider one time unit is 10 seconds in real time
		realTime = realTime %  (int)cycletime ;		
		int speed=0;
		if (realTime <= rushTime && realTime >= 0 ) {
			speed = 20;
		}
		else {
			// normal hours
			speed = r.nextInt(MAX_CAR_TRAVEL - MIN_CAR_TRAVEL)  + MIN_CAR_TRAVEL;
		}	
		accumulateTime += lastEventTime;
		return speed;
	}
	
	public double getMAX_FREQENCY_RUSH() {
		return MAX_FREQENCY_RUSH;
	}

	public void setMAX_FREQENCY_RUSH(double mAX_FREQENCY_RUSH) {
		MAX_FREQENCY_RUSH = mAX_FREQENCY_RUSH;
	}

	public double getMIN_FREQENCY_RUSH() {
		return MIN_FREQENCY_RUSH;
	}

	public void setMIN_FREQENCY_RUSH(double mIN_FREQENCY_RUSH) {
		MIN_FREQENCY_RUSH = mIN_FREQENCY_RUSH;
	}
}
