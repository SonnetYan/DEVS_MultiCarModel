package DDDS_2024_MultiCarModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import genDevs.simulation.coordinator;

public class RTPrediction_fromParticleData {

	Random RTPredictionRand;
	PrintWriter RT_PredictionResult;
	
	public RTPrediction_fromParticleData() {
		RTPredictionRand = new Random(PF_configData.RTPrediction_randSeed);
	}
	
	public void RT_Prediction() {
		
		try{
			RT_PredictionResult = new PrintWriter(new FileOutputStream(PF_configData.DataPathName+"RTPredictionResult.txt"), true);
		}
		catch (FileNotFoundException e) {System.out.println("File creation error!!!!!");}

		RT_PredictionResult.println("step"+"\t"+"ArrivingTime"
				+"\t"+"RTPred_eastMovWaitTime"
				+"\t"+"eastMovWT_Max"+"\t"+"eastMovWT_Min"+"\t"+"eastMovWT_std"
				+"\t"+"RTPred_westMovWaitTime"
				+"\t"+"westMovWT_Max"+"\t"+"westMovWT_Min"+"\t"+"westMovWT_std"
				+"\t"+"real_eastMovWaitTime"
				+"\t"+"real_westMovWaitTime"
				);
				
//		double[] RTPeastMovWaitingTime = new double[PF_configData.numberofsteps];
//		double[] RTPwestMovWaitingTime = new double[PF_configData.numberofsteps];
		
		for(int i=0; i<PF_configData.numberofsteps;i++) {
			System.out.println("--- start step:"+i);
			double eastMovWaitingTimeSum=0, westMovWaitingTimeSum=0;
			double eastMovWaitingTimeMax=0, eastMovWaitingTimeMin = 100000;
			double westMovWaitingTimeMax=0, westMovWaitingTimeMin = 100000;
			int numOfRuns_for_eachParticle = 1;//3; //  Using multiple runs makes sense because the model is stochastic
			double[] eastMovPredictedTime = new double[PF_configData.numberofparticles*numOfRuns_for_eachParticle];
			double[] westMovPredictedTime = new double[PF_configData.numberofparticles*numOfRuns_for_eachParticle];

			//read particle data 
			String particle_Step_Data = PF_configData.DataPathName+"run0_step_"+i+".txt"; 
			try{
				FileInputStream MyInputStream = new FileInputStream(particle_Step_Data);
				TextReader input;
				input = new TextReader(MyInputStream);

				//skip the first line
				input.readLine();

				for(int idx=0;idx<PF_configData.numberofparticles;idx++){
					//System.out.println("read data for step "+i);
					input.readInt(); // skip the index
					String tfState = input.readWord(); 
					double phaseSigma = input.readDouble();
					double elaspedGreenTime= input.readDouble();
					int westSideQueueSize = input.readInt();
					int eastSideQueueSize = input.readInt();
					double carPassingTime_mean = input.readDouble();
					String aa=input.readLine(); // skip the rest of the line

					//////////////////////////////////////////////////////////

					for(int runIdx=0; runIdx<numOfRuns_for_eachParticle; runIdx++) {
						//create the simulation model for prediction
						trafficControlSys_RTPrediction model = new trafficControlSys_RTPrediction(
								"Model"+idx, tfState, phaseSigma, elaspedGreenTime, 
								westSideQueueSize, eastSideQueueSize, carPassingTime_mean, RTPredictionRand); 

						// --------------- run simulation -----------------------------     	
						coordinator temp_c = new coordinator(model);
						temp_c.initialize();
						temp_c.simulate(100000); // The 100000 is an arbitrarily large number. The simulation stops when all models becomes passive

						// get prediction resutls 
						eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx] = model.trand.eastMovingCar_processingTime;
						westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx] = model.trand.westMovingCar_processingTime;
						eastMovWaitingTimeSum+=eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];
						westMovWaitingTimeSum+=westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];			

						if(eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx]>eastMovWaitingTimeMax)
							eastMovWaitingTimeMax=eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];
						if(eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx]<eastMovWaitingTimeMin)
							eastMovWaitingTimeMin=eastMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];
						if(westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx]>westMovWaitingTimeMax)
							westMovWaitingTimeMax=westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];
						if(westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx]<westMovWaitingTimeMin)
							westMovWaitingTimeMin=westMovPredictedTime[idx*numOfRuns_for_eachParticle+runIdx];
					}
				}
				double eastMovWaitingTimeAve = eastMovWaitingTimeSum/(PF_configData.numberofparticles*numOfRuns_for_eachParticle);
				double westMovWaitingTimeAve = westMovWaitingTimeSum/(PF_configData.numberofparticles*numOfRuns_for_eachParticle);
				
				double westMovWT_std, westMovWT_std_sum=0, eastMovWT_std, eastMovWT_std_sum=0;
				for(int idx=0; idx<(PF_configData.numberofparticles*numOfRuns_for_eachParticle);idx++) {
					westMovWT_std_sum+= (westMovPredictedTime[idx]-westMovWaitingTimeAve)*(westMovPredictedTime[idx]-westMovWaitingTimeAve);
					eastMovWT_std_sum+= (eastMovPredictedTime[idx]-eastMovWaitingTimeAve)*(eastMovPredictedTime[idx]-eastMovWaitingTimeAve);
				}
				westMovWT_std = Math.sqrt(westMovWT_std_sum/(PF_configData.numberofparticles*numOfRuns_for_eachParticle));
				eastMovWT_std = Math.sqrt(eastMovWT_std_sum/(PF_configData.numberofparticles*numOfRuns_for_eachParticle));
				
