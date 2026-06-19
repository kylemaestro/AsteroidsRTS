import java.util.*;
import java.awt.*;

public abstract class Projectile extends Effect{
	//Class Variables (mostly unused)
	int lifeLeft, damage;
	double speed;

	//Default Constructor
    public Projectile(){}

    //Abstract Methods
    public abstract void draw(Graphics g);

    //Movement
    public final void move(int mapWidth, int mapHeight){
    	lifeLeft--;
    	x+=xVelocity;
		y+=yVelocity;
		//Sets lifeLeft to 0 if it reaches the edge of the map
		if(x<-mapWidth)
			lifeLeft = lifeLeft - lifeLeft;
		else if(x>mapWidth)
			lifeLeft = lifeLeft - lifeLeft;
		if(y<-mapHeight)
			lifeLeft = lifeLeft - lifeLeft;
		else if(y>mapHeight)
			lifeLeft = lifeLeft - lifeLeft;


    }

    //Declares velocity
    public final void setVelocity(){
    	xVelocity=speed*Math.cos(angle);
		yVelocity=speed*Math.sin(angle);
    }

	//accessors
	public final int getLifeLeft(){return lifeLeft;}
	public final int getDamage(){return damage;}

	//mutators
	public final void setLifeLeft(int lifeLeft){this.lifeLeft=lifeLeft;}
}