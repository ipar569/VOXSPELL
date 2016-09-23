package prototype;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/* 
 * Author: Injae Park and Jack Huang
 */

public class Main extends JFrame implements ActionListener {

	//Fields defining the buttons to be used
	private JButton quiz = new JButton("New Spelling Quiz");
	private JButton review = new JButton("Review Mistakes");
	private JButton viewStats = new JButton("View Statistics");
	private JButton clearStats = new JButton("Clear Statistics");
	private JPanel titlePanel = new JPanel();
	private JLabel label = new JLabel("Welcome to the Spelling Aid!!");
	private JLabel label2 = new JLabel();

	private JPanel menuPanel = new JPanel();
	private int _level;

	public Main() {

		//Setting the size of the main menu and choosing the layout of it.
		setSize(500,500);
		//Choose default close option.
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		menuPanel.setLayout(new GridLayout(5,1,2,2));

		//Entering the heading of the main menu to make it look more user freindly.

		//label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial",Font.BOLD, 18));
		label.setBounds(120, 10, 500, 60);
		label2.setBounds(230, 50, 200, 30);
		titlePanel.setLayout(null);
		//label2.setVerticalAlignment(SwingConstants.BOTTOM);
		titlePanel.add(label);
		titlePanel.add(label2);
		//Adding the heading label to the main menu
		menuPanel.add(titlePanel);
		//Adding Actionlistener to each buttons
		quiz.addActionListener(this);
		review.addActionListener(this);
		viewStats.addActionListener(this);
		clearStats.addActionListener(this);

		//Adding buttons to the Main menu.
		menuPanel.add(quiz);
		menuPanel.add(review);
		menuPanel.add(viewStats);
		menuPanel.add(clearStats);

		this.add(menuPanel);


	}

	private void levelSelect() {
		String[] levelStrings = { "1", "2", "3", "4", "5", "6", "7", "8", 
				"9", "10", "11" };
		final JComboBox<String> combo = new JComboBox<>(levelStrings);
		String[] options = { "OK" };


		String num = (String) JOptionPane.showInputDialog(this, "Please select a level", "Level Select", 
				JOptionPane.PLAIN_MESSAGE, null, levelStrings, levelStrings[0]);
		if(num==null){
			this.dispose();
		}else{
		_level = Integer.parseInt(num);
		this.setVisible(true);
		setTitle();
		}
	}

	public void setTitle(){
		//label.setText("Welcome to the Spelling Aid Level "+_level+"!!");
		label2.setText("Level "+_level);
	}

	

	public void actionPerformed(ActionEvent e) {
		//Finding the button where the action event occured i.e. finding 
		//the button that is clicked
		JButton button = (JButton) e.getSource();  
		try {

			//If quiz button is clicked
			if (button.equals(quiz)){  

				//If no wordlist is found show error message to user
				File f = new File("NZCER-spelling-lists.txt");
				if(!f.exists()){
					JOptionPane.showMessageDialog(this, "No wordlist file is found!!\n(Please place wordlist file in the working directory)", "Warning", getDefaultCloseOperation());
					//If there is no word inside the lsit
				}else{ 
					WordList word = new WordList("NZCER-spelling-lists.txt");
					if(word.getWordCount(_level)<1){
						JOptionPane.showMessageDialog(this, "No word to be tested!!", "Warning", getDefaultCloseOperation());
					}else{
						//else start the quiz
						setVisible(false);
						Quiz q = new Quiz("NZCER-spelling-lists.txt",this, _level);

						q.setVisible(true);
					}
				}
				return;  
				//If review button is clicked
			}else if (button.equals(review)){  

				File f = new File(".failed"+_level);
				//If failed file does not exist or there is no word inside it
				if(!f.exists()){
					JOptionPane.showMessageDialog(this, "No failed word to be tested!!", "Warning", getDefaultCloseOperation());
				}else{ 

					WordList word = new WordList(".failed",_level);
					if(word.getWordCount(_level)<1){
						JOptionPane.showMessageDialog(this, "No failed word to be tested!!", "Warning", getDefaultCloseOperation());
					}else{
						//else start the review
						setVisible(false);
						Quiz q = new Quiz(".failed",this, _level);
						q.setVisible(true);
					}
				}
				return;  
				//If viewStats button is clicked
			}else if (button.equals(viewStats)){  
				//ViewStats view = new ViewStats();
				//view.setVisible(true);
				makeTable();

				return;  
				//If clearStats button is clicked
			}else if (button.equals(clearStats)){  
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int clear = JOptionPane.showConfirmDialog (this, "Would You Like to Clear the Statistics?","Warning",dialogButton);
				if(clear == JOptionPane.YES_OPTION){
					clearStats();
				}

				return;  
			} 
			//If exception is caught
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
	/*
	 * This functions makes the table using the ViewAccuracy class. Table is placed in a 
	 * new JPanel.
	 */
	protected void makeTable() {
		ViewAccuracy va = new ViewAccuracy();
		JTable table = new JTable(va);
		final JFrame fr = new JFrame();
		fr.setSize(500,500);
		fr.setVisible(true);
		JPanel statsPanel = new JPanel();
		//Add a close button to close the frame
		JButton returnBtn = new JButton("Close Stats");
		statsPanel.setLayout(new BorderLayout());
		statsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		returnBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				fr.dispose();
			}
			
		});
		statsPanel.add(returnBtn, BorderLayout.SOUTH);
		fr.add(statsPanel);
		
	}

	public void nextLevel(){
		_level++;
	}

	/*
	 * main method that brings up the main menu
	 */
	public static void main(String[] agrs){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main frame = new Main();
				frame.createAccuracy();
				frame.levelSelect();
			}
		});
	}

	//This method clears the stats by overwriting existing files that
	//stores information.
	private void clearStats() throws IOException{
		//String array contains the name of the files to be cleared.
		String[] files = {".mastered",".faulted",".failed_total",".failed"};

		for(String f:files){
			//Check if file exist or not and if don't exist, create new one.
			File file = new File(f);
			if(!file.exists()) {
				file.createNewFile();
			} 
			//If exist clear the file by setting append to false.
			Writer output;
			output = new BufferedWriter(new FileWriter(f,false));
			output.close();
		}

		for (int i = 1; i <= 11; i++) {
			File accuracy = new File(".accuracy_" + i);
			accuracy.delete();
		}
		createAccuracy();
	}
	
	//Creates save files to store the accuracy, then add zeros to the file. There is a save 
	//file for each level
	private void createAccuracy() {

		for (int i = 1; i <= 11; i++) {
			try {
				File accuracy = new File(".accuracy_" + i);
				if (! accuracy.exists()) {
					accuracy.createNewFile();

					FileWriter fw = new FileWriter(accuracy);
					BufferedWriter bw = new BufferedWriter(fw);

					bw.write("0" + "\n");
					bw.write("0" + "\n");

					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}