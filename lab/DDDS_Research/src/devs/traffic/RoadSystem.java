package devs.traffic;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import GenCol.entity;
import genDevs.simulation.coordinator;
import simView.ViewableComponent;
import simView.ViewableDigraph;

public class RoadSystem extends ViewableDigraph implements SimulationSystemInterface {
	private final double TIMESTEP = 40;
	private final int RUSHHOUR = 300;
	private final int CYCLE = 300;
	private final double SLOWCHANCE = 1/40.0;
	private List<Vehicle> slowVehs = new ArrayList<>();
	private int slowRoad = -1;			// Slow Vehicle index
	
	private List<RoadSegment> roadsegments = new LinkedList<>();
	CarGenerator carGenerator;
	CarGenerator carGenerator2=null;
	ObservationCollect obCol; 
	private coordinator r = null;
	private Random random = null;

	private Vector<ExitEvent> results = new Vector<ExitEvent>();
	private double simulationTime = 0;
	private final int roadIdxGenerator1 = 0;
	private final int roadIdxGenerator2;
	
	int testcase = 1;	//use to generate different test case and change the corresponding method 
	
	public static class ExitEvent
	{
		private String carName;
		private String roadName;
		private double systemTime;
		public ExitEvent(String car, String road, double time)
		{
			this.carName = car;
			this.systemTime = time;
			this.roadName = road;
		}

		public ExitEvent(ExitEvent event) {
			this(event.getCarName(), event.roadName, event.systemTime);
		}
		public String getCarName() {
			return carName;
		}

		public double getSystemTime() {
			return systemTime;
		}

		public String getRoadName() {
			return roadName;
		}
	}
	
	public void outputExitEvent(String carName, String bridge, double systemTime) {
		results.add(new ExitEvent(carName, bridge, systemTime));
	}
	
	public void printResults() {
		System.out.println("Event number: " + this.results.size());
		for (ExitEvent e : this.results)
			System.out.println(e.carName + " exits from " + e.roadName + " at " + e.systemTime);
	}
	
	public RoadSystem() {
		this("RoadSystem",5, new Random(1));
		
		setCarGenerator(new CustomVehicleGenerator("gen", 0, new Random(1111), 0, 13000));
		for (int i=1;i<5;i++) {
			for (int j=30; j>0; j-- ) {
				Vehicle v = new Vehicle("veh_"+ i + j);
				v.setPosition(j*3);
				getRoadsegments().get(i).listVehicles.add(v);
			}
		}
	}

	public RoadSystem(String nm, int numCells,Random rand,int testcase)  {
		super(nm);
		this.testcase = testcase;
		random = rand;
		createRoads(numCells,rand);
		createObservationCollect(numCells);
		this.roadIdxGenerator2 = numCells/2-1;
		makeCoupling();	
		//create coordinator for system transition
		r = new coordinator(this);
		r.initialize();
	}
	
