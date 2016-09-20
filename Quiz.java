package prototype;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

/*
 * written by Injae Park
 */

public class Quiz extends JFrame implements ActionListener {
	
	private JTextField txt = new JTextField("");
	private JButton btn = new JButton("Submit");
	private JButton speak = new JButton("Listen Again");
	private JButton spelling = new JButton("Listen to Spelling");
	private int _testNo=1;
	private int _wc;
	private JLabel label,label1,label2;
	private int incorrect;
	private String _file;
	private Main _main;
	private ArrayList<String> _testList = new ArrayList<String>();
	private int _maxNum;
	private int _level;

	private int _attempts;
	private int _fails;
	private int _correct;
	private String _voice;
	private ArrayList<String> _voices;
	private JComboBox<String> _selectVoices;
	
	//Constructor takes two input. One file name contained the wordlist and second is 
	//the object where the quiz is excuted.
	public Quiz(String file,Main main,int level) throws  Exception {
		//Initialising field variables using arguments
		_main=main;
		_file=file;
		_level = level;

		getAccuracy();
		
		_selectVoices = selectVoice();
		_voice = _voices.get(0);
		
		//Setting the size and layout of the spelling quiz
		setSize(500,500);
		setLayout(new GridLayout(4,1));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		changeVoice("voice_akl_nz_jdt_diphone");
		//Creating Panels to be used.
		JPanel middle = new JPanel();
		JPanel bottom = new JPanel();

		JLabel label3 = new JLabel("Change Voice: ");
		//Setting the heading of the quiz using label.
		if(file.equals(".failed")){
			label1 = new JLabel("Review Mistakes");
		}else{
			label1 = new JLabel("New Spelling Quiz!!");
		}
		//Defining the font style of the heading
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setFont(new Font("Arial",Font.BOLD, 18));
		
		WordList wordlist;
		//Creaeting the wordlist using the file name
		if(file.equals("NZCER-spelling-lists.txt")){
			wordlist = new WordList(_file);
			_maxNum = 10;
		}else{
			wordlist = new WordList(file,level);
			_maxNum = 5;
		}//Getting the word count.
		_wc = wordlist.getWordCount(level);
		
		//Choosing the number of quiz depending on the word count
		String tts = "";
		if(_wc<_maxNum){
			tts="Spell word 1 of "+_wc+": ";
			label = new JLabel(tts);
		}else{
			tts="Spell word 1 of "+_maxNum+": ";
			label = new JLabel(tts);
		}
		
		//Word to be tested
		setTestList(wordlist);
		
		//Setting the size of the JText field.
		txt.setPreferredSize(new Dimension(200,30));
		
		//Initialising label2
		label2 = new JLabel("");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Adding label and buttons to the middle pane.
		middle.add(label);
		middle.add(txt);
		
		middle.add(btn);
		
		//If it is for reivew, add speak button
		middle.add(speak);
		if(_maxNum==5){
			middle.add(spelling);
		}

		//Adding labels and pane to the main frame.
		add(label1);
		add(middle);
		add(label2);
		bottom.add(label3);
		bottom.add(_selectVoices);
		add(bottom);
		
		_selectVoices.addActionListener(this);
		
		//Adding action listeners to the button.
		btn.addActionListener(this);
		speak.addActionListener(this);
		spelling.addActionListener(this);
		
		//Speaking out instruction to start and the word to be tested.
		festival(_testList.get(_testNo-1));
		
	}

	public void actionPerformed(ActionEvent e) {
		//Getting the word that user wrote
		String word = txt.getText();
		
		if(e.getSource().equals(_selectVoices)){
		String data = (String) _selectVoices.getItemAt(_selectVoices.getSelectedIndex());
		_voice = data;
		return;
		}
		try{
			//If user pressed speak button, spelling of the word
			//is spoken by festival.
			JButton button = (JButton) e.getSource();  
				if (button.equals(speak)){  

					festival(_testList.get(_testNo-1));
					return;
				}else if (button.equals(spelling)){
					festivalAlphabet(_testList.get(_testNo-1));
					return;
				}
			//If user is correct
			if(_testList.get(_testNo-1).equalsIgnoreCase(word)){
				//Showing and telling correct message
				label2.setText("Correct!!");
				
				//Remove word from failed test list
				removeFailed(_testList.get(_testNo-1));
				
				_attempts++;
				
				//If user gets incorrect first time, the word is added to faulted list
				/*if(incorrect==1){
					faulted();
				//else the word is added to teh mastered list
				}else{
					mastered();
				} */
				
				//Increase the test number
				_attempts++;
				_testNo++;
				_correct++;
				
				//Setting the new label
				label.setText("Spell word "+(_testNo)+" of "+_maxNum+": ");
				if(_wc<_maxNum)
					label.setText("Spell word "+(_testNo)+" of "+ _wc+": ");
				
				
				incorrect =0;
			//If user gets incorrect
			}else{
				//If second time failing
				if(incorrect<1){
					//Setting message to the user about the fault
					label2.setText("Incorrect, please try again!");
					festival(label2.getText()+"\n "+_testList.get(_testNo-1));
					//Word is spoken again.
					incorrect++;
					return;
				//First time failing
				}else{
					//Result message to user
					label2.setText("Failed Test!");

					_attempts++;
					_fails++;
					
					//Adding failed word to the failed list.
					failed();
					failedTotal();
					
					//Changing field as needed
					
					_testNo++;
					incorrect =0;
					
					//Setting new label for new quiz
					label.setText("Spell word "+(_testNo)+" of "+_maxNum+": ");
					if(_wc<_maxNum)
						label.setText("Spell word "+(_testNo)+" of "+ _wc+": ");

				}
			}

			updateAccuracy();
			//Clearing the Jtext field
			txt.setText("");
			//If test is finished
			if((_testNo==_maxNum+1)||(_wc<_testNo)){
				//Telling the user the teset is finished
				label2.setText(label2.getText()+" Quiz Finished!!");
				festival(label2.getText());
				if(_maxNum==5){
				//Bring back the main menu
				_main.setVisible(true);
				dispose();
				}else{
					SubMenu sub = new SubMenu(_file,_main,_level,_correct,_testNo-1);
					sub.setVisible(true);
					dispose();
				}
				
			}else{
				//Continue the quiz
				festival(label2.getText()+"..."+_testList.get(_testNo-1));
			}
		}catch(Exception excep){
			excep.printStackTrace();
		}
	}
	
