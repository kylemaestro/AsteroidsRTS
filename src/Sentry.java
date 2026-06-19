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

class Sentry extends Unit{

	public Sentry(){}

	//Constructor
	public Sentry(double x, double y, int health, int turretNum, int faction)
	{
		this.x=x;
		this.y=y;
		this.health=health;
		this.defaultHealth=health;
		this.faction=faction;
		this.turretNum=turretNum;
		setColor(faction);
		setRotationalSpeed(0.1);
		selectRadius = 25;
	}

	public void move()
	{
		if(controlled)
			fillColor=Color.green;
		else
			setColor(faction);

		if(turningLeft)
			angle-=rotationalSpeed;
		if(turningRight)
			angle+=rotationalSpeed;
		if(angle>(2*Math.PI))
			angle-=(2*Math.PI);
		else if(angle<0)
			angle+=(2*Math.PI);
	}
	
	public void draw(Graphics g)
	{
		super.draw(g);
	}
	
}