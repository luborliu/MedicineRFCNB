package bo.cnb;

import java.util.ArrayList;

public class FrequencyComputation {
	
	//貌似用
	public static ArrayList<Pair> getFrequencies(ArrayList<String> wordsList,
			ArrayList<ArrayList<Integer>> frequenciesInEachDoc) {
		
		ArrayList<Pair> pairAL = new ArrayList<Pair>();
		
		for(int i=0; i<wordsList.size(); i++) {
			Pair p = new Pair();
			p.setWord(wordsList.get(i));
			pairAL.add(p);
		}
		
		for(int i=0; i<frequenciesInEachDoc.size(); i++) {
			for(int j=0; j<frequenciesInEachDoc.get(i).size(); j++) {
				if(frequenciesInEachDoc.get(i).get(j)!=0) {				
					pairAL.get(j).setFrequency(
							pairAL.get(j).getFrequency()+frequenciesInEachDoc.get(i).get(j));
					
				}
			}
		}				
		return pairAL;		
	}
	
	/**
	 * Used to get the appearance of the words, that is, 
	 * if there exists the word(no matter how many in one document), we will get 1
	 * else, we will get 0 
	 * then get the (#of appearance)/(#of documents) which is always <1 
	 * @param wordsList
	 * @param frequenciesInEachDoc
	 * @return
	 */
	public static ArrayList<PairPercent> getFrequenciesBoolean(ArrayList<String> wordsList,
			ArrayList<ArrayList<Integer>> frequenciesInEachDoc) {
		
		ArrayList<Pair> pairAL = new ArrayList<Pair>();
		
		ArrayList<PairPercent> ppAL = new ArrayList<PairPercent>();
		
		for(int i=0; i<wordsList.size(); i++) {
			Pair p = new Pair();
			
			p.setWord(wordsList.get(i));
			
			pairAL.add(p);
		}
		
		for(int i=0; i<frequenciesInEachDoc.size(); i++) {
			for(int j=0; j<frequenciesInEachDoc.get(i).size(); j++) {
				if(frequenciesInEachDoc.get(i).get(j)!=0) {				
					pairAL.get(j).setFrequency(
							pairAL.get(j).getFrequency()+1);
					
				}
			}
		}	
		
		for(int i=0; i<pairAL.size(); i++) {
			PairPercent pp =convertPairToPercent(pairAL.get(i), frequenciesInEachDoc.size()); 
			ppAL.add(pp);
		}
		return ppAL;	
			
	}
	
		
    /**
	 * get the frequencies for every words in each document 
	 * (total#of the word of the class)
	 * ----------------------------------
	 * (total # of the documents of the class) 
	 * @param wordsList
	 * @param frequenciesInEachDoc
	 * @return
	 */
	public static ArrayList<PairPercent> getFrequenciesEachDocument(ArrayList<String> wordsList,
				ArrayList<ArrayList<Integer>> frequenciesInEachDoc) {		
		ArrayList<Pair> pairAL = new ArrayList<Pair>();
		
		ArrayList<PairPercent> ppAL = new ArrayList<PairPercent>();
		
		for(int i=0; i<wordsList.size(); i++) {
			Pair p = new Pair();
			
			p.setWord(wordsList.get(i));
			
			pairAL.add(p);
		}
		
		for(int i=0; i<frequenciesInEachDoc.size(); i++) {
			for(int j=0; j<frequenciesInEachDoc.get(i).size(); j++) {
				if(frequenciesInEachDoc.get(i).get(j)!=0) {				
					pairAL.get(j).setFrequency(
							pairAL.get(j).getFrequency()+frequenciesInEachDoc.get(i).get(j));
					
				}
			}
		}	
		
		for(int i=0; i<pairAL.size(); i++) {
			PairPercent pp =convertPairToPercent(pairAL.get(i), frequenciesInEachDoc.size()); 
			ppAL.add(pp);
		}
		return ppAL;	
		
		
	}
	/**
	 * (total# of the word i in the class) +ai
	 * ---------------------------------------
	 * (total# of the words in the class) + a
	 * 
	 *   Nci+ai
	 * ----------  
	 *   Nc + a
	 *   This is the (theta ci) in MNB
	 * @param wordsList
	 * @param frequenciesInEachDoc
	 * @return
	 */
	public static ArrayList<PairPercent> getFrequenciesThetaCIEachClass(ArrayList<String> wordsList,
				ArrayList<ArrayList<Integer>> frequenciesInEachDoc) {

		
		ArrayList<Pair> pairAL = new ArrayList<Pair>();
		
		ArrayList<PairPercent> ppAL = new ArrayList<PairPercent>();
		
		for(int i=0; i<wordsList.size(); i++) {
			Pair p = new Pair();
			
			p.setWord(wordsList.get(i));
			
			pairAL.add(p);
		}
		
		for(int i=0; i<frequenciesInEachDoc.size(); i++) {
			for(int j=0; j<frequenciesInEachDoc.get(i).size(); j++) {
				if(frequenciesInEachDoc.get(i).get(j)!=0) {				
					pairAL.get(j).setFrequency(
							pairAL.get(j).getFrequency()+frequenciesInEachDoc.get(i).get(j));					
				}
			}
		}	
		
		//compute ai and a
		//ai = 1
		//a = sum of ai, that is, pariAL.size() the number of the different words
		
		int ai = 1;
		int a = pairAL.size();
		int Nc = 0;
		for(int i=0; i<pairAL.size(); i++) {
			Nc = Nc+pairAL.get(i).getFrequency();
		}
		for(int i=0; i<pairAL.size(); i++) {
			PairPercent pp = new PairPercent();
			pp.setWord(pairAL.get(i).getWord());
			pp.setFrequency(((double)(pairAL.get(i).getFrequency())+(double)ai)/
					(double)(Nc+a));
			
			ppAL.add(pp);
		}
		
		
		return ppAL;	
		
		
	}
	