	public RoadSystem(String nm, int numCells,Random rand) {
		super(nm);
		random = rand;
		createRoads(numCells,rand);
		createObservationCollect(numCells);
		this.roadIdxGenerator2 = numCells/2-1;
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
	
	public RoadSystem(RoadSystem other) {
		super(other.getName());
		random = new Random();
		testcase = other.testcase;
		simulationTime = other.simulationTime;
		slowRoad = other.slowRoad;	
		roadsegments =  new ArrayList<>();
		this.roadIdxGenerator2 = other.getRoadsegments().size()/2-1;
		
		for (RoadSegment seg : other.getRoadsegments()) {
			RoadSegment road = new RoadSegment(seg,this);
			getRoadsegments().add(road);
			add(road);			
		}
		
		//Clone Slow Vehicles
		int numSlow = slowVehs.size();
		slowVehs = new ArrayList<>();		
		for (RoadSegment seg : other.getRoadsegments()) {
			for (entity veh : seg.listVehicles) {
				if (numSlow <= 0) 
					break;
				if ( ((Vehicle)veh).getType() == 3) {
					slowVehs.add((Vehicle)veh);
					numSlow --;
				}
			}
		}
		
		this.obCol = new ObservationCollect(TIMESTEP, getRoadsegments().size());
		add(obCol);
		
		if (other.carGenerator.getClass().getSimpleName().equals("CustomVehicleGenerator")) {		
			carGenerator = new CustomVehicleGenerator(other.carGenerator);
		}
		else {
			carGenerator = new CarGenerator(other.carGenerator);
		}
		add(carGenerator);
		
		if (other.carGenerator2 != null) {
			if (other.carGenerator2.getClass().getSimpleName().equals("CustomVehicleGenerator")) {		
				carGenerator2 = new CustomVehicleGenerator(other.carGenerator);
			} else {
				carGenerator2 = new CarGenerator(other.carGenerator);
			}
			add(carGenerator2);
		}
		makeCoupling();

		//copy coordinator
		r = new coordinator(this);
		r.initialize();
	}
	
	public RoadSystem(String nm) {
		this(nm,5,new Random(1));
		// TODO Auto-generated constructor stub
		addOutport("output");
	}
		
	private void createRoads (int numSeg, Random rand) {	
		if(testcase == 1) {
			RoadSegment road;
			for (int i = 1; i <= numSeg; i++) {
				road = new RoadSegment("Seg_"+i, rand);
				getRoadsegments().add(road);
				add(road);	
			}
			carGenerator = new CustomVehicleGenerator("gen", 0, rand, 0,800);
			((CustomVehicleGenerator)carGenerator).setMAX_FREQENCY_RUSH(7);
			((CustomVehicleGenerator)carGenerator).setMIN_FREQENCY_RUSH(0);
			add(carGenerator);	
		}
		else {
			RoadSegment road;
			for (int i = 1; i <= numSeg; i++) {
				road = new RoadSegment("Seg_"+i, rand);
				getRoadsegments().add(road);
				add(road);	
			}
					
			carGenerator = new CustomVehicleGenerator("gen1", 0, rand, 0, 800);
			((CustomVehicleGenerator)carGenerator).setMAX_FREQENCY_RUSH(7);
			((CustomVehicleGenerator)carGenerator).setMIN_FREQENCY_RUSH(0);
			
			carGenerator2 = new CustomVehicleGenerator("gen2", 880, rand, 0,930);
			((CustomVehicleGenerator)carGenerator2).setMAX_FREQENCY_RUSH(0.9);
			((CustomVehicleGenerator)carGenerator2).setMIN_FREQENCY_RUSH(0.9);
			
			add(carGenerator);	
			add(carGenerator2);	
		}
	}
	
	private void makeCoupling() {
		if (testcase == 1) {
			//Circle Road
			for (int i=0; i < getRoadsegments().size();i++) {
				addCoupling(getRoadsegments().get(i), "out", getRoadsegments().get((i+1)%getRoadsegments().size()), "in");
				addCoupling(getRoadsegments().get((i+1)%getRoadsegments().size()), "number", getRoadsegments().get(i), "numberAhead");
			}
			
			// couple carGenerator
			addCoupling(carGenerator, "out", getRoadsegments().get(roadIdxGenerator1), "in");
			
			// couple observation collect
			for (int i=0; i < getRoadsegments().size();i++) {
				addCoupling(obCol, "collectOut", getRoadsegments().get(i), "collectIn");
				addCoupling(getRoadsegments().get(i), "observationOut", obCol, "observationIn");
			}
		}
		
		else {
			//Circle Road
			for (int i=0; i < getRoadsegments().size();i++) {
				addCoupling(getRoadsegments().get(i), "out", getRoadsegments().get((i+1)%getRoadsegments().size()), "in");
				addCoupling(getRoadsegments().get((i+1)%getRoadsegments().size()), "number", getRoadsegments().get(i), "numberAhead");
			}
			
			// couple carGenerator
			addCoupling(carGenerator, "out", getRoadsegments().get(roadIdxGenerator1), "in");
			addCoupling(carGenerator2, "out", getRoadsegments().get(roadIdxGenerator2), "in");
			
			// couple observation collect
			for (int i=0; i < getRoadsegments().size();i++) {
				addCoupling(obCol, "collectOut", getRoadsegments().get(i), "collectIn");
				addCoupling(getRoadsegments().get(i), "observationOut", obCol, "observationIn");
			}
		}
		
	}
	
	public static void main (String[] args) {		
		Random randReal = new Random(1111);
		RoadSystem roadsystem = new RoadSystem("real",40, randReal,1);	
		roadsystem.run(800);		
		System.out.println("0 \t" +roadsystem);	
		
		int step = 40;
		for (int i=0;i<15; i++) {	
			if (i == 2) {
				roadsystem.setRoadSpeedCoefficient(24, 0.25);
			} else if (i == 5)  {
				roadsystem.setRoadSpeedCoefficient(24, 1);
			}
			roadsystem.run(step);
			System.out.println(step*(i+1) + "\t" +roadsystem);
		}		
	}
	
	public void setCarGenerator(CarGenerator carGen) {
		this.removeCoupling(carGenerator, "out", getRoadsegments().get(roadIdxGenerator1), "in");
		this.remove(this.carGenerator);
		this.carGenerator = carGen;
		add(carGen);
		//add coupling
		addCoupling(carGenerator, "out", getRoadsegments().get(roadIdxGenerator1), "in");
		carGen.accumulateTime = carGenerator.accumulateTime;
	}
	
	public void setCarGenerator2(CarGenerator carGen) {
		this.removeCoupling(carGenerator2, "out", getRoadsegments().get(roadIdxGenerator2), "in");
		this.remove(this.carGenerator2);
		this.carGenerator2 = carGen;
		add(carGenerator2);
		//add coupling
		addCoupling(carGenerator2, "out", getRoadsegments().get(roadIdxGenerator2), "in");
		carGen.accumulateTime = carGenerator2.accumulateTime;
	}
	
	public String toString() {
		String str =""; 
		for (RoadSegment road: getRoadsegments() ) {
			str += road.toString() + "\t";
		}	
		return str;
	}
	
	/**
	 * run method to simulate this system for some time
	 * @param time
	 */
	public void run(int time) {		
		if(carGenerator!=null)	carGenerator.accumulateTime =  simulationTime;
		if(carGenerator2!=null) 	carGenerator2.accumulateTime =  simulationTime;
		r = new coordinator(this);
		r.initialize();
		r.simulate(time);
		simulationTime  += time;
	}
	
	public void addNoiseNumber() {
		for (RoadSegment road : getRoadsegments()) {
			road.addNoiseOnNumber(this.random);
		}
	}
	
	/**
	 * Add random noise 
	 * @param rand
	 */
	public void addNoise() {
		//Slow segments change to every segment
		for (RoadSegment seg: getRoadsegments()) {
			if (this.random.nextDouble() < SLOWCHANCE) {
				seg.speedCoefficient = 0.8*random.nextDouble() + 0.2;
			}
			else
				seg.speedCoefficient = 1;
		}
	}

	/**
	 * Get speed measurement 
	 * @return
	 */
	public double[] getSpeedMeasurement () {	
		double [] observation = new double [this.getRoadsegments().size()];
		for (int i=0; i< getRoadsegments().size(); i++) {
			observation[i] = getRoadsegments().get(i).getSpeedMeasurement() / 30.0;
			//adding noise to the speed measurement
			observation[i] += RoadSystemConstants.RANDOM.nextGaussian() * observation[i] * 0.05;
			observation[i] = observation[i] > 1 ? 1 :observation[i];
		}
		return observation;
	}
	
	/**
	 * Get number measurement 
	 * @return
	 */
	public double[] getNumberMeasurement () {
		double [] number = new double [this.getRoadsegments().size()];
		for (int i=0; i< getRoadsegments().size(); i++) {
			number[i] = getRoadsegments().get(i).getNumOfVehicles() / getRoadsegments().get(i).numMax;
			number[i] = number[i] > 1 ? 1 :number[i];
		}
		return number;
	}
	
	public double distance(RoadSystem other) {		
		double distance = 0;
		for (int i = 0; i < getRoadsegments().size(); i++) {
			RoadSegment road1 = getRoadsegments().get(i);
			RoadSegment road2 = other.getRoadsegments().get(i);
			distance += Math.pow(road1.distance(road2), 2);
		}
		return Math.sqrt(distance/getRoadsegments().size());
	}
	
	public double distanceComprehensive(RoadSystem other) {
		double distance = 0;
		for (int i = 0; i < getRoadsegments().size(); i++) {
			RoadSegment road1 = getRoadsegments().get(i);
			RoadSegment road2 = other.getRoadsegments().get(i);
			distance += Math.pow(road1.distance(road2), 2);
		}	
		return Math.sqrt(distance);
	}
	
	public double speeddistance(RoadSystem other) {
		double distance = 0;
		for (int i = 0; i < getRoadsegments().size(); i++) {
			RoadSegment road1 = getRoadsegments().get(i);
			RoadSegment road2 = other.getRoadsegments().get(i);
			distance += Math.pow(road1.avgSpeedDistance(road2)/30.0, 2);
		}
		return Math.sqrt(distance);
	}
	
	public void addCustomedVehicle(int roadSegIdx, double period) {	
		RoadSegment roadSegment = getRoadsegments().get(roadSegIdx);
		CustomedVehicle veh = new CustomedVehicle("slowVeh" + slowVehs.size(), period, 3);
		veh.setPosition(0);
		veh.setSimulationInterface(this);
		roadSegment.listVehicles.add(veh);
		slowVehs.add(veh);
	}
	
	public void addCustomedVehicle(int roadSegIdx, double period,double customizedSpeed) {
		RoadSegment roadSegment = getRoadsegments().get(roadSegIdx);
		CustomedVehicle veh = new CustomedVehicle("slowVeh" + slowVehs.size(), period, customizedSpeed);
		veh.setPosition(0);
		veh.setSimulationInterface(this);
		roadSegment.listVehicles.add(veh);
		slowVehs.add(veh);
	}
	
	public void setSlowRoad(int roadSegIdx) {
		if (this.slowRoad != -1)
			getRoadsegments().get(slowRoad).isSetSlow = false;
		getRoadsegments().get(roadSegIdx).isSetSlow = true;	
	}
	
	public void setRoadSpeedCoefficient(int roadIdx, double coefficient) {
		assert coefficient < 1 && coefficient > 0;
		getRoadsegments().get(roadIdx).setSpeedCoefficient(coefficient);
	}
	
	public void setRoadSplitRate (int roadIdx, double splitRate) {
		//split percentage that determine how much traffic would go to the next segment
		assert splitRate < 1 && splitRate > 0 && roadIdx >= 0 && roadIdx < getRoadsegments().size() && roadIdx>=0;
		getRoadsegments().get(roadIdx).setSplitRate(splitRate);
	}
	
	public double getStdev() {
		double stdev = 0;
		double avg = 0;
		
		for (RoadSegment road: this.getRoadsegments()) {
			avg += road.listVehicles.size();
		}
		
		avg /= getRoadsegments().size();
		for (RoadSegment road: this.getRoadsegments()) {
			stdev += ( road.listVehicles.size() - avg ) * ( road.listVehicles.size() - avg );
		}
		return Math.sqrt(stdev);
	}
	
	public double getSimulationTime() {
		return this.simulationTime;
	}
	
    /**
     * Automatically generated by the SimView program.
     * Do not edit this manually, as such changes will get overwritten.
     */
    public void layoutForSimView()
    {
        preferredSize = new Dimension(1311, 1300);
        if((ViewableComponent)withName("Seg_5")!=null)
             ((ViewableComponent)withName("Seg_5")).setPreferredLocation(new Point(901, 249));
        if((ViewableComponent)withName("gen")!=null)
             ((ViewableComponent)withName("gen")).setPreferredLocation(new Point(79, 50));
        if((ViewableComponent)withName("ObservationCollect")!=null)
             ((ViewableComponent)withName("ObservationCollect")).setPreferredLocation(new Point(709, 19));
        if((ViewableComponent)withName("Seg_3")!=null)
             ((ViewableComponent)withName("Seg_3")).setPreferredLocation(new Point(427, 399));
        if((ViewableComponent)withName("Seg_1")!=null)
             ((ViewableComponent)withName("Seg_1")).setPreferredLocation(new Point(-7, 283));
        if((ViewableComponent)withName("Seg_4")!=null)
             ((ViewableComponent)withName("Seg_4")).setPreferredLocation(new Point(705, 360));
        if((ViewableComponent)withName("Seg_2")!=null)
             ((ViewableComponent)withName("Seg_2")).setPreferredLocation(new Point(177, 361));
    }

	public List<RoadSegment> getRoadsegments() {
		return roadsegments;
	}

	public void setRoadsegments(List<RoadSegment> roadsegments) {
		this.roadsegments = roadsegments;
	}
}
