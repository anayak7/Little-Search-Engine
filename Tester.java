package lse;

import java.io.*;
import java.util.*;

public class Tester {

	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) 
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("Enter file name that contains all text files to be scanned:");
		String docstext = sc.nextLine();

		System.out.println("Enter noise words file name:");
		String noisewords= sc.nextLine();

		LittleSearchEngine lse= new LittleSearchEngine();
		lse.makeIndex(docstext, noisewords);
		
		
		System.out.println("top 5 search results are: " + lse.top5search("red", "car"));
	}

}
