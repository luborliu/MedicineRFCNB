package bo.cnb;

import java.util.ArrayList;
import java.util.Scanner;

public class DataPreprocessing {
	
	private ArrayList<String> allData = new ArrayList<String>();
	private ArrayList<String> trainingDOCS = new ArrayList<String>();		//这个是更raw的数据
	private ArrayList<String> testDOCS = new ArrayList<String>();
	
	private ArrayList<String> wordsList = new ArrayList<String>();
	
	private ArrayList<ArrayList<Integer>> frequenciesInEachDocumentI = new ArrayList<ArrayList<Integer>>(); //这就是个raw data 未加工的数据
	private ArrayList<ArrayList<Integer>> frequenciesInEachDocumentE = new ArrayList<ArrayList<Integer>>();
	
	//private ArrayList<ArrayList<Integer>> frequenciesInEachTraningDocumentI = new ArrayList<ArrayList<Integer>>(); //这就是个raw data 未加工的数据
	//private ArrayList<ArrayList<Integer>> frequenciesInEachTrainingDocumentE = new ArrayList<ArrayList<Integer>>();
	
	
	
	//docs to predict
	private ArrayList<ArrayList<Pair>> docsToPredictPair = new ArrayList<ArrayList<Pair>>();
	private ArrayList<String> typeDocToPredict = new ArrayList<String>();
	
	private ArrayList<PairPercent> wordFreqClassI;		//这个就是处理过的数据了
	private ArrayList<PairPercent> wordFreqClassE;
	
	private ArrayList<PairPercent> wordFreqClassIplusOc;
	private ArrayList<PairPercent> wordFreqClassEplusOc;
	
	private double probThetaI;
	private double probThetaE;
	
	private double factor;
	private double factorPlusOc;
	
	
	/**
	 * 构造函数
	 * @param filePath
	 * @param percent the percent of the data to be used for training
	 */
	public DataPreprocessing(String filePath, double percent) {

		this.allData = FileIO.loadDocsFromFile(filePath);
		//所有的词，对于某个training document可能不包括这个词
		ArrayList<String> wordsList = FileIO.readDocsWordsFromFile(filePath);
		
		int count = allData.size();
		ArrayList<Integer> randomNumAL = Util.generateRandomNumbers(percent, count);
		
		ArrayList<String> trainingDoc = new ArrayList<String>();
		ArrayList<String> testDoc = new ArrayList<String>();
		for(int i=0; i<allData.size(); i++) {
			if(randomNumAL.contains(i)) {
				trainingDoc.add(allData.get(i));
			} else
				testDoc.add(allData.get(i));
		}
		this.trainingDOCS = trainingDoc;
		this.testDOCS = testDoc;	
		
		this.frequenciesInEachDocumentE = this.readDocsFromTrainingSets(trainingDoc, "E");
		this.frequenciesInEachDocumentI = this.readDocsFromTrainingSets(trainingDoc, "I");
/////没考虑word occurrence in the docs				
		this.wordFreqClassE = this.getWordFrequenciesE(wordsList, trainingDoc);
		this.wordFreqClassI = this.getWordFrequenciesI(wordsList, trainingDoc);
/////考虑word occurrence in the docs		
		this.wordFreqClassEplusOc = this.getWordFrequenciesPlusOcE(wordsList, trainingDoc);		
		this.wordFreqClassIplusOc = this.getWordFrequenciesPlusOcI(wordsList, trainingDoc);
/////
		this.wordsList = wordsList;
		
		this.probThetaI = this.calculateProbI(trainingDoc);
		this.probThetaE = this.calculateProbE(trainingDoc);
		
		//preprocess the docs to predict in test phase
		ArrayList<ArrayList<Pair>> docsToPredict = new ArrayList<ArrayList<Pair>>();
		ArrayList<String> types = new ArrayList<String>();
		docsToPredict = getDocsToPredict(testDoc, wordsList, types); //注意这里的wordslist是与traningdata共用的 所以如果以后要改需注意 为test另new一个wordslist
		this.docsToPredictPair = docsToPredict;
		this.typeDocToPredict = types;
		
		
//		this.factor = this.factorGeneration(trainingDoc, false);
//		this.factorPlusOc = this.factorGeneration(trainingDoc, true);
	//this.factor = this.factorGenerationRandom(trainingDoc, false);
	//this.factorPlusOc = this.factorGenerationRandom(trainingDoc, true);
	
	}
	
