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

public abstract class Entity {
	
	//Class Variables
	double x=0.0, y=0.0, angle=0.0;
	Color color = Color.pink; //Nice default, Cameron :3
	int faction = 0;
	Color fillColor = Color.pink;

	//Default Constructor
    public Entity(){}

    //Concrete methods
    public void move(){}
    public void draw(Graphics g)
    {
    	
    }

    //Accessors
    public final double getX(){return x;}
    public final double getY(){return y;}
    public final double getAngle(){return angle;}
    public final Color getColor(){return color;}

    //Mutators
    public final void setX(double x){this.x=x;}
    public final void setY(double y){this.y=y;}
    public final void setAngle(double angle){this.angle=angle;}

    //Sets the color here
    public final void setColor(int faction){
    	switch(faction){
    		//If faction is not initialized
    		case 0:
    			color = Color.gray;
    			break;
    		//Red (faction 1)
    		case 1:
    			color = new Color(255, 0, 25);
    			break;
    		//Blue (faction 2)
    		case 2:
    			color = new Color(0, 153, 255);
    			break;
    		default:
    			color = Color.gray;
    			break;
    	}

    	if(AsteroidsRTS.textureMode==0)
    		fillColor = Color.black;
   	 	else if(AsteroidsRTS.textureMode==1)
    		fillColor = Color.white;
    }
}