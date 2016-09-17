package prototype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.SwingWorker;

public class Festival extends SwingWorker<Void, Integer>{
		private String _tts;
		
		public Festival(String tts){
			_tts=tts;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			festival(_tts);
			return null;
		}
		
		private void festival(String tts) throws Exception{
			//command to be excuted.
			String cmd = "echo "+tts+"| festival --tts";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			//Excute the command
			Process process = builder.start();
			//Wait for the previous process to be finihsed
			process.waitFor();
		
		}
		
		
		
}
