package devs.traffic;

import java.util.Random;

import devs.traffic.CarGenerator;

public class WaveCarGenerator extends CarGenerator {
	private static final int GRANUNARITY = 300;	//for each half cycle, how many vehicles can be generated
	private static final int CONTROL = 5;		// controls the number of choices 
	private static final int INTERVAL = 10; 	// Controls the interval between two values
	public WaveCarGenerator() {
	}

	public WaveCarGenerator(String name, int period, Random rand) {
		super(name, period, rand);
	}
	
	public WaveCarGenerator(CarGenerator carGen) {
		super(carGen);
	}
	
	/* 
	 * try to generate cars according to the current count
	 * consider the process time is in between the range [5, 40] and interval 5
	 * for each value in the range will generate 10 cars
	 * use trigonometric function to calculate passing time in order to generate repeatedly
	*/ 
	@Override
	protected int getNextCarProcessingTime() {
		int carTravelTime = 0;
		//consider [0, PI] mapping to [0,1], then [1, 8] to [5, 40] 
		double trigonoValue = (Math.sin(count*Math.PI/GRANUNARITY - Math.PI*0.5) + 1 ) /2 ;	
		carTravelTime = (int) ((trigonoValue * CONTROL ) + 1)*INTERVAL ;		
		return carTravelTime;
	}

	public static void main (String[] args) {
		int carTravelTime;
		for (int i=0; i<1000; i++) {
			double trigonoValue = (Math.sin(i*Math.PI/GRANUNARITY - Math.PI*0.5) + 1 ) /2 ;	
			carTravelTime = (int) ((trigonoValue * CONTROL ) + 1)*INTERVAL ;		
			System.out.print(carTravelTime+ " ");
			if (i % 10 == 0 && i != 0) {
			System.out.println();}				
		}	
	}
}
