//Import statements
import java.awt.*;
import java.util.*;


class Turret extends Effect{
	//Class variables
	int shotDelay, shotDelayLeft=0, range, accuracy, shotLife, unitNum;
	boolean visible;
	boolean shooting = false;
	int target = 100000;
	Random rand = new Random();
	double targetAngle;
	String shotType;

	//Default graphic (triangle)
	int[] origGunX = {6,-5,-5};
	int[] origGunY = {0,5,-5};
	int[] gunXPts=new int[3];
	int[] gunYPts=new int[3];

	//constructor for unused turrets (For example, turrets on walls or farms)
	public Turret(){
	}

	public Turret(double x, double y, int shotDelay, int range, int accuracy, int shotLife, boolean visible, String shotType, int unitNum, int faction)
	{
		this.x=x;
		this.y=y;
		this.shotDelay=shotDelay;
		this.faction=faction;
		this.range=range;
		this.visible=visible;
		this.accuracy=accuracy;
		this.shotLife=shotLife;
		this.shotType=shotType;
		this.unitNum=unitNum;
		setColor(faction);
	}

	//draw for baseTurret and deployable
	public void draw(Graphics g)
	{			
		if(visible){
			//calculates the rotation
			for(int i=0; i<3; i++){
				gunXPts[i]=(int)(origGunX[i]*Math.cos(angle)-origGunY[i]*Math.sin(angle)+x+.5);
				gunYPts[i]=(int)(origGunX[i]*Math.sin(angle)+origGunY[i]*Math.cos(angle)+y+.5);
			}

			g.setColor(color);
			g.fillPolygon(gunXPts,gunYPts,3);
		}
	}

	public void move()
	{
		if(shotDelayLeft>0)
			shotDelayLeft--;

		if(angle>(2*Math.PI))
			angle-=(2*Math.PI);
		else if(angle<0)
			angle+=(2*Math.PI);

	} //Closes move()

	//accessors
	public boolean getVisible(){return visible;}
	public int getAccuracy(){return accuracy;}
	public int getShotLife(){return shotLife;}
	public boolean getShooting(){return shooting;}
	public int getRange(){return range;}
	public int getTarget(){return target;}
	public String getShotType(){return shotType;}
	public int getUnitNum(){return unitNum;}

	//mutators
	public void setAccuracy(int accuracy){this.accuracy = accuracy;}
	public void setVisible(boolean visible){this.visible=visible;}
	public void setShotLife(int shotLife){this.shotLife = shotLife;}
	public void setRange(int range){this.range = range;}
	public void setShooting(boolean shooting){this.shooting=shooting;}
	public void setTarget(int target){this.target=target;}
	public void setShotType(String shotType){this.shotType=shotType;}
	public void setUnitNum(int unitNum){this.unitNum=unitNum;}

	//Can only shoot if it is "shooting" and the delay has passed
	public boolean canShoot()
	{
		if(shotDelayLeft>0)
			return false;
		if(shooting==false)
			return false;
		else
			return true;
	}

	//fires Shot
	public Shot shootShot()
	{
		//resets the delay
		shotDelayLeft = shotDelay;
		//Factors in accuracy (1 is flawless accuracy)
		int lr;
		lr = rand.nextInt(2);
		if(lr == 0)
			targetAngle = angle - ((rand.nextInt(accuracy)*Math.PI)/180);
		else
			targetAngle = angle + ((rand.nextInt(accuracy)*Math.PI)/180);

		//constructs the Shot
		return new Shot(x, y, targetAngle, shotLife, faction);
	}

	//fires Laser
	public Laser shootLaser()
	{
		//resets the delay
		shotDelayLeft = shotDelay;
		//Factors in accuracy (1 is flawless accuracy)
		int lr;
		lr = rand.nextInt(2);
		if(lr == 0)
			targetAngle = angle - ((rand.nextInt(accuracy)*Math.PI)/180);
		else
			targetAngle = angle + ((rand.nextInt(accuracy)*Math.PI)/180);

		//constructs the Shot
		return new Laser(x, y, targetAngle, shotLife, faction);
	}

	//region in which the turret locates targets
	public Rectangle getRangeBounds(){
		Rectangle r;
		r = new Rectangle((int)getX() - range, (int)getY() - range, range*2, range*2);
		return r;
	}

}//class