package prototype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;


//Written by Injae Park
public class WordList {
	
	private ArrayList<String> _wordList=new ArrayList<String>();
	private int[] pos = new int[11];
	
	public WordList(String file) throws IOException{
		
			BufferedReader wordlist = new BufferedReader(new FileReader("."+File.separator+file));
			
			String line;
			int a=0;
			//Reading word where and adding it to arrayList if it is not an empty line
			while ((line = wordlist.readLine()) != null){
				if(!"".equals(line.trim())){
					_wordList.add(line);
					String[] lines = line.split("");
					if(lines[0].equals("#")){
						pos[a] = _wordList.size()-1;
						a++;
					}
				}
			}
	}
	
	//Return wordCount for each level.
	public int getWordCount(int level){
		return pos[level]-pos[level-1];
	}
	
	//Get random word from the arrayList
	public String getRandomWord(int level){
		Random r = new Random();
		
		//Getting random position
		int rand = Math.abs(r.nextInt()) % this.getWordCount(1);
		
		return _wordList.get(rand+pos[level-1]);
	}
	
	//Sort the list and return the word at the position specified.
	public String getSortedWord(int a){
		Collections.sort(_wordList);
		return _wordList.get(a);
	}
	
	public ArrayList<String> createTestList(int level, int num){
		LinkedHashSet<String> list = new LinkedHashSet<String>();
		while(list.size()<num||list.size()==getWordCount(level))
			list.add(getRandomWord(level));
		
		ArrayList<String> testList = new ArrayList<String>();
		testList.addAll(list);
		return testList;
	}
	
}