//				RTPeastMovWaitingTime[i] = eastMovWaitingTimeAve;
//				RTPwestMovWaitingTime[i] = westMovWaitingTimeAve;
				
				RT_PredictionResult.println(i+"\t"+(i+1)*PF_configData.stepInterval  // the time for step i is (i+1)*PF_configData.stepInterval
						+"\t"+eastMovWaitingTimeAve
						+"\t"+eastMovWaitingTimeMax+"\t"+eastMovWaitingTimeMin+"\t"+eastMovWT_std
						+"\t"+westMovWaitingTimeAve
						+"\t"+westMovWaitingTimeMax+"\t"+westMovWaitingTimeMin+"\t"+westMovWT_std
						);
			}
			catch (IOException e){
				throw new RuntimeException(e.toString());
			}
		}
		
		
//		double[] eastMovingX = new double[1000];
//		double[] eastMovingY = new double[1000];
//		int eastMovingDataNum = 0;
		// Read the real eastMoving waiting data and save it in the RT_PredictionResult file so that the results can be compared
		String eastMoving_output = PF_configData.DataPathName+"eastMoving_output.txt"; 
		try{
			FileInputStream MyInputStream = new FileInputStream(eastMoving_output);
			TextReader input;
			input = new TextReader(MyInputStream);

			//skip the first line
			input.readLine();
			
			while (input.ready()) {
				input.readDouble(); // skip the arriving time
				input.readWord();// skip the event name
				input.readInt();// skip the event value "1"
				double realArrivintTime = input.readDouble();
				double realWaitingTime = input.readDouble(); 
				
//				eastMovingX[eastMovingDataNum] = realArrivintTime;
//				eastMovingY[eastMovingDataNum] = realWaitingTime;	
//				eastMovingDataNum++;
				
				if(realArrivintTime>=PF_configData.numberofsteps * PF_configData.stepInterval)
					break;
				
				RT_PredictionResult.println("\t"
				+realArrivintTime
				+"\t"+"\t"
				+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"
				+"\t"+realWaitingTime
				);
			}
		}
		catch (IOException e){
			throw new RuntimeException(e.toString());
		}
		
		// Read the real westMoving waiting data and save it in the RT_PredictionResult file so that the results can be compared
//		double[] westMovingX = new double[1000];
//		double[] westMovingY = new double[1000];
//		int westMovingDataNum = 0;
		String westMoving_output = PF_configData.DataPathName+"westMoving_output.txt"; 
		try{
			FileInputStream MyInputStream = new FileInputStream(westMoving_output);
			TextReader input;
			input = new TextReader(MyInputStream);

			//skip the first line
			input.readLine();
			
			while (input.ready()) {
				input.readDouble(); // skip the arriving time
				input.readWord();// skip the event name
				input.readInt();// skip the event value "1"
				double realArrivintTime = input.readDouble();
				double realWaitingTime = input.readDouble(); 
				
//				westMovingX[eastMovingDataNum] = realArrivintTime;
//				westMovingY[eastMovingDataNum] = realWaitingTime;	
//				westMovingDataNum++;

				if(realArrivintTime>=PF_configData.numberofsteps * PF_configData.stepInterval)
					break;
				
				RT_PredictionResult.println("\t"
				+realArrivintTime
				+"\t"+"\t"+"\t"
				+"\t"+"\t"+"\t"+"\t"+"\t"+"\t"
				+"\t"+realWaitingTime
				);
			}
		}
		catch (IOException e){
			throw new RuntimeException(e.toString());
		}
		
