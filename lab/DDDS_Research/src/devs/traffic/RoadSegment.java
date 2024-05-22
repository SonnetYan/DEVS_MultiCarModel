package devs.traffic;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import GenCol.entity;
import GenCol.intEnt;
import genDevs.modeling.message;
import simView.ViewableAtomic;

public class RoadSegment extends ViewableAtomic {
	protected List<Vehicle> listVehicles = new LinkedList<>();
	protected double segLength = 100;
	protected int numberAhead = 0;
	public static final double vMax = 30;
	public static final int carLength = 3;
	public static final double vMin = 2;
	public static final double densityThreshold = 0.1;
	protected double speedCoefficient = 1;
	boolean isSetSlow = false;
	private double EndRoadSplitRate = 1;
	private boolean isSplit = false;
	private Random random;
	protected entity curVehicle = null;
	public int numMax;
	
	public RoadSegment() {
		this("RoadSegment");
	}

	public RoadSegment(String nm, Random rand) {
		this(nm);
		random = rand;
	}

	public RoadSegment(String nm) {
		super(nm);
		addInport("numberAhead");
		addInport("in");
		addInport("collectIn");
		addOutport("number");
		addOutport("out");
		addOutport("observationOut");
		
		numberAhead = 0;
		numMax = (int) (segLength+1) / carLength ;
	}
	
	public RoadSegment(RoadSegment other, RoadSystem system) {
		super(other.getName());

		addInport("numberAhead");
		addInport("in");
		addInport("collectIn");
		addOutport("number");
		addOutport("out");
		addOutport("observationOut");
		this.segLength = other.segLength;
		this.numberAhead = other.numberAhead;
		this.numMax = other.numMax;
		this.isSetSlow = other.isSetSlow;		
		this.curVehicle = other.curVehicle;
		this.random = new Random();
		
		// copy all vehicles
		listVehicles = new LinkedList<>();
		for (entity ent : other.listVehicles) {
			Vehicle veh = null;
			if (((Vehicle) ent).getType() == 3) {
				//continue;
				veh = new CustomedVehicle((CustomedVehicle) ent);
				((CustomedVehicle) veh).setSimulationInterface(system);
			} else {
				veh = new Vehicle((Vehicle) ent);
			}			
			listVehicles.add(veh);
		}
	}
	
	public RoadSegment(RoadSegment other) {
		super(other.getName());

		addInport("numberAhead");
		addInport("in");
		addInport("collectIn");
		addOutport("number");
		addOutport("out");
		addOutport("observationOut");
		this.segLength = other.segLength;
		this.numberAhead = other.numberAhead;
		this.numMax = other.numMax;
		this.isSetSlow = other.isSetSlow;
		this.curVehicle = other.curVehicle;
		this.isSplit = other.isSplit;
		this.EndRoadSplitRate = other.EndRoadSplitRate;
		this.random = new Random();
		
		// copy all vehicles
		listVehicles = new LinkedList<>();
		for (entity ent : other.listVehicles) {
			Vehicle veh = null;
			if (((Vehicle) ent).getType() == 3) {
				//continue;
				veh = new CustomedVehicle((CustomedVehicle) ent);
			} else {
				veh = new Vehicle((Vehicle) ent);
				veh.setPosition(((Vehicle) ent).getPosition());
				veh.setSpeed(((Vehicle) ent).getSpeed());
			}			
			listVehicles.add(veh);
		}
	}

	public void initialize() {
		if (listVehicles.isEmpty()) {
			passivate();
		} else {
			updateSpeed();
			holdIn("active", getNextPassingTime());
		}
	}

