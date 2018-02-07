import java.util.Random;


public class AI {

	/*Class to create a computer player. An instance of this class will
	 * be able to return a move given a state;**/
	
	NeuralNetwork network;
	
	public AI(NeuralNetwork net) {
		network = net;
	}
	
	/*Takes in a state and returns the x-y coordinates of the move
	 * deemed best by the AI after simulating to the end.**/
	public int[] getMove(State state) {
		int[] move = null;
		double bestScore = -10;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			newState = newState.simulateNMoves(this,3);
			double[] networkArray = newState.getNetworkArray();
			Example ex = new Example(networkArray);
			double value = network.calculate(ex)[0];
			if (value*state.nextTurn>bestScore) {
				move = xy;
				bestScore = value*state.nextTurn;
			}
		}
		return move;
	}
	
	public int[] getMonteCarloMove(State state) {
		int[] move = null;
		double bestScore = -10001;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double value = getMonteCarloValue(newState);
			if (value*state.nextTurn>bestScore) {
				move = xy;
				bestScore = value*state.nextTurn;
			}
		}
		return move;
	}
	
	/*Returns a move after just looking at the next possible states.**/
	public int[] getNextMove(State state) {
		int[] move = null;
		double bestScore = -10;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double[] networkArray = newState.getNetworkArray();
			Example ex = new Example(networkArray);
			double value = network.calculate(ex)[0];
			if (value*state.nextTurn>bestScore) {
				move = xy;
				bestScore = value*state.nextTurn;
			}
		}
		return move;
	}
	
	/*Literally just returns a random move**/
	public int[] getRandomMove(State state) {
		Random r = new Random();
		int[] possibleMoves = state.getMoves();
		int n = r.nextInt(possibleMoves.length);
		return state.getDoubleInt(possibleMoves[n]);
	}
	
	public int[] getMiniMaxMove(State state, int depth) {
		int[] move = null;
		double bestScore = -10;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double value = getMiniMaxValue(newState,depth,-1,1,state.nextTurn);
			//getMiniMaxValue already adjusts the score by a factor of +-1 to 
			//account for whose turn it is. This way, when this function is called,
			//a large positive value is always good for the computer player
			if (value>bestScore) {
				move = xy;
				bestScore = value;
			}
		}
		//System.out.println(bestScore);
		return move;
	}
	
	public double getMiniMaxValue(State state, int d, double min, double max, int originalColor) {
		if (state.gameOver || d==0) {
			Example ex = new Example(state.getNetworkArray());
			return network.calculate(ex)[0]*originalColor;
		}
		if (state.nextTurn==originalColor) { //if it's a max state
			double v = min;
			int[] moves = state.getMoves();
			for (int move : moves) {
				int[] xy = state.getDoubleInt(move);
				State newState = new State(state,xy[0],xy[1]);
				double vPrime = getMiniMaxValue(newState,d-1,v,max,originalColor);
				if (vPrime>v) {
					v = vPrime;
				}
				if (v>max) {
					return max;
				}
			}
			return v;
		}
		else if (state.nextTurn==-1*originalColor) { //if it's a min state
			double v = max;
			int[] moves = state.getMoves();
			for (int move : moves) {
				int[] xy = state.getDoubleInt(move);
				State newState = new State(state,xy[0],xy[1]);
				double vPrime = getMiniMaxValue(newState,d-1,min,v,originalColor);
				if (vPrime<v) {
					v = vPrime;
				}
				if (v<min) {
					return min;
				}
			}
			return v;
		
		}
		return 0.0; //should never actually get to this.
	}
	
	public int[] getProbMove2(State state) {
		int[] move = null;
		double bestScore = -1001;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double value = 0;
			for (int i=0;i<00;i++) { //need to change this back to i=300
				State randomWalkState = newState.simulateToEndRandom();
				value += randomWalkState.winning;
			}
			////////
			int[] possibleMoves2 = newState.getMoves();
			if (possibleMoves2.length>0) {
				double bestScore2 = -100000;
				for (int m : possibleMoves2) {
					int[] xy2 = newState.getDoubleInt(m);
					State newState2 = new State(newState,xy2[0],xy2[1]);
					double value2 = 0;
					for (int i=0;i<150;i++) {
						State randomWalkState = newState2.simulateToEndRandom();
						value2 += randomWalkState.winning;
					}
					
					if (value2*newState.nextTurn>bestScore2) {
						//move = xy;
						bestScore2 = value2*newState.nextTurn;
					}
				}
				value = bestScore2*newState.nextTurn;
			}
			////////
			if (value*state.nextTurn>bestScore) {
				move = xy;
				bestScore = value*state.nextTurn;
			}
		}
		return move;
	}
	
	public int[] getProbMove(State state) {
		int[] move = null;
		double bestScore = -1001;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double value = 0;
			for (int i=0;i<300;i++) {
				State randomWalkState = newState.simulateToEndRandom();
				value += randomWalkState.winning;
			}
			
			if (value*state.nextTurn>bestScore) {
				move = xy;
				bestScore = value*state.nextTurn;
			}
		}
		return move;
	}
	
	public int[] getScoreProbMove2(State state) {
		int[] move = null;
		double bestScore = -101;
		int[] possibleMoves = state.getMoves();
		for (int n : possibleMoves) {
			int[] xy = state.getDoubleInt(n);
			State newState = new State(state,xy[0],xy[1]);
			double value = 0;
			int[] possibleMoves2 = newState.getMoves();
			for (int nn : possibleMoves2) {
				int[] xy2 = state.getDoubleInt(nn);
				State newState2 = new State(newState,xy2[0],xy2[1]);
				newState2 = newState2.simulateToEnd(this);
				value+=(newState2.whiteScore-newState2.blackScore);
			}
			value /= Math.max(possibleMoves2.length,1);
			
			if (value*state.nextTurn>bestScore) {
				move = xy;
			}
		}
		return move;
	}
	
	/*Used to find the value of a move for getMove.
	 * Takes in that possible move and does a bunch of weighted simulations to find
	 * the average value of that move.**/
	public double getMonteCarloValue(State state) {
		int m = 150;
		double value = 0.0;
		Random r = new Random();
		int[] move;
		int x;
		int y;
		State originalState = state;
		int n = (int)(30 + 2*(state.whiteScore+state.blackScore));
		//int n = (int)((state.whiteScore+state.blackScore)*m/64.0);
		for (int i=0;i<n;i++) {
			state = originalState;
			while (!state.gameOver) {
				int[] possibleMoves = state.getMoves();
				int length = possibleMoves.length;
				double[] percentages = new double[length];
				double totalPercent = 0;
				State[] stateList = new State[length];
				for (int j=0;j<length;j++) {
					move = state.getDoubleInt(possibleMoves[j]);
					x=move[0];y=move[1];
					State newState = new State(state,x,y);
					stateList[j] = newState;
					Example ex = new Example(newState.getNetworkArray());
					double netVal = (network.calculate(ex)[0]*state.nextTurn+1.0)/2.0;
					percentages[j] = netVal;
					totalPercent += netVal;
				}
				for (int j=0;j<length;j++) {
					percentages[j] = percentages[j]/totalPercent;
				}
				double randDouble = r.nextDouble();
				int moveCounter = 0;
				double moveSum = 0.0;
				while (moveSum<randDouble) {
					state = stateList[moveCounter];
					moveSum += percentages[moveCounter];
					moveCounter = Math.min(moveCounter + 1,length);
				}
			}
			value += state.winning;
		}
		//double otherVal = originalState.percentWins(m-n);
		//double tempVal = ((m-n)*otherVal+value)/((double)m);
		return value/((double)n);
		//return tempVal;
	}
	
}
