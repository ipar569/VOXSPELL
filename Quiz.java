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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/*
 * written by Injae Park
 */

public class Quiz extends JFrame implements ActionListener {
	
	private JTextField txt = new JTextField("");
	private JButton btn = new JButton("Submit");
	private JButton speak = new JButton("Listen to Spelling");
	private int _testNo=1;
	private int _wc;
	private JLabel label,label1,label2;
	private String _testWord;
	private int incorrect;
	private String _file;
	private Main _main;
	
	//Constructor takes two input. One file name contained the wordlist and second is 
	//the object where the quiz is excuted.
	public Quiz(String file,Main main) throws  Exception {
		//Initialising field variables using arguments
		_main=main;
		_file=file;
		
		
		//Setting the size and layout of the spelling quiz
		setSize(500,500);
		setLayout(new GridLayout(3,1));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Creating Panels to be used.
		JPanel middle = new JPanel();

		//Setting the heading of the quiz using label.
		if(file.equals(".failed")){
			label1 = new JLabel("Review Mistakes");
		}else{
			label1 = new JLabel("New Spelling Quiz!!");
		}
		//Defining the font style of the heading
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setFont(new Font("Arial",Font.BOLD, 18));
		
		//Creaeting the wordlist using the file name
		WordList wordlist = new WordList(_file);
		//Getting the word count.
		_wc = wordlist.getWordCount();
		
		//Choosing the number of quiz depending on the word count
		String tts = "";
		if(_wc<3){
			tts="Spell word 1 out of "+_wc+": ";
			label = new JLabel(tts);
		}else{
			tts="Spell word 1 out of 3: ";
			label = new JLabel(tts);
		}
		
		//Word to be tested
		setWord();
		
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
		if(_file.equals(".failed")){
			middle.add(speak);
		}

		//Adding labels and pane to the main frame.
		add(label1);
		add(middle);
		add(label2);
		
		//Adding action listeners to the button.
		btn.addActionListener(this);
		speak.addActionListener(this);
		
		//Speaking out instruction to start and the word to be tested.
		festival(tts);
		festival(_testWord);
	}

	public void actionPerformed(ActionEvent e) {
		//Getting the word that user wrote
		String word = txt.getText();
		boolean b = onlyAlphabet(word);
		
		if(b==false){
			JOptionPane.showMessageDialog(this, "Everything other than alphabet is invalid(even spaces)!! Please, try again!!", "Warning", 0);
			return;
		}
			
		
		try{
			//If user pressed speak button, spelling of the word
			//is spoken by festival.
			JButton button = (JButton) e.getSource();  
				if (button.equals(speak)){  

				festivalAlphabet(_testWord);
				return;
				}
			//If user is correct
			if(_testWord.equalsIgnoreCase(word)){
				//Showing and telling correct message
				label2.setText("Correct!!");
				festival(label2.getText());
				
				
				
				//Increase the test number
				_testNo++;
				
				//Setting the new label
				label.setText("Spell word "+(_testNo)+" out of 3: ");
				if(_wc<3)
					label.setText("Spell word "+(_testNo)+" out of "+ _wc+": ");
				
				//If user gets incorrect first time, the word is added to faulted list
				if(incorrect==1){
					faulted();
				//else the word is added to teh mastered list
				}else{
					mastered();
				}
				
				//New word is set
				setWord();
				
				//Remove word from failed test list
				removeFailed(_testWord);

				incorrect =0;
			//If user gets incorrect
			}else{
				//If second time failing
				if(incorrect<1){
					//Setting message to the user about the fault
					label2.setText("Incorrect, please try again!!");
					festival(label2.getText());
					//Word is spoken again.
					festival(_testWord);
					incorrect++;
					return;
				//First time failing
				}else{
					//Result message to user
					label2.setText("Failed Test");
					festival(label2.getText());
					
					//Changing field as needed
					_testNo++;
					incorrect =0;
					
					//Setting new label for new quiz
					label.setText("Spell word "+(_testNo)+" out of 3: ");
					if(_wc<3)
						label.setText("Spell word "+(_testNo)+" out of "+ _wc+": ");
					
					
					
					//Adding failed word to the failed list.
					failed();
					failedTotal();
					
					//Setting new word to be tested.
					setWord();

				}
			}
			
			//Clearing the Jtext field
			txt.setText("");
			//If test is finished
			if((_testNo==4)||(_wc<_testNo)){
				//Telling the user the teset is finished
				label2.setText("Quiz Finished!!");
				festival(label2.getText());
				
				//Bring back the main menu
				_main.setVisible(true);
				dispose();
			}else{
				//Continue the quiz
				festival(label.getText());
				festival(_testWord);
			}
		}catch(Exception excep){
			excep.printStackTrace();
		}
	}
	//Method that tells u if a string contains something other than alphabet
	private boolean onlyAlphabet(String s){
		return s.matches("[a-zA-Z]+");
	}
	//Method that uses festival to speak out the string passed into it
	private void festival(String tts) throws Exception{
		//command to be excuted.
		String cmd = "echo "+tts+"| festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		//Excute the command
		Process process = builder.start();
		//Wait for the previous process to be finihsed
		process.waitFor();
	
	}
	
	//Speaking out the spelling of the word passed into it
	private void festivalAlphabet(String tts) throws Exception{
		
		String[] alpha = tts.split("");
		for(int i=0;i<alpha.length;i++)
			festival(alpha[i]);
	}
	
	//Setting the word to be tested using getRandomWord method from word list
	private void setWord() throws IOException{
		WordList wordlist = new WordList(_file);
		_testWord = wordlist.getRandomWord();	
	}
	
	//Putting Failed word into failed list
	private void failed() throws IOException{
		File failed = new File(".failed");
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		
		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(failed,true)); 
		output.append(_testWord+"\n");
		output.close();
	}
	
	private void removeFailed(String word) throws IOException{
		File failed = new File(".failed");
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
		File failed = new File(".failed_total");
		//If file does not exist, create new file
		if(!failed.exists()) {
			failed.createNewFile();
		} 
		
		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(failed,true)); 
		output.append(_testWord+"\n");
		output.close();
	}
	
	//Adding correct word to mastered list
	private void mastered() throws IOException{
		File mastered = new File(".mastered");
		//If file does not exist, create new file
		if(!mastered.exists()) {
			mastered.createNewFile();
		} 

		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(mastered,true));
		output.append(_testWord+"\n");
		output.close();
	}

	//Adding correct word to faulted list
	private void faulted() throws IOException{
		File faulted = new File(".faulted");
		//If file does not exist, create new file
		if(!faulted.exists()) {
			faulted.createNewFile();
		} 

		//Appending the word to the file
		Writer output;
		output = new BufferedWriter(new FileWriter(faulted,true));
		output.append(_testWord+"\n");
		output.close();
	}
}