	private double calculateProbI(ArrayList<String> trainingDOCS) {
		ArrayList<ArrayList<Integer>> docI = readDocsFromTrainingSets(trainingDOCS, "I");
		double p = (double)docI.size()/(double)trainingDOCS.size();
		return p;
	}
	private double calculateProbE(ArrayList<String> trainingDOCS) {
		ArrayList<ArrayList<Integer>> docE = readDocsFromTrainingSets(trainingDOCS, "E");
		double p = (double)docE.size()/(double)trainingDOCS.size();
		return p;
	}
	
	/**
	 * consider the influence of the occurrence of the words in the docs
	 * @param wordList
	 * @param trainingDOCS
	 * @return
	 */
	private ArrayList<PairPercent> getWordFrequenciesPlusOcI(ArrayList<String> wordList,
			ArrayList<String> trainingDOCS) {
		
		ArrayList<ArrayList<Integer>> docI = readDocsFromTrainingSets(trainingDOCS, "I");
		//ArrayList<Pair> wordsFrequenciesI= FrequencyComputation.getFrequencies(wordList, docI);
		
		//return FrequencyComputation.getFrequenciesThetaCIEachClass(wordList, docI);
		
		//改为我的蛋疼想法 加一个Oc 使结果累死于tfidf
		return FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance(wordList, docI);

	}
	
	/**
	 * consider the influence of the occurrence of the words in the docs
	 * @param wordList
	 * @param trainingDOCS
	 * @return
	 */
	private ArrayList<PairPercent> getWordFrequenciesPlusOcE(ArrayList<String> wordList,
			ArrayList<String> trainingDOCS/**, ArrayList<Integer> columnNum**/) {
		
		ArrayList<ArrayList<Integer>> docE = readDocsFromTrainingSets(trainingDOCS, "E");
		//ArrayList<Pair> wordsFrequenciesI= FrequencyComputation.getFrequencies(wordList, docI);
		
		//return FrequencyComputation.getFrequenciesThetaCIEachClass(wordList, docE);
		//改为我的蛋疼想法 加一个Oc 使结果累死于tfidf
		return FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance(wordList, docE);

	}
	/**
	 * not consider the influence of occurance of the words in different docs
	 * @param wordList
	 * @param trainingDOCS
	 * @return
	 */
	private ArrayList<PairPercent> getWordFrequenciesI(ArrayList<String> wordList,
			ArrayList<String> trainingDOCS) {
		
		ArrayList<ArrayList<Integer>> docI = readDocsFromTrainingSets(trainingDOCS, "I");
		//ArrayList<Pair> wordsFrequenciesI= FrequencyComputation.getFrequencies(wordList, docI);
		
		return FrequencyComputation.getFrequenciesThetaCIEachClass(wordList, docI);
		
		//改为我的蛋疼想法 加一个Oc 使结果累死于tfidf
		//return FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance(wordList, docI);

	}
	
	/**
	 * not consider the occurances of the words in different documents
	 * @param wordList
	 * @param trainingDOCS
	 * @return
	 */
	private ArrayList<PairPercent> getWordFrequenciesE(ArrayList<String> wordList,
			ArrayList<String> trainingDOCS/**, ArrayList<Integer> columnNum**/) {
		
		ArrayList<ArrayList<Integer>> docE = readDocsFromTrainingSets(trainingDOCS, "E");
		//ArrayList<Pair> wordsFrequenciesI= FrequencyComputation.getFrequencies(wordList, docI);
		
		return FrequencyComputation.getFrequenciesThetaCIEachClass(wordList, docE);
		//改为我的蛋疼想法 加一个Oc 使结果累死于tfidf
		//return FrequencyComputation.getFrequenciesThetaCIEachClassPlusOccurance(wordList, docE);

	}
	
	
	
	/**
	 * 实验证明根本起不到作用因为每次至多会有个位数的单词没包括在trainingdata中 所以无意义
	 * extract the wordlist for the training documents 
	 * because some words in the original wordlist may not occur in the training datasets
	 * @param wordList
	 * @param trainingDOCS
	 * @return
	 */
	private ArrayList<String> getWordListofTrainingData(ArrayList<Integer> columnNum,
			ArrayList<String> wordList, ArrayList<String> trainingDOCS) {
		
		ArrayList<ArrayList<Integer>> docI = readDocsFromTrainingSets(trainingDOCS, "I");
		ArrayList<ArrayList<Integer>> docE = readDocsFromTrainingSets(trainingDOCS, "E");
			
		ArrayList<Pair> wordsFrequenciesI= FrequencyComputation.getFrequencies(wordList, docI);
		ArrayList<Pair> wordsFrequenciesE= FrequencyComputation.getFrequencies(wordList, docE);
		
		//ArrayList<Integer> columnNum = new ArrayList<Integer>();
		
		for(int i=0; i<wordsFrequenciesI.size();i++) {
			if(wordsFrequenciesI.get(i).getFrequency()==0
					&&wordsFrequenciesE.get(i).getFrequency()==0) {
				wordList.remove(wordsFrequenciesI.get(i).getWord());
				columnNum.add(i);
				
			}
		}		
		return wordList;	
	}
	
