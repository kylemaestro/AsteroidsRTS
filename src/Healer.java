
class Healer extends Unit{
}
//Taken from paint in main class. Public void drawBeam(int targetX, int targetY) will replace this and will be locatede in Healer.java
/*
		//Draws lines to connect healers with their targets
		for(int y=0; y<healerRedList.size(); y++)
		{
			if(healerRedList.get(y).getShooting()&&healerRedList.get(y).getTarget()>=0&&
				healerRedList.get(y).getTarget()!=100000&&targetRedList.size()-1>=healerRedList.get(y).getTarget()){
				g.setColor(healerRedList.get(y).getBeamColor());
				g.drawLine((int)healerRedList.get(y).getX(), (int)healerRedList.get(y).getY(),
				(int)targetRedList.get(healerRedList.get(y).getTarget()).getX(),
				(int)targetRedList.get(healerRedList.get(y).getTarget()).getY());
			}
		}
		for(int y=0; y<healerBlueList.size(); y++)
		{
			if(healerBlueList.get(y).getShooting()&&healerBlueList.get(y).getTarget()>=0
				&& healerBlueList.get(y).getTarget()!=100000&&targetBlueList.size()-1>=healerBlueList.get(y).getTarget()){
				g.setColor(healerBlueList.get(y).getBeamColor());
				g.drawLine((int)healerBlueList.get(y).getX(), (int)healerBlueList.get(y).getY(),
				(int)targetBlueList.get(healerBlueList.get(y).getTarget()).getX(),
				(int)targetBlueList.get(healerBlueList.get(y).getTarget()).getY());
			}
		}
*/