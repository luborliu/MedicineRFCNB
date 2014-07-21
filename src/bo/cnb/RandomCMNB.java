package bo.cnb;

import java.util.ArrayList;

public class RandomCMNB {
	
	private int number; // number of CMNBs
	private double featurePercent; // percenage of the features selected
	private ArrayList<CMNB> cmnbLists;
	
	private ArrayList<PairPercent> wordFreqClassIBeforeFeatureSelection;
	private ArrayList<PairPercent> wordFreqClassEBeforeFeatureSelection;
	private double probThetaI;
	private double probThetaE;
	
	//以下三个变量用来随机选择features 方便删除无用的features
	private ArrayList<ArrayList<Integer>> frequenciesInEachDocumentI;
	private ArrayList<ArrayList<Integer>> frequenciesInEachDocumentE;
	private ArrayList<String> wordsList;
	
	
	public RandomCMNB() {
	}
	/**
	public RandomCMNB(int number, double featurePercent, ArrayList<PairPercent> wordFreqClassI,
			ArrayList<PairPercent> wordFreqClassE, double probThetaI, double probThetaE) {
		this.number = number;
		this.featurePercent = featurePercent;
		this.cmnbLists = new ArrayList<CMNB>();
		this.wordFreqClassEBeforeFeatureSelection = wordFreqClassE;
		this.wordFreqClassIBeforeFeatureSelection = wordFreqClassI;
		this.probThetaE = probThetaE;
		this.probThetaI = probThetaI;
		
		//int featureNum = (int)(featurePercent*number);
		
		for(int i=0; i<number; i++) {
			ArrayList<Integer> randomNums = Util.generateRandomNumbers(1-featurePercent, wordFreqClassI.size());
			
			ArrayList<PairPercent> wordFreqClassIAfterFeatureSelection = new ArrayList<PairPercent>();
			ArrayList<PairPercent> wordFreqClassEAfterFeatureSelection = new ArrayList<PairPercent>();
			for(int m=0; m<wordFreqClassI.size();m++) {
				wordFreqClassEAfterFeatureSelection.add(wordFreqClassE.get(m));
				wordFreqClassIAfterFeatureSelection.add(wordFreqClassI.get(m));
			}
			
			for(int j=0;j<randomNums.size(); j++) {
				PairPercent pp = new PairPercent();
				pp.setWord(wordFreqClassI.get(randomNums.get(j)).getWord());
				pp.setFrequency(0);
				wordFreqClassEAfterFeatureSelection.set(randomNums.get(j), pp);
				wordFreqClassIAfterFeatureSelection.set(randomNums.get(j), pp);												
			}
			
			CMNB cmnb = new CMNB(wordFreqClassIAfterFeatureSelection, 
					wordFreqClassEAfterFeatureSelection, probThetaI, probThetaE);
			
			cmnbLists.add(cmnb);
		}			
	
	}
	**/
	