	/**
	 * read the documents from the training ArrayList<String> according to the type E/I
	 * 
	 * @param trainingDOCS
	 * @param typ
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> readDocsFromTrainingSets(
			ArrayList<String> trainingDOCS, String typ) {		
		typ = "\""+typ+"\"";
		
		ArrayList<ArrayList<Integer>> pairAL = new ArrayList<ArrayList<Integer>>();
		
					
			for(int i=0; i<trainingDOCS.size();i++) {
				String doc = trainingDOCS.get(i);
				ArrayList<Integer> intAL =new ArrayList<Integer>();				
				
				Scanner sc = new Scanner(doc);
					String id = null;
					String docID = null;
				String type = null;
					id =sc.next();
					docID = sc.next();
				type = sc.next();
				//type = "\""+type+"\"";
				if(type.equals(typ)) {
					while(sc.hasNext()) {
						int number = sc.nextInt();
						intAL.add(number);
					}					
					pairAL.add(intAL);
				}
				
			}
						
		return pairAL;
		
	}	
	
	/**
	 * 获取test data的documents 用来给CMNB中的predict方法使用 来预测 明天继续好好检查有错没
	 * @param testDOCS 
	 * @param wordList
	 * @param testDocTypes用来保存document的type
	 * @return
	 */
	public ArrayList<ArrayList<Pair>> getDocsToPredict(ArrayList<String> testDOCS, 
			ArrayList<String> wordList, ArrayList<String> testDocTypes) {
		
		ArrayList<ArrayList<Integer>> docsAL = readDocsFromTestSets(testDOCS, testDocTypes);
		
		ArrayList<ArrayList<Pair>> docsToPredict = new ArrayList<ArrayList<Pair>>();
		
		for(int i=0; i<docsAL.size();i++) {
			ArrayList<Pair> docToPredict = new ArrayList<Pair>();
			for(int j=0; j<docsAL.get(i).size();j++) {
				Pair p = new Pair();
				p.setWord(wordList.get(j));
				p.setFrequency(docsAL.get(i).get(j));
				docToPredict.add(p);				
			}
			docsToPredict.add(docToPredict);
			
		}
		return docsToPredict;
	}
	
