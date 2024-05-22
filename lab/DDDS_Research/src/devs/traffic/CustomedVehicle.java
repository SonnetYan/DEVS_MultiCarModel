package devs.traffic;

public class CustomedVehicle extends Vehicle {
	private double period = 960; 				// Time to complete a full period (should be 4 times of simulation step)
	private static final int CONTROL = 2;		// Controls the number of choices 
	private static final int INTERVAL = 26; 	// Controls the interval between two 
	private SimulationSystemInterface system;
	private double customizedSpeed = 3;
	
	public CustomedVehicle(String name, double period, double cusSpeed) {
		super(name); 
		this.period = period;
		this.customizedSpeed = cusSpeed;
		this.setType(3);
		this.speed = cusSpeed;
	}

	public CustomedVehicle(CustomedVehicle veh) {
		super(veh.name);
		this.setType(3);
		this.setPeriod(veh.getPeriod());
		this.setPosition(veh.getPosition());
		this.setSpeed(veh.getSpeed());
		this.setCustomizedSpeed(veh.getCustomizedSpeed());
	}
	
	public double getCustomizedSpeed() {
		return customizedSpeed;
	}

	public void setCustomizedSpeed(double customizedSpeed) {
		this.customizedSpeed = customizedSpeed;
	}

	public void setSpeed(double speed, double simTime) {
		if (isInSlowPeriod(simTime))  {
			this.speed = this.customizedSpeed;
			//System.out.println("Turn into Slow Vehicle");
		}
		else {
			this.speed = speed;
		}
	}
	
	@Override
	public double updatePosition(double time) {
		if (isInSlowPeriod(time))  {
			this.speed = this.customizedSpeed;
		}
		else {
			this.speed = speed;
		}		
		return getPosition() + getSpeed() * time;
	}

	public double getPeriod() {
		return period;
	}

	@Override
	public double getSpeed() {
		return speed;
	}
	
	public void setPeriod(double period) {
		this.period = period;
	}
	
	public boolean isInSlowPeriod (double time) {
		return (system.getSimulationTime() + time)  < ( period );
	}
	
	public void setSimulationInterface (SimulationSystemInterface system) {
		this.system = system;
	}
}
