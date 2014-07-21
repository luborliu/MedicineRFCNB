package bo.cnb;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		//System.out.println("triptan");
		
	//	DataPreprocessing dp = new DataPreprocessing("src/estrogens9898.csv", 0.5);
	//	DataPreprocessing dp = new DataPreprocessing("src/triptan9898.csv", 0.5);
	//	DataPreprocessing dp = new DataPreprocessing("src/oralh9898.csv", 0.5);
	DataPreprocessing dp = new DataPreprocessing("src/bb9898.csv", 0.5);
	//	DataPreprocessing dp = new DataPreprocessing("src/bb9999.csv", 0.5);
		
	//	DataPreprocessing dp = new DataPreprocessing("src/bb98980.csv", 0.5);

		//ArrayList<Integer> ali = Util.generateRandomNumbers(0.35, 10);
		ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();
		ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
		
		double f1= dp.getFactor()-0.02;
		//double f2 =dp.getFactorPlusOc()-0.01;	
//		double f3= dp.getFactor()-0.03;
//		double f4 =dp.getFactorPlusOc()-0.04;	
//		double f1=0.98;
//		double f2 = 0.88;
		double f3 = 0.97-0.04;
//		double f4 = 0.88;
		
		//double factor = 0.98;
		//for(double factor = 0.97; factor<0.98;factor=factor+0.001) {
		
		
		
		/**
		 * 开始测试cmnb without oc
		 */
		double WSS1 = 0;
	//	double factor1 = evaluateCMNB(dp, false, WSS1, f1); 这个也不测了
		
			/**
		 * 开始测试cmnb+oc
		 */
		
//		double WSS2 = 0;
//		double factor2 = evaluateCMNB(dp, true, WSS2, f2);

	
		/*****
		 * 开始测试random CNB
		 * 
		 */				
		System.out.println(":::FFFFFF::RFCNB-F::::::::");
	for(int i=0;i<3;i++) {
	evaluateRandomCMNB(dp, false, f3);
//		evaluateRandomCMNB(dp, true, f4);				
	}
		
