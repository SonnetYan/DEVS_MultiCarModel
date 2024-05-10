package DDDS_2024_ParameterEstimate;

public class PF_configData {

	public static int numberofsteps = 200;//80;
	public static double stepInterval = 30; //60;  // 18 seconds

	public static int numberofparticles = 5000;//6000;//1000;
	
	public static int currentStepIndex = 0;///
	public static int currentParticleIndex = 0;
	public static double currTime = 0;


	public static boolean generateParticleStep = false;

// if this parameter need to be estimated, set the flag be true
	public static boolean oneLightSwitchTimeFlag = false; // treat two direction as one parameter
	public static boolean estimate_oneLightSwitchTimeFlag = false; //estimate it as one or do not estimate it
	public static boolean lightSwitchTime_E2W_Flag = true;
	public static boolean lightSwitchTime_W2E_Flag = true;
	public static boolean carPassingTimeFlag = true;

//	public static String realName = "Real_5.0/";
//	public static String DataPathName = "DATA/"+realName+"/observeData/";//"chapter6Tutorial2/";
//	public static String stepResultPathName = "DATA/"+realName+"/stepResult_PE/";//"chapter6Tutorial2/";
//	public static String realSensorDataFileName = "observationData.txt";


	public static String DataPathName = "DATA/Batch/WSC/";
	public static String observedDataPathName = "DATA/Batch/WSC/";
	public static String realSensorDataFileName = "observationData.txt";

	public static String resultDataFileName= "DATA/Batch/WSC/";
	
//	public static long PF_globalRandSeed = 88788;//444;// ORGINAL ONE 5/2/2024

	public static long PF_globalRandSeed = 66774;//444;//
	
	public static long RTPrediction_randSeed = 66666;
	
}






