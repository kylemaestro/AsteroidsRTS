import java.awt.*;

class Sonar extends Unit{
	//class variables
	double rotSpeed = .05, pingSpeed = 1.0;
	int pingDelay = 75, pingDelayLeft = 0, pingRadius = 10, maxPingRadius = 45;
	boolean pinging = true;

	//Default graphic without rotation
		int[] defRadarX = {-8, -8, 8, 8};
		int[] defRadarY = {-2, 2, 2 ,-2};
		int[] XRadarPts = new int[4];
		int[] YRadarPts = new int[4];

	//Constructor
	public Sonar(double x, double y, int health, /*int sightRange,*/ int faction){
		this.x=x;
		this.y=y;
		this.health=health;
		this.defaultHealth=health;
		this.faction=faction;
		setColor(faction);
		setSightRadius(700);
	}

	public void move(){
		angle += rotSpeed;
		if(pingDelayLeft>0 && !pinging)
			pingDelayLeft--;
		if(pinging && pingRadius<maxPingRadius)
			pingRadius += pingSpeed;
		if(pingDelayLeft==0)
			pinging = true;
	}

	public void draw(Graphics g){
    	for(int i=0; i<4; i++){
			defXPts[i]=(int)(defX[i]*Math.cos(angle)-defY[i]*Math.sin(angle)+x+.5);
			defYPts[i]=(int)(defX[i]*Math.sin(angle)+defY[i]*Math.cos(angle)+y+.5);
			XRadarPts[i]=(int)(defRadarX[i]*Math.cos(angle)-defRadarY[i]*Math.sin(angle)+x+.5);
			YRadarPts[i]=(int)(defRadarX[i]*Math.sin(angle)+defRadarY[i]*Math.cos(angle)+y+.5);
		}

    	//Default image
    	g.setColor(fillColor);
    	g.fillOval((int)x-10,(int)y-10,20,20);
    	g.setColor(color);
    	g.drawOval((int)x-10,(int)y-10,20,20);

    	//Draws the rotating radar sensor
    	g.setColor(color);
		g.fillPolygon(XRadarPts,YRadarPts,4);

		//Pings every ping cycle
		if(pinging && pingRadius<maxPingRadius){
			g.setColor(color);
			g.drawOval((int)x-pingRadius, (int)y-pingRadius, 2*pingRadius, 2*pingRadius);
		}
		if(pingRadius >= maxPingRadius && pinging){
			pinging = false;
			pingDelayLeft = pingDelay;
			pingRadius = 10;
		}

		//Draws the green circle around selected units
		if(selected){
			g.setColor(Color.green);
			g.drawOval((int)x-25, (int)y-25, 50, 50);
		}


    }
}
