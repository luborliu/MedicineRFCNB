package bo.cnb;

import java.util.ArrayList;

public class CMNB {
	
	private ArrayList<PairPercent> wordFreqClassI;
	private ArrayList<PairPercent> wordFreqClassE;
	private double probThetaI;
	private double probThetaE;
	
	
	
	public CMNB() {
		wordFreqClassE = new ArrayList<PairPercent>();
		wordFreqClassI = new ArrayList<PairPercent>();	
		probThetaI = 0;
		probThetaE = 0;
	}
	
	public CMNB(ArrayList<PairPercent> wordFreqI, ArrayList<PairPercent> wordFreqE, 
			double probThetaI, double probThetaE) {
		this.wordFreqClassE = wordFreqE;
		this.wordFreqClassI = wordFreqI;	
		this.probThetaE = probThetaE;
		this.probThetaI = probThetaI;
				
	}
	
	
	/**
	 * Given a document with paris of word and the word's frequency(int)
	 * we can use this method to predict the label of the document E/I
	 * @param docToPredict
	 * @return
	 */
	public String predictWithCMNB(ArrayList<Pair> docToPredict) {
		
		double likelihoodI = computeLikelihoodCMNB(docToPredict, "I");
		double likelihoodE = computeLikelihoodCMNB(docToPredict, "E");
		String result = null;
		if(likelihoodI<=likelihoodE) {
			result = "\"E\"";
		} else {
			result = "\"I\"";
		}
		return result;		
	}
	
	/**
	 * predict with fcmnb 
	 * Given a document with paris of word and the word's frequency(int)
	 * we can use this method to predict the label of the document E/I
	 * @param docToPredict
	 * @return
	 */
	public String predictWithFCMNB(ArrayList<Pair> docToPredict, double factor) {
		
		double likelihoodI = computeLikelihoodCMNB(docToPredict, "I");
		double likelihoodE = computeLikelihoodCMNB(docToPredict, "E");
		String result = null;
		if(likelihoodI<=factor*likelihoodE) {
			result = "\"E\"";
		} else {
			result = "\"I\"";
		}
		return result;		
	}
	
	/**
	 * Given a document with paris of word and the word's frequency(int)
	 * we can use this method to predict the label of the document E/I
	 * @param docToPredict
	 * @return
	 */
	public String predictWithMNB(ArrayList<Pair> docToPredict) {
		
		double likelihoodI = computeLikelihoodMNB(docToPredict, "I");
		double likelihoodE = computeLikelihoodMNB(docToPredict, "E");
		String result = null;
		if(likelihoodI<=likelihoodE) {
			result = "\"E\"";
		} else {
			result = "\"I\"";
		}
		return result;		
	}
	
	/**
	 * 预测document是相关还是不相关 based on mnb with factor
	 * @param docToPredict
	 * @param factor
	 * @return
	 */
	public String predictWithMNB(ArrayList<Pair> docToPredict, double factor) {
		
		double likelihoodI = computeLikelihoodMNB(docToPredict, "I");
		double likelihoodE = computeLikelihoodMNB(docToPredict, "E");
		String result = null;
		if(likelihoodI<=factor*likelihoodE) {
			result = "\"E\"";
		} else {
			result = "\"I\"";
		}
		return result;		
	}
	
	/**
	 * compute the likelihood of the two types- E I using complement class
	 * CMNB
	 * @param docToPredict the document to be predicted and 
	 * it is an arraylist with pairs of [ word and the word's corresponding frequency(int)]
	 * @param type String E or I
	 * @return
	 */
	private double computeLikelihoodCMNB(ArrayList<Pair> docToPredict, String type) {
		
		double likelihood = 0;
		
		if (type.equals("E")) {
			likelihood = Util.LOG2(probThetaE);
			//likelihood = 0;
			for(int i=0; i<docToPredict.size(); i++) {
				String word = docToPredict.get(i).getWord();
				double fi = (double)(docToPredict.get(i).getFrequency());
				double freqC = Util.getFrequencyOfSpecificWord(word, wordFreqClassI);
				if(freqC == 0) {
					freqC =1; // so that log(freq)==0, 
							 // in this way we can ignore the influence of the word that 
							 // does not even occur once in the traning data
				}
				if(fi!=0&&freqC!=1)
				likelihood = likelihood - fi*Util.LOG2(freqC);
			} 
			
		} else if(type.equals("I")) {
			likelihood = Util.LOG2(probThetaI);
			//likelihood = 0;
			for(int i=0; i<docToPredict.size(); i++) {
				String word = docToPredict.get(i).getWord();
				double fi = (double)(docToPredict.get(i).getFrequency());
				double freqC = Util.getFrequencyOfSpecificWord(word, wordFreqClassE);
				if(freqC == 0) {
					freqC =1; // so that log(freq)==0, 
							 // in this way we can ignore the influence of the word that 
							 // does not even occur once in the traning data
				}
				if(fi!=0&&freqC!=1)
				   likelihood = likelihood - fi*Util.LOG2(freqC);
			}
		}		  
		return likelihood;				
	}
	
	/**
	 * compute the likelihood of the two types- E I
	 * 
	 * @param docToPredict the document to be predicted and 
	 * it is an arraylist with pairs of [ word and the word's corresponding frequency(int)]
	 * @param type String E or I
	 * @return
	 */
	private double computeLikelihoodMNB(ArrayList<Pair> docToPredict, String type) {
		
		double likelihood = 0;
		
		if (type.equals("E")) {
			likelihood = Util.LOG2(probThetaE);
			for(int i=0; i<docToPredict.size(); i++) {
				String word = docToPredict.get(i).getWord();
				double fi = (double)(docToPredict.get(i).getFrequency());
				double freq = Util.getFrequencyOfSpecificWord(word, wordFreqClassE);
				if(freq == 0) {
					freq =1; // so that log(freq)==0, 
							 // in this way we can ignore the influence of the word that 
							 // does not even occur once in the traning data
				}
				
				likelihood = likelihood + fi*Util.LOG2(freq);
			} 
			
		} else if(type.equals("I")) {
			likelihood = Util.LOG2(probThetaI);
			for(int i=0; i<docToPredict.size(); i++) {
				String word = docToPredict.get(i).getWord();
				double fi = (double)(docToPredict.get(i).getFrequency());
				double freq = Util.getFrequencyOfSpecificWord(word, wordFreqClassI);
				if(freq == 0) {
					freq =1; // so that log(freq)==0, 
							 // in this way we can ignore the influence of the word that 
							 // does not even occur once in the traning data
				}
				
				likelihood = likelihood + fi*Util.LOG2(freq);
			}
		}		
		return likelihood;				
	}
	
	
	

	public ArrayList<PairPercent> getWordFreqClassI() {
		return wordFreqClassI;
	}

	public void setWordFreqClassI(ArrayList<PairPercent> wordFreqClassI) {
		this.wordFreqClassI = wordFreqClassI;
	}

	public ArrayList<PairPercent> getWordFreqClassE() {
		return wordFreqClassE;
	}

	public void setWordFreqClassE(ArrayList<PairPercent> wordFreqClassE) {
		this.wordFreqClassE = wordFreqClassE;
	}

	public double getProbThetaI() {
		return probThetaI;
	}

	public void setProbThetaI(double probThetaI) {
		this.probThetaI = probThetaI;
	}

	public double getProbThetaE() {
		return probThetaE;
	}

	public void setProbThetaE(double probThetaE) {
		this.probThetaE = probThetaE;
	}
	
	

}
