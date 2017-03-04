package com.cs420.project2;

import java.util.*;

public class LocalSearch {
	
	static final int ROW = 20;
	static final double MUTATOR_FACTOR = 0.009;
	static final int FITNESS_FACTOR = 190;
	static final int POPULATION_SIZE = 101;
	static int[] board = new int[ROW];
	static int[] newBoard = new int[ROW];


	public static void main(String[] args) {		
		System.out.println("Local Search\n");
		
		System.out.println("Hill-Climbing Search: \n");
		
		System.out.println("(Example 1)");
		newBoard = hillClimbingSearch(makeRandBoard(board));
		printBoard(newBoard);
		
		System.out.println("\n(Example 2)");
		newBoard = hillClimbingSearch(makeRandBoard(board));
		printBoard(newBoard);
		
		System.out.println("\n(Example 3)");
		newBoard = hillClimbingSearch(makeRandBoard(board));
		printBoard(newBoard);		
		
		System.out.println("\nGenetic Algorithm: \n");
		
		System.out.println("(Example 1)");
		newBoard = geneticAlgorithm(makePopulation(board));
		printBoard(newBoard);
		
		System.out.println("\n(Example 2)");
		newBoard = geneticAlgorithm(makePopulation(board));
		printBoard(newBoard);
		
		System.out.println("\n(Example 3)");
		newBoard = geneticAlgorithm(makePopulation(board));
		printBoard(newBoard);	
	}

	public static int[] makeRandBoard(int[] board) {
		for (int col = 0; col < board.length; col++) {
			board[col] = new Random().nextInt(ROW);
		}
		return board;
	}
	
	public static void printBoard(int[] board) {
		String[][] board2D = new String[ROW][ROW];
		
		for (int row = 0; row < board2D.length; row++) {
			for (int col = 0; col < board2D[row].length; col++) {
				board2D[row][col] = "* ";
			}
		}
		
		for (int col = 0; col < board2D.length; col++) {
			board2D[board[col]][col] = "Q ";
		}
		
		for(int row = 0; row < board2D.length; row++) {
			for (int col = 0; col < board2D[row].length; col++) {
				System.out.print(board2D[row][col]);
			}
			System.out.println();
		}
	}

	public static int[] hillClimbingSearch(int[] board) {
		Node current, neighbor = new Node();
		
		current = new Node(board);
		System.out.println("H: " + current.getH());
		printBoard(current.getBoard());
		while(true) {
			neighbor = lowestValuedSuccessor(current);
						
			if (neighbor.getH() >= current.getH()) {
				System.out.println("\nH: " + current.getH());
				return current.getBoard();
			} else {current = neighbor;}
		}
	}
	
	public static Node lowestValuedSuccessor(Node current) {
		current.setNeighbors();
		Node neighbor = current.getNeighbors().poll();
		current.getNeighbors().clear();
		return neighbor;
	}
	
	public static int[] geneticAlgorithm(List<Node2> population) {
		List<Node2> newPopulation = new ArrayList<>();
		int totalFitness = 0, indiFitness = 0;
		Node2 x, y, child;
		
		while (true) {
			for (int i = 0; i < population.size(); i++) {
				x = population.get(new Random().nextInt(population.size()));
				y = population.get(new Random().nextInt(population.size()));
				child = reproduce(x, y);
				newPopulation.add(child);
			}
			population = newPopulation;
			
			for (Node2 indi : population) totalFitness += indi.getFitness();
			for (Node2 indi : population) indi.setTotalFitness(totalFitness);
			for (Node2 indi : population) {
				indiFitness = indi.getFitness();
				indi.setSurvival((indiFitness*1.0)/(totalFitness*1.0));
			}
			
			for(int i = 0; i < population.size(); i++) {
				if (population.get(i).getSurvival() < MUTATOR_FACTOR) {
					Node2 individual = population.get(i);
					population.remove(i);
					population.add(i, mutate(individual));
				}
			}
			
			for (int i = 0; i < population.size(); i++) {
				if (population.get(i).getFitness() == FITNESS_FACTOR) {
					return population.get(i).getIndividual();
				}
			}
		}
	}
	
	public static Node2 makeRandIndividual(int[] board) {
		for (int col = 0; col < board.length; col++) {
			board[col] = new Random().nextInt(ROW);
		}
		return new Node2(board);
	}
	
