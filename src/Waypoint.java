import java.awt.*;

public class Waypoint extends Effect{
	//Class varaibles
	int shipNum;

	public Waypoint(int shipNum, int faction){
		alive=false;
		this.shipNum=shipNum;
		this.faction=faction;
		setColor(faction);
	}

	public void draw(Graphics g)
	{
		if(alive==true)
		{
			//outer-most circle
			g.setColor(color);
			g.fillOval((int)getX() - 8, (int)getY() - 8, 16, 16);

			//middle circle
			g.setColor(fillColor);
			g.fillOval((int)getX() - 5, (int)getY() - 5, 10, 10);

			//iner-most circle
			g.setColor(color);
			g.fillOval((int)getX() - 2, (int)getY() - 2, 4, 4);
		}
	}

	//Accessors
	public int getShipNum(){return shipNum;}

	//Mutators
	public void setShipNum(int shipNum){this.shipNum=shipNum;}
}