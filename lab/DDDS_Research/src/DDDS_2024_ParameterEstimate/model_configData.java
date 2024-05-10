package DDDS_2024_ParameterEstimate;

public interface model_configData {
		
	double pf_maxElapseTimeWhenBusy_E2W = 120; // Add this line
    double pf_maxElapseTimeWhenBusy_W2E = 120; // Add this line
	double pf_maxElapseTimeWhenBusy_oneDirection = 120; // Add this line

	double pf_carPassingTime_mean = 4.0;


	double carPassingTime_rangeDelta = 1.0;//2.0; // Pluse minus 0.1 from mean
	double carPassingTime_LowBound = 2.0;//0.15;//1.0; // 1 second ---- this is used if treating car passing as a truncated normal distribution between low and high
	double carPassingTime_UpBound = 10.0;//0.35;//3.0; // 5 seconds ---- this is used if treating car passing as a truncated normal distribution between low and high



	double lightSwitchTime_LowBound = 10.0;//0.15;//1.0; // 1 second ---- this is used if treating car passing as a truncated normal distribution between low and high
	double lightSwitchTime_UpBound = 200.0;//0.35;//3.0; // 5 seconds ---- this is used if treating car passing as a truncated normal distribution between low and high
	double weightSigma_constant = 1.0;//0.8;  //Resampling weight adjust

	double carPassingTime_sigma = 0.1;//0.1;//1.0; //  ---- this is used if treating car passing as a truncated normal distribution between low and high
	double lightSwitchTime_sigma = 3;//0.1;//1.0; //  ---- this is used if treating car passing as a truncated normal distribution between low and high
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
	public static double generator_eastMoving_lambda = 1.0/9.0;//1.0/8.0;// 0.3/2;
	public static double generator_westMoving_lambda = 1.0/7.0;  // 1 vehicle per 12 seconds



	/**
	 * Case 1 random number seed
	 */
//	long randSeed_eastMoving = 8123997;
//	long randSeed_westMoving = 512799;
//	long randSeed_IntersectionM = 3987979;

	/**
	 * Case 2 random number seed
	 */
//	long randSeed_eastMoving = 21111;
//	long randSeed_westMoving = 222;
//	long randSeed_IntersectionM = 333;
	
	long randSeed_eastMoving = 66778;
	long randSeed_westMoving = 43224;
	long randSeed_IntersectionM = 333;
}







