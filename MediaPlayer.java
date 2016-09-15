package prototype;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        
        mediaPath = "big_buck_bunny_1_minute.avi";
        
        mediaFrame.setLocation(100, 100);
        mediaFrame.setSize(1050, 600);
        mediaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mediaFrame.setVisible(true);
        
        video.playMedia(mediaPath);
	}

}

