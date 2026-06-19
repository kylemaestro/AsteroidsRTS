///////////////////////////////////////////////////////////////////////////////////////////////
//     _____            __                      .__    .___     ______________________________
//	  /  _  \   _______/  |_  ___________  ____ |__| __| _/_____\______   \__    ___/   _____/
//	 /  /_\  \ /  ___/\   __\/ __ \_  __ \/  _ \|  |/ __ |/  ___/|       _/ |    |  \_____  \
//	/    |    \\___ \  |  | \  ___/|  | \(  <_> )  / /_/ |\___ \ |    |   \ |    |  /        \
//	\____|__  /____  > |__|  \___  >__|   \____/|__\____ /____  >|____|_  / |____| /_______  /
//	        \/     \/            \/                     \/    \/        \/                 \/
//	Indev 0.6.1
///////////////////////////////////////////////////////////////////////////////////////////////


//kyalmeister@gmail.com
//cameron.ford.311@k12.friscoisd.org

//Import statements
import java.text.DecimalFormat;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.awt.geom.Area;
import java.awt.geom.AffineTransform;
import java.awt.*;
import java.util.*;
import java.lang.Object;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class AsteroidsRTS extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	String versionNum = "v. 0.6.1";

	//Developer Modes
	public boolean displayVersion = true;
	public boolean displayMouseCoordinates = true;
	public boolean displayMapCoordinates = true;
	public boolean drawMapBorder = true;
	public static boolean drawSectorBorders = true;
	public boolean enableZoom = false; //Not functioning

	//Public Statics
	public static boolean doInstantStops = true; //Not functioning
	public static boolean doInstantStarts = true; //Not functioning
	public static boolean stoppedShipsFaceTarget = true; /*NOT IMPLEMENTED!*/
	public static boolean drawAllWaypoints = false;
	public boolean turnAndMove = false;
	public static int textureMode = 0; //0 or 1

	//Gameplay variables
	int redUnitCap = 50;
	int redUnitCount = 0;
	int blueUnitCap = 50;
	int blueUnitCount = 0;
	int redStartCash = 500;
	int blueStartCash = 500;

	//Applet variables
	final int appletWinX = 1280, appletWinY = 720;
	int windowX, windowY;
	int originX = 0, originY = 0;
	double translateX, translateY;
	double scale = 1;
	double zoomSpeed = .01;

	//Map variables
	public static int sectorsX = 10, sectorsY = 10, sectorSizeX = 1000, sectorSizeY = 1000;
	public static int mapSizeX = (sectorsX * sectorSizeX)/2, mapSizeY = (sectorsY * sectorSizeY)/2;
	int starAmount = 200 * (sectorsX * sectorsY);

	//Graphics variables
	public static boolean enableAntiAliasing = true;
	Rectangle2D.Double fog;
	Area fogArea;
	Rectangle2D.Double visible;

	//Time variables
	long startTime, endTime, framePeriod, timerLength;
	public long currentTime = System.currentTimeMillis();

	//For rounding purposes
	DecimalFormat tenths = new DecimalFormat("#.#");

	//Player's team (changed with keyListener)
	//Note: Faction 1 = Red, Faction 2 = Blue
	int playerFaction = 1;

	//Mouse variables
	int mouseX, mouseY;
	int mapX, mapY;
	int mouseClickX, mouseClickY, mouseReleaseX, mouseReleaseY;
	boolean scrolled = false;
	int wheelNotches = 0;
	int lastOffsetX, lastOffsetY;
	int clickX, clickY;
	int pressX, pressY;
	int releaseX, releaseY;
	int enterX, enterY;
	int exitX, exitY;
	int dragX, dragY;
	int moveX, moveY;
	int mouseButton;

	//Used for multi-object units such as sentries and ships
	int nextRedUnit=0;
	int nextRedTurret=0;
	int nextRedWaypoint=0;
	int nextBlueUnit=0;
	int nextBlueTurret=0;
	int nextBlueWaypoint=0;

	//Mouse-related class variables
	int selectedNumRed = -1, selectedNumBlue = -1, hoveredNumRed = -1, hoveredNumBlue = -1;
	boolean unitSelected=false;
	boolean controlMode=false;
	int controlledNum = -1;
	int factionSelected, factionHovered = 0;
	int selectedType = 0, hoveredType; //0=none, 1=ship, 2=wall, 3=healer

	//Color declaration (BLUE)
	Color blue = new Color(0, 153, 255);

	//Random number generator
	Random rand = new Random();

	//Generates buffered image
	Dimension dim;
	Image img;
	Graphics g;

	//Object arrays/hashmaps
	ArrayList<Unit> unitRedList = new ArrayList<Unit>();
	ArrayList<Unit> unitBlueList = new ArrayList<Unit>();
	ArrayList<Projectile> projectileRedList = new ArrayList<Projectile>();
	ArrayList<Projectile> projectileBlueList = new ArrayList<Projectile>();
	ArrayList<Explosion> explosionList = new ArrayList<Explosion>();
	ArrayList<Turret> turretRedList = new ArrayList<Turret>();
	ArrayList<Turret> turretBlueList = new ArrayList<Turret>();
	ArrayList<Waypoint> waypointRedList = new ArrayList<Waypoint>();
	ArrayList<Waypoint> waypointBlueList = new ArrayList<Waypoint>();
	ArrayList<Star> stars = new ArrayList<Star>();
	ArrayList<Ellipse2D> sightRedList = new ArrayList<Ellipse2D>();
	ArrayList<Ellipse2D> sightBlueList = new ArrayList<Ellipse2D>();
	Sector[][] sectors = new Sector[sectorsX][sectorsY];
	GUI playerGUI;
	Minimap minimap;

	//Thread variables
	Thread thread;
	private volatile boolean threadSuspended;


	//////////////////////
	//  ___      _ _
	// |_ _|_ _ (_) |_
	//  | || ' \| |  _|
	// |___|_||_|_|\__|
	//
	//////////////////////


	//Initialization method
	public void init()
	{
		//Applet window size (1000px 600px)
		resize(appletWinX, appletWinY);

		//Keyboard/Mouse listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		addMouseWheelListener(this);

		//Creates stars
		for(int x=0; x<starAmount; x++)
			stars.add(new Star());

		//Creates and arranges sectors into grid
		for(int x=0; x<sectorsX; x++){
			for(int y=0; y<sectorsY; y++){
				sectors[x][y] = new Sector();
			}
		}
		arrangeSectors();

		//Creates the player GUI and minimap
		playerGUI = new GUI();
		minimap = new Minimap();

		//Starts timer
		startTime = 0;
		endTime = 0;
		framePeriod = 25; //25 ms ~ 40fps
		//TODO: Multithread game so that movement speeds are indp. of repaint speed (i.e FPS)

		//Double Buffering
		dim = getSize();
		img = createImage(dim.width, dim.height);
		g = img.getGraphics();

		//Creates main thread and starts it
		thread = new Thread(this);
		thread.start();
	}


	////////////////////////
	//  ___      _     _
	// | _ \__ _(_)_ _| |_
	// |  _/ _` | | ' \  _|
	// |_| \__,_|_|_||_\__|
	//
	////////////////////////


	public void paint(Graphics gfx)
	{
		//Creates the Graphics2D object used in transforming/anti-aliasing
		Graphics2D g2d = (Graphics2D)g;

		//AffineTransform creation
		AffineTransform transformer = new AffineTransform();

		//Translating based on mouse dragging
		transformer.translate(translateX, translateY);
		transformer.scale(scale, scale);
		g2d.setTransform(transformer);

		//Anti Aliasing :D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		//Draws the map
		g.setColor(Color.black);
		g.fillRect(-mapSizeX*2, -mapSizeY*2, mapSizeX*4, mapSizeY*4);

		//Draws all Projectiles
		for(int y=0; y<projectileRedList.size(); y++)
			if(insideViewWindow(projectileRedList.get(y)))
				projectileRedList.get(y).draw(g);
		for(int y=0; y<projectileBlueList.size(); y++)
			if(insideViewWindow(projectileBlueList.get(y)))
				projectileBlueList.get(y).draw(g);

		//Draws all Units
		for(int y=0; y<unitRedList.size(); y++)
			if(insideViewWindow(unitRedList.get(y)))
				unitRedList.get(y).draw(g);
		for(int y=0; y<unitBlueList.size(); y++)
			if(insideViewWindow(unitBlueList.get(y)))
				unitBlueList.get(y).draw(g);

		//Draws all Turrets
		for(int y=0; y<turretRedList.size(); y++)
			if(insideViewWindow(turretRedList.get(y)))
				turretRedList.get(y).draw(g);
		for(int y=0; y<turretBlueList.size(); y++)
			if(insideViewWindow(turretBlueList.get(y)))
				turretBlueList.get(y).draw(g);

		//Draws all Explosions
		for(int y=0; y<explosionList.size(); y++)
			if(insideViewWindow(explosionList.get(y)))
				explosionList.get(y).draw(g);

		//Draws all Waypoints with lines connecting them to their ships
		if(playerFaction==1){
			for(int y=0; y<waypointRedList.size(); y++){
				if(waypointRedList.get(y).getAlive()){
					if(drawAllWaypoints || unitRedList.get(waypointRedList.get(y).getShipNum()).getSelected()){
						waypointRedList.get(y).draw(g);
						g.setColor(waypointRedList.get(y).getColor());
						g.drawLine((int)waypointRedList.get(y).getX(), (int)waypointRedList.get(y).getY(),
							(int)unitRedList.get(waypointRedList.get(y).getShipNum()).getX(), (int)unitRedList.get(waypointRedList.get(y).getShipNum()).getY());
					}
				}
			}
		}
		if(playerFaction==2){
			for(int y=0; y<waypointBlueList.size(); y++){
				if(waypointBlueList.get(y).getAlive()){
					if(drawAllWaypoints || unitBlueList.get(waypointBlueList.get(y).getShipNum()).getSelected()){
						waypointBlueList.get(y).draw(g);
						g.setColor(waypointBlueList.get(y).getColor());
						g.drawLine((int)waypointBlueList.get(y).getX(), (int)waypointBlueList.get(y).getY(),
							(int)unitBlueList.get(waypointBlueList.get(y).getShipNum()).getX(), (int)unitBlueList.get(waypointBlueList.get(y).getShipNum()).getY());
					}
				}
			}
		}

		//Draws all stars
		for(int y=0; y<stars.size(); y++)
			if(insideViewWindow(stars.get(y)))
				stars.get(y).draw(g);

		//Creates the fog of war
		//NOTE: Anything ABOVE this line will be BELOW the fog. Anything BELOW this line will be ABOVE the fog.
		g.setColor(Color.gray);
		fog = new Rectangle2D.Double();
		fog.setFrame(0-mapSizeX+25, 0-mapSizeY+25, mapSizeX*2-25, mapSizeY*2-25);
		fogArea = new Area(fog);

		//Subtracts from the fog of war based on what units the player's faction has on the map
		if(playerFaction==1)
		{
			for(int x=0; x<unitRedList.size(); x++)
			{
				//Creates an arraylist with Area objects that store a units sight radius.
				//These Areas are then cut out from the fog of war, providing areas of sight for units.
				sightRedList.add(new Ellipse2D.Double());
				sightRedList.get(x).setFrame(unitRedList.get(x).getX()-unitRedList.get(x).getSightRadius(),
					unitRedList.get(x).getY()-unitRedList.get(x).getSightRadius(), unitRedList.get(x).getSightRadius()*2, unitRedList.get(x).getSightRadius()*2);
				Area sightArea = new Area(sightRedList.get(x));
				fogArea.subtract(sightArea);

			}
		}
		else if(playerFaction==2)
		{
			for(int x=0; x<unitBlueList.size(); x++)
			{
				//Creates an arraylist with Area objects that store a units sight radius.
				//These Areas are then cut out from the fog of war, providing areas of sight for units.
				sightBlueList.add(new Ellipse2D.Double());
				sightBlueList.get(x).setFrame(unitBlueList.get(x).getX()-unitBlueList.get(x).getSightRadius(),
					unitBlueList.get(x).getY()-unitBlueList.get(x).getSightRadius(), unitBlueList.get(x).getSightRadius()*2, unitBlueList.get(x).getSightRadius()*2);
				Area sightArea = new Area(sightBlueList.get(x));
				fogArea.subtract(sightArea);

			}
		}

		//Draws the fog AFTER it has been carved up...
		g2d.fill(fogArea);

		//Draws all sector borders
		for(int x=0; x<sectorsX; x++){
			for(int y=0; y<sectorsY; y++){
				sectors[x][y].draw(g);
			}
		}

		//Control Mode
		if(controlMode){
			g.setColor(Color.white);
			g.drawString("ESC to exit command mode", 450-(int)translateX, (appletWinY-580)-(int)translateY);
		}

		//Draws the playerGUI and minimap
		playerGUI.setX(0-(int)translateX);
		playerGUI.setY(0-(int)translateY);
		playerGUI.draw(g);
		minimap.setX(0-(int)translateX);
		minimap.setY(0-(int)translateY);
		minimap.draw(g);

		//Version number
		if(displayVersion){
			g.setColor(Color.green);
			g.drawString("Remake version " +versionNum, 25-(int)translateX, (25)-(int)translateY);
		}

		//Mouse coordinates
		if(displayMouseCoordinates){
			g.drawString("Mouse X: "+mouseX+" Mouse Y: "+mouseY+"", 200-(int)translateX, (25)-(int)translateY);
		}

		//Map coordinates
		if(displayMapCoordinates){
			g.drawString("Map X: "+mapX+" Map Y: "+mapY+"", 375-(int)translateX, (25)-(int)translateY);
		}

		//Map border drawing
		if(drawMapBorder){
			g.setColor(Color.red);
			g.fillRect(-mapSizeX, -mapSizeY, mapSizeX*2, 25);
			g.fillRect(-mapSizeX, mapSizeY-25, mapSizeX*2, 25);
			g.fillRect(-mapSizeX, -mapSizeY, 25, mapSizeY*2);
			g.fillRect(mapSizeX-25, -mapSizeY, 25, mapSizeY*2);
		}

		//Paints the player's team for testing functions
		if(playerFaction==1){
			g.setColor(Color.red);
			g.drawString("Red Team", (appletWinX-75)-(int)translateX, (25)-(int)translateY);
		}
		if(playerFaction==2){
			g.setColor(blue);
			g.drawString("Blue Team", (appletWinX-75)-(int)translateX, (50)-(int)translateY);
		}

		gfx.drawImage(img, 0, 0, this);

	}

	public void update(Graphics gfx)
	{
		paint(gfx);
	}


	///////////////////
	//  ___
	// | _ \_  _ _ _
	// |   / || | ' \
	// |_|_\\_,_|_||_|
	//
	///////////////////


	public void run()
	{
		for(;;)
		{
			//Time keepers
			currentTime = System.currentTimeMillis();
			startTime = System.currentTimeMillis();

			//Collisions
			collisionsRed();
			collisionsBlue();

			//Turret targeting
			turretTargetRed();
			turretTargetBlue();

			//Ship movement
			shipRedMovement();
			shipBlueMovement();

			//Star movement
			if(timerLength == 0)
				timerLength = currentTime + 1000;
			if(currentTime > timerLength)
			{
				for(int x=0; x<stars.size(); x++)
					stars.get(x).move();

				timerLength = 0;
			}

			//Makes turrets shoot projectiles
			shootTurrets();

			//Clears dead explosions
			clearExplosions();

			//Kills Projectiles out of life
			clearProjectiles();

			//Moves all units
			for(int y=0; y<unitRedList.size(); y++)
				unitRedList.get(y).move();
			for(int y=0; y<unitBlueList.size(); y++)
				unitBlueList.get(y).move();

			//Moves all projectiles
			for(int y=0; y<projectileRedList.size(); y++)
				projectileRedList.get(y).move(mapSizeX, mapSizeY);
			for(int y=0; y<projectileBlueList.size(); y++)
				projectileBlueList.get(y).move(mapSizeX, mapSizeY);

			//Moves all Turrets
			for(int y=0; y<turretRedList.size(); y++)
				turretRedList.get(y).move();
			for(int y=0; y<turretBlueList.size(); y++)
				turretBlueList.get(y).move();

			//Refreshes screen
			repaint();

			try
			{
				endTime = System.currentTimeMillis(); //Mark end time

				if(framePeriod - (endTime - startTime)>0) //Dont sleep for negative amount of time :s
					Thread.sleep(framePeriod - (endTime-startTime));

				synchronized(this) { //Allows for non-deprecated suspending/resuming of thread
                    while (threadSuspended)
                        wait();
                }

			}catch(InterruptedException e){
			}
		}
	}


	/////////////////////////////
	//  __  __
	// |  \/  |___ _  _ ___ ___
	// | |\/| / _ \ || (_-</ -_)
	// |_|  |_\___/\_,_/__/\___|
	//
	/////////////////////////////

	//Method to differentiate buttons. Not required for mouseListener
	private void checkButton(MouseEvent e)
	{
		switch(e.getButton())
		{
			case MouseEvent.BUTTON1:
				mouseButton = 1;
			break;

			case MouseEvent.BUTTON2:
				mouseButton = 2;
			break;

			case MouseEvent.BUTTON3:
				mouseButton = 3;
			break;

			default:
				mouseButton = 0;
		}
	}

	//Main mouseListener method to detect events
	public void mousePressed(MouseEvent e)
	{
		if(!controlMode){
			checkButton(e);
			pressX = e.getX();
			pressY = e.getY();

			lastOffsetX = pressX;
			lastOffsetY = pressY;

			pressX = pressX - (int)(translateX*scale);
			pressY = pressY - (int)(translateY*scale);

			if(mouseButton == 1)
			{
				if(checkSelection(pressX, pressY)){
					if(!unitSelected)
						unitSelected = true;
				}else
					unitSelected = false;
			}

			if(mouseButton == 3 && unitSelected){
				//Only move-to orders for Ships(type 1) are currently implemented
				if(selectedType==1){
					setWaypoint(pressX, pressY, factionSelected);
				}
			}
		}
	}

	public void mouseDragged(MouseEvent e){
		if(mouseButton==2){
			//New x and y are defined by current mouse location subtracted
			//by previously processed mouse location
			int newX = e.getX() - lastOffsetX;
			int newY = e.getY() - lastOffsetY;

			//Increment last offset to last processed by drag event.
			lastOffsetX += newX;
			lastOffsetY += newY;

			//Update the applet window
			this.translateX += newX;
			this.translateY += newY;

			repaint();
		}
	}

	public void mouseMoved(MouseEvent e)
	{
		mouseX = e.getX();
		mouseY = e.getY();

		mapX = mouseX - (int)(translateX*scale);
		mapY = mouseY - (int)(translateY*scale);
		
		checkHovered(mapX, mapY);
	}

	//Unused mouseListener methods
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseWheelMoved(MouseWheelEvent e){}


	////////////////////////////////////////////
	//  _  __         _                      _
	// | |/ /___ _  _| |__  ___  __ _ _ _ __| |
	// | ' </ -_) || | '_ \/ _ \/ _` | '_/ _` |
	// |_|\_\___|\_, |_.__/\___/\__,_|_| \__,_|
	//           |__/
	////////////////////////////////////////////


	//Keylistener main method
	public void keyPressed(KeyEvent e){

		int key = e.getKeyCode();
		System.out.println("Keycode of last input: "+key);

		//Toggles teams with numpad 0
		if(key==96 && !controlMode){
			selectedType=0;
			selectedNumRed=-1;
			selectedNumBlue=-1;
			switch(playerFaction){
				case 1:
					playerFaction=2;
				break;
				case 2:
					playerFaction=1;
				break;
				default:
					System.out.println("Error: playerFaction is not 1 or 2");
				break;
			}
			for(int y=0; y<unitRedList.size(); y++)
				unitRedList.get(y).setSelected(false);
			for(int y=0; y<unitBlueList.size(); y++)
				unitBlueList.get(y).setSelected(false);
		}

		//Pressing ESC will exit control mode, allowing mouse commands once more
		if(key==27){
			for(int y=0; y<unitRedList.size(); y++)
				unitRedList.get(y).setControlled(false);
			for(int y=0; y<unitBlueList.size(); y++)
				unitBlueList.get(y).setControlled(false);
			for(int y=0; y<unitRedList.size(); y++)
				unitRedList.get(y).setSelected(false);
			for(int y=0; y<unitBlueList.size(); y++)
				unitBlueList.get(y).setSelected(false);

			controlMode=false;
			controlledNum=-1;
		}

		//Changes artstyle with ;
		if(key==59 && !controlMode){
			if(textureMode==1){
				textureMode=0;
			}else if(textureMode==0){
				textureMode=1;
			}
			for(int y=0; y<unitRedList.size(); y++)
				unitRedList.get(y).setColor(1);
			for(int y=0; y<unitBlueList.size(); y++)
				unitBlueList.get(y).setColor(2);
		}

		//Creates a scout with 1
		if(key==49 && !controlMode){
			//(int x, int y, double angle, double acceleration, double rotationalSpeed, int health, String shotType, int shotDelay,int range, int accuracy, int shotLife, int faction)
			createScout(mapX, mapY, 3*Math.PI/2, .02, .03, 20, "laser", 40, 250, 7, 125, playerFaction);
		}

		//Creates a sonar with 4
		if(key==52 && !controlMode){
			if(playerFaction==1){
				unitRedList.add(new Sonar(mapX, mapY, 10, 1));
				nextRedUnit++;
			}
			if(playerFaction==2){
				unitBlueList.add(new Sonar(mapX, mapY, 10, 2));
				nextBlueUnit++;
			}
		}

		//Creates a wall with 5
		if(key==53 && !controlMode){
			if(playerFaction==1){
				unitRedList.add(new Wall(mapX, mapY, Math.PI/2, 25, 1));
				nextRedUnit++;
			}
			if(playerFaction==2){
				unitBlueList.add(new Wall(mapX, mapY, Math.PI/2, 25, 2));
				nextBlueUnit++;
			}
		}

		//Creates a sentry with 6
		if(key==54 && !controlMode){
			//(int x, int y, int health, int shotDelay, int range, int accuracy, int shotLife, String shotType, int faction)
			createSentry(mapX, mapY, 20, 10, 500, 5, 75, "laser", playerFaction);
			//No need for nextUnit or nextTurret variables here because they are taken care of in createSentry
		}

		//Creates a shot with 7
		if(key==55 && !controlMode){
			if(playerFaction==1)
				projectileRedList.add(new Shot(mapX, mapY, 0, 125, 1));
			if(playerFaction==2)
				projectileBlueList.add(new Shot(mapX, mapY, 0, 125, 2));
		}
		//Creates a laser with 8
		if(key==56 && !controlMode){
			if(playerFaction==1)
				projectileRedList.add(new Laser(mapX, mapY, 0, 75, 1));
			if(playerFaction==2)
				projectileBlueList.add(new Laser(mapX, mapY, 0, 75, 2));
		}

		//Movement Controls
		if(controlMode && controlledNum!=-1){
			if(playerFaction==1){
				//W makes ship accelerate
				if(key==87 && unitRedList.get(controlledNum) instanceof Ship)
					unitRedList.get(controlledNum).setAccelerating(true);
				//S makes left turn
				if(key==65)
					unitRedList.get(controlledNum).setTurningLeft(true);
				//D makes left turn
				if(key==68)
					unitRedList.get(controlledNum).setTurningRight(true);
				//SPACE allows shooting
				if(key==32)
					turretRedList.get(unitRedList.get(controlledNum).getTurretNum()).setShooting(true);
			}
			if(playerFaction==2){
				if(key==87 && unitBlueList.get(controlledNum) instanceof Ship)
					unitBlueList.get(controlledNum).setAccelerating(true);
				if(key==65)
					unitBlueList.get(controlledNum).setTurningLeft(true);
				if(key==68)
					unitBlueList.get(controlledNum).setTurningRight(true);
				if(key==32)
					turretBlueList.get(unitBlueList.get(controlledNum).getTurretNum()).setShooting(true);
			}
		}
	}

	//Used for stopping commands once control keys are released
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();

		//Movement Controls
		if(controlMode && controlledNum!=-1){
			if(playerFaction==1){
				//W makes ship accelerate
				if(key==87 && unitRedList.get(controlledNum) instanceof Ship)
					unitRedList.get(controlledNum).setAccelerating(false);
				//S makes left turn
				if(key==65)
					unitRedList.get(controlledNum).setTurningLeft(false);
				//D makes left turn
				if(key==68)
					unitRedList.get(controlledNum).setTurningRight(false);
				//SPACE allows shooting
				if(key==32)
					turretRedList.get(unitRedList.get(controlledNum).getTurretNum()).setShooting(false);
			}
			if(playerFaction==2){
				if(key==87 && unitBlueList.get(controlledNum) instanceof Ship)
					unitBlueList.get(controlledNum).setAccelerating(false);
				if(key==65)
					unitBlueList.get(controlledNum).setTurningLeft(false);
				if(key==68)
					unitBlueList.get(controlledNum).setTurningRight(false);
				if(key==32)
					turretBlueList.get(unitBlueList.get(controlledNum).getTurretNum()).setShooting(false);
			}
		}
	}

	//Unused keyListener methods
	public void keyTyped(KeyEvent e){}


	/////////////////////////////////////////////////////////////
	//  _____                 _     _____                  _
	// |_   _|  _ _ _ _ _ ___| |_  |_   _|_ _ _ _ __ _ ___| |_
	//   | || || | '_| '_/ -_)  _|   | |/ _` | '_/ _` / -_)  _|
	//   |_| \_,_|_| |_| \___|\__|   |_|\__,_|_| \__, \___|\__|
	//                                           |___/
	/////////////////////////////////////////////////////////////

	public void turretTargetRed()
	{
		//Math Variables for calculations
		double delY = 0, delX = 0, targetAngle = 0;

		//Traverses the turret array
		for(int y=0; y<turretRedList.size(); y++)
		{
			//Turrets do not automatically shoot if their ship is under control
			if(controlMode && unitRedList.get(turretRedList.get(y).getUnitNum()).getControlled()){
				turretRedList.get(y).setAngle(unitRedList.get(turretRedList.get(y).getUnitNum()).getAngle());
				return;
			}

			//If the turret is not shooting, then it's target is null
			if(turretRedList.get(y).getShooting()==false){
				turretRedList.get(y).setTarget(100000);
			}

			//Stops all shooting if there are no targets left to shoot at
			if(unitBlueList.size()==0){
				turretRedList.get(y).setShooting(false);
			}

			//If there is no target, then it looks for a new one within range
			if(turretRedList.get(y).getTarget()==100000||unitBlueList.size()-1<turretRedList.get(y).getTarget())
			{
				for(int h=0; h<unitBlueList.size(); h++)
				{
					if(turretRedList.get(y).getRangeBounds().contains(unitBlueList.get(h).getX(), unitBlueList.get(h).getY())&&
						rand.nextInt(5)==1)
					{
						turretRedList.get(y).setTarget(h);
						targetAngle = 0;
						turretRedList.get(y).setShooting(true);
					}else{
						if(turretRedList.get(y).getTarget()==100000)
							turretRedList.get(y).setShooting(false);
					}
				}
			}

			//If a ship has a target, it checks to make sure it is still within range
			if(turretRedList.get(y).getTarget()!=100000){
				if(unitBlueList.size()-1>=turretRedList.get(y).getTarget()){
					if(turretRedList.get(y).getRangeBounds().contains(
					unitBlueList.get(turretRedList.get(y).getTarget()).getX(),
					unitBlueList.get(turretRedList.get(y).getTarget()).getY())){
					}else{
						turretRedList.get(y).setShooting(false);
						turretRedList.get(y).setTarget(100000);
					}
				}
			}

			//The math required for targeting
			if(turretRedList.get(y).getTarget()!=100000||turretRedList.get(y).getShooting())
			{
				if(unitBlueList.size()-1>=turretRedList.get(y).getTarget()){
					delX = unitBlueList.get(turretRedList.get(y).getTarget()).getX()-turretRedList.get(y).getX();
					delY = unitBlueList.get(turretRedList.get(y).getTarget()).getY()-turretRedList.get(y).getY();

					if(delX<0)
						targetAngle = (Math.atan(delY/delX))+Math.PI;
					else
						targetAngle =Math.atan(delY/delX);
						turretRedList.get(y).setAngle(targetAngle);
				}
			}
		}
	}

	public void turretTargetBlue()
	{
		//Math Variables for calculations
		double delY = 0, delX = 0, targetAngle = 0;

		//Traverses the turret array
		for(int y=0; y<turretBlueList.size(); y++)
		{
			//Turrets do not automatically shoot if their ship is under control
			if(controlMode && unitBlueList.get(turretBlueList.get(y).getUnitNum()).getControlled()){
				turretBlueList.get(y).setAngle(unitBlueList.get(turretBlueList.get(y).getUnitNum()).getAngle());
				return;
			}

			//If the turret is not shooting, then it's target is null
			if(turretBlueList.get(y).getShooting()==false){
				turretBlueList.get(y).setTarget(100000);
			}

			//Stops all shooting if there are no targets left to shoot at
			if(unitRedList.size()==0){
				turretBlueList.get(y).setShooting(false);
			}

			//If there is no target, then it looks for a new one within range
			if(turretBlueList.get(y).getTarget()==100000||unitRedList.size()-1<turretBlueList.get(y).getTarget())
			{
				for(int h=0; h<unitRedList.size(); h++)
				{
					if(turretBlueList.get(y).getRangeBounds().contains(unitRedList.get(h).getX(), unitRedList.get(h).getY())&&
						rand.nextInt(5)==1)
					{
						turretBlueList.get(y).setTarget(h);
						targetAngle = 0;
						turretBlueList.get(y).setShooting(true);
					}else{
						if(turretBlueList.get(y).getTarget()==100000)
							turretBlueList.get(y).setShooting(false);
					}
				}
			}

			//If a ship has a target, it checks to make sure it is still within range
			if(turretBlueList.get(y).getTarget()!=100000){
				if(unitRedList.size()-1>=turretBlueList.get(y).getTarget()){
					if(turretBlueList.get(y).getRangeBounds().contains(
					unitRedList.get(turretBlueList.get(y).getTarget()).getX(),
					unitRedList.get(turretBlueList.get(y).getTarget()).getY())){
					}else{
						turretBlueList.get(y).setShooting(false);
						turretBlueList.get(y).setTarget(100000);
					}
				}
			}

			//The math required for targeting
			if(turretBlueList.get(y).getTarget()!=100000||turretBlueList.get(y).getShooting())
			{
				if(unitRedList.size()-1>=turretBlueList.get(y).getTarget()){
					delX = unitRedList.get(turretBlueList.get(y).getTarget()).getX()-turretBlueList.get(y).getX();
					delY = unitRedList.get(turretBlueList.get(y).getTarget()).getY()-turretBlueList.get(y).getY();

					if(delX<0)
						targetAngle = (Math.atan(delY/delX))+Math.PI;
					else
						targetAngle =Math.atan(delY/delX);
						turretBlueList.get(y).setAngle(targetAngle);
				}
			}
		}
	}

	//Creates shots if turrets are shooting
	public void shootTurrets(){
		for(int y=0; y<turretRedList.size(); y++)
		{
			if(turretRedList.get(y).getShooting() && turretRedList.get(y).canShoot())
			{
				if(turretRedList.get(y).getShotType().equalsIgnoreCase("shot")){
					projectileRedList.add(turretRedList.get(y).shootShot());
				}else if(turretRedList.get(y).getShotType().equalsIgnoreCase("laser")){
					projectileRedList.add(turretRedList.get(y).shootLaser());
				}

			}
		}
		for(int y=0; y<turretBlueList.size(); y++)
		{
			if(turretBlueList.get(y).getShooting() && turretBlueList.get(y).canShoot())
			{
				if(turretBlueList.get(y).getShotType().equalsIgnoreCase("shot")){
					projectileBlueList.add(turretBlueList.get(y).shootShot());
				}else if(turretBlueList.get(y).getShotType().equalsIgnoreCase("laser")){
					projectileBlueList.add(turretBlueList.get(y).shootLaser());
				}
			}
		}
	}


	/////////////////////////////////////////////////////////////
	//  ___ _    _        __  __                           _
	// / __| |_ (_)_ __  |  \/  |_____ _____ _ __  ___ _ _| |_
	// \__ \ ' \| | '_ \ | |\/| / _ \ V / -_) '  \/ -_) ' \  _|
	// |___/_||_|_| .__/ |_|  |_\___/\_/\___|_|_|_\___|_||_\__|
	//            |_|
	/////////////////////////////////////////////////////////////

	public void shipRedMovement()
	{
		for(int y=0; y<unitRedList.size(); y++)
		{
			if(unitRedList.get(y) instanceof Ship){
				//Binds turrets to ships when they move
				turretRedList.get(unitRedList.get(y).getTurretNum()).setX(unitRedList.get(y).getX());
				turretRedList.get(unitRedList.get(y).getTurretNum()).setY(unitRedList.get(y).getY());

				if(!unitRedList.get(y).getControlled()){
					//Method Variables
					double targetAngle = 0;
					double shipAngle;
					double tarAng = 0;
					double maxRight = 5000, maxLeft = 5000;
					boolean turnLeft = false; //If true, it will turn left, and if false: right
					boolean going = false; //Detects if it's in the process of going towards the wayOne
					double targetDistance=0; //The distance between shipOne and wayOne

					if(unitRedList.get(y).getAccelerating() == true)
						going = true;

					//Checks if the waypoint is alive
					if(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getAlive() == true)
					{
						//Uses same targeting mechanism as the sentries
						double delY, delX;
						delX = waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX()-unitRedList.get(y).getX();
						delY = waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY()-unitRedList.get(y).getY();

						//Calculates the targetAngle; used to face the waypoint
						if(delX<0)
							targetAngle = (Math.atan(delY/delX))+Math.PI;
						else
							targetAngle =Math.atan(delY/delX);

						//Fixes the top right quadrant issue AKA: The magical wonderful code that saved the world
						if(delX > 0 && delY < 0)
							targetAngle = targetAngle+Math.PI*2;

						//Rounds the angles and does some hocus pocus stuff. (Cameron wrote this :s)
						tarAng = Math.round(targetAngle*10);
						shipAngle = Math.round(unitRedList.get(y).getAngle()*10);

						//Calculates if turning left or right is most optimal
						if(maxRight == 5000)
						{
							maxRight = shipAngle + 31.5;
							maxLeft = shipAngle - 31.5;

							if(tarAng < maxRight && tarAng > shipAngle)
								turnLeft = false;

							if(tarAng > maxLeft && tarAng < shipAngle)
								turnLeft = true;

							if(maxRight > 63)
							{
								maxRight = maxRight - 63;
								if(tarAng < maxRight)
									turnLeft = false;
							}

							if(maxLeft < 0)
							{
								maxLeft = maxLeft + 63;
								if(tarAng > maxLeft)
									turnLeft = true;
							}

						} //Closes if(maxRight == 5000)


						//Checks if waypoint is above the ship
						if(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY() > unitRedList.get(y).getY())
						{
							//ship is to the right of waypoint
							if(unitRedList.get(y).getX() > waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())
								targetDistance = Math.sqrt( ((waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY()-unitRedList.get(y).getY())*(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY()-unitRedList.get(y).getY())) + ((unitRedList.get(y).getX()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())*(unitRedList.get(y).getX()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())) );

							//ship is to the left of waypoint
							else
								targetDistance = Math.sqrt( ((waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY()-unitRedList.get(y).getY())*(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY()-unitRedList.get(y).getY())) + ((waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX()-unitRedList.get(y).getX())*(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX()-unitRedList.get(y).getX())) );

						//Otherwise; the waypoint is below the ship
						}else{

							//ship is to the right of waypoint
							if(unitRedList.get(y).getX() > waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())
								targetDistance = Math.sqrt( ((unitRedList.get(y).getY()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY())*(unitRedList.get(y).getY()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY())) + ((unitRedList.get(y).getX()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())*(unitRedList.get(y).getX()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX())) );
							//ship is to the left of waypoint
							else
								targetDistance = Math.sqrt( ((unitRedList.get(y).getY()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY())*(unitRedList.get(y).getY()-waypointRedList.get(unitRedList.get(y).getWaypointNum()).getY())) + ((waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX()-unitRedList.get(y).getX())*(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getX()-unitRedList.get(y).getX())) );

						}

						//Detects if the ship is not facing the target, in which it needs to turn in place until it is
						if((unitRedList.get(y).getAngle() != targetAngle) && (going==false))
						{
							unitRedList.get(y).setAccelerating(turnAndMove);

							if(turnLeft == true)
								unitRedList.get(y).setTurningLeft(true);
							else
								unitRedList.get(y).setTurningRight(true);
						}

						//Detects if it is roughly facing the waypoint
						if(Math.round(unitRedList.get(y).getAngle()*10) == Math.round(targetAngle*10))
						{
							//Snaps the ship to face the waypoint when close to avoid bugs
							unitRedList.get(y).setAngle(targetAngle);

							//Stops the turning
							unitRedList.get(y).setTurningLeft(false);
							unitRedList.get(y).setTurningRight(false);

							//"At 5 pixels, it has gotten close enough...whatever. It will pass as acceptable
							if(targetDistance>5)
								going = true;
							if(targetDistance<5)
								going = false;

							if(going)
								unitRedList.get(y).setAccelerating(true);
							if(going == false)
							{
								unitRedList.get(y).setAccelerating(false);
								waypointRedList.get(unitRedList.get(y).getWaypointNum()).setAlive(false);
							}

						}

					}else{ //Stops the ship if there exists no waypoint
						unitRedList.get(y).setAccelerating(false);
						unitRedList.get(y).setTurningLeft(false);
						unitRedList.get(y).setTurningRight(false);
					}
				}
			}
		}
	}

	public void shipBlueMovement()
	{
		for(int y=0; y<unitBlueList.size(); y++)
		{
			if(unitBlueList.get(y) instanceof Ship){
				//Binds turrets to ships when they move
				turretBlueList.get(unitBlueList.get(y).getTurretNum()).setX(unitBlueList.get(y).getX());
				turretBlueList.get(unitBlueList.get(y).getTurretNum()).setY(unitBlueList.get(y).getY());

				if(!unitBlueList.get(y).getControlled()){
					//Method Variables
					double targetAngle = 0;
					double shipAngle;
					double tarAng = 0;
					double maxRight = 5000, maxLeft = 5000;
					boolean turnLeft = false; //If true, it will turn left, and if false: right
					boolean going = false; //Detects if it's in the process of going towards the wayOne
					double targetDistance=0; //The distance between shipOne and wayOne

					if(unitBlueList.get(y).getAccelerating() == true)
						going = true;

					//Checks if the waypoint is alive
					if(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getAlive() == true)
					{
						//Uses same targeting mechanism as the sentries
						double delY, delX;
						delX = waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX()-unitBlueList.get(y).getX();
						delY = waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY()-unitBlueList.get(y).getY();

						//Calculates the targetAngle; used to face the waypoint
						if(delX<0)
							targetAngle = (Math.atan(delY/delX))+Math.PI;
						else
							targetAngle =Math.atan(delY/delX);

						//Fixes the top right quadrant issue AKA: The magical wonderful code that saved the world
						if(delX > 0 && delY < 0)
							targetAngle = targetAngle+Math.PI*2;

						//Rounds the angles and does some hocus pocus stuff. (Cameron wrote this :s)
						tarAng = Math.round(targetAngle*10);
						shipAngle = Math.round(unitBlueList.get(y).getAngle()*10);

						//Calculates if turning left or right is most optimal
						if(maxRight == 5000)
						{
							maxRight = shipAngle + 31.5;
							maxLeft = shipAngle - 31.5;

							if(tarAng < maxRight && tarAng > shipAngle)
								turnLeft = false;

							if(tarAng > maxLeft && tarAng < shipAngle)
								turnLeft = true;

							if(maxRight > 63)
							{
								maxRight = maxRight - 63;
								if(tarAng < maxRight)
									turnLeft = false;
							}

							if(maxLeft < 0)
							{
								maxLeft = maxLeft + 63;
								if(tarAng > maxLeft)
									turnLeft = true;
							}

						} //Closes if(maxRight == 5000)


						//Checks if waypoint is above the ship
						if(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY() > unitBlueList.get(y).getY())
						{
							//ship is to the right of waypoint
							if(unitBlueList.get(y).getX() > waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())
								targetDistance = Math.sqrt( ((waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY()-unitBlueList.get(y).getY())*(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY()-unitBlueList.get(y).getY())) + ((unitBlueList.get(y).getX()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())*(unitBlueList.get(y).getX()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())) );

							//ship is to the left of waypoint
							else
								targetDistance = Math.sqrt( ((waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY()-unitBlueList.get(y).getY())*(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY()-unitBlueList.get(y).getY())) + ((waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX()-unitBlueList.get(y).getX())*(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX()-unitBlueList.get(y).getX())) );

						//Otherwise; the waypoint is below the ship
						}else{

							//ship is to the right of waypoint
							if(unitBlueList.get(y).getX() > waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())
								targetDistance = Math.sqrt( ((unitBlueList.get(y).getY()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY())*(unitBlueList.get(y).getY()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY())) + ((unitBlueList.get(y).getX()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())*(unitBlueList.get(y).getX()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX())) );
							//ship is to the left of waypoint
							else
								targetDistance = Math.sqrt( ((unitBlueList.get(y).getY()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY())*(unitBlueList.get(y).getY()-waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getY())) + ((waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX()-unitBlueList.get(y).getX())*(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getX()-unitBlueList.get(y).getX())) );

						}

						//Detects if the ship is not facing the target, in which it needs to turn in place until it is
						if((unitBlueList.get(y).getAngle() != targetAngle) && (going==false))
						{
							unitBlueList.get(y).setAccelerating(turnAndMove);

							if(turnLeft == true)
								unitBlueList.get(y).setTurningLeft(true);
							else
								unitBlueList.get(y).setTurningRight(true);
						}

						//Detects if it is roughly facing the waypoint
						if(Math.round(unitBlueList.get(y).getAngle()*10) == Math.round(targetAngle*10))
						{
							//Snaps the ship to face the waypoint when close to avoid bugs
							unitBlueList.get(y).setAngle(targetAngle);

							//Stops the turning
							unitBlueList.get(y).setTurningLeft(false);
							unitBlueList.get(y).setTurningRight(false);

							//"At 5 pixels, it has gotten close enough...whatever. It will pass as acceptable
							if(targetDistance>5)
								going = true;
							if(targetDistance<5)
								going = false;

							if(going)
								unitBlueList.get(y).setAccelerating(true);
							if(going == false)
							{
								unitBlueList.get(y).setAccelerating(false);
								waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).setAlive(false);
							}

						}

					}else{ //Stops the ship if there exists no waypoint
						unitBlueList.get(y).setAccelerating(false);
						unitBlueList.get(y).setTurningLeft(false);
						unitBlueList.get(y).setTurningRight(false);
					}
				}
			}
		}
	}



	////////////////////////////////////////////////////////
	//  __  __ _          __  __     _   _            _
	// |  \/  (_)___ __  |  \/  |___| |_| |_  ___  __| |___
	// | |\/| | (_-</ _| | |\/| / -_)  _| ' \/ _ \/ _` (_-<
	// |_|  |_|_/__/\__| |_|  |_\___|\__|_||_\___/\__,_/__/
	//
	////////////////////////////////////////////////////////

	//CheckCollisions
	public void collisionsRed(){
		for(int m = 0; m<projectileBlueList.size(); m++)
		{
			for(int y=0; y<unitRedList.size(); y++)
			{
				if(unitRedList.get(y).getBounds().contains(projectileBlueList.get(m).getX(),projectileBlueList.get(m).getY()))
				{
					unitRedList.get(y).setHealth(unitRedList.get(y).getHealth()-projectileBlueList.get(m).getDamage());
					//Deletes target if health is 0
					if(unitRedList.get(y).getHealth()<=0){
						explosionList.add(new Explosion((int)unitRedList.get(y).getX(), (int)unitRedList.get(y).getY(), 50, 5, 1));
						if(unitRedList.get(y) instanceof Sentry){
							removeSentry(y, 1);
						}else{
							removeUnit(y, 1);
						}
						resetTurrets();
					}
					explosionList.add(new Explosion((int)projectileBlueList.get(m).getX(), (int)projectileBlueList.get(m).getY(), 15, 2, 2));
					projectileBlueList.remove(m);
					break;
				}
			}
		}
	}
	public void collisionsBlue(){
		for(int m = 0; m<projectileRedList.size(); m++)
		{
			for(int y=0; y<unitBlueList.size(); y++)
			{
				if(unitBlueList.get(y).getBounds().contains(projectileRedList.get(m).getX(),projectileRedList.get(m).getY()))
				{
					unitBlueList.get(y).setHealth(unitBlueList.get(y).getHealth()-projectileRedList.get(m).getDamage());
					//Deletes target if health is 0
					if(unitBlueList.get(y).getHealth()<=0){
						explosionList.add(new Explosion((int)unitBlueList.get(y).getX(), (int)unitBlueList.get(y).getY(), 50, 5, 2));
						if(unitBlueList.get(y) instanceof Sentry){
							removeSentry(y, 2);
						}else{
							removeUnit(y, 2);
						}
						resetTurrets();
					}
					explosionList.add(new Explosion((int)projectileRedList.get(m).getX(), (int)projectileRedList.get(m).getY(), 15, 2, 1));
					projectileRedList.remove(m);
					break;
				}
			}
		}
	}

	//Clears dead explosions
	public void clearExplosions(){
		for(int y=0; y<explosionList.size(); y++){
			if(explosionList.get(y).getDead())
				explosionList.remove(y);
		}
	}

	//Clears projectiles that are out of life
	public void clearProjectiles(){
		for(int y=0; y<projectileRedList.size(); y++){
			if(projectileRedList.get(y).getLifeLeft()<=0)
				projectileRedList.remove(y);
		}
		for(int y=0; y<projectileBlueList.size(); y++){
			if(projectileBlueList.get(y).getLifeLeft()<=0)
				projectileBlueList.remove(y);
		}
	}

	//Sentry and turret combined into one contstructor
	public void createSentry(int x, int y, int health, int shotDelay, int range, int accuracy, int shotLife, String shotType, int faction){
		if(faction==1){
			unitRedList.add(new Sentry(x, y, health, nextRedTurret, faction));
			turretRedList.add(new Turret(unitRedList.get(nextRedUnit).getX(), unitRedList.get(nextRedUnit).getY(), shotDelay, range, accuracy, shotLife, true, shotType, nextRedUnit, faction));
			nextRedUnit++;
			nextRedTurret++;
		}
		if(faction==2){
			unitBlueList.add(new Sentry(x, y, health, nextBlueTurret, faction));
			turretBlueList.add(new Turret(unitBlueList.get(nextBlueUnit).getX(), unitBlueList.get(nextBlueUnit).getY(), shotDelay, range, accuracy, shotLife, true, shotType, nextBlueUnit, faction));
			nextBlueUnit++;
			nextBlueTurret++;
		}
	}

	//Scout and turret combined into one massive contstructor
	public synchronized void createScout(int x, int y, double angle, double acceleration, double rotationalSpeed, int health, String shotType, int shotDelay,int range, int accuracy, int shotLife, int faction){

		//Allows for non-deprecated sleeping
		threadSuspended = true;
		if(!threadSuspended)
			notify();

		if(faction==1){
			unitRedList.add(new Scout(x, y, angle, acceleration, rotationalSpeed, nextRedTurret, health, nextRedWaypoint, faction));
			turretRedList.add(new Turret(unitRedList.get(nextRedUnit).getX(), unitRedList.get(nextRedUnit).getY(), shotDelay, range, accuracy, shotLife, false, shotType, nextRedUnit, faction));
			waypointRedList.add(new Waypoint(nextRedUnit, faction));
			nextRedUnit++;
			nextRedTurret++;
			nextRedWaypoint++;
		}
		if(faction==2){
			unitBlueList.add(new Scout(x, y, angle, acceleration, rotationalSpeed, nextBlueTurret, health, nextBlueWaypoint, faction));
			turretBlueList.add(new Turret(unitBlueList.get(nextBlueUnit).getX(), unitBlueList.get(nextBlueUnit).getY(), shotDelay, range, accuracy, shotLife, false, shotType, nextBlueUnit, faction));
			waypointBlueList.add(new Waypoint(nextBlueUnit, faction));
			nextBlueUnit++;
			nextBlueTurret++;
			nextBlueWaypoint++;
		}

		//Allows for non-deprecated resuming
		threadSuspended = false;
		if(threadSuspended)
			notify();
	}

	//This also works for ships
	public void removeSentry(int sentryNum, int faction){
		boolean needToWaypoint=false;
		int turret;
		int waypoint = 1000000;

		if(faction==1){
			turret = unitRedList.get(sentryNum).getTurretNum();
			turretRedList.remove(unitRedList.get(sentryNum).getTurretNum());
			if(unitRedList.get(sentryNum) instanceof Ship){
				needToWaypoint=true;
				waypoint = unitRedList.get(sentryNum).getWaypointNum();
				waypointRedList.remove(unitRedList.get(sentryNum).getWaypointNum());
				nextRedWaypoint--;
			}
			if(unitRedList.get(sentryNum).getControlled())
				controlMode=false;
			unitRedList.remove(sentryNum);
			nextRedTurret--;
			nextRedUnit--;

			//Sets back all turret numbers for the sentries above the one being removed
			for(int y=sentryNum; y<unitRedList.size(); y++){
				if(unitRedList.get(y) instanceof Sentry){
					unitRedList.get(y).setTurretNum(unitRedList.get(y).getTurretNum()-1);
					if(unitRedList.get(y) instanceof Ship && needToWaypoint){
						unitRedList.get(y).setWaypointNum(unitRedList.get(y).getWaypointNum()-1);
					}
				}
			}
			//Sets back the unitNum of all turrets above the one being destroyed
			for(int y=turret; y<turretRedList.size(); y++){
				turretRedList.get(y).setUnitNum(turretRedList.get(y).getUnitNum()-1);
			}
			if(needToWaypoint){
				//Sets back the shipNum of all waypoints above the one being removed
				for(int y=waypoint; y<waypointRedList.size(); y++){
					waypointRedList.get(y).setShipNum(waypointRedList.get(y).getShipNum()-1);
				}
			}
		}
		if(faction==2){
			turret = unitBlueList.get(sentryNum).getTurretNum();
			turretBlueList.remove(unitBlueList.get(sentryNum).getTurretNum());
			if(unitBlueList.get(sentryNum) instanceof Ship){
				needToWaypoint=true;
				waypoint = unitBlueList.get(sentryNum).getWaypointNum();
				waypointBlueList.remove(unitBlueList.get(sentryNum).getWaypointNum());
				nextBlueWaypoint--;
			}
			if(unitBlueList.get(sentryNum).getControlled())
				controlMode=false;
			unitBlueList.remove(sentryNum);
			nextBlueTurret--;
			nextBlueUnit--;

			//Sets back all turret numbers for the sentries above the one being removed
			for(int y=sentryNum; y<unitBlueList.size(); y++){
				if(unitBlueList.get(y) instanceof Sentry){
					unitBlueList.get(y).setTurretNum(unitBlueList.get(y).getTurretNum()-1);
					if(unitBlueList.get(y) instanceof Ship && needToWaypoint){
						unitBlueList.get(y).setWaypointNum(unitBlueList.get(y).getWaypointNum()-1);
					}
				}
			}
			//Sets back the unitNum of all turrets above the one being destroyed
			for(int y=turret; y<turretBlueList.size(); y++){
				turretBlueList.get(y).setUnitNum(turretBlueList.get(y).getUnitNum()-1);
			}
			if(needToWaypoint){
				//Sets back the shipNum of all waypoints above the one being removed
				for(int y=waypoint; y<waypointBlueList.size(); y++){
					waypointBlueList.get(y).setShipNum(waypointBlueList.get(y).getShipNum()-1);
				}
			}
		}
	}

	//When a unit is destroyed, all turrets and waypoints for ships/sentries above it must have their unitNum decreased
	public void removeUnit(int number, int faction){
		if(faction==1){
			for(int y=number; y<unitRedList.size(); y++){
				if(unitRedList.get(y) instanceof Sentry){
					turretRedList.get(unitRedList.get(y).getTurretNum()).setUnitNum(turretRedList.get(unitRedList.get(y).getTurretNum()).getUnitNum()-1);
				}
				if(unitRedList.get(y) instanceof Ship){
					waypointRedList.get(unitRedList.get(y).getWaypointNum()).setShipNum(waypointRedList.get(unitRedList.get(y).getWaypointNum()).getShipNum()-1);
				}
			}
			unitRedList.remove(number);
			nextRedUnit--;
		}
		if(faction==2){
			for(int y=number; y<unitBlueList.size(); y++){
				if(unitBlueList.get(y) instanceof Sentry){
					turretBlueList.get(unitBlueList.get(y).getTurretNum()).setUnitNum(turretBlueList.get(unitBlueList.get(y).getTurretNum()).getUnitNum()-1);
				}
				if(unitBlueList.get(y) instanceof Ship){
					waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).setShipNum(waypointBlueList.get(unitBlueList.get(y).getWaypointNum()).getShipNum()-1);
				}
			}
			unitBlueList.remove(number);
			nextBlueUnit--;
		}
	}

	//Used to reset all turrets, meaning they loose their current target and look for a new one whenever this method is called
	public void resetTurrets(){
		for(int y=0; y<turretRedList.size(); y++){
			turretRedList.get(y).setTarget(100000);
		}
		for(int y=0; y<turretBlueList.size(); y++){
			turretBlueList.get(y).setTarget(100000);
		}
	}

	//Checks for what ship was clicked on by the player
	public boolean checkSelection(int x, int y){
		selectedNumRed=-1;
		selectedNumBlue=-1;
		selectedType=0;
		boolean bool = false;
		//Checks for units
		if(playerFaction==1)
		{
			for(int j=0; j<unitRedList.size();j++){
				if(unitRedList.get(j).getBounds().contains(x, y)&&!bool){
					if(unitRedList.get(j).getSelected()){
						bool=true;
						unitRedList.get(j).setControlled(true);
						controlledNum=j;
						turretRedList.get(unitRedList.get(j).getTurretNum()).setShooting(false);
						unitRedList.get(j).setAccelerating(false);
						unitRedList.get(j).setTurningRight(false);
						unitRedList.get(j).setTurningLeft(false);
						if(unitRedList.get(j) instanceof Ship)
							waypointRedList.get(unitRedList.get(j).getWaypointNum()).setAlive(false);
						controlMode=true;
						unitRedList.get(j).setSelected(false);
					}else{
						setSelectedType(unitRedList.get(j));
						unitRedList.get(j).setSelected(true);
						bool=true;
						selectedNumRed=j;
						factionSelected=1;
					}
				}else{
					unitRedList.get(j).setSelected(false);
				}
			}
		}
		if(playerFaction==2)
		{
			for(int j=0; j<unitBlueList.size();j++){
				if(unitBlueList.get(j).getBounds().contains(x, y)&&!bool){
					if(unitBlueList.get(j).getSelected()){
						bool=true;
						unitBlueList.get(j).setControlled(true);
						controlledNum=j;
						turretBlueList.get(unitBlueList.get(j).getTurretNum()).setShooting(false);
						unitBlueList.get(j).setAccelerating(false);
						unitBlueList.get(j).setTurningRight(false);
						unitBlueList.get(j).setTurningLeft(false);
						if(unitBlueList.get(j) instanceof Ship)
							waypointBlueList.get(unitRedList.get(j).getWaypointNum()).setAlive(false);
						controlMode=true;
						unitBlueList.get(j).setSelected(false);
					}else{
						setSelectedType(unitBlueList.get(j));
						unitBlueList.get(j).setSelected(true);
						bool=true;
						selectedNumBlue=j;
						factionSelected=1;
					}
				}else{
					unitBlueList.get(j).setSelected(false);
				}
			}
		}
		return bool;
	}

	public void setSelectedType(Unit selection){
		if(selection instanceof Ship){
			selectedType=1;
		}else if(selection instanceof Wall){
			selectedType=2;
		}/*else if(selection instanceof Healer){
			selectedType=3;
		}*/else if(selection instanceof Sentry){
			selectedType=4;
		}else if(selection instanceof Sonar){
			selectedType=5;
		}
	}
	
	//Checks for what ship was hovered over by the player
	public boolean checkHovered(int x, int y){
		hoveredNumRed=-1;
		hoveredNumBlue=-1;
		hoveredType=0;
		boolean bool = false;
		//Checks for units
		if(playerFaction==1)
		{
			for(int j=0; j<unitRedList.size();j++){
				if(unitRedList.get(j).getBounds().contains(x, y)&&!bool){
					if(unitRedList.get(j).getHovered()){
						
					}else{
						checkHoveredType(unitRedList.get(j));
						unitRedList.get(j).setHovered(true);
						bool=true;
						hoveredNumRed=j;
						factionHovered=1;
					}
				}else{
					unitRedList.get(j).setHovered(false);
				}
			}
		}
		if(playerFaction==2)
		{
			for(int j=0; j<unitBlueList.size();j++){
				if(unitBlueList.get(j).getBounds().contains(x, y)&&!bool){
					if(unitBlueList.get(j).getHovered()){
						
					}else{
						checkHoveredType(unitBlueList.get(j));
						unitBlueList.get(j).setHovered(true);
						bool=true;
						hoveredNumBlue=j;
						factionHovered=1;
					}
				}else{
					unitBlueList.get(j).setHovered(false);
				}
			}
		}
		return bool;
	}

	public void checkHoveredType(Unit selection){
		if(selection instanceof Ship){
			hoveredType=1;
		}else if(selection instanceof Wall){
			hoveredType=2;
		}/*else if(selection instanceof Healer){
			hoveredType=3;
		}*/else if(selection instanceof Sentry){
			hoveredType=4;
		}else if(selection instanceof Sonar){
			hoveredType=5;
		}
	}

	//Sets a waypoint that the player specifies
	public void setWaypoint(int x, int y, int faction){
		if(faction==1){
			if(waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).getAlive()==false){
				waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).setAlive(true);
				waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).setX(x);
				waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).setY(y);
			}else if(waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).getAlive()==true){
				waypointRedList.get(unitRedList.get(selectedNumRed).getWaypointNum()).setAlive(false);
			}
		}
		if(faction==2){
			if(waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).getAlive()==false){
				waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).setAlive(true);
				waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).setX(x);
				waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).setY(y);
			}else if(waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).getAlive()==true){
				waypointBlueList.get(unitBlueList.get(selectedNumBlue).getWaypointNum()).setAlive(false);
			}
		}
	}

	//Converts applet window coordinates into map based coordinates
	//NOTE: This method is currently broken, as not every coord passed to it ought to be translated
	//      by the translateX variable. Perhaps make convertXToMapCoords / convertYToMapCoords ?
	public int convertToMapCoords(int winCoord)
	{
		return winCoord - (int)(translateX*scale);
	}

	//Checks if the given object is within the viewWindow, as to only draw things that are visible
	public boolean insideViewWindow(Entity entity)
	{
		//Creates a new rectangle to represent the visible window
		visible = new Rectangle2D.Double();

		visible.setFrame(-50-(int)translateX, -50-(int)translateY, 1330, 770);

		//Returns if the entity is inside or outside the visible window
		if(visible.contains(entity.getX(), entity.getY()))
			return true;
		else
			return false;
	}

	//Arranges sectors in a grid
	public void arrangeSectors()
	{
		int sectorOriginX=0-mapSizeX, sectorOriginY=0-mapSizeY;

		for(int x=0; x<sectorsX; x++){
			for(int y=0; y<sectorsY; y++){

				sectors[x][y].setX(sectorOriginX);
				sectors[x][y].setY(sectorOriginY);

				sectorOriginX += sectorSizeX;
			}

			sectorOriginY += sectorSizeY;
			sectorOriginX = 0-mapSizeX;
		}
	}





} //Class