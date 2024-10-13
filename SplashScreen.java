package CST8221;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

//In general, packages are great for organizing your code, but only use them when called to do so.
//package splash;

/* CST8221-JAP: LAB 02, Application Splash Screen
   File name: SplashScreenDemo.java
*/

/**
 * A simple class demonstrating how to create a splash screen for an application
 * using JWindow as a container. The splash screen will appear on the screen for
 * the duration given to the constructor. The class includes a main() method for
 * testing purposes. Normally, this class should be used by the main() of an
 * application. Note: Since JWindow uses a default frame when made visible it
 * does not receives events. If you want to process events using JWindow, you
 * must create an undecorated frame and attach JWindow to this frame using an
 * appropriate JWindow constructor.
 * 
 * @version 1.20.1
 * @author Hetvi Patel
 * @since Java 2
 */
public class SplashScreen extends JWindow {
	/** Swing components are serializable and require serialVersionUID */
	private static final long serialVersionUID = 6248477390124803341L;
	/** Splash screen duration */
	private final int duration;


	/**
	 * Default constructor. Sets the show time of the splash screen.
	 * @param duration Duration
	 */
	public SplashScreen(int duration) {
		this.duration = duration * 1000;
	}

	/**
	 * Shows a splash screen in the center of the desktop for the amount of time
	 * given in the constructor.
	 */
	public void showSplashWindow() {
		// create content pane
		JPanel content = new JPanel(new BorderLayout());
		// or use the window content pane
		// JPanel content = (JPanel)getContentPane();
		content.setBackground(Color.GRAY);

		// Set the window's bounds, position the window in the center of the screen
		int width = 534 + 10;
		int height = 263 + 10;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;
		// set the location and the size of the window
		setBounds(x, y, width, height);

		/*
		 * Create the splash screen by loading an image into the JLabel. There are many
		 * ways of going about this. You can experiment by commenting out our line X and
		 * uncommenting one of the other JLabel lines.
		 */

		// accessing an image in a package named splash without using getResource - needs an absolute path 
		// This does make it difficult to relocate the code, however. Relative paths are almost always better.
		// JLabel label = new JLabel(new ImageIcon("c:\\...\\SplashSwing.gif"));
		
		//image placed in the package source folder - presuming you were using a package.
		//You'll want to adjust the directory name to match whatever package you used.
		//JLabel label = new JLabel(new ImageIcon(getClass().getResource("/splash/SplashSwing.gif"))); 
		
		//image placed in folder named resources placed into the source folder
		//JLabel label = new JLabel(new ImageIcon(getClass().getResource("/splash/resources/SplashSwing.gif")));
		
		//using URL with an image placed in resources folder in source folder
		//Breaking the process down into steps:
		//    URL imgURL = this.getClass().getResource("resources/SplashSwing.gif");
		//    Image img = Toolkit.getDefaultToolkit().getImage(imgURL);
		//    JLabel label = new JLabel(new ImageIcon(img));
		
		//accessing an image in the default packge
		//works if compiled and run from command prompt - not if run from Netbeans
		//JLabel label = new JLabel(new ImageIcon("SplashSwing.gif"));

		JLabel label;
		try {
			label = new JLabel(new ImageIcon("Assets/logo.png"));
		} catch (Exception e) {
			// e.printStackTrace();
			label = new JLabel("No image");
		}

		JLabel demo = new JLabel("Hetvi Patel & Dhara Patel's App Splash Window Demo", JLabel.CENTER);
		demo.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
		content.add(label, BorderLayout.CENTER);
		content.add(demo, BorderLayout.SOUTH);
		// create custom RGB color
		Color customColor = new Color(44, 197, 211);
		content.setBorder(BorderFactory.createLineBorder(customColor, 10));

		// replace the window content pane with the content JPanel
		setContentPane(content);

		// make the splash window visible
		setVisible(true);

		// Snooze for awhile, pretending the code is loading something awesome while
		// our splashscreen is entertaining the user.
		try {

			Thread.sleep(duration);
		} catch (InterruptedException e) {
			/* log an error here? *//* e.printStackTrace(); */}
		// destroy the window and release all resources
		dispose();
		// You can hide the splash window. The resources will not be released.
		// setVisible(false);
	}

	/**
	 * The main method. Used for testing purposes.
	 * 
	 * @param args the time duration of the splash screen in milliseconds. If not
	 *             specified, the default duration is 10000 msec (10 sec).
	 */
	public static void execute(String[] args) {
		int duration = 10;
		if (args.length > 3) {
			try {
				duration = Integer.parseInt(args[3]);
			} catch (NumberFormatException mfe) {
				System.out.println("Wrong command line argument: must be an integer number");
				System.out.println("The default duration 10000 milliseconds will be used");
				// mfe.printStackTrace();
			}
		}
		// Create the screen
		SplashScreen splashWindow = new SplashScreen(duration);
		// Show the Splash screen
		splashWindow.showSplashWindow();
		// Create and display the main application GUI
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame frame = new JFrame("Application Frame");
				frame.setMinimumSize(new Dimension(350, 350));
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLocationRelativeTo(null);// screen center
				// frame.setLocationByPlatform(true);
				frame.setVisible(true);
			}
		});
	}// end main
}// end SplashScreenDemo class