//		第二阶段
		for(double factor = 0.985; factor<0.99;factor=factor+0.001) { //0.975-0.985			
			//evaluateRandomCMNBInstanceSelection(dp,true,factor);
			//factor = 1;
//		System.out.print("ONE::::OC:::::::");
//		evaluateRandomCMNBInstanceSelectionOne(dp, true, factor);
//System.out.print("ONE::::NoOC:::::::");
//evaluateRandomCMNBInstanceSelectionOne(dp, false, factor);这个也不测了
			
//		System.out.print("RandomForest:::::::OC::::::::");
//		evaluateRandomCMNBInstanceSelection(dp,true,factor);
			System.out.print("IIII::::::RFCNB-I:::::::::");
			evaluateRandomCMNBInstanceSelection(dp,false,factor);
			
//			System.out.println("ONE::::OC:::::::");
//			evaluateRandomMNBInstanceSelectionOne(dp, true, factor);
//			System.out.println("ONE::::NoOC:::::::");
//			evaluateRandomMNBInstanceSelectionOne(dp, false, factor);
//			
//			System.out.println("RandomForest:::::::OC::::::::");
//			evaluateRandomMNBInstanceSelection(dp,true,factor);
//			System.out.println("RandomForest::::::NoOC:::::::::");
//			evaluateRandomMNBInstanceSelection(dp,false,factor);			
			
			
		}
			

	}
	
	/**
	 * 
	 * @param dp 预处理类
	 * @param occurrence boolean类型 true代表考虑了occurrence false代表不考虑
	 * @param WSS 用来存储每次factor对应的WSS 为最终选择factor做依据
	 * @return
	 */
	public static double evaluateCMNB(DataPreprocessing dp, boolean occurrence, double WSS, double factor) {
		
		ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();
		ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
		
		CMNB cmnb = new CMNB();
		if(occurrence) {					
			cmnb = new CMNB(dp.getWordFreqClassIplusOc(), dp.getWordFreqClassEplusOc(), dp.getProbThetaI(), dp.getProbThetaE());
		} else {
			cmnb = new CMNB(dp.getWordFreqClassI(), dp.getWordFreqClassE(), dp.getProbThetaI(), dp.getProbThetaE());
		}
		double recall = 0;
		WSS = 0;
		double precision = 0;
		double accuracy = 0;
		//double factor = 0.8;
		//do{
			ArrayList<String> typePrediction = new ArrayList<String>(); //预测的type
			
			for(int i=0; i<docsToPredict.size();i++) {
				String s =cmnb.predictWithFCMNB(docsToPredict.get(i), factor);
				typePrediction.add(s);
			}
			int correctCount=0;
			int correctCountI = 0;
			int actualCountI = 0;
			for(int i=0; i<typePrediction.size();i++) {
				if(typePrediction.get(i).equals(typeActual.get(i))) {
					correctCount++;
					if(typePrediction.get(i).equals("\"I\"")) {
						correctCountI++;
					}
				}
				if(typeActual.get(i).equals("\"I\"")) {
					actualCountI++;
				}
			}
		
			int TN = correctCount-correctCountI;
			int FN = actualCountI-correctCountI;
			recall = (double)correctCountI/actualCountI;
			accuracy = (double)correctCount/typePrediction.size();
			precision = (double)correctCountI/(correctCountI+ typePrediction.size()-correctCount-FN);
			WSS = (double)(TN+FN)/typePrediction.size()-1+recall;
			
			//factor = factor+0.01;
		
	//	}	while(recall>0.96); 			 
		
		System.out.print("accuracy is: "+accuracy+"\t");
		System.out.print("precision is: "+ precision+"\t");
		System.out.print("recall is: "+ recall+"\t");
		System.out.print("WSS is: "+WSS+"\t");
		System.out.print("Factor is: "+ factor+"\n");				
			
		return factor;
	
	}
	
	public static void evaluateRandomCMNB(DataPreprocessing dp, boolean occurrence, double factor) {
		
		
		//factor = 1;
		RandomCMNB rCMNB = new RandomCMNB();
		
		//建立30个classifier 100个classifier
		if(occurrence) {
			rCMNB=	new RandomCMNB(30, 0.2, 
					dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
					dp.getProbThetaI(), dp.getProbThetaE(),dp.getWordsList());
			//factor = factor-0.03;
		} else {
			rCMNB=	new RandomCMNB(30, 0.2, 
					dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
					dp.getProbThetaI(), dp.getProbThetaE(),dp.getWordsList(), false);
			//factor = factor-0.05;
			}
				
		ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();	
		ArrayList<String> typePredictionRCMNB = new ArrayList<String>(); //预测的type
		ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
		
		for(int i=0; i<docsToPredict.size();i++) {
			//String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
			String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);			
			typePredictionRCMNB.add(s);
		}
		int RcorrectCount=0;
		int RcorrectCountI = 0;
		int RactualCountI = 0;
		for(int i=0; i<typePredictionRCMNB.size();i++) {
			if(typePredictionRCMNB.get(i).equals(typeActual.get(i))) {
				RcorrectCount++;
				if(typePredictionRCMNB.get(i).equals("\"I\"")) {
					RcorrectCountI++;
				}
			}
			if(typeActual.get(i).equals("\"I\"")) {
				RactualCountI++;
			}
		}
		
		int RTN = RcorrectCount-RcorrectCountI;
		int RFN = RactualCountI-RcorrectCountI;
		
		double Rrecall = (double)RcorrectCountI/RactualCountI;
		double RWSS = (double)(RTN+RFN)/typePredictionRCMNB.size()-1+Rrecall;
		double Rprecision = (double)RcorrectCountI/(RcorrectCountI+ typePredictionRCMNB.size()-RcorrectCount-RFN);
		
		System.out.print("accuracy is: "+ (double)RcorrectCount/typePredictionRCMNB.size()+"\t");
		System.out.print("precision is: "+ Rprecision+"\t");
		System.out.print("recall is: "+ Rrecall+"\t");
		System.out.print("WSS is: "+RWSS+"\t");
		System.out.print("Factor is: "+ factor+"\t");
		System.out.println();				
		
	}
	