	public static List<Node2> makePopulation(int[] individual) {
		List<Node2> population = new ArrayList<>();
		int totalFitness = 0, indiFitness = 0;
		
		for (int i = 0; i < POPULATION_SIZE; i++) {
			population.add(makeRandIndividual(individual));
		}
		
		for (Node2 indi : population) totalFitness += indi.getFitness();
		
		for (Node2 indi : population) indi.setTotalFitness(totalFitness);
		
		for (Node2 indi : population) {
			indiFitness = indi.getFitness();
			indi.setSurvival((indiFitness*1.0)/(totalFitness*1.0));
		}
		
		return population;
	}
	
	public static Node2 reproduce(Node2 x, Node2 y) {
		int divider = new Random().nextInt(x.getIndividual().length);
		int[] temp = new int[x.getIndividual().length];
		System.arraycopy(x.getIndividual(), 0, temp, 0, x.getIndividual().length);
		
		for (int i = 0; i < divider; i++) temp[i] = y.getIndividual()[i];
		
		return new Node2(temp);
	}
	
	public static Node2 mutate(Node2 child) {
		int temp = 0;
		int index = new Random().nextInt(child.getIndividual().length);
		int index2 = new Random().nextInt(child.getIndividual().length);
		if (index == index2) index2 = new Random().nextInt(child.getIndividual().length);
		
		temp = child.getIndividual()[index];
		child.getIndividual()[index] = index2;
		child.getIndividual()[index2] = temp;
		return child;
	}
}

class Node {
	private String id;
	private int row = 20, h;
	private int[] board = new int[row];
	private final Queue<Node> neighbors = new PriorityQueue<>(11, new NeighborComparator());
	
	public Node(){}
	
	public Node (int[] board) {
		System.arraycopy(board, 0, this.board, 0, board.length);
		for (int col = 0; col < board.length-1; col++) {
			h += attackablePairs(col, board[col], board);
		}
	}
	
	public Node (int[] board, int h) {
		System.arraycopy(board, 0, this.board, 0, board.length);		
		this.h = h;
	}
	
	public Queue<Node> getNeighbors() {return neighbors;}
	
	public int[] getBoard() {return board;}
	
	public String getId() {return id;}
	
	public int getH() {return h;}
	
	public void setH(int h) {this.h = h;}
	
	public void setNeighbors() {
		int h = 0, temp = 0;
		
		for (int i = 0; i < board.length - 1; i++) {
			temp = board[i];
			for (int j = 0; j < board.length; j++) {
				board[i] = j;
				for (int col = 0; col < board.length - 1; col++) {				
					h += attackablePairs(col, board[col], board);
				}
				this.getNeighbors().add(new Node(board, h));
				h = 0;
			}
			board[i] = temp;
		}
	}
	
	private int attackablePairs(int col, int row, int[] board) {
		int h = 0;
		
		for (int i=col; i < board.length; i++) {  
            if (i != col) {
              if (board[i] == row) h++;
              if (Math.abs(i-col) == Math.abs(board[i]-row)) h++;
            }
         }
		return h;
	}
}

class NeighborComparator implements Comparator<Node> {
	public int compare (Node firstNode, Node secondNode) {
		if (firstNode.getH() > secondNode.getH()) {return 1;}
		else if (secondNode.getH() > firstNode.getH()) {return -1;}		
		return 0;
	}
}

class Node2 {
	private final int FITNESS_FACTOR = 190;
	private double survival;
	private int fitness, totalFitness = 0, row = 20;
	private int[] individual = new int[row];
	
	public Node2(){}
	
	public Node2 (int[] individual) {
		int clashes = 0;
		System.arraycopy(individual, 0, this.individual, 0, individual.length);
		for (int col = 0; col < individual.length-1; col++) {
			clashes += attackablePairs(col, individual[col], individual);
		}
		
		this.fitness = FITNESS_FACTOR - clashes;
	}
	
	public Node2 (int[] individual, double survival) {
		int clashes = 0;
		System.arraycopy(individual, 0, this.individual, 0, individual.length);
		for (int col = 0; col < individual.length-1; col++) {
			clashes += attackablePairs(col, individual[col], individual);
		}
		
		this.fitness = FITNESS_FACTOR - clashes;
		this.survival = survival;
	}
		
	public void setSurvival(double survival) {this.survival = survival;}
	
	public void setTotalFitness(int totalFitness) {this.totalFitness = totalFitness;}
	
	public int getTotalFitness() {return totalFitness;}
	
	public double getSurvival() {return survival;}
	
	public int getFitness() {return fitness;}
	
	public int[] getIndividual() {return individual;}
	
	private int attackablePairs(int col, int row, int[] board) {
		int clashes = 0;
		
		for (int i=col; i < board.length; i++) {  
            if (i != col) {
              if (board[i] == row) clashes++;
              if (Math.abs(i-col) == Math.abs(board[i]-row)) clashes++;
            }
         }
		return clashes;
	}
}




























