package bo.cnb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;



public class FileIO {
	
	public static ArrayList<String> readDocsWordsFromFile(String path) {
		
		DataInputStream in;
		BufferedReader inBuffer = null;
		
		ArrayList<String> wordsAL = new ArrayList<String>();
		try {
			in = new DataInputStream(new BufferedInputStream(   
			        new FileInputStream(path)));						
			
			inBuffer = new BufferedReader(   
	                new InputStreamReader(in));
			
			String wordsList = null;	
			
			wordsList = inBuffer.readLine();
			
			Scanner sc = new Scanner(wordsList);
			//String id =null;
String idDoc = sc.next();
String type = sc.next();
			while(sc.hasNext()) {	
				wordsAL.add(sc.next());
			}
																				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Unable to open input file");
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try {
				inBuffer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}        		
		return wordsAL;
				
	}
	
	/**
	 * This method is to read documents from file and convert every document to a string 
	 * then store it in an arrayList This is used for randomly separating
	 * @param path
	 * @return
	 */
	public static ArrayList<String> loadDocsFromFile(String path) {
		DataInputStream in;
		BufferedReader inBuffer = null;
		
		//ArrayList<ArrayList<Integer>> pairAL = new ArrayList<ArrayList<Integer>>();
		ArrayList<String> docAL = new ArrayList<String>();
				
		try {
			in = new DataInputStream(new BufferedInputStream(   
			        new FileInputStream(path)));						
			
			inBuffer = new BufferedReader(   
	                new InputStreamReader(in));
			
			String doc = null;	
			inBuffer.readLine();
			
			while((doc=inBuffer.readLine())!= null) {
				docAL.add(doc);
				
			}																		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Unable to open input file");
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try {
				inBuffer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}        		
		return docAL;
	}
	
	
	public static ArrayList<ArrayList<Integer>> readDocsFromFile(String path, String typ) {
		
		DataInputStream in;
		BufferedReader inBuffer = null;
		
		ArrayList<ArrayList<Integer>> pairAL = new ArrayList<ArrayList<Integer>>();
		
				
		try {
			in = new DataInputStream(new BufferedInputStream(   
			        new FileInputStream(path)));						
			
			inBuffer = new BufferedReader(   
	                new InputStreamReader(in));
			
			String doc = null;	
			inBuffer.readLine();
			
			while((doc=inBuffer.readLine())!= null) {
				
				ArrayList<Integer> intAL =new ArrayList<Integer>();

				//doc = inBuffer.readLine();
				
				Scanner sc = new Scanner(doc);
					String id = null;
					String docID = null;
				String type = null;
					id =sc.next();
					docID = sc.next();
				type = sc.next();
				if(type.equals(typ)) {
					while(sc.hasNext()) {
						int number = sc.nextInt();
						intAL.add(number);
					}					
					pairAL.add(intAL);
				}				
			}																		
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Unable to open input file");
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			try {
				inBuffer.close();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}        		
		return pairAL;
		
	}	

}