	/**
	 * generate the test documents and record the type of every document in ArrayList testDocTypes
	 * @param testDOCS
	 * @param testDocTypes
	 * @return
	 */
	private ArrayList<ArrayList<Integer>> readDocsFromTestSets(ArrayList<String> testDOCS, ArrayList<String> testDocTypes) {		
		
		ArrayList<ArrayList<Integer>> pairAL = new ArrayList<ArrayList<Integer>>();
		
					
		for(int i=0; i<testDOCS.size();i++) {
			String doc = testDOCS.get(i);
			ArrayList<Integer> intAL =new ArrayList<Integer>();				
				
			Scanner sc = new Scanner(doc);
				String id = null;
				String docID = null;
			String type = null;
				id =sc.next();
				docID = sc.next();
			type = sc.next();
			//type = "\""+type+"\"";
			testDocTypes.add(type);
			while(sc.hasNext()) {
				int number = sc.nextInt();
				intAL.add(number);
			}					
			pairAL.add(intAL);								
		}
						
		return pairAL;
		
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

	public ArrayList<String> getAllData() {
		return allData;
	}

	public void setAllData(ArrayList<String> allData) {
		this.allData = allData;
	}

	public ArrayList<String> getTrainingDOCS() {
		return trainingDOCS;
	}

	public void setTrainingDOCS(ArrayList<String> trainingDOCS) {
		this.trainingDOCS = trainingDOCS;
	}

	public ArrayList<String> getTestDOCS() {
		return testDOCS;
	}

	public void setTestDOCS(ArrayList<String> testDOCS) {
		this.testDOCS = testDOCS;
	}

	public ArrayList<ArrayList<Pair>> getDocsToPredictPair() {
		return docsToPredictPair;
	}

	public void setDocsToPredictPair(ArrayList<ArrayList<Pair>> docsToPredictPair) {
		this.docsToPredictPair = docsToPredictPair;
	}

	public ArrayList<String> getTypeDocToPredict() {
		return typeDocToPredict;
	}

	public void setTypeDocToPredict(ArrayList<String> typeDocToPredict) {
		this.typeDocToPredict = typeDocToPredict;
	}

	public ArrayList<String> getWordsList() {
		return wordsList;
	}

	public void setWordsList(ArrayList<String> wordsList) {
		this.wordsList = wordsList;
	}

	public ArrayList<ArrayList<Integer>> getFrequenciesInEachDocumentI() {
		return frequenciesInEachDocumentI;
	}

	public void setFrequenciesInEachDocumentI(
			ArrayList<ArrayList<Integer>> frequenciesInEachDocumentI) {
		this.frequenciesInEachDocumentI = frequenciesInEachDocumentI;
	}

	public ArrayList<ArrayList<Integer>> getFrequenciesInEachDocumentE() {
		return frequenciesInEachDocumentE;
	}

	public void setFrequenciesInEachDocumentE(
			ArrayList<ArrayList<Integer>> frequenciesInEachDocumentE) {
		this.frequenciesInEachDocumentE = frequenciesInEachDocumentE;
	}

	public ArrayList<PairPercent> getWordFreqClassIplusOc() {
		return wordFreqClassIplusOc;
	}

	public void setWordFreqClassIplusOc(ArrayList<PairPercent> wordFreqClassIplusOc) {
		this.wordFreqClassIplusOc = wordFreqClassIplusOc;
	}

	public ArrayList<PairPercent> getWordFreqClassEplusOc() {
		return wordFreqClassEplusOc;
	}

	public void setWordFreqClassEplusOc(ArrayList<PairPercent> wordFreqClassEplusOc) {
		this.wordFreqClassEplusOc = wordFreqClassEplusOc;
	}		
	
	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public double getFactorPlusOc() {
		return factorPlusOc;
	}

	public void setFactorPlusOc(double factorPlusOc) {
		this.factorPlusOc = factorPlusOc;
	}

	public double factorGeneration(ArrayList<String> trainingDocs, boolean occurrence) {
		int count = trainingDocs.size();
		
		double factorSUM = 0;
System.out.print("factor calculating");		
		for(int x=0;x<5;x++) {
				
			ArrayList<Integer> randomNumAL = Util.generateRandomNumbers(0.5, count);
			
			ArrayList<String> ftrainingDoc = new ArrayList<String>();
			ArrayList<String> ftestDoc = new ArrayList<String>();
			
			for(int i=0; i<trainingDocs.size(); i++) {
				if(randomNumAL.contains(i)) {
					ftrainingDoc.add(trainingDocs.get(i));
				} else
					ftestDoc.add(trainingDocs.get(i));
			}
			//this.trainingDOCS = trainingDoc;
			//this.testDOCS = testDoc;	
		
	/////没考虑word occurrence in the docs				
			ArrayList<PairPercent> wordFreqClassE = this.getWordFrequenciesE(wordsList, ftrainingDoc);
			ArrayList<PairPercent> wordFreqClassI = this.getWordFrequenciesI(wordsList, ftrainingDoc);
	/////考虑word occurrence in the docs		
			ArrayList<PairPercent> wordFreqClassEplusOc = this.getWordFrequenciesPlusOcE(wordsList, ftrainingDoc);		
			ArrayList<PairPercent> wordFreqClassIplusOc = this.getWordFrequenciesPlusOcI(wordsList, ftrainingDoc);
	/////
			
			
			double probThetaI = this.calculateProbI(ftrainingDoc);
			double probThetaE = this.calculateProbE(ftrainingDoc);
			
			//preprocess the docs to predict in test phase
			ArrayList<ArrayList<Pair>> docsToPredict = new ArrayList<ArrayList<Pair>>();
			ArrayList<String> types = new ArrayList<String>();
			docsToPredict = getDocsToPredict(ftestDoc, wordsList, types); //注意这里的wordslist是与traningdata共用的 所以如果以后要改需注意 为test另new一个wordslist
		
			
			CMNB cmnb = new CMNB();
			if(occurrence) {					
				cmnb = new CMNB(wordFreqClassIplusOc, wordFreqClassEplusOc, probThetaI, probThetaE);
			} else {
				cmnb = new CMNB(wordFreqClassI, wordFreqClassE, probThetaI, probThetaE);
			}
			double recall = 0;
			
			double factor = 0.8;
			do{
				ArrayList<String> typePrediction = new ArrayList<String>(); //预测的type
				
					for(int i=0; i<docsToPredict.size();i++) {
					String s =cmnb.predictWithFCMNB(docsToPredict.get(i), factor);
					typePrediction.add(s);
				}
				int correctCount=0;
				int correctCountI = 0;
				int actualCountI = 0;
				for(int i=0; i<typePrediction.size();i++) {
					if(typePrediction.get(i).equals(types.get(i))) {
						correctCount++;
						if(typePrediction.get(i).equals("\"I\"")) {
							correctCountI++;
						}
					}
					if(types.get(i).equals("\"I\"")) {
						actualCountI++;
					}
				}
			
				recall = (double)correctCountI/actualCountI;
					
				factor = factor+0.01;
			
				}	while(recall>0.96); 			 
		
		
		factorSUM = factor+factorSUM;
		System.out.print(".factoris"+factor);
		
		}
		System.out.println(factorSUM/5);
		return factorSUM/5;				
	}
	
	

	public double factorGenerationRandom(ArrayList<String> trainingDocs, boolean occurrence) {
		int count = trainingDocs.size();
		
		double factorSUM = 0;
System.out.print("factor calculating");		
		for(int x=0;x<10;x++) {
				
			ArrayList<Integer> randomNumAL = Util.generateRandomNumbers(0.5, count);
			
			ArrayList<String> ftrainingDoc = new ArrayList<String>();
			ArrayList<String> ftestDoc = new ArrayList<String>();
			
			for(int i=0; i<trainingDocs.size(); i++) {
				if(randomNumAL.contains(i)) {
					ftrainingDoc.add(trainingDocs.get(i));
				} else
					ftestDoc.add(trainingDocs.get(i));
			}
			//this.trainingDOCS = trainingDoc;
			//this.testDOCS = testDoc;	

			ArrayList<ArrayList<Integer>> frequenciesInEachDocumentE = this.readDocsFromTrainingSets(trainingDocs, "E");
			ArrayList<ArrayList<Integer>> frequenciesInEachDocumentI = this.readDocsFromTrainingSets(trainingDocs, "I");	
						
			double probThetaI = this.calculateProbI(ftrainingDoc);
			double probThetaE = this.calculateProbE(ftrainingDoc);
			
			//preprocess the docs to predict in test phase
			ArrayList<ArrayList<Pair>> docsToPredict = new ArrayList<ArrayList<Pair>>();
			ArrayList<String> types = new ArrayList<String>();
			docsToPredict = getDocsToPredict(ftestDoc, wordsList, types); //注意这里的wordslist是与traningdata共用的 所以如果以后要改需注意 为test另new一个wordslist
		
			
			RandomCMNB rcmnb = new RandomCMNB();
	
			
			if(occurrence) {
				rcmnb=	new RandomCMNB(50, 0.1, 
						frequenciesInEachDocumentI, frequenciesInEachDocumentE,
						probThetaI, probThetaE,wordsList);
				//factor = factor-0.03;
			} else {
				rcmnb=	new RandomCMNB(50, 0.1, 
						frequenciesInEachDocumentI, frequenciesInEachDocumentE,
						probThetaI, probThetaE,wordsList, false);
				//factor = factor-0.05;
				}
			double recall = 0;
			
			double factor = 0.8;
			do{
				ArrayList<String> typePrediction = new ArrayList<String>(); //预测的type
				
				for(int i=0; i<docsToPredict.size();i++) {
					String s =rcmnb.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
					typePrediction.add(s);
				}
				//int correctCount=0;
				int correctCountI = 0;
				int actualCountI = 0;
				for(int i=0; i<typePrediction.size();i++) {
					if(typePrediction.get(i).equals(types.get(i))) {
						//correctCount++;
						if(typePrediction.get(i).equals("\"I\"")) {
							correctCountI++;
						}
					}
					if(types.get(i).equals("\"I\"")) {
						actualCountI++;
					}
				}
			
				recall = (double)correctCountI/actualCountI;
					
				factor = factor+0.01;
			
				}	while(recall>0.96); 			 
		
		System.out.print(factor+"-");
		factorSUM = factor+factorSUM;
		System.out.print(".");
		
		}
		System.out.println("factor is :"+ factorSUM/10);
		return factorSUM/10;				
	}

}
