package DDDS_2024_DataGeneorator;

public class model_configData {
	
	public static int run = 0;  //don't change this one
	public static int total_run = 1;


	public static double carPassingTime_mean = 4.0;//2.0;//2.0; //1.5;// ---- this is used if treating car passing as a truncated normal distribution between low and high
	public static double carPassingTime_sigma = 0.1;//0.1;//1.0; //  ---- this is used if treating car passing as a truncated normal distribution between low and high
	public static double carPassingTime_rangeDelta = 1.0;//2.0; // Pluse minus 0.1 from mean
	public static double carPassingTime_LowBound = 2.0;//0.15;//1.0; // 1 second ---- this is used if treating car passing as a truncated normal distribution between low and high
	public static double carPassingTime_UpBound = 10.0;//0.35;//3.0; // 5 seconds ---- this is used if treating car passing as a truncated normal distribution between low and high

    /**
     * For street A generator -- for greenTime_streetA=36, greenTime_streetB=24
     * lambda = 1/7.0 ----> queue increases over time but not out of control
     * lambda = 1/8.0 ----> queue seems to be balanced 
     * lambda = 1/10.0 ----> queue is empty frequently
     *  
     * For street B generator
     * lambda = 1/11.0 ----> queue seems to be balanced 
     *  
     */

	public static double maxElapseTimeWhenBusy_E2W = 120;//west moving
	public static double maxElapseTimeWhenBusy_W2E = 80; //east MOving

	public static double generator_eastMoving_lambda = 1.0/9.0;//1.0/8.0;// 0.3/2; //W2E
	public static double generator_westMoving_lambda = 1.0/7.0;  // 1 vehicle per 12 seconds //E2W

	/**
	 * Case 1 random number seed  
	 */
//	public static long randSeed_eastMoving = 21111;
//	public static long randSeed_westMoving = 222;
//	public static long randSeed_IntersectionM = 333;

	/**
	 * Case 2 random number seed
	 */

	public static long randSeed_eastMoving = 67676;
	public static long randSeed_westMoving = 45454;
	public static long randSeed_IntersectionM = 666;

	/**
	 * Case 3 random number seed
	 */
//	public static  long randSeed_eastMoving = 12123;
//	public static  long randSeed_westMoving = 65121;
//	public static  long randSeed_IntersectionM = 778;

}