	private JComboBox<String> selectVoice() throws Exception{

		Festival f = new Festival("","");
		_voices = f.listOfVoices();
		
		String[] str = new String[_voices.size()];
		for(int i = 0;i<_voices.size();i++)str[i]=_voices.get(i);

		
		JComboBox<String> voices = new JComboBox<String>(str);
		
		
		return voices;
	}
	
	private void changeVoice(String voice) throws IOException{
		File failed = new File(".festivalrc");
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		
		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(failed,false)); 
		output.append("(set! voice_default '"+voice +")");
		output.close();
	}
	
	/*
	//Method that tells u if a string contains something other than alphabet
	private boolean onlyAlphabet(String s){
		return s.matches("[a-zA-Z]+");
	}
	*/
	
	//Method that uses festival to speak out the string passed into it
	private void festival(String tts) throws Exception{
		Festival say = new Festival(tts,_voice);
		say.execute();
	
	}
	
	//Speaking out the spelling of the word passed into it
	private void festivalAlphabet(String tts) throws Exception{
		String word="";
		String[] alpha = tts.split("");
		
		for(int i=0;i<alpha.length;i++)
			 word = word + alpha[i]+" ";
		festival(word);
	}
	
	/*
	//Setting the word to be tested using getRandomWord method from word list
	private void setWord() throws IOException{
		WordList wordlist = new WordList(_file);
		_testWord = wordlist.getRandomWord(1);	
	}
	*/
	
	private void setTestList(WordList wordlist) throws IOException{
		_testList = wordlist.createTestList(_level,_maxNum);	
	}
	

	private void getAccuracy() throws IOException {
		File accuracy = new File(".accuracy_" + _level);
		if (! accuracy.exists()) {
			accuracy.createNewFile();
		} else {
		
			FileReader fr = new FileReader(accuracy);
			BufferedReader br = new BufferedReader(fr);
			String str;
			str = br.readLine();
			_attempts = Integer.parseInt(str);
			str = br.readLine();
			_fails = Integer.parseInt(str);
			
		}
	}
	private void updateAccuracy() throws IOException {
		File accuracy = new File(".accuracy_" + _level);
	
		PrintWriter pw = new PrintWriter(accuracy);
		pw.close();
		
		FileWriter fw = new FileWriter(accuracy);
		BufferedWriter bw = new BufferedWriter(fw);
		
		bw.write(_attempts + "\n");
		bw.write(_fails + "\n");
		bw.close();
	}
	
	//Putting Failed word into failed list
	private void failed() throws IOException{
		File failed = new File(".failed"+_level);
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		
		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(failed,true)); 
		output.append(_testList.get(_testNo-1)+"\n");
		output.close();
	}
	
	private void removeFailed(String word) throws IOException{
		File failed = new File(".failed"+_level);
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		//Creating temporary file to store data
		File temp = new File(".temp");
		if(!temp.exists())
			temp.createNewFile();
		
		//Choosing input and output files
		BufferedReader input = new BufferedReader(new FileReader("."+File.separator+failed));
		PrintWriter output= new PrintWriter(new FileWriter("."+File.separator+temp));
		
		String line;
		
		//Reading word where and adding it to arrayList if it is not an empty line
		while ((line = input.readLine()) != null){
			//If the line does not equal to line to remove, it is copied to temp file
			if(!word.equalsIgnoreCase(line.trim())){
				output.println(line);
				output.flush();
			}	
		}
		
		//Closing input output streams
		input.close();
		output.close();
		
		//Delete orginal file
		failed.delete();
		
		//Changing output file to be the failed list file.
		temp.renameTo(failed);
	}
	
	//Adding failed word to the failed_total list
	private void failedTotal() throws IOException{
		File failed = new File(".failed_total"+_level);
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		
		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(failed,true)); 
		output.append(_testList.get(_testNo-1)+"\n");
		output.close();
	}
	
protected void createAccuracy() throws IOException {
		
		for (int i = 1; i <= 11; i++) {
			File accuracy = new File(".accuracy_" + i);
			if (! accuracy.exists()) {
				accuracy.createNewFile();
				
				FileWriter fw = new FileWriter(accuracy);
				BufferedWriter bw = new BufferedWriter(fw);
				
				bw.write("0" + "\n");
				bw.write("0" + "\n");
				
				bw.close();
			}

		}
	}

}