//		///////////////////////////////////////////////////////////////////////////
//		// calculate the actual delay time based on actual data points
//		double[] actualEastMovDelayTime = new double[PF_configData.numberofsteps];
//		int dataIdx_before = 0;
//		int dataIdx_after = 0;
//		for (int i=0; i< PF_configData.numberofsteps; i++) {
//			double DATime = (i+1)*PF_configData.stepInterval;
//			
//			// search the eastMovingX[] to find the right data points
//			for(int j=dataIdx_after; j<eastMovingDataNum;j++) {
//				if(eastMovingX[j]==DATime) {
//					actualEastMovDelayTime[i] = eastMovingY[j];
//					dataIdx_after = j;
//				}
//				else if(eastMovingX[j]<DATime && eastMovingX[j+1]>DATime) {
//					dataIdx_before = j;
//					dataIdx_after = j+1;
//					//compute the actualDelayTime
//					double ratio = (eastMovingY[dataIdx_after]-eastMovingY[dataIdx_before])/(eastMovingX[dataIdx_after]-eastMovingX[dataIdx_before]);
//					actualEastMovDelayTime[i] = eastMovingY[dataIdx_before]+ratio*(DATime-eastMovingX[dataIdx_before]);
//				}
//				else
//					continue;
//			}
//		}
//		
//		double[] actualWestMovDelayTime = new double[PF_configData.numberofsteps];
//		dataIdx_before = 0;
//		dataIdx_after = 0;
//		for (int i=0; i< PF_configData.numberofsteps; i++) {
//			double DATime = (i+1)*PF_configData.stepInterval;
//			
//			// search the eastMovingX[] to find the right data points
//			for(int j=dataIdx_after; j<westMovingDataNum;j++) {
//				if(westMovingX[j]==DATime) {
//					actualWestMovDelayTime[i] = westMovingY[j];
//					dataIdx_after = j;
//				}
//				else if(westMovingX[j]<DATime && westMovingX[j+1]>DATime) {
//					dataIdx_before = j;
//					dataIdx_after = j+1;
//					//compute the actualDelayTime
//					double ratio = (westMovingY[dataIdx_after]-westMovingY[dataIdx_before])/(westMovingX[dataIdx_after]-westMovingX[dataIdx_before]);
//					actualWestMovDelayTime[i] = westMovingY[dataIdx_before]+ratio*(DATime-westMovingX[dataIdx_before]);
//				}
//				else
//					continue;
//			}
//		}
//		
//		PrintWriter RT_PredictionResult2=null;
//		try{
//			RT_PredictionResult2 = new PrintWriter(new FileOutputStream(PF_configData.DataPathName+"RTPredictionResult2.txt"), true);
//		}
//		catch (FileNotFoundException e) {System.out.println("File creation error!!!!!");}
//
//		RT_PredictionResult2.println("step"+"\t"+"ArrivingTime"
//				+"\t"+"RTPred_eastMovWaitTime"
//				+"\t"+"actual_eastMovWaitingTime"
////				+"\t"+"RTPred_eastMovWaitTime"
////				+"\t"+"actual_eastMovWaitingTime"
//				);		
//		
////		int numberOfAccurateSteps=0;
////		double error=10000, errorPct=100000;
////		for (int i=0; i< PF_configData.numberofsteps; i++) {
////			int accurate = 0;
////			if(i>4) {
////				error = Math.abs(RTPeastMovWaitingTime[i]-actualEastMovDelayTime[i]);
////				errorPct = error/actualEastMovDelayTime[i];
////				if(error <20) {
////					accurate=1;
////					numberOfAccurateSteps++;
////				}
////				else if(errorPct<0.2) {
////					accurate=1;
////					numberOfAccurateSteps++;					
////				}					
////			}
//
//			int numberOfAccurateSteps=0;
//			double error=10000, errorPct=100000;
//			for (int i=0; i< PF_configData.numberofsteps; i++) {
//				int accurate = 0;
//				if(i>4) {
//					error = Math.abs(RTPwestMovWaitingTime[i]-actualWestMovDelayTime[i]);
//					errorPct = error/actualWestMovDelayTime[i];
//					if(error <20) {
//						accurate=1;
//						numberOfAccurateSteps++;
//					}
//					else if(errorPct<0.2) {
//						accurate=1;
//						numberOfAccurateSteps++;					
//					}					
//				}
//
//			RT_PredictionResult2.println(i+"\t"+(i+1)*PF_configData.stepInterval  // the time for step i is (i+1)*PF_configData.stepInterval
////					+"\t"+RTPeastMovWaitingTime[i]
////					+"\t"+actualEastMovDelayTime[i]
//					+"\t"+RTPwestMovWaitingTime[i]
//					+"\t"+actualWestMovDelayTime[i]
//					+"\t"+error
//					+"\t"+errorPct
//					+"\t"+accurate*10
//					+"\t"+numberOfAccurateSteps
//			);
//		}		
		
		System.out.println("Finish prediction and save data ");
	}
	
	public static void main(String[] argc) {
		RTPrediction_fromParticleData rtp = new RTPrediction_fromParticleData();
		rtp.RT_Prediction();
	}
	
}