public static void evaluateRandomCMNBInstanceSelection(DataPreprocessing dp, boolean occurrence, double factor) {
		
		
		//factor = 1;
		RandomCMNB rCMNB = new RandomCMNB();
		
		if(!occurrence) {
			rCMNB=	new RandomCMNB(30, 1, 
					dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
					dp.getWordsList(), 1, false); //把这里的factor直接改为1.0了因为我发现这个影响太小
			
		} else {
			rCMNB=	new RandomCMNB(30, 1, 
					dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
					dp.getWordsList(), 1, true);//把这里的factor直接改为1.0了因为我发现这个影响太小
			
			}
				
		ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();	
		ArrayList<String> typePredictionRCMNB = new ArrayList<String>(); //预测的type
		ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
		
		for(int i=0; i<docsToPredict.size();i++) {
			//String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
			String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), 0.975);	//这里的factor不应该和前面的一样呀		
			typePredictionRCMNB.add(s);
		}
		int RcorrectCount=0;
		int RcorrectCountI = 0;
		int RactualCountI = 0;
		for(int i=0; i<typePredictionRCMNB.size();i++) {
			if(typePredictionRCMNB.get(i).equals(typeActual.get(i))) {
				RcorrectCount++;
				if(typePredictionRCMNB.get(i).equals("\"I\"")) {
					RcorrectCountI++;
				}
			}
			if(typeActual.get(i).equals("\"I\"")) {
				RactualCountI++;
			}
		}
		
		int RTN = RcorrectCount-RcorrectCountI;
		int RFN = RactualCountI-RcorrectCountI;
		
		double Rrecall = (double)RcorrectCountI/RactualCountI;
		double RWSS = (double)(RTN+RFN)/typePredictionRCMNB.size()-1+Rrecall;
		double Rprecision = (double)RcorrectCountI/(RcorrectCountI+ typePredictionRCMNB.size()-RcorrectCount-RFN);
		System.out.println();
		System.out.println("RandomCMNB");
		System.out.print("accuracy is: "+ (double)RcorrectCount/typePredictionRCMNB.size()+"\t");
		System.out.print("precision is: "+ Rprecision+"\t");
		System.out.print("recall is: "+ Rrecall+"\t");
		System.out.print("WSS is: "+RWSS+"\t");
		System.out.print("Factor is: "+ factor+"\t");
		System.out.println();				
		
	}
public static void evaluateRandomMNBInstanceSelection(DataPreprocessing dp, boolean occurrence, double factor) {
	
	
	//factor = 1;
	RandomCMNB rCMNB = new RandomCMNB();
	
	if(!occurrence) {
		rCMNB=	new RandomCMNB(100, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, false); //把这里的factor直接改为1.0了因为我发现这个影响太小
		
	} else {
		rCMNB=	new RandomCMNB(100, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, true);//把这里的factor直接改为1.0了因为我发现这个影响太小
		
		}
			
	ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();	
	ArrayList<String> typePredictionRCMNB = new ArrayList<String>(); //预测的type
	ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
	
	for(int i=0; i<docsToPredict.size();i++) {
		//String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
		String s = rCMNB.getMNBPredictionByVotingSchemeWithFactor(docsToPredict.get(i), 0.975);	//这里的factor不应该和前面的一样呀		
		typePredictionRCMNB.add(s);
	}
	int RcorrectCount=0;
	int RcorrectCountI = 0;
	int RactualCountI = 0;
	for(int i=0; i<typePredictionRCMNB.size();i++) {
		if(typePredictionRCMNB.get(i).equals(typeActual.get(i))) {
			RcorrectCount++;
			if(typePredictionRCMNB.get(i).equals("\"I\"")) {
				RcorrectCountI++;
			}
		}
		if(typeActual.get(i).equals("\"I\"")) {
			RactualCountI++;
		}
	}
	
	int RTN = RcorrectCount-RcorrectCountI;
	int RFN = RactualCountI-RcorrectCountI;
	
	double Rrecall = (double)RcorrectCountI/RactualCountI;
	double RWSS = (double)(RTN+RFN)/typePredictionRCMNB.size()-1+Rrecall;
	double Rprecision = (double)RcorrectCountI/(RcorrectCountI+ typePredictionRCMNB.size()-RcorrectCount-RFN);
	System.out.println();
	System.out.println("RandomMNB");
	System.out.print("accuracy is: "+ (double)RcorrectCount/typePredictionRCMNB.size()+"\t");
	System.out.print("precision is: "+ Rprecision+"\t");
	System.out.print("recall is: "+ Rrecall+"\t");
	System.out.print("WSS is: "+RWSS+"\t");
	System.out.print("Factor is: "+ factor+"\t");
	System.out.println();				
	
}