	public void deltext(double e, message x) {
		Continue(e);
		updatePosition(e); 
		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "numberAhead", i)) {
				intEnt ent = (intEnt) x.getValOnPort("numberAhead", i);
				numberAhead = ent.getv();
			}
		}

		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "in", i)) {
				// comes with posn & speed
				Vehicle veh = (Vehicle) x.getValOnPort("in", i); 
				veh.setPosition(0);
				listVehicles.add(veh); // add all vehicles  before updating
			}
		}

		for (int i = 0; i < x.getLength(); i++) {
			if (messageOnPort(x, "collectIn", i)) {
				holdIn("outputMeasurement", 0);
				return;
			}
		}

		if (!listVehicles.isEmpty()) {
			updateSpeed();
			holdIn("active", getNextPassingTime());
		}
		else {
			passivate();
		}

	}

	public void deltint() {
		if (phaseIs("active")) {
			// phase is active
			updatePosition(sigma);
			curVehicle = null;
			if (!isSplit) {
				numberAhead += 1;
			}				
		}
		
		//schedule the next event 
		if (!listVehicles.isEmpty()) {	
			// schedule the next vehicle
			updateSpeed();
			holdIn("active", getNextPassingTime());
		} else
			passivate();
	}

	public message out() {
		message m = new message();
		if (phaseIs("active") && !listVehicles.isEmpty()) {
			entity veh =  listVehicles.remove(0);
			curVehicle = veh;			
			if (random.nextDouble()<EndRoadSplitRate) {
				m.add(makeContent("out", veh));
				m.add(makeContent("number", new intEnt(listVehicles.size())));
				isSplit = false;
			} else {
				isSplit = true;
			}				
		} else if (phaseIs("outputMeasurement")) {
			m.add(makeContent("observationOut", new entity("Sensor data")));
		} else {
			m = null;
		}
		return m;
	}

	public void updateSpeed() {
		double newSpeed = computeSpeed() * speedCoefficient;
		for (entity veh : listVehicles) {
			Vehicle v = (Vehicle) veh;
			if (v.getType() == 3) {
				((CustomedVehicle) v).setSpeed(newSpeed, this.getSimulationTime());
			} else if (v.getType() == 1) {
				v.setSpeed(vMin);
			} else if (isSetSlow == true) {
				v.setSpeed(vMin);
			} else {
				v.setSpeed(newSpeed);
			}
		}
	}

	private double computeSpeed() {
		double speed = 0;
		int numberVehicles = listVehicles.size();
		
		if (numberVehicles < densityThreshold * numMax)
			speed = vMax;
		else if (numberVehicles > numMax)
			speed = vMin;
		else 
			speed = vMin + (vMax - vMin) * (numMax - numberVehicles) / numMax;
				
		return speed * computeAlpha();
	}

	private double computeAlpha () {
		double alpha = 0; 
		if ( numberAhead <= 0  || numberAhead < numMax * 0.2) 
			return 1.0;
		else if (numberAhead >= numMax) 
			return 0.0;
		else
			alpha = 0.4+ 0.6 * (numMax - numberAhead) / numMax;	
		return alpha;
	}
	
	/**
	 * Update the position but the vehicle from behind cannot pass the front
	 */
	private void updatePosition(double elapse) {
		double frontPos = this.segLength;
		for (int i = 0; i < listVehicles.size(); i++ ) {		
			Vehicle v = (Vehicle) listVehicles.get(i);
			double newPos = v.updatePosition(elapse);
			if (curVehicle == null)
				newPos = (newPos <= frontPos ? newPos : frontPos);
			else
				newPos = (newPos <= frontPos - carLength ? newPos : frontPos - carLength);
			newPos = newPos > 0? newPos : 0;
			v.setPosition(newPos); 
			frontPos = newPos;
		}
	}
	
	public void addNoiseOnNumber(Random rand) {
		int oldNumber = listVehicles.size();
		double vehToAdd =  ((rand.nextDouble() * 8  - 4) );
		if (vehToAdd < 0) {
			while (vehToAdd < 0 && listVehicles.size() > 0 ) {
				int pick = rand.nextInt(listVehicles.size());			
				if (((Vehicle) listVehicles.get(pick)).getType() != 3)
					listVehicles.remove(pick);
				vehToAdd++;
			}
		} else {
			while (vehToAdd > 0 && listVehicles.size() < numMax) {
				double position = rand.nextDouble() * segLength;
				ListIterator<Vehicle> iter = listVehicles.listIterator(listVehicles.size());
				while (iter.hasPrevious()) {
					Vehicle veh = (Vehicle)iter.previous();
					if (veh.getPosition() < position) {
						iter.add(new Vehicle(position,0));
						break;
					}
				}
				vehToAdd--;
			}
		}
		updateSpeed();
	}

	public double getNextPassingTime() {
		Vehicle vehicle = (Vehicle) listVehicles.get(0);
		if (vehicle.getSpeed() - 0.0 < 0.000000001) {
			return INFINITY;
		}
		else
			return (this.segLength - vehicle.getPosition()) / vehicle.getSpeed();
	}

	@Override
	public String getTooltipText() {
		return super.getTooltipText() + "\nAverage Speed: "
				+ (!listVehicles.isEmpty() ? ((Vehicle) listVehicles.get(0)).getSpeed() : 0) + "\nVehicle Number:"
				+ listVehicles.size() + "\nFront Position:" + (!listVehicles.isEmpty() ? ((Vehicle) listVehicles.get(0)).getPosition() : 0);
	}

	public double getAvgSpeed() {
		if (listVehicles.isEmpty())
			return 0;
		double avg = 0;
		for (entity veh : listVehicles) {
			avg += ((Vehicle) veh).getSpeed();
		}
		return avg / listVehicles.size();
	}

	public String toString() {
		return String.format("%2d ", getNumOfVehicles());
	}

	public String getDetailedInformation() {
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.getName() + " ");
		for (int i=0; i<listVehicles.size(); i++) {
			Vehicle veh = (Vehicle)listVehicles.get(i);
			sb.append( veh.getName() + "**" + String.format("%.2f",veh.getPosition()) + "## ");
		}			 
		return sb.toString();
	}
	public int getNumOfVehicles() {
		return listVehicles.size()>numMax ? numMax:listVehicles.size();
	}

	public double distance(RoadSegment other) {
		return Math.abs(listVehicles.size() - other.listVehicles.size());
	}

	public double avgSpeedDistance(RoadSegment other) {
		double s1, s2;
		s1 = this.getSpeedMeasurement();
		s2 = other.getSpeedMeasurement();
		return Math.abs(s1 - s2);
	}

	public void setSpeedCoefficient(double coefficient) {
		this.speedCoefficient = coefficient;
	}

	public double getSpeedMeasurement() {
		if (isSetSlow == true)
			return 2.0;
		if (listVehicles.size() > 0) {
			if (((Vehicle) listVehicles.get(0)).getType() == 1)
				return 2.0;
			else if (((Vehicle) listVehicles.get(0)).getType() == 3) {
				CustomedVehicle veh = ((CustomedVehicle) listVehicles.get(0));
				if (veh.isInSlowPeriod(this.getSimulationTime())) {
					return 2.0;
				}
			}
		}
		double avg = 0;
		for (entity veh : listVehicles) {
			if (((Vehicle) veh).getType() == 1)
				avg += 2.0;
			else
				avg += ((Vehicle) veh).getSpeed();
		}
		return listVehicles.size() > 0 ? avg / listVehicles.size() : vMax;
	}
	
	public void setSplitRate(double splitRate) {
		this.EndRoadSplitRate = splitRate;
	}
}
