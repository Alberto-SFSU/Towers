package Towers;

import java.util.Scanner;
import java.util.Stack;

/**
 * Main class that initializes an array of Pegs with a number of disks.
 * This class simulates the Tower of Hanoi problem.
 * @author Alberto Mancini; CSC 413-02 TuTh 11-12:15P
 */
public class TowersOfHanoi {
	final static int NUM_PEGS = 3; //the finite number of Pegs for this exercise

	/**
	 * Main method that initializes a set of Pegs.
	 * The first peg is initialized with the specified number of disks of incremental weights.
	 * @param args command line string input that specifies the number of disks
	 */
	public static void main(String[] args) {
		int numDisks = 0;
		
		//initializes number of disks from command line argument
		if(args.length > 0) {
			try {
				numDisks = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid argument. Expected an integer value.");
				System.out.println(e.toString());
				System.exit(1);
			}
		}
		else { //if no command line argument provided, prompt user for input
			Scanner sc = new Scanner(System.in);
			System.out.println("Tower of Hanoi\nHow many disks are initially stacked on Peg A?");
			try {
				numDisks = Integer.parseInt(sc.nextLine());
			}
			catch(NumberFormatException e) {
				System.out.println("Invalid argument. Expected an integer value.");
				System.out.println(e.toString());
				System.exit(1);
			}
			
			sc.close();
		}
		
		//initialize PegBoard with initial stack of Disks
		PegBoard board = new PegBoard(NUM_PEGS);
		for(int i = numDisks; i >= 1; i--) {
			Disk newDisk = new Disk(i);
			board.addDisk(0, newDisk);
		}
		
		
		//------------------formatting------------------//
		System.out.format("%-25s%s%n", "Move", "Peg Configuration");
		
		String diskStack = "";
		for(int i = numDisks; i >= 1; i--) {
			diskStack += i + " ";
		}
		
		String format = "%-25s%";
		for(int i = 0; i < NUM_PEGS; i++) {
			format += "-" + (diskStack.length() + 10) + "s%"; //ensures at least 10 character spacing between config columns
		}
		format += "n";
		
		System.out.format(format, "", "A" , "B" , "C");
		
		System.out.format("%-25s%s%n", "init", diskStack);
		
		board.setPrintFormat("%-" + (diskStack.length()+10) + "s"); //adjust spacing for peg configs to match initial formatting for the specified number of Disks
		//----------------formatting_end----------------//
		
		//recursively solve Towers of Hanoi for the given number of Disks using three pegs
		towers(numDisks, 0, 1, 2, board);
	}
	
	/**
	 * Recursive Towers of Hanoi algorithm that moves the specified number of disks from a source Peg to a destination Peg.
	 * Disk are moved in such a way that a disk of greater weight will not be placed on one with a smaller weight.
	 * This algorithm works for a PegBoard consisting of three Pegs.
	 * @param numDisks the number of disks
	 * @param src the index of the source Peg from a PegBoard
	 * @param util the index of a utility Peg
	 * @param dest the index of the destination Peg
	 * @param pb the PegBoard containing the working Pegs
	 */
	public static void towers(int numDisks, int src, int util, int dest, PegBoard pb) {
		if(numDisks == 1) { //base case
			pb.moveDisk(src, dest);
		}
		
		if(numDisks > 1) {
			towers(numDisks-1, src, dest, util, pb); //move top n-1 from src to util
			pb.moveDisk(src, dest); //move last disk from src to dest
			towers(numDisks-1, util, src, dest, pb); //move disks from util to dest
		}
	}
}


/**
 * Represents a Disk object of a given weight.
 */
class Disk {
	private int weight;
	
	/**
	 * Constructs a Disk object with a specified weight
	 * @param weight the weight (size) of the Disk
	 */
	public Disk(int weight) {
		this.weight = weight;
	}
	
	/**
	 * Gets the weight of the Disk
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Sets the weight of the Disk to a new int value
	 * @param newWeight the new weight
	 */
	public void setWeight(int newWeight) {
		weight = newWeight;
	}
}


/**
 * This class maintains an array of Pegs that may hold a given stack of Disks.
 * PegBoard manages a number of Pegs and the movement of Disks between them.
 */
class PegBoard {
	private Peg pegs[];
	private String printFormat; //spacing for Peg config printing

	/**
	 * Constructs a PegBoard with the specified number of Pegs.
	 * @param numOfPegs the number of Pegs on the PegBoard
	 */
	public PegBoard(int numOfPegs) {
		pegs = new Peg[numOfPegs];
		char name = 'A';
		for(int i = 0; i < numOfPegs; i++) {
			pegs[i] = new Peg(name + "");
			name++;
		}
		
		printFormat = "";
	}
	

