//Import statements
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

//Class for the minimap
class Minimap{
	
	//Class variables
	private int x, y;
	
	//Main backdrop
	Rectangle2D.Double backdrop;
	
	//Map backdrop
	Rectangle2D.Double mapBackdrop;
	
	//Constructor
	public void Minimap(){}
	
	//Draw
	public void draw(Graphics g)
	{	
		//G2D
		Graphics2D g2d = (Graphics2D)g;
		
		//Main backdrop
		g.setColor(Color.darkGray);
		
		backdrop = new Rectangle2D.Double();
		backdrop.setFrame(x+75, y+75, 120, 120);
		g2d.fill(backdrop);
		
		g.setColor(Color.black);
		mapBackdrop = new Rectangle2D.Double();
		mapBackdrop.setFrame(x+85, y+85, 100, 100);
		g2d.fill(mapBackdrop);		
	}
	
	//Modifiers
	public void setX(int x){this.x=x;}
    public void setY(int y){this.y=y;}
	
}