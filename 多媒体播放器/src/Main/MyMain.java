package Main;

import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import view.DisplayFrame;

public class MyMain {
	public static void main(String[] args)
	{
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	DisplayFrame d = new DisplayFrame(args);
            }
        });
		
	}
}
