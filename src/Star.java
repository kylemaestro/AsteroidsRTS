import java.awt.*;
import java.util.*;

//Class for, well, what else? Stars!
class Star extends Entity{
	
	//RNG
	Random rand = new Random();
	
	//Itty bitty constructor--bite sized!
	public Star()
	{
		x = rand.nextInt(AsteroidsRTS.mapSizeX);
		y = rand.nextInt(AsteroidsRTS.mapSizeY);
		
		switch(rand.nextInt(4))
		{
			case 0:
				x = x - (x * 2);
				break;
			case 1:
				y = y - (y * 2);
				break;
			case 2:
				x = x - (x * 2);
				y = y - (y * 2);
				break;
			case 3:
				//::tumbleweeds::
				break;
		}
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.white);
		
		g.fillRect((int)x, (int)y, 2, 2);
		
	}
	
	
	//Stars move pixel by pixel to set them apart from smooth moving ships, etc.
	public void move()
	{
		x++;
		
		if(x > AsteroidsRTS.mapSizeX + 5)
			x = -5;
	}
}