	/**
	 * (total# of the word i in the class) +ai
	 * ---------------------------------------
	 * (total# of the words in the class) + a
	 * 
	 *   Nci+ai		  Oci+ai
	 * ----------  *------------
	 *   Nc + a		 #doc + a
	 *   This is the (theta ci) in MNB
	 * @param wordsList
	 * @param frequenciesInEachDoc
	 * @return
	 */
	public static ArrayList<PairPercent> getFrequenciesThetaCIEachClassPlusOccurance(ArrayList<String> wordsList,
				ArrayList<ArrayList<Integer>> frequenciesInEachDoc) {

		
		ArrayList<Pair> pairAL = new ArrayList<Pair>();
		ArrayList<Pair> pairOcAL = new ArrayList<Pair>();//bingbing用来存我的那个想法 就是那个Oci
		
		ArrayList<PairPercent> ppAL = new ArrayList<PairPercent>();
		
		for(int i=0; i<wordsList.size(); i++) {
			Pair p = new Pair();
			
			p.setWord(wordsList.get(i));
									
			pairAL.add(p);
			pairOcAL.add(p);
			
		}
		
		for(int i=0; i<frequenciesInEachDoc.size(); i++) {
			for(int j=0; j<frequenciesInEachDoc.get(i).size(); j++) {
				if(frequenciesInEachDoc.get(i).get(j)!=0) {				
					pairAL.get(j).setFrequency(
							pairAL.get(j).getFrequency()+frequenciesInEachDoc.get(i).get(j));
					pairOcAL.get(j).setFrequency(
							pairOcAL.get(j).getFrequency()+1);
				}
			}
		}	
		
		//compute ai and a
		//ai = 1
		//a = sum of ai, that is, pariAL.size() the number of the different words
		
		int ai = 1;
		int a = pairAL.size();
		int Nc = 0;
		
		//int Oc = 0;
		for(int i=0; i<pairAL.size(); i++) {
			Nc = Nc+pairAL.get(i).getFrequency();
			
		}
		for(int i=0; i<pairAL.size(); i++) {
			PairPercent pp = new PairPercent();
			pp.setWord(pairAL.get(i).getWord());
			pp.setFrequency((((double)(pairAL.get(i).getFrequency())+(double)ai)/
					(double)(Nc+a))*((((double)(pairOcAL.get(i).getFrequency())+(double)ai))/
					(double)(frequenciesInEachDoc.size()+a)));
			//一会检查下 应该没错吧
			ppAL.add(pp);
		}
		
		
		return ppAL;	
		
		
	}
	
	
	private static PairPercent convertPairToPercent(Pair p, int docNum) {
		
		double f = (double)(p.getFrequency())/(double)docNum;
		
		PairPercent pp = new PairPercent();
		pp.setWord(p.getWord());		
		pp.setFrequency(f);
		
		return pp;
		
	}

}
