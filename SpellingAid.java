import java.awt.BorderLayout;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class SpellingAid extends JFrame implements ActionListener{
	private String _path;
	private Quiz _currentQuiz;

	private List<String> _wordList;
	private List<String> _fileNames;
	private List<String> _failedWords;
	private ArrayList<Integer> _masterList = new ArrayList<Integer>();
	private ArrayList<Integer> _faultList = new ArrayList<Integer>();
	private ArrayList<Integer> _failList = new ArrayList<Integer>();
	private ArrayList<Integer> _attemptList = new ArrayList<Integer>();

	private JTextField txt = new JTextField("");
	private JButton newQuizBtn = new JButton("New Quiz");
	private JButton reviewBtn = new JButton("Review Mistakes");
	private JButton statsBtn = new JButton("View Statistics");
	private JButton clearBtn = new JButton("Clear Statistics");
	private JButton submitBtn = new JButton("Submit");
	private JButton returnBtn = new JButton("Return to Menu");


	private JLabel _menuLabel = new JLabel("Welcome to Spelling Aid");
	private JLabel _spellLabel = new JLabel("Please Spell");
	private JLabel _statLabel = new JLabel("Statistics");
	private JLabel _titleLabel = new JLabel("");

	private JPanel menuPanel = new JPanel();
	private JPanel quizPanel = new JPanel();
	private JPanel submitPanel = new JPanel();
	private JPanel _topPanel = new JPanel();



	public enum Stat { MASTER, FAULT, FAIL }
	public enum Modes {NORMAL, REVIEW};


	public SpellingAid() {
		//create the main frame
		super("Spelling Aid");
		setSize(400, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Make the menu panel
		menuPanel.setLayout(new GridLayout(5,1));

		_menuLabel.setFont(new Font("Verdana", 1, 20));
		newQuizBtn.addActionListener(this); 
		reviewBtn.addActionListener(this);
		statsBtn.addActionListener(this);
		clearBtn.addActionListener(this);
		menuPanel.add(_menuLabel);
		menuPanel.add(newQuizBtn);
		menuPanel.add(reviewBtn);
		menuPanel.add(statsBtn);
		menuPanel.add(clearBtn);

		add(menuPanel);
		
		//This contains the logic for the submit button.
		Action action = new AbstractAction() { 
			public void actionPerformed(ActionEvent e) {
				String speltWord = txt.getText();
				if (!speltWord.matches("[a-zA-Z]+")) {
					JOptionPane.showMessageDialog(new JPanel(), "Input contains non-alphabetical characters. Please try again");
				} else {
					if (_currentQuiz != null) {
						_currentQuiz.checkSpelling(speltWord);
						if (_currentQuiz.canBeReviewed()){ //ask if user want to hear spelling  (only in review mode)
							int n = JOptionPane.showConfirmDialog(new JPanel(), "Do you want to hear the spelling?", 
									"Hear Spelling", JOptionPane.YES_NO_OPTION);
							if (n == JOptionPane.YES_OPTION) {
								_currentQuiz.spellWord();
							} else if  (! _currentQuiz.isFinished()) { //if no ask another question (if quiz not finished)
								_currentQuiz.askQuestion();
							} else if (_currentQuiz.isFinished()){
								menu();
							}
						} else if  ((! _currentQuiz.isFinished()) && (! _currentQuiz.isFault())) { //ask another question - if user mastered or failed word
							_currentQuiz.askQuestion();
						} else if (_currentQuiz.isFinished()){ //return to menu after finishing word
							menu();
						}
					} //note word spelt word incorrect first time (faulted), checkSpelling() method will take care of it
				}
				txt.setText("");
			}
		};
		//quiz panel is the panel that is used when taking a quiz
		quizPanel.setLayout(new GridLayout(2,1));
		//Create and add header Label and spelling label
		_topPanel.setLayout(null);
		_spellLabel.setBounds(10, 120, 200, 50);
		_titleLabel.setBounds(150, 20, 200, 70);
		_titleLabel.setFont(new Font("Verdana", 1, 20));
		_topPanel.add(_titleLabel);
		_topPanel.add(_spellLabel);
		quizPanel.add(_topPanel);
		
		//Create and add textfield and a submit button
		submitPanel.setLayout(null);
		txt.setBounds(10, 10, 380, 30);
		txt.addActionListener(action); //Add that custom actionListener to textfield
		submitPanel.add(txt);
		submitBtn.setBounds(100, 70, 200, 30);
		submitBtn.addActionListener(action); //Add that custom actionListener to submit button
		submitPanel.add(submitBtn);
		quizPanel.add(submitPanel);
	

	}
	/**
	 * This method creates the menu panel again
	 */
	private void menu() {
		getContentPane().removeAll();
		getContentPane().add(menuPanel);
		revalidate();
		repaint();
	}
	/**
	 * This method makes the table used in View Statistics
	 */
	private void makeTable() {
		ModelTable mt = new ModelTable(); //Make custom table to show the words and stats
		JTable table = new JTable(mt);
		
		//Add a sorter for the table
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		
		//Sort words in alphabetical order
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		//Create panels for Statistics. Add table to panel.
		JPanel statsPanel = new JPanel();
		statsPanel.setLayout(new BorderLayout());
		statsPanel.add(_statLabel, BorderLayout.NORTH);
		statsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		returnBtn.addActionListener(this);
		statsPanel.add(returnBtn, BorderLayout.SOUTH);
		
		//replace current panel with the Stats panel
		getContentPane().removeAll();
		getContentPane().add(statsPanel);
		revalidate();
		repaint();
	}
	/**
	 * This method creates the necessary files to store the stats and the failed words
	 */
	private void createFiles() {
		_path = Paths.get(".").toAbsolutePath().normalize().toString(); //get path of current directory
		String[] fileNames = {".mastered_stat", ".faulted_stat", ".failed_stat", ".attempts"};
		_fileNames = Arrays.asList(fileNames);
		try {
			_wordList = Files.readAllLines(Paths.get(_path + "/wordlist"), StandardCharsets.UTF_8); //add words from wordlist to a list
			int lineNum = _wordList.size();
			//Make the files (if they don't already exist)
			File file1 = new File(_path + "/.failed_words.txt");
			if (! file1.exists()) {
				file1.createNewFile();
			}
			_failedWords = Files.readAllLines(Paths.get(_path + "/.failed_words.txt"), StandardCharsets.UTF_8); //add words from failed word list to a list
			for (String name : _fileNames) {
				File file = new File(_path + "/" + name + ".txt");
				if (! file.exists()) {
					file.createNewFile();

					FileWriter fw = new FileWriter(file.getAbsolutePath());
					BufferedWriter bw = new BufferedWriter(fw);
					//Write zeros to the stats files
					for (int i = 0; i < lineNum; i++) {
						bw.write("0\n");
					}
					bw.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillArrays(".mastered_stat", _masterList);
		fillArrays(".faulted_stat", _faultList);
		fillArrays(".failed_stat", _failList);
		fillArrays(".attempts", _attemptList);
	}
	/**
	 * This function takes integer values from stats files and adds them to a list
	 */
	private void fillArrays (String fileName, ArrayList<Integer> list) {
		try {
			FileReader fr = new FileReader(_path + "/" + fileName + ".txt");
			BufferedReader br = new BufferedReader(fr);
			String str;
			while ((str = br.readLine()) != null) {
				list.add(Integer.parseInt(str));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function updates the stats of a given word. Depending on what stat (mastered, faulted, failed)
	 * it needs to update, it will select the appropriate file and list and makes changes to it. 
	 */
	protected void update (String word, Stat stat) {
		int index = 0;
		String fileName = "";
		ArrayList<Integer> list =null;
		if (_wordList.contains(word)) {
			index = _wordList.indexOf(word); //get index of word.
		}
		switch (stat) { //get the appropriate file name corresponding list
		case MASTER:
			fileName = ".mastered_stat.txt";
			list = _masterList;
			break;
		case FAULT:
			fileName = ".faulted_stat.txt";
			list = _faultList;
			break;
		case FAIL:
			fileName = ".failed_stat.txt";
			list = _failList;
			if (! _failedWords.contains(word)) {
				_failedWords.add(word);
				updateStringFile(_failedWords, ".failed_words.txt");
			}
			break;
		}
		int temp = list.get(index);
		temp++;
		list.set(index, temp); //add '1' to the corresponding index of that word in the list
		updateIntegerFile(list, fileName); //update that file
		
		int temp2 = _attemptList.get(index);
		temp2++;
		_attemptList.set(index, temp2); //do the same for the attempts list
		updateIntegerFile(_attemptList, ".attempts.txt"); //update that file
	}
	/**
	 * This function clears a file and writes the contents of a string list to the file
	 */
	protected void updateStringFile (List<String> list, String name) {
		List <String> list1 = list;
		try {
			PrintWriter pw = new PrintWriter(_path + "/" + name);
			pw.close();

			FileWriter fw = new FileWriter(_path + "/" + name);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String str : list1) {
				bw.write(str + "\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * This function clears a file and write the contents of an integer list to the file
	 */
	protected void updateIntegerFile (ArrayList<Integer> list, String name) {
		ArrayList<Integer> list1 = list;
		try {
			PrintWriter pw = new PrintWriter(_path + "/" + name);
			pw.close();

			FileWriter fw = new FileWriter(_path + "/" + name);
			BufferedWriter bw = new BufferedWriter(fw);

			for (int i : list1) {
				bw.write(i + "\n");
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Removes a word from fail list and updates list of failed words file
	 */
	public void removeFromFail(String word) {
		if (_failedWords.contains(word)) {
			_failedWords.remove(word);
			updateStringFile(_failedWords, ".failed_words.txt");
		}
	}
	
	/**
	 * This file deletes all save files and recreates the files. It also resets the field lists.
	 */
	public void clearFiles () {

		for (String str : _fileNames) {
			File file = new File(_path + "/" + str + ".txt");
			file.delete();
		}
		File file1 = new File(_path + "/.failed_words.txt");
		file1.delete();
		//clear lists
		_masterList.clear();
		_faultList.clear();
		_attemptList.clear();
		_failList.clear();
		_failedWords.clear();
		
		createFiles();
	}

	/**
	 * This function provides logic to the buttons in the app when they are clicked
	 * (excluding the submit button - because it's special)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		//if the quiz button the pressed create a new quiz object, and ask a question.
		if (source.equals(newQuizBtn)) {
			getContentPane().removeAll();
			getContentPane().add(quizPanel); //change panel to a quiz panel
			revalidate();
			repaint();

			_currentQuiz = new Quiz(Modes.NORMAL); //set mode to normal
			_currentQuiz.askQuestion();

		}  else if (source.equals(reviewBtn)) { //if review button pressed, create new quiz object with review mode
			if (_failedWords.size() == 0) { //check if there are any failed words
				JOptionPane.showMessageDialog(this, "No failed words. Please do more quizzes");
			} else {
				getContentPane().removeAll();
				getContentPane().add(quizPanel);
				revalidate();
				repaint();

				_currentQuiz = new Quiz(Modes.REVIEW);
				_currentQuiz.askQuestion();
			}
		} else if (source.equals(statsBtn)) { //make table and change the panel if stats button is pressed
			makeTable();
		} else if (source.equals(returnBtn)){ //return button takes user back to menu
			menu();
		} else if (source.equals(clearBtn)) { //asks, in a dialog, if the user wants to clear files
			int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear your stats?", 
					"Clear", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				clearFiles(); //delete and remake files if yes is pressed.
				JOptionPane.showMessageDialog(this, "Stats cleared :)");
			}
		}

	}

	public class Quiz {
		private int _noOfQuestions;
		private int _count = 1;
		private int _size;
		private String _word = "";
		private boolean _fault = false;
		private boolean _reviewMode = false;
		private boolean _canBeReviewed = false;
		private List<String> _list;


		public Quiz(Modes mode) {
			switch (mode) { //select the appropriate word list depending on mode. Get size of word list
			case NORMAL:
				_list = _wordList;
				_size = _wordList.size();
				_titleLabel.setText("New Quiz");
				break;
			case REVIEW:
				_list = _failedWords;
				_size = _failedWords.size();
				_titleLabel.setText("Review");
				_reviewMode = true;
				break;
			}
			//see how many questions the quiz can ask
			if (_size == 0) {
				_noOfQuestions = 0;
			} else if (_size >= 3) {
				_noOfQuestions = 3;
			} else {
				_noOfQuestions = _size;
			}
		}
		
		public void askQuestion() {
			_size = _list.size();
			Random rand = new Random(); 
			int  lineNum = rand.nextInt(_size); //generate random number given size of word list

			_word = _list.get(lineNum); //get that word from list
			//ask question using festival
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "echo \" Spell " + _word + "\" | festival --tts");
			try {
				Process process = builder.start();
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			_spellLabel.setText("Spell word " + _count +  " of " + _noOfQuestions);
		}

		public void checkSpelling(String word) {
			_canBeReviewed = false;
			ProcessBuilder builder;
			if (word.equalsIgnoreCase(_word)) { //word is spelt correctly
				builder = new ProcessBuilder("/bin/bash", "-c", "echo \"Correct \" | festival --tts");
				_count++;
				if (_fault) { //update appropriate stats file and list
					update(_word, SpellingAid.Stat.FAULT);
				} else {
					update(_word, SpellingAid.Stat.MASTER);
				}
				removeFromFail(_word);
				_fault = false;
			} else if (! _fault) { //spelt incorrectly the first time
				builder = new ProcessBuilder("/bin/bash", "-c", "echo \" Incorrect. Try once more."
						+ "\t" + _word + ".\t" + _word + "\" | festival --tts");
				_spellLabel.setText("Incorrect. Try once more:");
				_fault = true; 
			}	else { //spelt incorrectly the second time
				_fault = false;
				builder = new ProcessBuilder("/bin/bash", "-c", "echo \"Incorrect \" | festival --tts");
				_count++;
				update(_word, SpellingAid.Stat.FAIL);
				if (_reviewMode) {
					_canBeReviewed = true; //in review mode, user can hear spelling

				}

			}
			try {
				Process process = builder.start();
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		/**
		 * This function spells the word out for the user
		 */
		public void spellWord() {
			_count--;
			_fault = true;
			String[] splitWord = _word.split("(?!^)"); //split word
			List<String> splitList = Arrays.asList(splitWord);
			try {
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "echo \"" + _word + "\" | festival --tts");
				Process process = builder.start();
				process.waitFor();
				//spell word
				for (String str : splitList) {
					builder = new ProcessBuilder("/bin/bash", "-c", "echo \"" + str + "    \" | festival --tts");
					process = builder.start();
					process.waitFor();

				}
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		}

		public boolean isFinished() {
			if (_count > _noOfQuestions) {
				return true;
			} else {
				return false;
			}

		}
		public boolean isFault() {
			return _fault;
		}
		public boolean canBeReviewed() {
			return _canBeReviewed;
		}
	}

	class ModelTable extends AbstractTableModel {
		private final String[] COLUMN_HEADERS = {"Words", "Mastered", "Faulted", "Failed", "Attempts" };
		private final Class<?> _colClasses[] = {String.class, Integer.class,Integer.class,Integer.class,Integer.class};
		private ArrayList<Integer> _indexList = new ArrayList<Integer>();
		private int _size = 0;
		public ModelTable() {
			//checks for the attempted words. Thus getting the row count of table
			//get the index number of that word and add to list
			for (int i = 0; i < _attemptList.size(); i++) {
				if (_attemptList.get(i) > 0) {
					_indexList.add(i);
				}
			}
			_size = _indexList.size();
		}

		@Override
		public int getRowCount() {
			return _size;
		}

		@Override
		public int getColumnCount() {
			return 5;
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			int index = _indexList.get(rowIndex); //use the index number of that word to get its stats
			if (columnIndex == 0 ) {
				return _wordList.get(index);
			} else if (columnIndex == 1) {
				return _masterList.get(index);	
			} else if (columnIndex == 2) {
				return _faultList.get(index);
			} else if (columnIndex == 3) {
				return _failList.get(index);
			} else if (columnIndex == 4) {
				return _attemptList.get(index);
			}

			return null;
		}
		@Override
		public String getColumnName(int column) {
			return COLUMN_HEADERS[column];

		}
		@Override
		public Class<?> getColumnClass(int column) {
			return _colClasses[column];
		}
	}


	public static void main(String[] agrs){

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SpellingAid frame = new SpellingAid();
				frame.createFiles();
				frame.setVisible(true);
			}
		});
	}
}