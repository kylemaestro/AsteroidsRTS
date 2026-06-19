import java.awt.*;

public abstract class Ship extends Sentry{
	//Class variables

	//Constructor
	public Ship(){}

	public final void move()
	{
		if(turningLeft)
			angle-=rotationalSpeed;
		if(turningRight)
			angle+=rotationalSpeed;
		if(angle>(2*Math.PI))
			angle-=(2*Math.PI);
		else if(angle<0)
			angle+=(2*Math.PI);
		if(accelerating)
		{
			if(AsteroidsRTS.doInstantStarts==true){
				xVelocity=acceleration*Math.cos(angle)*45;
				yVelocity=acceleration*Math.sin(angle)*45;
			}else{
				xVelocity+=acceleration*Math.cos(angle);
				yVelocity+=acceleration*Math.sin(angle);
			}

		}else{
			if(AsteroidsRTS.doInstantStops==true){
				xVelocity=0;
				yVelocity=0;
			}
		}
		x+=xVelocity;
		y+=yVelocity;
		xVelocity*=velocityDecay;
		yVelocity*=velocityDecay;
	}

}