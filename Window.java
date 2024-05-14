import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.net.MalformedURLException;
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
	private static final long serialVersionUID = 1L;

	double rightbound = 0;
	
	double leftbound = 0;
	
	double upbound = 0;
	
	double downbound = 0;
	
	int XVel = 0;	
	
	int YVel = 0;
	
	int MouseVel = 0;
	
	Point OldMousePos = MouseInfo.getPointerInfo().getLocation();
	
	ScheduledExecutorService Controller = Executors.newScheduledThreadPool(1);
	
	Window()
	{
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try 
		{
			JLabel Bug = new JLabel(new ImageIcon(new URL("https://media.tenor.com/E7uOu0_B1RYAAAAM/cockroach-spin.gif")));
			this.add(Bug);
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		
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
