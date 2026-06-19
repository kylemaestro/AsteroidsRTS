import java.awt.*;
class Wall extends Unit{
	//Default graphic
	int[] defX = {-25, -25, 25, 25};
	int[] defY = {-5, 5, 5 ,-5};

	int[] defX2 = {-25, -25, -14, -14};
	int[] defY2 = {-6, 6, 6, -6};

	int[] defX3 = {15, 15, 26, 26};
	int[] defY3 = {-6, 6, 6, -6};

	//used for drawing according to rotation
	int[] XPts = new int[4];
	int[] YPts = new int[4];

	int[] XPts2 = new int[4];
	int[] YPts2 = new int[4];

	int[] XPts3 = new int[4];
	int[] YPts3 = new int[4];

	//Constructor
	public Wall(double x, double y, double angle, int health, int faction)
	{
		this.x=x;
		this.y=y;
		this.angle=angle;
		this.health=health;
		this.defaultHealth=health;
		this.faction=faction;
		setColor(faction);
	}


	//draw
	public void draw(Graphics g)
	{
		//uses angle to redefine coordinates and rotate
		for(int i=0; i<4; i++){
			XPts[i]=(int)(defX[i]*Math.cos(angle)-defY[i]*Math.sin(angle)+x+.5);
			YPts[i]=(int)(defX[i]*Math.sin(angle)+defY[i]*Math.cos(angle)+y+.5);

			XPts2[i]=(int)(defX2[i]*Math.cos(angle)-defY2[i]*Math.sin(angle)+x+.5);
			YPts2[i]=(int)(defX2[i]*Math.sin(angle)+defY2[i]*Math.cos(angle)+y+.5);

			XPts3[i]=(int)(defX3[i]*Math.cos(angle)-defY3[i]*Math.sin(angle)+x+.5);
			YPts3[i]=(int)(defX3[i]*Math.sin(angle)+defY3[i]*Math.cos(angle)+y+.5);
		}

		//calculates centerpoint
		int cenX = (XPts[2] - XPts[0]) / 2;
		int cenY = (YPts[1] - YPts[0]) / 2;

		//Draws according to textureMode
		if(AsteroidsRTS.textureMode==0){
			g.setColor(color);
			g.drawPolygon(XPts,YPts,4);
			g.setColor(Color.darkGray);
			g.fillPolygon(XPts2, YPts2, 4);
			g.fillPolygon(XPts3, YPts3, 4);
		}
		if(AsteroidsRTS.textureMode==1){
			g.setColor(Color.white);
			g.fillPolygon(XPts,YPts,4);
			g.setColor(color);
			g.drawPolygon(XPts,YPts,4);
			g.drawLine(XPts[0], YPts[0], XPts[2], YPts[2]);
			g.drawLine(XPts[1], YPts[1], XPts[3], YPts[3]);
		}

		//Draws the green circle around selected walls
		if(selected){
			g.setColor(Color.green);
			g.drawOval((int)((x)-50), (int)((y)-50), 100, 100);
		}

	}

	//Hitbox
	public Polygon getBounds(){
		Polygon r;
		r = new Polygon(XPts,YPts,4);
		return r;
	}

}