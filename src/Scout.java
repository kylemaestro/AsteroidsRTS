import java.awt.*;

class Scout extends Ship{
	//Default graphic without rotation
	final double[] origXPts = {14,-10,-6,-10}, origYPts = {0,-8,0,8},
		origFlame1XPts = {-6,-23,-6}, origFlame1YPts = {-3,0,3};
	int[] xPts = new int[4];
	int[] yPts = new int[4];
	int[] flame1XPts = new int[3];
	int[] flame1YPts = new int[3];

	//Constructor
	public Scout(double x, double y, double angle, double acceleration, double rotationalSpeed, int turretNum, int health, int waypointNum, int faction)
	{
		this.x=x;
		this.y=y;
		this.angle=angle;
		this.health=health;
		this.defaultHealth=health;
		this.faction=faction;
		this.turretNum=turretNum;
		this.waypointNum=waypointNum;
		this.rotationalSpeed=rotationalSpeed;
		this.acceleration=acceleration;
		selectRadius = 30;
		xVelocity = 0;
		yVelocity = 0;
		setColor(faction);
	}

	public void draw(Graphics g)
	{	
		super.draw(g); //This is so cool
		
		if(controlled)
			fillColor=Color.green;
		else
			setColor(faction);

		//Draws flames if accelerating
		if(accelerating)
		{
			//Calculates rotation for the flame
			for(int i=0; i<3; i++)
			{
				flame1XPts[i]=(int)(origFlame1XPts[i] * Math.cos(angle) - origFlame1YPts[i] * Math.sin(angle) + x + .5);
				flame1YPts[i]=(int)(origFlame1XPts[i] * Math.sin(angle) + origFlame1YPts[i] * Math.cos(angle) + y + .5);
			}

			//Colors flames different based on player ID
			g.setColor(color);
			g.fillPolygon(flame1XPts, flame1YPts, 3);
		}

		//Calculates rotation for ship
		for(int i=0; i<4; i++){
			xPts[i]=(int)(origXPts[i]*Math.cos(angle)-origYPts[i]*Math.sin(angle)+x+.5);
			yPts[i]=(int)(origXPts[i]*Math.sin(angle)+origYPts[i]*Math.cos(angle)+y+.5);
		}

		//Color based on texture mode
		g.setColor(fillColor);
		g.fillPolygon(xPts, yPts, 4);

		g.setColor(color);
		g.drawPolygon(xPts, yPts, 4);

	}

	public Polygon getBounds(){
		return new Polygon(xPts, yPts, 4);
	}
}
