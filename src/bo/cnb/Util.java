package bo.cnb;

import java.util.ArrayList;
import java.util.Random;

public class Util {
	
	/**
	 * generate percent*totalNum random numbers:
	 * e.g. to generate 500 random numbers between 0 and 1000 
	 * call generateRandomNumbers(0.5,1000)
	 * @param percent
	 * @param totalNum
	 * @return arraylist<Integer>
	 */
	public static ArrayList<Integer> generateRandomNumbers(double percent, int totalNum) {
		
		int count = (int)(percent*totalNum);		
		
		ArrayList<Integer> list = new ArrayList<Integer>(); //save the random numbers
                
        //int n = 10;
        Random rand = new Random();
        boolean[] bool = new boolean[totalNum];
         
        int num =0;
         
        for (int i = 0; i<count; i++){
                  
            do{
                //如果产生的数相同继续循环
                num = rand.nextInt(totalNum);    
              
            }while(bool[num]);
             
            bool[num] =true;
             
            list.add(num);        
         
        }             
		
		return list;				
	}
	
	public static double LOG2(double x) {
		return Math.log(x)/Math.log(2);
	}
	
	/**
	 * 
	 * @param word
	 * @param ppAL
	 * @return frequency f
	 * if f==0 then we need to Convert It to 1 to fit the function of 
	 * argmax(logP(ThetaC) + SUM(fi*logThetaCI)) 
	 * Here ThetaCI is the "f"
	 */
	public static double getFrequencyOfSpecificWord(String word, ArrayList<PairPercent> ppAL) {

		double f = 0;
		for(int i=0; i<ppAL.size(); i++) {
			if(ppAL.get(i).getWord().equals(word)) {
				
				f= ppAL.get(i).getFrequency();
				break;
			}
		}
		
		return f;
		
	}
	
	
	
	

}