	/**
	 * Moves the top Disk from one Peg to the top of the Disk stack of another Peg.
	 * A Disk of greater weight cannot be placed on a Disk of a smaller weight.
	 * Does not move a Disk if the move will be illegal.
	 * @param src the index of the source Peg
	 * @param dest the index of the destination Peg
	 * @throws ArrayIndexOutOfBoundsException indexes must be chosen from the available Pegs on PegBoard
	 */
	public void moveDisk(int src, int dest) throws ArrayIndexOutOfBoundsException {
		//check for valid indexes
		if(src < 0 || src > pegs.length-1 || dest < 0 || dest > pegs.length-1) {
			throw new ArrayIndexOutOfBoundsException("One or more invalid indexes specified for this PegBoard.");
		}

		//check if the specified move is legal
		if(pegs[src].isEmpty() || pegs[src].peek() >= pegs[dest].peek() && !pegs[dest].isEmpty()) {
			return;
		}
		
		Disk myDisk = pegs[src].pop();
		pegs[dest].push(myDisk);
		
		//printing
		String move = "" + myDisk.getWeight() + " from " + pegs[src].getName() + " to " + pegs[dest].getName();
		printMove(printFormat, move);
	}
	
	/**
	 * Adds a Disk to the top of the Disk stack of the specified Peg.
	 * Does nothing if the new Disk has a weight greater than the Disk it will be placed on.
	 * @param src the index of the Peg
	 * @param disk the Disk to place on the Peg
	 */
	public void addDisk(int src, Disk disk) {
		if(pegs[src].isEmpty() || pegs[src].peek() >= disk.getWeight()) {
			pegs[src].push(disk);
		}
	}
	
	/**
	 * Private print method used to print the Disk movement activity and present Desk configuration across all Pegs.
	 * @param format string format code for spacing the printing of Disk configurations
	 * @param move string containing Disk movement activity in English
	 */
	private void printMove(String format, String move) {
		String config[] = new String[pegs.length];
		for(int i = 0; i < config.length; i++) {
			String tmp = "";
			for(int j = 0; j < pegs[i].getSize(); j++) {
				tmp += pegs[i].getDisk(j).getWeight() + " ";
			}
			
			config[i] = tmp;
		}
		
		//default spacing if none set by PegBoard
		if(format.equals("")) {
			System.out.print(move + "     ");
			for(int i = 0; i < config.length; i++) {
				System.out.print(config[i] + "     ");
			}
		}
		else {
			System.out.format("%-25s", move);
			for(int i = 0; i < config.length; i++) {
				System.out.format(format, config[i]);
			}
		}
		
		System.out.print("\n");
	}
	
	/**
	 * Sets the spacing format to be used in Disk activity printing.
	 * @param format string format code for spacing the printing of Disk configurations
	 */
	public void setPrintFormat(String format) {
		printFormat = format;
	}

	/**
	 * Inner Peg class that maintains a Stack of Disks.
	 * Contains operations for managing Disk accessing Disk stacks
	 */
	private class Peg {
		private Stack<Disk> disks;
		private String name;
		
		/**
		 * Default constructor
		 */
		public Peg() {
			disks = new Stack<>();
			name = "";
		}
		
		/**
		 * Constructs a Peg holding no Disks.
		 * @param name a name for the Peg
		 */
		public Peg(String name) {
			disks = new Stack<>();
			this.name = name;
		}
		
		/**
		 * Gets the Peg's name.
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Checks whether the Peg is holding any Disks.
		 * @return whether the Peg is holding any Disks
		 */
		public boolean isEmpty() {
			return disks.isEmpty();
		}

		/**
		 * Gets the number of held Disks.
		 * @return the number of held Disks
		 */
		public int getSize() {
			return disks.size();
		}
		
		/**
		 * Gets the Disk at the specified location of the Disk stack.
		 * Stacks are indexed from bottom to top.
		 * @param index the index
		 * @return the Disk at the specified index
		 */
		public Disk getDisk(int index) {
			return disks.get(index);
		}
		
		/**
		 * Gets the weight of the Disk at the top of the Disk stack.
		 * @return the weight of the last Disk, -1 if there are no Disks
		 */
		public int peek() {
			if(!disks.isEmpty()) {
				return disks.peek().getWeight();
			}
			
			return -1;
		}
		
		/**
		 * Removes the Disk at the top of the Disk stack.
		 * @return the Disk, null if there are no Disks on the Peg
		 */
		public Disk pop() {
			if(!disks.isEmpty()) {
				return disks.pop();
			}
			
			return null;
		}
		
		
		/**
		 * Places a Disk on top of the Disk stack.
		 * @param newDisk the Disk to add to the Peg
		 */
		public void push(Disk newDisk) {
			disks.push(newDisk);
		}
	}
}