	public RandomCMNB(int number, double featurePercent, ArrayList<ArrayList<Integer>> freqenciesEachDocumentI,
			ArrayList<ArrayList<Integer>> freqenciesEachDocumentE, double probThetaI, double probThetaE, 
			ArrayList<String> wordsList) {
		this.number = number;
		this.featurePercent = featurePercent;
		this.cmnbLists = new ArrayList<CMNB>();
		//this.wordFreqClassEBeforeFeatureSelection = wordFreqClassE;
		//this.wordFreqClassIBeforeFeatureSelection = wordFreqClassI;
		this.frequenciesInEachDocumentE = freqenciesEachDocumentE;
		this.frequenciesInEachDocumentI = freqenciesEachDocumentI;
		this.wordsList = wordsList;
		this.probThetaE = probThetaE;
		this.probThetaI = probThetaI;
		
		//int featureNum = (int)(featurePercent*number);
		
		for(int i=0; i<number; i++) {
			ArrayList<Integer> randomNums = Util.generateRandomNumbers(1-featurePercent, wordsList.size());
			
			ArrayList<PairPercent> wordFreqClassIAfterFeatureSelection = new ArrayList<PairPercent>();
			ArrayList<PairPercent> wordFreqClassEAfterFeatureSelection = new ArrayList<PairPercent>();
			
			
			ArrayList<ArrayList<Integer>> freqEachDocIAfterFS = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> freqEachDocEAfterFS = new ArrayList<ArrayList<Integer>>();
			ArrayList<String> wordslistAfterFeatureSelection = new ArrayList<String>();
			
			for(int j=0;j<freqenciesEachDocumentE.size();j++) {
				ArrayList<Integer> freqEachdocE = new ArrayList<Integer>();
				for(int m=0; m<freqenciesEachDocumentE.get(j).size(); m++) {
					if(!randomNums.contains(m)) {
						freqEachdocE.add(freqenciesEachDocumentE.get(j).get(m));
					}
				}
				freqEachDocEAfterFS.add(freqEachdocE);				
			}
			for(int j=0;j<freqenciesEachDocumentI.size();j++) {				
				ArrayList<Integer> freqEachdocI = new ArrayList<Integer>();
				for(int m=0; m<freqenciesEachDocumentI.get(j).size(); m++) {
					if(!randomNums.contains(m)) {						
						freqEachdocI.add(freqenciesEachDocumentI.get(j).get(m));						
					}
				}
			
				freqEachDocIAfterFS.add(freqEachdocI);
			}
			
			for(int j=0; j<wordsList.size();j++) {
				if(!randomNums.contains(j)) {
					wordslistAfterFeatureSelection.add(wordsList.get(j));
				}
			}
			
			
				
			wordFreqClassEAfterFeatureSelection =
					FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance
					(wordslistAfterFeatureSelection, freqEachDocEAfterFS);
			wordFreqClassIAfterFeatureSelection	=
					FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance
					(wordslistAfterFeatureSelection, freqEachDocIAfterFS);
		
			
			
			CMNB cmnb = new CMNB(wordFreqClassIAfterFeatureSelection, 
					wordFreqClassEAfterFeatureSelection, probThetaI, probThetaE);
			
			cmnbLists.add(cmnb);
		}			
	
	}
	
	/**
	 * 生成一个不考虑Oc的随机模型
	 * @param number
	 * @param featurePercent
	 * @param freqenciesEachDocumentI
	 * @param freqenciesEachDocumentE
	 * @param probThetaI
	 * @param probThetaE
	 * @param wordsList
	 * @param occurence default=false
	 */
	public RandomCMNB(int number, double featurePercent, ArrayList<ArrayList<Integer>> freqenciesEachDocumentI,
			ArrayList<ArrayList<Integer>> freqenciesEachDocumentE, double probThetaI, double probThetaE, 
			ArrayList<String> wordsList, boolean occurence) {
		occurence = false;
		this.number = number;
		this.featurePercent = featurePercent;
		this.cmnbLists = new ArrayList<CMNB>();
		//this.wordFreqClassEBeforeFeatureSelection = wordFreqClassE;
		//this.wordFreqClassIBeforeFeatureSelection = wordFreqClassI;
		this.frequenciesInEachDocumentE = freqenciesEachDocumentE;
		this.frequenciesInEachDocumentI = freqenciesEachDocumentI;
		this.wordsList = wordsList;
		this.probThetaE = probThetaE;
		this.probThetaI = probThetaI;
		
		//int featureNum = (int)(featurePercent*number);
		
		for(int i=0; i<number; i++) {
			ArrayList<Integer> randomNums = Util.generateRandomNumbers(1-featurePercent, wordsList.size());
			
			ArrayList<PairPercent> wordFreqClassIAfterFeatureSelection = new ArrayList<PairPercent>();
			ArrayList<PairPercent> wordFreqClassEAfterFeatureSelection = new ArrayList<PairPercent>();
			
			
			ArrayList<ArrayList<Integer>> freqEachDocIAfterFS = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> freqEachDocEAfterFS = new ArrayList<ArrayList<Integer>>();
			ArrayList<String> wordslistAfterFeatureSelection = new ArrayList<String>();
			
			for(int j=0;j<freqenciesEachDocumentE.size();j++) {
				ArrayList<Integer> freqEachdocE = new ArrayList<Integer>();
				for(int m=0; m<freqenciesEachDocumentE.get(j).size(); m++) {
					if(!randomNums.contains(m)) {
						freqEachdocE.add(freqenciesEachDocumentE.get(j).get(m));
					}
				}
				freqEachDocEAfterFS.add(freqEachdocE);				
			}
			for(int j=0;j<freqenciesEachDocumentI.size();j++) {				
				ArrayList<Integer> freqEachdocI = new ArrayList<Integer>();
				for(int m=0; m<freqenciesEachDocumentI.get(j).size(); m++) {
					if(!randomNums.contains(m)) {						
						freqEachdocI.add(freqenciesEachDocumentI.get(j).get(m));						
					}
				}
			
				freqEachDocIAfterFS.add(freqEachdocI);
			}
			
			for(int j=0; j<wordsList.size();j++) {
				if(!randomNums.contains(j)) {
					wordslistAfterFeatureSelection.add(wordsList.get(j));
				}
			}
			
			
			//与上面的方法唯一不同之处在下面这几行	
			wordFreqClassEAfterFeatureSelection =
					FrequencyComputation.getFrequenciesThetaCIEachClass
					(wordslistAfterFeatureSelection, freqEachDocEAfterFS);
			wordFreqClassIAfterFeatureSelection	=
					FrequencyComputation.getFrequenciesThetaCIEachClass
					(wordslistAfterFeatureSelection, freqEachDocIAfterFS);
		
			
			
			CMNB cmnb = new CMNB(wordFreqClassIAfterFeatureSelection, 
					wordFreqClassEAfterFeatureSelection, probThetaI, probThetaE);
			
			cmnbLists.add(cmnb);
		}
		
	
	
	}
	
	
	public String getPredictionByVotingSchemeWithFactor(ArrayList<Pair> docToPredict, double factor) {
		String result = "\"I\"";
		
		ArrayList<CMNB> cmnbLists = this.cmnbLists;
		int Icount = 0;
		int Ecount = 0;
		for(int i=0; i<this.number;i++) {
			
			if(cmnbLists.get(i).predictWithFCMNB(docToPredict,factor).equals("\"I\"")) {
				Icount++;
			} else {
				Ecount++;
			}			
		}
		
		if(Icount>Ecount) result = "\"I\"";
		else result = "\"E\"";		
		
		return result;
		
	}
	
