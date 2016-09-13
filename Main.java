import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/* 
 * Author: Injae Park
 */

public class Main extends JFrame implements ActionListener {
	
	//Fields defining the buttons to be used
	private JButton quiz = new JButton("New Spelling Quiz");
	private JButton review = new JButton("Review Mistakes");
	private JButton viewStats = new JButton("View Statistics");
	private JButton clearStats = new JButton("Clear Statistics");
	
	public Main() {
		
		//Setting the size of the main menu and choosing the layout of it.
		setSize(500,500);
		setLayout(new GridLayout(5,1,2,2));
		//Choose default close option.
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//Entering the heading of the main menu to make it look more user freindly.
		JLabel label = new JLabel("Welcome to the Spelling Aid!!");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Arial",Font.BOLD, 18));
		
		//Adding the heading label to the main menu
		add(label);
		
		//Adding Actionlistener to each buttons
		quiz.addActionListener(this);
		review.addActionListener(this);
		viewStats.addActionListener(this);
		clearStats.addActionListener(this);
		
		//Adding buttons to the Main menu.
		add(quiz);
		add(review);
		add(viewStats);
		add(clearStats);
		
	}

	public void actionPerformed(ActionEvent e) {
		//Finding the button where the action event occured i.e. finding 
		//the button that is clicked
		JButton button = (JButton) e.getSource();  
		try {
			
			//If quiz button is clicked
			if (button.equals(quiz)){  
				
				//If no wordlist is found show error message to user
				File f = new File("wordlist");
				if(!f.exists()){
					JOptionPane.showMessageDialog(this, "No wordlist file is found!!\n(Please place wordlist file in the working directory)", "Warning", getDefaultCloseOperation());
				//If there is no word inside the lsit
				}else{ 
					WordList word = new WordList("wordlist");
					if(word.getWordCount()<1){
						JOptionPane.showMessageDialog(this, "No word to be tested!!", "Warning", getDefaultCloseOperation());
					}else{
					//else start the quiz
					setVisible(false);
					Quiz q = new Quiz("wordlist",this);
					q.setVisible(true);
					}
				}
				return;  
			//If review button is clicked
			}else if (button.equals(review)){  
				
				File f = new File(".failed");
				//If failed file does not exist or there is no word inside it
				if(!f.exists()){
					JOptionPane.showMessageDialog(this, "No failed word to be tested!!", "Warning", getDefaultCloseOperation());
				}else{ 
					WordList word = new WordList(".failed");
					if(word.getWordCount()<1){
						JOptionPane.showMessageDialog(this, "No failed word to be tested!!", "Warning", getDefaultCloseOperation());
					}else{
					//else start the review
					setVisible(false);
					Quiz q = new Quiz(".failed",this);
					q.setVisible(true);
					}
				}
				return;  
			//If viewStats button is clicked
			}else if (button.equals(viewStats)){  

				ViewStats view = new ViewStats();
				view.setVisible(true);

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
	 * main method that brings up the main menu
	 */
	public static void main(String[] agrs){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Main frame = new Main();
				frame.setVisible(true);
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
	}
}