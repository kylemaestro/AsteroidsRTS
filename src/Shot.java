import java.awt.*;

class Shot extends Projectile{

	//Primary constructor
	public Shot(double x, double y, double angle, int lifeLeft, int faction){
		setColor(faction);
		this.faction=faction;
		this.lifeLeft=lifeLeft;
		this.x=x;
		this.y=y;
		this.angle=angle;

		//Statistics specific to Shot
		damage=1;
		speed=6;

		setVelocity();
	}

	public void draw(Graphics g){
		//The standard 3x3 dot used in the older versions
		g.setColor(color);
		g.fillOval((int)(x-.5), (int)(y-.5), 3, 3);
	}
}
