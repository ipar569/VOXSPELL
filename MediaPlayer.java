package prototype;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class MediaPlayer {
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	private JFrame mediaFrame = new JFrame();
	private String mediaPath = "";
	
	private boolean pause = true;
	
	MediaPlayer() {

        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), "/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib"
            );
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        final EmbeddedMediaPlayer video = mediaPlayerComponent.getMediaPlayer();
        
	    JPanel panel = new JPanel(new BorderLayout());
	       panel.add(mediaPlayerComponent, BorderLayout.CENTER);
	        
	       mediaFrame.setContentPane(panel);
        final JButton pauseBtn = new JButton("Pause");
        
        JPanel secondPanel = new JPanel(new GridLayout(2,1));
        panel.add(secondPanel, BorderLayout.SOUTH);
        final JProgressBar pb = new JProgressBar();
        pb.setValue(0);
        secondPanel.add(pauseBtn);
        secondPanel.add(pb);
        pauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				video.pause();	
				if (pause) {
					pauseBtn.setText("Play");
					pause = false;
				} else {
					pauseBtn.setText("Pause");
					pause = true;
				}
			}
        	
        });
        
        Timer timer = new Timer(50, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				long time = (long) video.getTime();
				//System.out.println(time);
				long totalTime = (long) video.getLength();
				//System.out.println(totalTime);
				long percentage = ( (time * 100) / totalTime );
				int n = (int) percentage;
				//System.out.println(percentage);
				pb.setValue(n);
			}
        	
        });
        		
        timer.start();	
        mediaPath = "big_buck_bunny_1_minute.avi";
        
        mediaFrame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		video.stop();
        		mediaFrame.dispose();
        	}
        });
        
        mediaFrame.setLocation(100, 100);
        mediaFrame.setSize(1050, 600);
        mediaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mediaFrame.setVisible(true);
        
        video.playMedia(mediaPath);
        
	}

}
