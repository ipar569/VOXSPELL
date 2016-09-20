package prototype;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SubMenu extends JFrame implements ActionListener{

	private JButton backMain = new JButton("Go Back to Main Menu");
	private JButton nextLevel = new JButton("Go to Next Level");
	private JButton repeat = new JButton("Repeat Quiz");
	private JButton video = new JButton("Play Video!!");
	
	private JPanel menuPanel = new JPanel();
	private int _level;
	private Main _main;
	private String _file;
	public SubMenu(String file, Main main, int level, int correct, int testNum){
		//Setting the size of the main menu and choosing the layout of it.
				_file = file;
				_main = main;
				_level = level;
		
		
		setSize(500,500);
		//Choose default close option.
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				menuPanel.setLayout(new GridLayout(5,1,2,2));
				
				//Entering the heading of the main menu to make it look more user freindly.
				JLabel label = new JLabel("The Quiz is finished! You got "+correct+" out of "+testNum+"!!");
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("Arial",Font.BOLD, 18));
				
				//Adding the heading label to the main menu
				menuPanel.add(label);
				//Adding Actionlistener to each buttons
				backMain.addActionListener(this);
				
				repeat.addActionListener(this);
				
				nextLevel.addActionListener(this);
				
				video.addActionListener(this);
				
				//Adding buttons to the Main menu.
				menuPanel.add(backMain);
				menuPanel.add(repeat);
				
				if(correct>=0){
					menuPanel.add(nextLevel);
					menuPanel.add(video);
				}
				
				this.add(menuPanel);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//Finding the button where the action event occured i.e. finding 
		//the button that is clicked
		try{
		JButton button = (JButton) e.getSource();
		if (button.equals(backMain)){  
			_main.setVisible(true);
			dispose();
		}else if(button.equals(repeat)){
			dispose();
			Quiz q = new Quiz("wordlist",_main, _level);
			q.setVisible(true);
		}else if(button.equals(nextLevel)){
			_main.setVisible(true);
			_main.nextLevel();
			_main.setTitle();
			dispose();
		}else if(button.equals(video)){
			MediaPlayer player = new MediaPlayer();
		}
		}catch(Exception e2){
			e2.printStackTrace();
		}
	}

}
