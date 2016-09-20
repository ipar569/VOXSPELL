package prototype;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
        panel.add(pauseBtn, BorderLayout.SOUTH);
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
        mediaPath = "big_buck_bunny_1_minute.avi";
        
        mediaFrame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		video.stop();
        		System.exit(0);
        	}
        });
        
        mediaFrame.setLocation(100, 100);
        mediaFrame.setSize(1050, 600);
        mediaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mediaFrame.setVisible(true);
        
        video.playMedia(mediaPath);
        
	}

}
