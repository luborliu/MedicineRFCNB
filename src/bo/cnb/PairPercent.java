package bo.cnb;

public class PairPercent {
	
	private String word;
	private double frequency; //similar to tfidf
	
	public PairPercent() {

		this.word="";
		this.frequency = 0;
	
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
	

}
