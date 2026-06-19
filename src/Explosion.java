import java.awt.*;

class Explosion extends Effect{
	//Class variables
	int radius=0, size, speed;
	boolean dead=false;

	//Constructor
	public Explosion(int x, int y, int size, int speed, int faction){
		this.x=x;
		this.y=y;
		this.size=size;
		this.speed=speed;
		setColor(faction);
	}

	public void draw(Graphics g){
		g.setColor(color);
		if(radius<size){
			g.fillOval((int)x-radius, (int)y-radius, radius*2, radius*2);
			radius+=speed;
		}else{
			dead=true;
		}
	}

	public boolean getDead(){return dead;}
}