package prototype;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ViewAccuracy extends AbstractTableModel{
	private ArrayList<Integer> attemptsList = new ArrayList<Integer>();
	private ArrayList<Integer> failsList = new ArrayList<Integer>();
	private ArrayList<Double> accuracyList = new ArrayList<Double>();
	
	private final String[] COLUMN_HEADERS = {"Level", "Accuracy", "Attempts" };
	private final Class<?> _colClasses[] = {String.class, String.class, Integer.class};



	public ViewAccuracy() {
		
		for (int i = 1; i <= 11; i++) {
			try {
			FileReader fr = new FileReader(".accuracy_" + i);
			BufferedReader br = new BufferedReader(fr);
			String str;
			str = br.readLine();
			attemptsList.add(Integer.parseInt(str));
			str = br.readLine();
			failsList.add(Integer.parseInt(str));
			br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (int j = 0; j < 11; j++) {
			double num = 0;
			if (attemptsList.get(j) != 0) {
				num = (double) failsList.get(j) / (double) attemptsList.get(j);
				num = 100 - (num * 100);
				
				num = (double) Math.round(num * 100) / 100;
			} else {
				num = 0;
			}
			

			accuracyList.add(num);
		}
		
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 11;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 0) {
			int tempRow = rowIndex + 1;
			return "Level " + tempRow;
		} else if (columnIndex == 1) {
			return accuracyList.get(rowIndex) + "%";
		} else if (columnIndex == 2) {
			return attemptsList.get(rowIndex);
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
