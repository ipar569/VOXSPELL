package prototype;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


//Written by Injae Park
public class ViewStats extends JFrame implements ActionListener{
	
	private JButton btn = new JButton("  Exit  ");
	private JTable table;
	
	public ViewStats() throws IOException{
		//Setting the size of this frame
		setSize(500,500);
		setLayout(new BorderLayout());
		
		//Setting 2 panels
		JPanel down = new JPanel();
		JPanel middle = new JPanel();

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//Add actionListener to the button
		btn.addActionListener(this);
		
		//Create stats table
		createTable();
		
		//Butting the table into scrollPane which looks better
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		
		//Adding scroll to the panel
		middle.add(scrollPane);
		//adding button to the bottom panel
		down.add(btn);
		
		//Adding panels to the frame
		add(middle,BorderLayout.CENTER);
		add(down,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//When button is clicked close the view window.
		dispose();
	}
	
	//Method creates a Jtable
	private void createTable() throws IOException{
		//Setting the headings of each column
		String[] heading = {"Words","Mastered","Faulted","Failed"};
		
		//List of words to be added to the table
		WordList wordlist = new WordList("wordlist");
		int wc = wordlist.getWordCount(1);
		
		//Initialising array to store data
		Object[][] data = new Object[wc][4];
		
		//Setting the data
		for(int i=0;i<wc;i++){
			data[i]=insertData(wordlist.getSortedWord(i));
		}
		
		//Adding heading and the data to the table
		table=new JTable(data,heading);
	}
	
	//Method to get data from each file
	private Object[] insertData(String word) throws IOException{
		Object[] row = new Object[4];
		row[0] = word;
		//Files to be accessed
		String[] file ={".mastered",".faulted",".failed_total"};
		
		//Creating new file if the file doees not exist
		for(String f:file){
		File files = new File(f);
			if(!files.exists()) {																
				files.createNewFile();
			} 
		}
		
		//Reading the file and counting specific word passed in.
		for(int i=0;i<3;i++){
			BufferedReader wordlist = new BufferedReader(new FileReader("."+File.separator+file[i]));
			String line;
			int wordCount=0;
			while ((line = wordlist.readLine()) != null){
				if(word.equalsIgnoreCase(line)){
					wordCount++;
				}
			}
			row[i+1]=wordCount;
		}
		
		
		return row;
	}
}
