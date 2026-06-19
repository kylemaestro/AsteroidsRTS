import java.awt.*;
import java.util.*;

//Class for dividing the map into sectors of 1000x1000 pixels
class Sector extends Entity{
	
	//Class variables! :D
	private boolean containsOutpost, containsMothership, hotspot;
	private int length, width;
	
	public Sector()
	{
		length = AsteroidsRTS.sectorSizeX;
		width = AsteroidsRTS.sectorSizeY;	
	}
	
	public void draw(Graphics g)
	{
		if(AsteroidsRTS.drawSectorBorders)
		{
			g.setColor(Color.green);
			g.drawRect((int)x, (int)y, length, width);
		}
	}
	
	public void setContainsOutpost(boolean containsOutpost){ this.containsOutpost = containsOutpost; }
	public void setContainsMothership(boolean containsMothership){ this.containsMothership = containsMothership; }
	public void setHotspot(boolean hotspot){ this.hotspot = hotspot; }
	
	public boolean getContainsOutpost(){ return containsOutpost; }
	public boolean getContainsMothership(){ return containsMothership; }
	public boolean getHotspot(){ return hotspot; }
}