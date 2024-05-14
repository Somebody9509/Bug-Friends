import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Window extends JFrame implements Runnable
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
	 * Calculated velocity of the mouse.
	 */
	int MouseVel = 0;
	
	/**
	 * Position of the mouse one cycle ago.
	 */
	Point OldMousePos = MouseInfo.getPointerInfo().getLocation();
	
	/**
	 * Runs the function to update the position each millisecond.
	 */
	ScheduledExecutorService Controller = Executors.newScheduledThreadPool(1);
	
	/**
	 * Default and only constructor. Calculates the monitor bounds.
	 */
	Window()
	{
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
			URL url = Window.class.getResource("roach.gif");
			
			ImageIcon BugGif = new ImageIcon(url);
			BugGif.setImage(BugGif.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
			
			JLabel Bug = new JLabel(BugGif);
			this.add(Bug);
		
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
		int x = this.getLocation().x + ThreadLocalRandom.current().nextInt(-1, 2) + XVel/25;
		int y = this.getLocation().y + ThreadLocalRandom.current().nextInt(-1, 2) + YVel/25;
		
		this.setLocation(x, y);
		
		XVel += ThreadLocalRandom.current().nextInt(-1, 2);
		YVel += ThreadLocalRandom.current().nextInt(-1, 2);
		
		if(this.getLocation().x >= this.rightbound-this.getSize().width && this.XVel >= 0)
		{
			this.XVel = (int) (-XVel*0.99);
		}
		else if(this.getLocation().x <= -leftbound && this.XVel <= 0)
		{
			this.XVel = (int) (-XVel*0.99);
		}
		
		if(this.getLocation().y >= this.upbound-this.getSize().height && this.YVel >= 0)
		{
			this.YVel = (int) (-YVel*0.99);
		}
		else if(this.getLocation().y <= -downbound && this.YVel <= 0)
		{
			this.YVel = (int) (-YVel*0.99);
		}
		
		
		Point MouseLocation = MouseInfo.getPointerInfo().getLocation();
		MouseVel = (int) (MouseVel/1.05 + Math.abs(MouseLocation.x - OldMousePos.x) + Math.abs(MouseLocation.y - OldMousePos.y));
		
		x += this.getBounds().width/2;
		y += this.getBounds().height/2;
		
		int relX = MouseLocation.x - x;
		int relY = MouseLocation.y - y;
		
		if(this.XVel < 0 && relX < 0 && relX > -this.getBounds().width/2 && Math.abs(relY) < this.getBounds().height)
		{
			this.XVel = (int) (-XVel*0.99)+MouseVel;
		}
		else if(this.XVel > 0 && relX > 0 && relX < this.getBounds().width/2 && Math.abs(relY) < this.getBounds().height)
		{
			this.XVel = (int) (-XVel*0.99)+MouseVel;
		}
		
		if(this.YVel < 0 && relY < 0 && relY > -this.getBounds().height/2 && Math.abs(relX) < this.getBounds().width)
		{
			this.YVel = (int) (-YVel*0.99)+MouseVel;
		}
		else if(this.YVel > 0 && relY > 0 && relY < this.getBounds().height/2 && Math.abs(relX) < this.getBounds().width)
		{
			this.YVel = (int) (-YVel*0.99)+MouseVel;
		}
		
		
		OldMousePos = MouseLocation;
	}
}
