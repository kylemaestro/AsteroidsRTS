import java.text.DecimalFormat;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.*;
import java.lang.Object;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
public abstract class Unit extends Entity{
	
	//Class Variables (Not necessarily used by all classes)
	int selectRadius=50, health=50, defaultHealth = 50, turretNum, waypointNum, overHealAmmount, sightRadius = 250;
	boolean turningLeft, turningRight, accelerating, selected, hovered, controlled=false;
	double xVelocity, yVelocity, acceleration, velocityDecay, rotationalSpeed;

	//Default drawing information
	int[] defX = {-10, -10, 20, 20};
	int[] defY = {-10, 0, 20 ,-10};
	int[] defXPts = new int[4];
	int[] defYPts = new int[4];
	
	//Healthbar variables
	Ellipse2D.Double innerCircle, midCircle;
	Rectangle2D.Double cutRect;
	Area innerArea, midArea, cutArea;
	double healthPerPixel;
	double cutHeight;
	
	//Blue and red colors
	Color blue = new Color(0, 153, 255);
	Color red = new Color(255, 0, 25);

	//Default Constructor
    public Unit(){}

    //Concrete methods
    public void draw(Graphics g)
    {
    	for(int i=0; i<4; i++)
    	{
			defXPts[i]=(int)(defX[i]*Math.cos(angle)-defY[i]*Math.sin(angle)+x+.5);
			defYPts[i]=(int)(defX[i]*Math.sin(angle)+defY[i]*Math.cos(angle)+y+.5);
		}
		
		//Draws the green circle around selected ships
		if(selected || hovered)
		{
			Graphics2D g2 = (Graphics2D) g;
			
			//Colors shots based on faction
			if(faction==1)
				g.setColor(red);
			
			if(faction==2)
				g.setColor(blue);
			
			if(AsteroidsRTS.enableAntiAliasing){
	        	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	       	 	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			}
			
			//Creates the Ellipse2D objects we can use to make fillable areas
			innerCircle = new Ellipse2D.Double();
			midCircle = new Ellipse2D.Double();
			cutRect = new Rectangle2D.Double();
			
			//Sets the origin and width/height of both circles used to make the thick band
			midCircle.setFrame(x-(selectRadius*2)/2, y-(selectRadius*2)/2, (selectRadius*2), (selectRadius*2));
			innerCircle.setFrame(x-((selectRadius*2)-10)/2, y-((selectRadius*2)-10)/2, ((selectRadius*2)-10), ((selectRadius*2)-10));
			
			//Sets the cutHeight to zero if the health equals the default health, avoids a strange minor bug
			if(health == defaultHealth)
				cutHeight = 0;
			
			//Calculates how much to cut out from the health
			healthPerPixel = (double)defaultHealth / ((double)selectRadius * 2);				
			if(defaultHealth - health != 0)
				cutHeight = ((double)defaultHealth - (double)health) / healthPerPixel;
				
			//Creates the rectangle we will use to cut from the health with
			cutRect.setFrame(x-selectRadius, y-selectRadius, selectRadius*2, cutHeight);
			
			//Creates the Area objects we use to subtract area when filling with a color
			innerArea = new Area(innerCircle);
			midArea = new Area(midCircle);
			cutArea = new Area(cutRect);
			
			midArea.subtract(innerArea);
			midArea.subtract(cutArea);
			g2.fill(midArea);
			
		}
    }

	public Polygon getBounds(){
		Polygon r;
		r = new Polygon(defXPts, defYPts, 4);
		return r;
	}

    //accessors
	public final int getHealth(){return health;}
	public final boolean getAccelerating(){return accelerating;}
	public final int getDefaultHealth(){return defaultHealth;}
	public final boolean getSelected(){return selected;}
	public final boolean getHovered(){return hovered;}
	public final int getSelectRadius(){return selectRadius;}
	public final int getTurretNum(){return turretNum;}
	public final int getWaypointNum(){return waypointNum;}
	public final double getRotationalSpeed(){return rotationalSpeed;}
	public final boolean getControlled(){return controlled;}
	public final int getSightRadius(){return sightRadius;}

	//mutators
	public final void setHealth(int health){this.health=health;}
	public final void setAccelerating(boolean accelerating){this.accelerating=accelerating;}
	public final void setTurningLeft(boolean turningLeft){this.turningLeft=turningLeft;}
	public final void setTurningRight(boolean turningRight){this.turningRight=turningRight;}
	public final void setSelected(boolean selected){this.selected=selected;}
	public final void setHovered(boolean hovered){this.hovered=hovered;}
	public final void setTurretNum(int turretNum){this.turretNum=turretNum;}
	public final void setWaypointNum(int waypointNum){this.waypointNum=waypointNum;}
	public final void setRotationalSpeed(double rotationalSpeed){this.rotationalSpeed=rotationalSpeed;}
	public final void setControlled(boolean controlled){this.controlled = controlled; setColor(faction);}
	public final void setSightRadius(int sightRadius){this.sightRadius=sightRadius;}

}

//all units are selectable, so all units can be deleted