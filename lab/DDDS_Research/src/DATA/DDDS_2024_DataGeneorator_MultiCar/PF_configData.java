package DATA.DDDS_2024_DataGeneorator_MultiCar;

public class PF_configData {

	public static int numberofsteps = 200;//80;
	public static double stepInterval = 30; //60;  // 18 seconds

	public static int numberofparticles = 2000;//6000;//1000;
	
	public static int currentStepIndex = 0;///
	public static int currentParticleIndex = 0;
	public static double currTime = 0;

	public static String DataPathName = "DATA/Batch/WSC/";
	public static String observedDataPathName = "DATA/Batch/WSC/";
	public static String realSensorDataFileName = "observationData.txt";
	
	public static long PF_globalRandSeed = 673267;//444;//
	
	public static long RTPrediction_randSeed = 66666;
	
}







