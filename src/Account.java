//Class for managing player's in-game currency
import java.util.*;

class Account{

	//Class Variables
	private int balance, profit, faction;

	/*Constructor*/
	public Account(int faction, int balance){
		this.faction=faction;
		this.balance=balance;
	}

	//Adds profits into players balance total
	public void moneyCycle(){
		balance+=profit;
	}

	/*Accessor Methods*/
	public int getBalance(){return balance;}
	public int getProfit(){return profit;}

	/*Modifier Methods*/
	public void setBalance(int balance){this.balance=balance;}
	public void setProfit(int profit){this.profit=profit;}

}