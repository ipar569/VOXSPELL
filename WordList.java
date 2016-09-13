import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


//Written by Injae Park
public class WordList {
	
	private ArrayList<String> _wordList=new ArrayList<String>();
	
	public WordList(String file) throws IOException{
		
			BufferedReader wordlist = new BufferedReader(new FileReader("."+File.separator+file));
			
			String line;
			
			//Reading word where and adding it to arrayList if it is not an empty line
			while ((line = wordlist.readLine()) != null){
				if(!"".equals(line.trim())){
					_wordList.add(line);
				}
			}
	}
	
	//Return wordCount
	public int getWordCount(){
		return _wordList.size();
	}
	
	//Get random word from the arrayList
	public String getRandomWord() throws IOException{
		Random r = new Random();
		
		//Getting random position
		int rand = Math.abs(r.nextInt()) % this.getWordCount();
		
		return _wordList.get(rand);
	}
	
	//Sort the list and return the word at the position specified.
	public String getSortedWord(int a){
		Collections.sort(_wordList);
		return _wordList.get(a);
	}
}
