package prototype;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import javax.swing.SwingWorker;

public class Festival extends SwingWorker<Void, Integer>{
		private String _tts;
		private String _voice;
		
		public Festival(String tts,String voice) throws Exception{
			_tts=tts;
			_voice = voice;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			setScheme(_tts, _voice);
			festival();
			return null;
		}
		
		private void festival() throws Exception{
			//command to be excuted.
			String cmd = "festival -b festival.scm";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			//Excute the command
			Process process = builder.start();
			//Wait for the previous process to be finihsed
			process.waitFor();
		
		}
		
		private void setScheme(String tts, String voice) throws IOException{
			File failed = new File("festival.scm");
			//If file does not exist, create new file
			if(!failed.exists()) {
				failed.createNewFile();
			} 
			
			//Appending the word to the file
			Writer output;
			output = new BufferedWriter(new FileWriter(failed,false)); 
			output.append("(voice_"+_voice+")\n");
			output.append("(Parameter.set 'Duration_Stretch 1.1)");
			String[] lines = tts.split(" ");
			for(int i =0; i<lines.length;i++){
				output.append("(SayText \""+lines[i]+"\")");
			}
			output.close();
		}
		
		public ArrayList<String> listOfVoices() throws Exception{
			ArrayList<String> voice = new ArrayList<String>();
			
			String cmd = "ls /usr/share/festival/voices/*english* > ./.voices";
			
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			//Excute the command
			Process process = builder.start();
			//Wait for the previous process to be finihsed
			process.waitFor();
			
			
			BufferedReader file = new BufferedReader(new FileReader("."+File.separator+".voices"));
			
			String line;
			
			while ((line = file.readLine()) != null){
				if(!"".equals(line.trim())){
					voice.add(line);
				}
			}
			
			return voice;
		}
		
}
