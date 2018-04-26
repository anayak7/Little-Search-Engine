package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		
		if(docFile == null)
		{
			throw new FileNotFoundException();
		}
		
		Scanner sc= new Scanner(new File(docFile)); //new scanner that scans through doc
		HashMap<String, Occurrence> keywords= new HashMap<String, Occurrence>(); //creating new hashmap to store keywords
		
		while(sc.hasNext()) //while there is a next line to check
		{
			String kw= getKeyword(sc.next()); //next token is passed through getKeyword method to retrieve next kw
			//System.out.println("kw is: " + kw);

			if(kw != null) //if the keyword returned is not null
			{
				kw=kw.trim();
				int freq=1;

				if(kw.isEmpty() == false)
				{
					if(keywords.containsKey(kw) == false) //if keyword is not in the hashmap
					{
						Occurrence ocurr= new Occurrence(docFile, freq++);
						
						keywords.put(kw, ocurr);
						
					} else { //if hashmap contains keyword
						
						keywords.get(kw).frequency++; //incrementing frequency
					}
				}
			}
		}
		
		sc.close();
		//System.out.println(keywords.keySet());
		return keywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList <Occurrence> newocc= new ArrayList<Occurrence>();
		
		for(String key : kws.keySet())
		{			
			//System.out.println("key is: " + key);
			Occurrence curr= kws.get(key); //gets the occ associated with the current key
			newocc=keywordsIndex.get(key); //gets the list of occs from master hash table
			
			//System.out.println("occ is: " + curr);

			if(keywordsIndex.containsKey(key) == false)
			{
				if(newocc == null)
				{	
					newocc= new ArrayList<Occurrence>(); //create new AL for new key
					newocc.add(curr);
					keywordsIndex.put(key, newocc); 
				}

			} else { //if key is already in keywordsIndex
				
				newocc.add(curr); //adding occurrence

			}
			
			ArrayList<Integer> arr= insertLastOccurrence(newocc); //to place occurrences in descending order
			//System.out.println("returned midpt arraylist is: " + insertLastOccurrence(newocc)); //to place occurrences in descending order

		}	
	}
	
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
	
		word=word.toLowerCase();
		String subword="";
		
		if(word != null)
		{			
			for(int i=0; i < word.length(); i++)
			{				
				if(!Character.isAlphabetic(word.charAt(i))) //checking to see if current char is punctuation
				{					
					if(word.charAt(i) == ('.') || word.charAt(i) == (',') || word.charAt(i) == ('?') || word.charAt(i) == (':') || word.charAt(i) == (';') || word.charAt(i) == ('!')) //checking for only valid punctuation
					{
						subword= word.substring(i);
						//System.out.println("subword that cuts off punctuation is: " + subword);
						
						if(letterCheck(subword) == true) //there's letters after punctuation
						{
							//System.out.println("I'm going to return null.");
							return null;
							
						} else { //there's only punctuation and no letters
							
							subword=word.substring(0, i); //updating subword to the actual word and cutting off punctuation
							//System.out.println("subword that precedes punctuation is: " + subword);

							if(!noiseWords.contains(subword)) //checking that subword is not a noise word
							{
								return subword;
								
							} else { //if subword is a noise word
								
								return null;
							}
						}
						
					} else { //if char is punctuation other than the ones listed above
						
						return null;
					}	
				} 
			}
		}
		
		if(noiseWords.contains(word))
		{
			word=null;
		}
		
		return word;
	}
	
	private boolean letterCheck(String word) //checks if there are any letters present after punctuation in the string
	{
		for(int i=0; i < word.length(); i++)
		{
			if(Character.isAlphabetic(word.charAt(i)))
			{
				return true;
			}
		}
		
		return false;
	}
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		//System.out.println("occs.size() is: " + (occs.size()));

		ArrayList <Integer> midfreqs= new ArrayList <Integer>();
		int first=0;
		int last=occs.size()-2; //storing index of last ordered number
		int lastfreq= occs.get(occs.size()-1).frequency; //storing a ref to the number that's not in order
		int midfreq=0;
		int mid=0;
		
		if(occs.size() <= 1)
		{
			return null;
			
		} else {
			
			while(last >= first)
			{
				mid= (first+last)/2;
				//System.out.println("first: " + first + " last: " + last + " mid: " + mid);

				midfreqs.add(mid); //adding midpoints
				
				midfreq= occs.get(mid).frequency;
				//System.out.println("midpt frequency: " + midfreq);
				
				//System.out.println("lastfreq is: " + lastfreq);
					
				if(midfreq == lastfreq) //if midfreq frequency equals lastfreq
				{
					break;
				}
				
				else if(midfreq < lastfreq)
				{
					//System.out.println("midfreq < lastfreq");
					
					last= mid-1; //decreasing last index

				} else { //frequency number at mid is higher than last frequency
					
					//System.out.println("midfreq > lastfreq");
					first=mid+1; //increasing first index
				}
				
			}	
			
			//places last word in correct spot in occs list
				if(midfreq >= lastfreq)
				{
					//System.out.println("I'm being added at mid+1");
					//System.out.println("occs.size();-1 is: " + (occs.size()-1));
					
					occs.add(mid+1, occs.get(occs.size()-1)); //placing lastfreq after mid
					occs.remove(occs.size()-1); //removing last occ from list
					
				} else {
					
					//System.out.println("I'm being added at mid");
					//System.out.println("occs.size();-1 is: " + (occs.size()-1));

					occs.add(mid, occs.get(occs.size()-1));
					occs.remove(occs.size()-1); //removing last occ from list
				}			
		}
		
		System.out.println("occs is: " + occs);
	
		return midfreqs;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList <String> topFive= new ArrayList <String>();
		ArrayList <Occurrence> kwocc1= new ArrayList <Occurrence>();
		ArrayList <Occurrence> kwocc2= new ArrayList <Occurrence>();
			
		if(keywordsIndex.containsKey(kw1))
		{
			kwocc1=keywordsIndex.get(kw1); //storing all kw1 occurrences
		}
		if(keywordsIndex.containsKey(kw2))
		{
			kwocc2=keywordsIndex.get(kw2); //storing all kw2 occurrences
		}
		
		if(kwocc1.isEmpty() && kwocc2.isEmpty()) //if the keywords do not appear in any text file
		{
			return null;
			
		} 
		
		//to iterate through the lists
		Iterator<Occurrence> it1= kwocc1.iterator();
		Iterator<Occurrence> it2= kwocc2.iterator();
		
		//to prevent null pointer exception when getting first element
		Occurrence curr1= null;
		try
		{
			curr1=it1.next();
		}
		catch(NoSuchElementException e)
		{
			curr1=null;
		}
		Occurrence curr2= null;
		try
		{
			curr2=it2.next();
		}
		catch(NoSuchElementException e)
		{
			curr2=null;
		}
		
		if(curr2 == null) //if kwocc1 is not null
		{			
			while(curr1 != null && topFive.size() != 5)
			{
				//System.out.println("curr1 is: " + curr1);

				if(!topFive.contains(curr1.document))
				{
					topFive.add(curr1.document);
				}
			
				if(it1.hasNext())
				{
					curr1= it1.next();
					
				} else {
					
					curr1 = null;
				}
			}
		}
		
		else if(curr1 == null) //if kwocc2 is not null
		{			
			while(curr2 != null && topFive.size() != 5)
			{
				// System.out.println("curr2 is: " + curr2);

				if(!topFive.contains(curr2.document))
				{
					topFive.add(curr2.document);
				}
				
				if(it2.hasNext())
				{
					curr2=it2.next();
					
				} else {
					
					curr2 = null;
				}
				

			}
			
		} 
		
		else if(curr1 != null && curr2 != null) //if both lists are not empty
		{
			while(curr1 != null && curr2 != null && topFive.size() != 5)
			{
				// System.out.println("curr1 is: " + curr1 + " and curr2 is: " + curr2);
				
				 if(curr1.frequency > curr2.frequency)
				 {
					 if(topFive.contains(curr1.document) == false)
					 {
						 topFive.add(curr1.document);
					 }
					 
					 if(it1.hasNext()) //iterating curr1 since it's added to the list
					 {
					 	 curr1=it1.next();
					 	 
					 } else {
						 
						 curr1 = null;
					 }
				 }

				 else if(curr1.frequency < curr2.frequency)
				 {
					 if(topFive.contains(curr2.document) == false)
					 {
						 topFive.add(curr2.document);
					 }
					 
					 if(it2.hasNext()) //iterating curr2 since it's added to the list
					 {
					 	 curr2=it2.next();
					 	 
					 } else {
						 
						 curr2 = null;
					 }
					 
				 }	

				 else if(curr1.frequency == curr2.frequency)
				 {
					 if(topFive.contains(curr1.document) == false)
					 {	
						 topFive.add(curr1.document);
					 }
					 
					 if(topFive.contains(curr2.document) == false)
					 {	
						 topFive.add(curr2.document);
					 }
					 //iterating both variables since they've both been added to the list 
					 if(it1.hasNext())
					 {
					 	 curr1=it1.next();
					 	 
					 } else {
						 
						 curr1 = null;
					 }
					 
					 if(it2.hasNext())
					 {
					 	 curr2=it2.next();
					 	 
					 } else {
						 
						 curr2 = null;
					 }
				 } 	 
			}
		}
				
		return topFive;
	
	}
}
