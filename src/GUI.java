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

//Class for the graphical user interface
class GUI{
	
	//Class variables
	private int x, y;
	
	//Main backdrop
	Rectangle2D.Double horizBackdrop, vertBackdrop;
	
	//Debug bar
	Rectangle2D.Double debugBar;
	Rectangle2D.Double debugBar2;
	
	//Constructor
	public void GUI(){}
	
	//Draw
	public void draw(Graphics g)
	{	
		//G2D
		Graphics2D g2d = (Graphics2D)g;
		
		//Main backdrop
		g.setColor(Color.darkGray);
		
		horizBackdrop = new Rectangle2D.Double();
		horizBackdrop.setFrame(x, y+620, 1280, 100);
		g2d.fill(horizBackdrop);
		
		vertBackdrop = new Rectangle2D.Double();
		vertBackdrop.setFrame(x+1180, y, 100, 720);
		g2d.fill(vertBackdrop);
		
		//Debug bar
		g.setColor(Color.darkGray);
		
		debugBar = new Rectangle2D.Double();
		debugBar2 = new Rectangle2D.Double();
		debugBar.setFrame(x, y, 1010, 40);
		debugBar2.setFrame(x+5, y+5, 1000, 30);
		g2d.fill(debugBar);
		g.setColor(Color.black);
		g2d.fill(debugBar2);
		
	}
	
	//Modifiers
	public void setX(int x){this.x=x;}
    public void setY(int y){this.y=y;}
	
}