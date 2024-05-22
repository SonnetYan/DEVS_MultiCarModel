package DDDS_2024_MultiCarModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class RTPrediction_ResultAnalysis {

	PrintWriter RT_PResultAnalysis;
	
	public RTPrediction_ResultAnalysis() {
	}
	
	public void RT_Prediction_Analysis() {
		
		try{
			RT_PResultAnalysis = new PrintWriter(new FileOutputStream(PF_configData.DataPathName+"RTPResultAnalysis.txt"), true);
		}
		catch (FileNotFoundException e) {System.out.println("File creation error!!!!!");}

		RT_PResultAnalysis.println("step"+"\t"+"ArrivingTime"
				+"\t"+"RTPred_eastMovWaitTime"
				+"\t"+"actual_eastMovWaitingTime"
				+"\t"+"error_eastMov"
				+"\t"+"errorPct_eastMov"
				+"\t"+"accurate_eastMov"
				+"\t"+"numberOfAccurateSteps_eastMov"
				+"\t"+"RTPWestMovWaitingTime"
				+"\t"+"actualWestMovDelayTime"
				+"\t"+"error_westMov"
				+"\t"+"errorPct_westMov"
				+"\t"+"accurate_westMov"
				+"\t"+"numberOfAccurateSteps_westMov"
				);
				
		// read the prediction results
		double[] RTPEastMovWaitingTime = new double[PF_configData.numberofsteps];
		double[] RTPWestMovWaitingTime = new double[PF_configData.numberofsteps];
		String predictionResult_output = PF_configData.DataPathName+"RTPredictionResult.txt"; 
		try{
			FileInputStream MyInputStream = new FileInputStream(predictionResult_output);
			TextReader input;
			input = new TextReader(MyInputStream);

			//skip the first line
			input.readLine();
			
			for(int i=0;i<PF_configData.numberofsteps;i++) {
				input.readInt();// skip the step value
				input.readDouble();// skip the time
				RTPEastMovWaitingTime[i]=input.readDouble();
				input.readDouble();// skip the value
				input.readDouble();// skip the value
				input.readDouble();// skip the value
				RTPWestMovWaitingTime[i]=input.readDouble();
				input.readLine();// skip the rest of the line
			}
		}
		catch (IOException e){
			throw new RuntimeException(e.toString());
		}
		
		// Read the real eastMoving waiting data and save it in the RT_PredictionResult file so that the results can be compared
		double[] eastMovingX = new double[1000];
		double[] eastMovingY = new double[1000];
		int eastMovingDataNum = 0;
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
				
				eastMovingX[eastMovingDataNum] = realArrivintTime;
				eastMovingY[eastMovingDataNum] = realWaitingTime;	
				eastMovingDataNum++;
				
				if(realArrivintTime>=PF_configData.numberofsteps * PF_configData.stepInterval)
					break;				
			}
		}
		catch (IOException e){
			throw new RuntimeException(e.toString());
		}
		
		// Read the real westMoving waiting data and save it in the RT_PredictionResult file so that the results can be compared
		double[] westMovingX = new double[1000];
		double[] westMovingY = new double[1000];
		int westMovingDataNum = 0;
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
				
				westMovingX[westMovingDataNum] = realArrivintTime;
				westMovingY[westMovingDataNum] = realWaitingTime;	
				westMovingDataNum++;

				if(realArrivintTime>=PF_configData.numberofsteps * PF_configData.stepInterval)
					break;
			}
		}
		catch (IOException e){
			throw new RuntimeException(e.toString());
		}
		
		///////////////////////////////////////////////////////////////////////////
		// calculate the actual delay time based on actual data points
		double[] actualEastMovDelayTime = new double[PF_configData.numberofsteps];
		int dataIdx_before = 0;
		int dataIdx_after = 0;
		for (int i=0; i< PF_configData.numberofsteps; i++) {
			double DATime = (i+1)*PF_configData.stepInterval;
			
			// search the eastMovingX[] to find the right data points
			for(int j=dataIdx_after; j<eastMovingDataNum;j++) {
				if(eastMovingX[j]==DATime) {
					actualEastMovDelayTime[i] = eastMovingY[j];
					dataIdx_after = j;
				}
				else if(eastMovingX[j]<DATime && eastMovingX[j+1]>DATime) {
					dataIdx_before = j;
					dataIdx_after = j+1;
					//compute the actualDelayTime
					double ratio = (eastMovingY[dataIdx_after]-eastMovingY[dataIdx_before])/(eastMovingX[dataIdx_after]-eastMovingX[dataIdx_before]);
					actualEastMovDelayTime[i] = eastMovingY[dataIdx_before]+ratio*(DATime-eastMovingX[dataIdx_before]);
				}
				else
					continue;
			}
		}
		
		// calculate the actual delay time based on actual data points
		double[] actualWestMovDelayTime = new double[PF_configData.numberofsteps];
		dataIdx_before = 0;
		dataIdx_after = 0;
		for (int i=0; i< PF_configData.numberofsteps; i++) {
			double DATime = (i+1)*PF_configData.stepInterval;
			
			// search the eastMovingX[] to find the right data points
			for(int j=dataIdx_after; j<westMovingDataNum;j++) {
				if(westMovingX[j]==DATime) {
					actualWestMovDelayTime[i] = westMovingY[j];
					dataIdx_after = j;
				}
				else if(westMovingX[j]<DATime && westMovingX[j+1]>DATime) {
					dataIdx_before = j;
					dataIdx_after = j+1;
					//compute the actualDelayTime
					double ratio = (westMovingY[dataIdx_after]-westMovingY[dataIdx_before])/(westMovingX[dataIdx_after]-westMovingX[dataIdx_before]);
					actualWestMovDelayTime[i] = westMovingY[dataIdx_before]+ratio*(DATime-westMovingX[dataIdx_before]);
				}
				else
					continue;
			}
		}
		
		double MAE_threshold = 30;//20.0;
		double MAPE_threshold = 0;//0.2;//0.2; 
		int numberOfAccurateSteps_eastMov=0;
		int numberOfAccurateSteps_westMov=0;
		double error_eastMov=10000, errorPct_eastMov=100000;
		double error_westMov=10000, errorPct_westMov=100000;
		for (int i=0; i< PF_configData.numberofsteps; i++) {
			int accurate_eastMov = 0;
			if(i>4) {
				error_eastMov = Math.abs(RTPEastMovWaitingTime[i]-actualEastMovDelayTime[i]);
				errorPct_eastMov = error_eastMov/actualEastMovDelayTime[i];
				if(error_eastMov <=MAE_threshold) {
					accurate_eastMov=1;
					numberOfAccurateSteps_eastMov++;
				}
				else if(errorPct_eastMov<=MAPE_threshold) {
					accurate_eastMov=1;
					numberOfAccurateSteps_eastMov++;					
				}					
			}

			int accurate_westMov = 0;
			if(i>4) {
				error_westMov = Math.abs(RTPWestMovWaitingTime[i]-actualWestMovDelayTime[i]);
				errorPct_westMov = error_westMov/actualWestMovDelayTime[i];
				if(error_westMov <=MAE_threshold) {
					accurate_westMov=1;
					numberOfAccurateSteps_westMov++;
				}
				else if(errorPct_westMov<=MAPE_threshold) {
					accurate_westMov=1;
					numberOfAccurateSteps_westMov++;					
				}					
			}

			RT_PResultAnalysis.println(i+"\t"+(i+1)*PF_configData.stepInterval  // the time for step i is (i+1)*PF_configData.stepInterval
					+"\t"+RTPEastMovWaitingTime[i]
					+"\t"+actualEastMovDelayTime[i]
					+"\t"+error_eastMov
					+"\t"+errorPct_eastMov
					+"\t"+accurate_eastMov*10
					+"\t"+numberOfAccurateSteps_eastMov
					+"\t"+RTPWestMovWaitingTime[i]
					+"\t"+actualWestMovDelayTime[i]
					+"\t"+error_westMov
					+"\t"+errorPct_westMov
					+"\t"+accurate_westMov*10
					+"\t"+numberOfAccurateSteps_westMov
			);
		}		
		
		System.out.println("Finish prediction and save data "+ numberOfAccurateSteps_eastMov +" _ "+numberOfAccurateSteps_westMov);
	}
	
	public static void main(String[] argc) {
		RTPrediction_ResultAnalysis rtp = new RTPrediction_ResultAnalysis();
		rtp.RT_Prediction_Analysis();
	}
	
}
