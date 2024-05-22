package devs.traffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import GenCol.entity;
import genDevs.simulation.coordinator;
import simView.ViewableDigraph;

public class StraightRoadSystem extends ViewableDigraph implements SimulationSystemInterface{
	private final double TIMESTEP = 30;
	private final int RUSHHOUR = 300;
	private final int CYCLE = 300;
	private final double SLOWCHANCE = 1/15.0;
	private List<Vehicle> slowVehs = new ArrayList<>();
	private int slowRoad = -1;			// Slow Vehicle index
	List<RoadSegment> roadsegments = new LinkedList<>();
	Map<Integer, CarGenerator> mapCarGenerators = new HashMap<>();
	ObservationCollect obCol; 
	//Variables for data assimilation
	private coordinator r = null;
	private Random random = null;

	private double simulationTime = 0;
	
	
	public StraightRoadSystem() {
		this("RoadSystem",5, new Random(1));
		//add test condition
		this.addVehicleGenerator(new CustomVehicleGenerator("gen", 0, new Random(1111), 0, 13000),0);
		for (int i=1;i<5;i++) {
			for (int j=30; j>0; j-- ) {
				Vehicle v = new Vehicle("veh_"+ i + j);
				v.setPosition(j*3);
				roadsegments.get(i).listVehicles.add(v);
			}
		}
	}

	public StraightRoadSystem(String name, int numCells,Random rand) {
		super(name);
		random = rand;
		createRoads(numCells);
		createObservationCollect(numCells);
		makeCoupling();
		
		//create coordinator for system transition
		r = new coordinator(this);
		r.initialize();
	}
	
	//add observation collect component
	private void createObservationCollect(int num) {
		this.obCol = new ObservationCollect(TIMESTEP, num);	
		this.add(obCol);
	}

	public StraightRoadSystem(StraightRoadSystem other) {
		super(other.name);
		//set random
		this.random = new Random();
		
		simulationTime = other.simulationTime;
		slowRoad = other.slowRoad;		
		roadsegments = new ArrayList<>();
		for (RoadSegment seg : other.roadsegments) {
			RoadSegment road = new RoadSegment(seg);
			roadsegments.add(road);
			add(road);			
		}
		
		//Clone Slow Vehicles
		int numSlow = slowVehs.size();
		slowVehs = new ArrayList<>();
		
		for (RoadSegment seg : other.roadsegments) {
			for (entity veh : seg.listVehicles) {
				if (numSlow <= 0) 
					break;
				if ( ((Vehicle)veh).getType() == 3) {
					slowVehs.add((Vehicle)veh);
					numSlow --;
				}
			}
		}
		
		this.obCol = new ObservationCollect(TIMESTEP, roadsegments.size());
		add(obCol);	
		mapCarGenerators = new HashMap<>();
		for (Integer roadidx: other.mapCarGenerators.keySet()) {
			CarGenerator carGenerator = null;
			CarGenerator otherGen = mapCarGenerators.get(roadidx);
			if (otherGen.getClass().getSimpleName().equals("CustomVehicleGenerator")) {		
				carGenerator = new CustomVehicleGenerator(otherGen);
			} else {
				carGenerator = new CarGenerator(otherGen);
			}
			mapCarGenerators.put(roadidx, carGenerator);
			add(carGenerator);	
		}
		makeCoupling();
		
		//copy coordinator
		r = new coordinator(this);
		r.initialize();
	}
	
	public StraightRoadSystem(String nm) {
		this(nm,5,new Random(1));
		addOutport("output");
	}

	private void createRoads (int numSeg) {
		RoadSegment vehicleStep;
		for (int i = 1; i <= numSeg; i++) {
			vehicleStep = new RoadSegment("Seg_"+i, this.random);
			roadsegments.add(vehicleStep);
			add(vehicleStep);	
		}
		
		addVehicleGenerator(new CustomVehicleGenerator("gen", 0, this.random, 0,10000),0);
		addVehicleGenerator(new CustomVehicleGenerator("gen", 0, this.random, 150,10000),4);
		addVehicleGenerator(new CustomVehicleGenerator("gen", 0, this.random, 300,10000),15);
	}
	
	public void addVehicleGenerator(CarGenerator generator, int roadIdx) {
		if (!mapCarGenerators.containsKey(roadIdx) ) {
			mapCarGenerators.put(roadIdx, generator);
			add(generator);
			addCoupling(generator, "out", roadsegments.get(0), "in");
		}
		else {
			this.removeCoupling(mapCarGenerators.get(roadIdx), "out", roadsegments.get(roadIdx), "in");
			this.remove(mapCarGenerators.get(roadIdx));
			mapCarGenerators.put(roadIdx, generator);
			add(generator);
		}
	}
	
	
	private void makeCoupling() {
		for (int i=0; i < roadsegments.size()-1;i++) {
			addCoupling(roadsegments.get(i), "out", roadsegments.get((i+1)%roadsegments.size()), "in");
			addCoupling(roadsegments.get((i+1)%roadsegments.size()), "number", roadsegments.get(i), "numberAhead");
		}
		addCoupling(roadsegments.get(roadsegments.size()-1), "out", this, "output");
				
		// couple observation collect
		for (int i=0; i < roadsegments.size();i++) {
			addCoupling(obCol, "collectOut", roadsegments.get(i), "collectIn");
			addCoupling(roadsegments.get(i), "observationOut", obCol, "observationIn");
		}
	}
	
