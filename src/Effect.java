import java.util.*;
import java.awt.*;

public abstract class Effect extends Entity{
	//Class Variables (mostly unused)
	boolean turningLeft, turningRight, accelerating, alive;
	double xVelocity, yVelocity, acceleration, velocityDecay, rotationalSpeed;

	//Default Constructor
    public Effect(){}

    //Concrete methods
    public void draw(Graphics g){
    	//Default image
    	g.setColor(color);
    	g.fillOval((int)x-25,(int)y-25,50,50);
    }

	//accessors
	public final boolean getAccelerating(){return accelerating;}
	public final boolean getAlive(){return alive;}
	public final double getXVelocity(){return xVelocity;}
	public final double getYVelocity(){return yVelocity;}

	//mutators
	public final void setAccelerating(boolean accelerating){this.accelerating=accelerating;}
	public final void setTurningLeft(boolean turningLeft){this.turningLeft=turningLeft;}
	public final void setTurningRight(boolean turningRight){this.turningRight=turningRight;}
	public final void setAlive(boolean alive){this.alive=alive;}
}