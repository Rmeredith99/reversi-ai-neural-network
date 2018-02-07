import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class State {

	/*Represents a state of a Reversi game with the board, pieces, 
	 * with variables to denotes the next player up and the possible 
	 * move locations
	 * 
	 * Attributes:
	 * 		board: a 2D int array representing the board.
	 * 			A 1 means a white piece, a -1 means a black piece, 
	 * 			and a 0 means there's no piece at the location.
	 * 		nextTurn: int denoting whose turn it is next.
	 * 			1 is white, -1 is black.
	 * 		whiteScore: score of the white player
	 * 		blackScore: score of the black player
	 * 		flipTiles: a list of tiles that will flip over if a given position is played.
	 * 		gameOver: boolean value to denote whether the game is over or not
	 * 		winning: integer to denote who's currently winning;1 for white, -1 for black, 0
	 * 			for tie
	 * 		**/
	
	int[][] board = new int[8][8]; 
	int nextTurn = -1;
	int whiteScore = 2;
	int blackScore = 2;
	boolean gameOver = false;
	int winning = 0;
	HashMap<Integer,ArrayList<Integer>> flipTiles = new HashMap<Integer,ArrayList<Integer>>();
	
	/*Initiates the game at the starting point**/
	public State() {
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				this.board[i][j] = 0;
			}
		}
		board[3][3] = 1;
		board[4][4] = 1;
		board[4][3] = -1;
		board[3][4] = -1;
	}
	
	/*Takes in a State and an x and y value where the next move will take place
	 * with the color set to the color of nextTurn.**/
	public State(State state, int x, int y) {
		//Transfers the board to the new state
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				board[i][j] = state.board[i][j];
			}
		}
		nextTurn = state.nextTurn;
		
		//If the move is valid, it makes the move, flips the tiles and changes
		//whose turn it is accordingly
		if (validMove(x,y)) {
			move(x,y);
			nextTurn *= -1;
			if (getMoves().length==0) { //if the opponent doesn't have a move
				nextTurn *= -1;
			}
		} else {
			System.out.println("That is not a valid move.");
		}
		
		//calculates the score of the game state
		int scoreWhite = 0;
		int scoreBlack = 0;
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				if (board[i][j]==-1) {
					scoreBlack += 1;
				} else if (board[i][j]==1) {
					scoreWhite += 1;
				}
			}
		}
		whiteScore = scoreWhite;
		blackScore = scoreBlack;
		if (scoreWhite + scoreBlack == 64 || this.getMoves().length==0) {
			gameOver = true;
		}
		if (blackScore<whiteScore) winning = 1;
		else if (blackScore > whiteScore) winning = -1;
		else winning = 0;
	}
	
	/*Takes in a move and returns a boolean that says whether a move is 
	 * valid or not**/
	public boolean validMove(int x, int y) {
		int move = getSingleInt(x,y);
		for (int n : getMoves()) {
			if (n==move) {
				return true;
			}
		}
		return false;
	}
	
	/*When called, this changes the board state by adding a piece to the 
	 * board in the color of nextTurn and flips all tiles in accordance
	 * to the rules of reversi..**/
	public void move(int x, int y) {
		board[y][x] = nextTurn;
		Integer n = getSingleInt(x,y);
		ArrayList<Integer> tilesToFlip = flipTiles.get(n);
		for (Integer tile : tilesToFlip) {
			int[] b = getDoubleInt(tile);
			int tileX = b[0];
			int tileY = b[1];
			board[tileY][tileX] = nextTurn;
		}
				
	}
	
	public int getSingleInt(int x, int y) {
		int n = 8*y + x;
		return n;
	}
	
	public int[] getDoubleInt(int n) {
		int[] b = new int[2];
		int x = n % 8;
		int y = n / 8;
		b[0] = x;
		b[1] = y;
		return b;
	}
	
	/*Returns an array of possible moves for player of color nextTurn
	 * with the current state.**/
	public int[] getMoves() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int n=0;n<64;n++) {
			int[] c = getDoubleInt(n);
			int x = c[0];
			int y = c[1];
			ArrayList<Integer> flipList = new ArrayList<Integer>();
			if (board[y][x] == 0) {
				boolean isMove = false;
				int x1 = Math.max(0, x-1);
				int x2 = Math.min(7, x+1);
				int y1 = Math.max(0, y-1);
				int y2 = Math.min(7, y+1);
				
				if (board[y1][x1] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x1;
					int b = y1;
					while (a>0 && b>0 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a -= 1;
						b -= 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y1][x2] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x2;
					int b = y1;
					while (a<7 && b>0 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a += 1;
						b -= 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y2][x1] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x1;
					int b = y2;
					while (a>0 && b<7 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a -= 1;
						b += 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y2][x2] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x2;
					int b = y2;
					while (a<7 && b<7 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a += 1;
						b += 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y][x1] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x1;
					int b = y;
					while (a>0 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a -= 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y][x2] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x2;
					int b = y;
					while (a<7 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						a += 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y1][x] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x;
					int b = y1;
					while (b>0 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						b -= 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				if (board[y2][x] == nextTurn*-1) {
					ArrayList<Integer> storeList = new ArrayList<Integer>();
					int a = x;
					int b = y2;
					while (b<7 && board[b][a]==nextTurn*-1) {
						storeList.add(getSingleInt(a,b));
						b += 1;
						if (board[b][a] == nextTurn) {
							isMove = true;
							for (Integer location : storeList) {
								flipList.add(location);
							}
						}
					}
				}
				
				if (isMove) {
					list.add(n);
					flipTiles.put(n, flipList);
				}
			}
			
		}
		
		Integer[] array = list.toArray(new Integer[list.size()]);
		int[] a = new int[array.length];
		for (int i=0;i<array.length;i++) {
			a[i] = (int) array[i];
		}
		return a;
	}
	
	/*Returns a string, "White" or "Black" saying whose turn it is**/
	public String whoseTurn() {
		if (nextTurn==1) {
			return "White";
		}
		return "Black";
	}

	/*Returns an array of ints that can be put into the neural network**/
	public double[] getNetworkArray() {
		double[] networkArray = new double[64];
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				int n = getSingleInt(i,j);
				networkArray[n] = (double) board[i][j];
			}
		}
		return networkArray;
	}

	/*Simulates moveCount number of moves from the current state and 
	 * returns the final state resulting from those moves.
	 * Simulation is random.**/
	public State simulate(int moveCount) {
		State state = this;
		Random r = new Random();
		int n;
		int[] moves;
		int[] xy;
		int randInt;
		for (int i=0;i<moveCount;i++) {
			moves = state.getMoves();
			if (moves.length>0) {
				randInt = r.nextInt(moves.length);
				n = moves[randInt];
				xy = state.getDoubleInt(n);
				state = new State(state,xy[0],xy[1]);
			} else {
				return state;
			}
			
		}
		return state;
	}
	
	public State simulateToEndRandom() {
		State state = this;
		Random r = new Random();
		int n;
		int[] moves;
		int[] xy;
		int randInt;
		while (!gameOver) {
			moves = state.getMoves();
			if (moves.length>0) {
				randInt = r.nextInt(moves.length);
				n = moves[randInt];
				xy = state.getDoubleInt(n);
				state = new State(state,xy[0],xy[1]);
			} else {
				return state;
			}
			
		}
		return state;
	}
	
	public double percentWins(int n) {
		State state = this;
		double val = 0;
		for (int i=0;i<n;i++) {
			State newState = state.simulateToEndRandom();
			val += newState.winning;
		}
		val = val/((double)n);
		return val;
	}
	
	public State simulateToEnd(AI computer) {
		State state = this;
		int[] move;
		int x;
		int y;
		while (state.getMoves().length>0) {
			move = computer.getNextMove(state);
			x = move[0];
			y = move[1];
			state = new State(state,x,y);
		}
		return state;
	}
	
	public State simulateNMoves(AI computer,int n) {
		State state = this;
		int[] move;
		int x;
		int y;
		for (int i=0;i<n;i++) {
			if (state.getMoves().length>0) {
				move = computer.getNextMove(state);
				x = move[0];
				y = move[1];
				state = new State(state,x,y);
			}
		}
		
		return state;
	}
	
	/*Simulates a game starting with randMoves number of random moves, so
	 * that the game isn't always the same. It returns an arraylist of 
	 * states from the game**/
	public ArrayList<State> StatesFromGame(AI computer,int randMoves){
		ArrayList<State> list = new ArrayList<State>();
		State state = this;
		//list.add(state);
		int[] move;
		int x;
		int y;
		state = state.simulate(randMoves);
		while (state.getMoves().length>0) {
			move = computer.getNextMove(state);
			x = move[0];
			y = move[1];
			state = new State(state,x,y);
			list.add(state);
		}
		return list;
	}
	
}