	/**
	 * 用MNB来预测某篇document是否相关 有factor
	 * @param docToPredict
	 * @param factor
	 * @return
	 */
	public String getMNBPredictionByVotingSchemeWithFactor(ArrayList<Pair> docToPredict, double factor) {
		String result = "\"I\"";
		
		ArrayList<CMNB> cmnbLists = this.cmnbLists;
		int Icount = 0;
		int Ecount = 0;
		for(int i=0; i<this.number;i++) {
			
			if(cmnbLists.get(i).predictWithMNB(docToPredict,factor).equals("\"I\"")) {
				Icount++;
			} else {
				Ecount++;
			}			
		}
		
		if(Icount>Ecount) result = "\"I\"";
		else result = "\"E\"";		
		
		return result;
		
	}
	
	
	/**
	 * instance selection 不选取features 而是选documents 来使
	 * @param number
	 * @param iPercent 随机选取documentI 的比例
	 * @param freqenciesEachDocumentI
	 * @param freqenciesEachDocumentE
	 * @param wordsList
	 * @param factorInstance 训练集中E类相对I类的比例 0-1.0 
	 */
	public RandomCMNB(int number, double iPercent, ArrayList<ArrayList<Integer>> freqenciesEachDocumentI,
			ArrayList<ArrayList<Integer>> freqenciesEachDocumentE, ArrayList<String> wordsList, double factorInstance, boolean oc) {
		this.number = number;
		//this.featurePercent = featurePercent;
		this.cmnbLists = new ArrayList<CMNB>();
		//this.wordFreqClassEBeforeFeatureSelection = wordFreqClassE;
		//this.wordFreqClassIBeforeFeatureSelection = wordFreqClassI;
		this.frequenciesInEachDocumentE = freqenciesEachDocumentE;
		this.frequenciesInEachDocumentI = freqenciesEachDocumentI;
		this.wordsList = wordsList;
		//this.probThetaE = probThetaE;
		//this.probThetaI = probThetaI;
		
		//int instanceINum = (int)(iPercent*freqenciesEachDocumentI.size());
		
		for(int i=0; i<number; i++) {
			ArrayList<Integer> randomNumsI = Util.generateRandomNumbers(iPercent, freqenciesEachDocumentI.size());
			double ePercent = 1;
			if(randomNumsI.size()<freqenciesEachDocumentE.size()) {
				ePercent = factorInstance*(double)randomNumsI.size()/freqenciesEachDocumentE.size();			
			}
			
			ArrayList<Integer> randomNumsE = Util.generateRandomNumbers(ePercent, freqenciesEachDocumentE.size());
		
			//System.out.print("I类数量Training:"+randomNumsI.size());
			//System.out.print("E类数量Training:"+randomNumsE.size());
			
			ArrayList<PairPercent> wordFreqClassIAfterInstanceSelection = new ArrayList<PairPercent>();
			ArrayList<PairPercent> wordFreqClassEAfterInstanceSelection = new ArrayList<PairPercent>();
			
			
			ArrayList<ArrayList<Integer>> freqEachDocIAfterInstanceS = new ArrayList<ArrayList<Integer>>();
			ArrayList<ArrayList<Integer>> freqEachDocEAfterInstanceS = new ArrayList<ArrayList<Integer>>();
			//ArrayList<String> wordslistAfterFeatureSelection = new ArrayList<String>();
			
			for(int j=0;j<freqenciesEachDocumentE.size();j++) {
				//ArrayList<Integer> freqEachdocE = new ArrayList<Integer>();
				//for(int m=0; m<freqenciesEachDocumentE.get(j).size(); m++) {
				if(randomNumsE.contains(j)) {
					//freqEachdocE.add(freqenciesEachDocumentE.get(j).get(m));
					freqEachDocEAfterInstanceS.add(freqenciesEachDocumentE.get(j));
				}
				//}
							
			}
			for(int j=0;j<freqenciesEachDocumentI.size();j++) {				
				if(randomNumsI.contains(j)) {						
					freqEachDocIAfterInstanceS.add(freqenciesEachDocumentI.get(j));						
				}
			}
						
			
			if(!oc) {
				
				wordFreqClassEAfterInstanceSelection =
						FrequencyComputation.getFrequenciesThetaCIEachClass
						(wordsList, freqEachDocEAfterInstanceS);
				wordFreqClassIAfterInstanceSelection =
						FrequencyComputation.getFrequenciesThetaCIEachClass
						(wordsList, freqEachDocIAfterInstanceS);
			
			} else {
				
				wordFreqClassEAfterInstanceSelection =
						FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance
						(wordsList, freqEachDocEAfterInstanceS);
				wordFreqClassIAfterInstanceSelection =
						FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance
						(wordsList, freqEachDocIAfterInstanceS);
				
				
			}
			
			double probI = (double)randomNumsI.size()/(randomNumsI.size()+randomNumsE.size());
			double probE = 1-probI;
			
			
			CMNB cmnb = new CMNB(wordFreqClassIAfterInstanceSelection, 
					wordFreqClassEAfterInstanceSelection, probI, probE);
			
			cmnbLists.add(cmnb);
		}			
	
	}
	
	
	
	
	
	public String getPredictionByVotingScheme(ArrayList<Pair> docToPredict) {
		String result = "\"I\"";
		
		ArrayList<CMNB> cmnbLists = this.cmnbLists;
		int Icount = 0;
		int Ecount = 0;
		for(int i=0; i<this.number;i++) {
			
			if(cmnbLists.get(i).predictWithCMNB(docToPredict).equals("\"I\"")) {
				Icount++;
			} else {
				Ecount++;
			}			
		}
		
		if(Icount>Ecount) result = "\"I\"";
		else result = "\"E\"";		
		
		return result;
		
	}
	
	
	
	
	
	
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public double getFeaturePercent() {
		return featurePercent;
	}
	public void setFeaturePercent(double featurePercent) {
		this.featurePercent = featurePercent;
	}
	public ArrayList<CMNB> getCmnbLists() {
		return cmnbLists;
	}
	public void setCmnbLists(ArrayList<CMNB> cmnbLists) {
		this.cmnbLists = cmnbLists;
	}
	
	

}
