import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Window extends JFrame implements Runnable, WindowListener
{
	/**
	 * Gets rid of the warning.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Location of the right edge of the screen.
	 */
	double rightbound = 0;
	
	/**
	 * Location of the left edge of the screen.
	 */
	double leftbound = 0;
	
	/**
	 * Location of the top edge of the screen.
	 */
	double upbound = 0;
	
	/**
	 * Location of the bottom edge of the screen.
	 */
	double downbound = 0;
	
	/**
	 * Velocity of the window on the X axis.
	 */
	int XVel = 0;	
	
	/**
	 * Velocity of the window on the Y axis.
	 */
	int YVel = 0;
	
	/**
	 * Calculated velocity of the mouse on the X axis.
	 */
	int MouseVelX = 0;
	
	/**
	 * Calculated velocity of the mouse on the Y axis.
	 */
	int MouseVelY = 0;
	
	/**
	 * True when the program is closing.
	 */
	boolean shuttingDown = false;
	
	/**
	 * Position of the mouse one cycle ago.
	 */
	Point OldMousePos = MouseInfo.getPointerInfo().getLocation();
	
	/**
	 * Runs the function to update the position each millisecond.
	 */
	ScheduledExecutorService Controller = Executors.newScheduledThreadPool(1);
	
	/**
	 * The gif/image displayed on the window.
	 */
	ImageIcon BugGif;
	
	/**
	 * The label to display the gif/image.
	 */
	JLabel Bug = new JLabel(BugGif);
	
	/**
	 * The panel used to display the labe.
	 */
	JPanel MainPanel = new JPanel();
	
	/**
	 * Default and only constructor. Calculates the monitor bounds.
	 */
	Window()
	{
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		
			URL url = getClass().getResource("roach.gif");
			
			BugGif = new ImageIcon(url);
			BugGif.setImage(BugGif.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
			
			Bug = new JLabel();
			Bug.setIcon(BugGif);
			
			MainPanel.setLayout(new CardLayout());
			
			MainPanel.add(Bug);
			
			this.add(MainPanel);
		
		this.setUndecorated(true);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		
		GraphicsDevice[] Screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		for(int i = 0; i < Screens.length; i++)
		{
			double y = Screens[i].getDefaultConfiguration().getBounds().y;
			double x = Screens[i].getDefaultConfiguration().getBounds().x;
			
			
			if(y >= 0)
			{
				upbound += y;
			}
			else
			{
				downbound -= y;
			}
			
			if(x >= 0)
			{
				rightbound += x;
			}
			else
			{
				leftbound -= x;
			}
		}
		
		this.rightbound += Screens[0].getDisplayMode().getWidth();
		this.upbound += Screens[0].getDisplayMode().getHeight();
		
		this.Controller.scheduleAtFixedRate(this, 1, 1, TimeUnit.MILLISECONDS);
	}

	@Override
	/**
	 * Moves the window based on velocity and RNG.
	 * Detects collisions with the mouse and monitor borders.
	 */
	public void run() 
	{
		if(shuttingDown)
		{
			System.exit(0);
		}
		
		
		int x = this.getLocation().x + ThreadLocalRandom.current().nextInt(-1, 2) + XVel/25;
		int y = this.getLocation().y + ThreadLocalRandom.current().nextInt(-1, 2) + YVel/25;
		
		this.setLocation(x, y);
		
		XVel += ThreadLocalRandom.current().nextInt(-1, 2);
		YVel += ThreadLocalRandom.current().nextInt(-1, 2);
		
		if(this.getLocation().x >= this.rightbound-this.getSize().width && this.XVel >= 0)
		{
			this.XVel = (int) (-XVel*0.97);
		}
		else if(this.getLocation().x <= -leftbound && this.XVel <= 0)
		{
			this.XVel = (int) (-XVel*0.97);
		}
		
		if(this.getLocation().y >= this.upbound-this.getSize().height && this.YVel >= 0)
		{
			this.YVel = (int) (-YVel*0.97);
		}
		else if(this.getLocation().y <= -downbound && this.YVel <= 0)
		{
			this.YVel = (int) (-YVel*0.97);
		}
		
		
		Point MouseLocation = MouseInfo.getPointerInfo().getLocation();
		MouseVelX = (int) (MouseVelX/1.05 + (MouseLocation.x - OldMousePos.x));
		MouseVelY = (int) (MouseVelY/1.05 + (MouseLocation.y - OldMousePos.y));
		
		x += this.getBounds().width/2;
		y += this.getBounds().height/2;
		
		int relX = MouseLocation.x - x;
		int relY = MouseLocation.y - y;
		
		if(this.XVel < 0 && relX < 0 && relX > -this.getBounds().width/2 && Math.abs(relY) < this.getBounds().height)
		{
			this.XVel = (int) (-XVel*0.97)+MouseVelX;
			this.YVel += MouseVelY;
		}
		else if(this.XVel > 0 && relX > 0 && relX < this.getBounds().width/2 && Math.abs(relY) < this.getBounds().height)
		{
			this.XVel = (int) (-XVel*0.97)+MouseVelX;
			this.YVel += MouseVelY;
		}
		
		if(this.YVel < 0 && relY < 0 && relY > -this.getBounds().height/2 && Math.abs(relX) < this.getBounds().width)
		{
			this.YVel = (int) (-YVel*0.97)+MouseVelY;
			this.XVel += MouseVelX;
		}
		else if(this.YVel > 0 && relY > 0 && relY < this.getBounds().height/2 && Math.abs(relX) < this.getBounds().width)
		{
			this.YVel = (int) (-YVel*0.97)+MouseVelY;
			this.XVel += MouseVelX;
		}
		
		
		OldMousePos = MouseLocation;
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowActivated(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowClosed(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Called when the user attempts to close the window.
	 * Makes it go splat and exits the program.
	 */
	public void windowClosing(WindowEvent arg0) 
	{
		this.Controller.shutdown();
		
		try 
		{
			AudioInputStream Stream = AudioSystem.getAudioInputStream(getClass().getResource("splat.wav"));
			DataLine.Info AudioInfo = new DataLine.Info(Clip.class, Stream.getFormat());
			Clip AudioClip = (Clip) AudioSystem.getLine(AudioInfo);
			AudioClip.open(Stream);
			AudioClip.start();
		} catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		BugGif.setImage(BugGif.getImage().getScaledInstance(500, 75, Image.SCALE_DEFAULT));
		Bug.setIcon(BugGif);
		this.pack();
		this.repaint();
		
		shuttingDown = true;
		Controller = Executors.newScheduledThreadPool(1);
		Controller.schedule(this, 3000, TimeUnit.MILLISECONDS);
		//^^^ This code sucks
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowDeactivated(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowDeiconified(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowIconified(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * Unused implemented method.
	 */
	public void windowOpened(WindowEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
}
