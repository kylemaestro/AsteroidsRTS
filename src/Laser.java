import java.awt.*;

class Laser extends Projectile{
		//Default graphic without rotation
		int[] defX = {-8, -8, 8, 8};
		int[] defY = {-1, 1, 1 ,-1};
		int[] XPts = new int[4];
		int[] YPts = new int[4];

	//Primary constructor
	public Laser(double x, double y, double angle, int lifeLeft, int faction){
		setColor(faction);
		this.faction=faction;
		this.lifeLeft=lifeLeft;
		this.angle=angle;
		this.x=x;
		this.y=y;

		//Statistics specific to Shot
		damage=1;
		speed=11;

		setVelocity();
	}

	public void draw(Graphics g){
		//A rectangle that moves with the short side foward
		for(int i=0; i<4; i++){
			XPts[i]=(int)(defX[i]*Math.cos(angle)-defY[i]*Math.sin(angle)+x+.5);
			YPts[i]=(int)(defX[i]*Math.sin(angle)+defY[i]*Math.cos(angle)+y+.5);
		}
		g.setColor(color);
		g.fillPolygon(XPts,YPts,4);
	}
}