public static void evaluateRandomCMNBInstanceSelectionOne(DataPreprocessing dp, boolean occurrence, double factor) {
	
	
	//factor = 1;
	RandomCMNB rCMNB = new RandomCMNB();
	
	if(!occurrence) {
		rCMNB=	new RandomCMNB(1, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, false);
		
	} else {
		rCMNB=	new RandomCMNB(1, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, true);
		
		}
			
	ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();	
	ArrayList<String> typePredictionRCMNB = new ArrayList<String>(); //预测的type
	ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
	
	for(int i=0; i<docsToPredict.size();i++) {
		//String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
		String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);	//这里的factor不应该和前面的一样呀		
		typePredictionRCMNB.add(s);
	}
	int RcorrectCount=0;
	int RcorrectCountI = 0;
	int RactualCountI = 0;
	for(int i=0; i<typePredictionRCMNB.size();i++) {
		if(typePredictionRCMNB.get(i).equals(typeActual.get(i))) {
			RcorrectCount++;
			if(typePredictionRCMNB.get(i).equals("\"I\"")) {
				RcorrectCountI++;
			}
		}
		if(typeActual.get(i).equals("\"I\"")) {
			RactualCountI++;
		}
	}
	
	int RTN = RcorrectCount-RcorrectCountI;
	int RFN = RactualCountI-RcorrectCountI;
	
	double Rrecall = (double)RcorrectCountI/RactualCountI;
	double RWSS = (double)(RTN+RFN)/typePredictionRCMNB.size()-1+Rrecall;
	double Rprecision = (double)RcorrectCountI/(RcorrectCountI+ typePredictionRCMNB.size()-RcorrectCount-RFN);
	System.out.println();
	System.out.println("One-RandomCMNB");
	System.out.print("accuracy is: "+ (double)RcorrectCount/typePredictionRCMNB.size()+"\t");
	System.out.print("precision is: "+ Rprecision+"\t");
	System.out.print("recall is: "+ Rrecall+"\t");
	System.out.print("WSS is: "+RWSS+"\t");
	System.out.print("Factor is: "+ factor+"\t");
	System.out.println();				
	
}


public static void evaluateRandomMNBInstanceSelectionOne(DataPreprocessing dp, boolean occurrence, double factor) {
	
	
	//factor = 1;
	RandomCMNB rCMNB = new RandomCMNB();
	
	if(!occurrence) {
		rCMNB=	new RandomCMNB(1, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, false);
		
	} else {
		rCMNB=	new RandomCMNB(1, 1, 
				dp.getFrequenciesInEachDocumentI(), dp.getFrequenciesInEachDocumentE(),
				dp.getWordsList(), 1, true);
		
		}
			
	ArrayList<ArrayList<Pair>> docsToPredict = dp.getDocsToPredictPair();	
	ArrayList<String> typePredictionRCMNB = new ArrayList<String>(); //预测的type
	ArrayList<String> typeActual = dp.getTypeDocToPredict();	//实际的type
	
	for(int i=0; i<docsToPredict.size();i++) {
		//String s = rCMNB.getPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);
		String s = rCMNB.getMNBPredictionByVotingSchemeWithFactor(docsToPredict.get(i), factor);	//这里的factor不应该和前面的一样呀		
		typePredictionRCMNB.add(s);
	}
	int RcorrectCount=0;
	int RcorrectCountI = 0;
	int RactualCountI = 0;
	for(int i=0; i<typePredictionRCMNB.size();i++) {
		if(typePredictionRCMNB.get(i).equals(typeActual.get(i))) {
			RcorrectCount++;
			if(typePredictionRCMNB.get(i).equals("\"I\"")) {
				RcorrectCountI++;
			}
		}
		if(typeActual.get(i).equals("\"I\"")) {
			RactualCountI++;
		}
	}
	
	int RTN = RcorrectCount-RcorrectCountI;
	int RFN = RactualCountI-RcorrectCountI;
	
	double Rrecall = (double)RcorrectCountI/RactualCountI;
	double RWSS = (double)(RTN+RFN)/typePredictionRCMNB.size()-1+Rrecall;
	double Rprecision = (double)RcorrectCountI/(RcorrectCountI+ typePredictionRCMNB.size()-RcorrectCount-RFN);
	System.out.println();
	System.out.println("ONE-RandomMNB");
	System.out.print("accuracy is: "+ (double)RcorrectCount/typePredictionRCMNB.size()+"\t");
	System.out.print("precision is: "+ Rprecision+"\t");
	System.out.print("recall is: "+ Rrecall+"\t");
	System.out.print("WSS is: "+RWSS+"\t");
	System.out.print("Factor is: "+ factor+"\t");
	System.out.println();				
	
}


	
	
	
	

}