	public static void main (String[] args) {
		Random randReal = new Random(1111);
		RoadSystem roadsystem = new RoadSystem("real",40, randReal);
		roadsystem.setCarGenerator(new CustomVehicleGenerator("gen", 800, randReal, 800, 13000));
		roadsystem.run(650);
		System.out.println("0 \t" +roadsystem);
		roadsystem.setRoadSpeedCoefficient(4, 0.2);
		for (int i=0;i<25; i++) {
			roadsystem.run(30);
			if (i == 5) roadsystem.setRoadSpeedCoefficient(4, 1);
			System.out.println(30*(i+1) + "\t" +roadsystem);
		}
	}

	
	public String toString() {
		String str =""; 
		for (RoadSegment road: roadsegments ) {
			str += road.toString() + "\t";
		}
		return str;
	}
	
	/**
	 * run method to simulate this system for some time
	 * @param time
	 */
	public void run(int time) {	
		for (CarGenerator gen: mapCarGenerators.values())
			gen.accumulateTime =  simulationTime;
		r = new coordinator(this);
		r.initialize();
		r.simulate(time);
		simulationTime  += time;
	}
	
	public void addNoiseNumber() {
		for (RoadSegment road : roadsegments) {
			road.addNoiseOnNumber(this.random);
		}
	}
	
	/**
	 * Add random noise 
	 * @param rand
	 */
	public void addNoise() {
		//Slow segments change to every segment
		for (RoadSegment seg: roadsegments) {
			if (this.random.nextDouble() < SLOWCHANCE) {
				seg.isSetSlow = true;
			}
			else
				seg.isSetSlow = false;
		}
	}

	/**
	 * Get speed measurement 
	 * @return
	 */
	public double[] getSpeedMeasurement () {	
		double [] observation = new double [this.roadsegments.size()];
		for (int i=0; i< roadsegments.size(); i++) {
			observation[i] = roadsegments.get(i).getSpeedMeasurement() / 30.0;
		}
		return observation;
	}
	
	/**
	 * Get number measurement 
	 * @return
	 */
	public double[] getNumberMeasurement () {
		double [] number = new double [this.roadsegments.size()];
		for (int i=0; i< roadsegments.size(); i++) {
			number[i] = roadsegments.get(i).getNumOfVehicles() / 60.0;
		}
		return number;
	}
	
	public double distance(StraightRoadSystem roadSystem) {
		double distance = 0;
		for (int i = 0; i < roadsegments.size(); i++) {
			RoadSegment road1 = roadsegments.get(i);
			RoadSegment road2 = roadSystem.roadsegments.get(i);
			distance += Math.pow(road1.distance(road2), 2);
		}
		return Math.sqrt(distance/roadsegments.size());
	}
	
	public double distanceComprehensive(RoadSystem other) {
		double distance = 0;
		for (int i = 0; i < roadsegments.size(); i++) {
			RoadSegment road1 = roadsegments.get(i);
			RoadSegment road2 = other.getRoadsegments().get(i);
			//road1.listVehicles.size();
			//road2.listVehicles.size();
			distance += Math.pow(road1.distance(road2), 2);
		}
		return Math.sqrt(distance);
	}
	
	public double speeddistance(StraightRoadSystem other) {
		double distance = 0;
		for (int i = 0; i < roadsegments.size(); i++) {
			RoadSegment road1 = roadsegments.get(i);
			RoadSegment road2 = other.roadsegments.get(i);
			distance += Math.pow(road1.avgSpeedDistance(road2)/30.0, 2);
		}
		return Math.sqrt(distance);
	}
	
	public void addCustomedVehicle(int roadSegIdx, double period) {	
		RoadSegment roadSegment = roadsegments.get(roadSegIdx);
		CustomedVehicle veh = new CustomedVehicle("slowVeh" + slowVehs.size(), period, 3);
		veh.setPosition(0);
		veh.setSimulationInterface(this);
		roadSegment.listVehicles.add(veh);
		slowVehs.add(veh);
	}
	
	public void addCustomedVehicle(int roadSegIdx, double period,double customizedSpeed) {
		
		RoadSegment roadSegment = roadsegments.get(roadSegIdx);
		CustomedVehicle veh = new CustomedVehicle("slowVeh" + slowVehs.size(), period, customizedSpeed);
		veh.setPosition(0);
		veh.setSimulationInterface(this);
		roadSegment.listVehicles.add(veh);
		slowVehs.add(veh);
	}
	
	public void setSlowRoad(int roadSegIdx) {
		if (this.slowRoad != -1)
			roadsegments.get(slowRoad).isSetSlow = false;
		roadsegments.get(roadSegIdx).isSetSlow = true;	
	}
	
	public void setRoadSpeedCoefficient(int roadIdx, double coefficient) {
		assert coefficient < 1 && coefficient > 0;
		roadsegments.get(roadIdx).setSpeedCoefficient(coefficient);
	}
	
	public double getStdev() {
		double stdev = 0;
		double avg = 0;
		
		for (RoadSegment road: this.roadsegments) {
			avg += road.listVehicles.size();
		}
		avg /= roadsegments.size();
		
		for (RoadSegment road: this.roadsegments) {
			stdev += ( road.listVehicles.size() - avg ) * ( road.listVehicles.size() - avg );
		}		
		return Math.sqrt(stdev);
	}
	
	public double getSimulationTime() {
		return this.simulationTime;
	}
}
