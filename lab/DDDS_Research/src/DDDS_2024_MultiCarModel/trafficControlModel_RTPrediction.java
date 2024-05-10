package DDDS_2024_MultiCarModel;

import java.util.Random;

import GenCol.entity;
import genDevs.modeling.DevsInterface;

public class trafficControlModel_RTPrediction extends trafficControlModel{

	public trafficControlModel_RTPrediction() {this("trafficControlModel_RTPrediction");}

	public trafficControlModel_RTPrediction(String name){
	    this(name,"eastMoving_passive", DevsInterface.INFINITY, 
	    		0, 0, 0,model_configData.carPassingTime_mean, new Random(model_configData.randSeed_IntersectionM));
	}

	public trafficControlModel_RTPrediction(String name, String tfcState, double sgm, double initElapseTimeGreen, int QWestSideSize, int QEastSideSize, double carPassingTime, Random rd){
	    super(name, tfcState, sgm, initElapseTimeGreen, QWestSideSize, QEastSideSize,
	    		carPassingTime, rd);

	    // Add two special cars at the end of the queue to check how much time it takes for them to pass 
	    queue_westSide.add(new entity("eastMovingCar_RTPrediction"));
	    queue_eastSide.add(new entity("westMovingCar_RTPrediction"));

	}
}
