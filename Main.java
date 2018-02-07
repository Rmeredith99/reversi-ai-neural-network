import java.util.ArrayList;
import java.util.Random;

public class Main {

	/*Class where the actual game actions are executed**/
	
	public static void main(String[] args) {
		GUI g = new GUI();
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.1);
		network.setLearningRate(.1);
		network.load("reversiNetwork2.txt");
		AI computer = new AI(network);
		
		//Command that starts the game
		//Choose how many players and what color 
		//you would like to be (if single player)
		g.startGUI(1,"Black",computer);
	}
	
	public static void trainNetwork(String name) {
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.1);
		network.setLearningRate(.1);
		Random r = new Random();
		
		for (int movesDeep=60;movesDeep>=0;movesDeep--) {
			System.out.println(movesDeep);
			network.load(name);
			AI computer = new AI(network);
			
			State state = new State();
			
			int numExamples = 100;
			Example[] exampleList = new Example[numExamples];
			for (int i=0;i<numExamples;i++) {
				//int rr = r.nextInt(60);
				//State newState = state.simulate(rr);
				State newState = state.simulate(movesDeep);
				double[] inputList = newState.getNetworkArray();
				newState = newState.simulateNMoves(computer,4);
				double[] value;
				if (movesDeep==60) {
					double val = newState.winning;
					value = new double[] {val};
				} else {
					value = network.calculate(new Example(newState.getNetworkArray()));
				}
				double[] outputList = new double[] {value[0]};
				Example ex = new Example(inputList,outputList);
				exampleList[i] = ex;
			}
			
			network.setErrorThreshold(.1);
			network.train(exampleList, 10);
			network.save(name);
		}
		
		/*
		State state = new State();
		State newState = state.simulate(0);
		Example ex = new Example(newState.getNetworkArray());
		double[] result = network.calculate(ex);
		System.out.println("" + newState.winning + "   " + result[0]);*/
	}
	
	public static void trainNetwork2(String name) {
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.1);
		network.setLearningRate(.1);
		int rounds = 100;
		for (int j=0;j<rounds;j++) {
			int examples = 100;
			Example[] trainingList = new Example[examples];
			network.load(name);
			for (int i=0;i<examples;i++) {
				System.out.println((double)(examples*j+i)/(rounds*examples)*100 + " Percent Done");
				//network.load(name);
				AI computer = new AI(network);
				State state = new State();
				ArrayList<State> stateList = state.StatesFromGame(computer,7);
				State winningState = stateList.get(stateList.size()-1);
				double[] winner = new double[] {winningState.winning};
				for (State s : stateList) {
					double[] input = s.getNetworkArray();
					Example ex = new Example(input,winner);
					trainingList[i] = ex;
				}
				
			}
			network.train(trainingList, 10);
			network.save(name);
		}
	}
	
	public static void trainNetwork3(String name) {
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.2);
		network.setLearningRate(.1);
		Random r = new Random();
		
		for (int rounds=0;rounds<50;rounds++) {
			
			for (int movesDeep=60;movesDeep>=0;movesDeep--) {
				System.out.println((double)(rounds*(60) + (60-movesDeep))/(50*60));
				network.load(name);
				AI computer = new AI(network);
				
				State state = new State();
				
				int numExamples = 50;
				Example[] exampleList = new Example[numExamples];
				for (int i=0;i<numExamples;i++) {
					//int rr = r.nextInt(60);
					//State newState = state.simulate(rr);
					State newState = state.simulate(movesDeep);
					double[] inputList = newState.getNetworkArray();
					newState = newState.simulateNMoves(computer,4);
					double[] value;
					if (movesDeep==60) {
						double val = newState.winning;
						value = new double[] {val};
					} else {
						value = network.calculate(new Example(newState.getNetworkArray()));
					}
					double[] outputList = new double[] {value[0]};
					Example ex = new Example(inputList,outputList);
					exampleList[i] = ex;
				}
				
				//network.setErrorThreshold(.1);
				network.train(exampleList, 10);
				network.save(name);
			}
		}
		
	}
	
	
	public static void trainNetwork4(String name) {
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.1);
		network.setLearningRate(.1);
		Random r = new Random();
		
		int maxMovesDeep = 30;
		for (int movesDeep=0;movesDeep<maxMovesDeep;movesDeep++) {
			network.load(name);
			State state = new State();
			
			int numExamples = 1000;
			Example[] exampleList = new Example[numExamples];
			for (int i=0;i<numExamples;i++) {
				System.out.println((double)(movesDeep*numExamples + i)/(numExamples*maxMovesDeep)*100);
				int rr = r.nextInt(60);
				State newState = state.simulate(rr);
				double[] inputList = newState.getNetworkArray();
				double value = 0;
				int samples = 100;
				for (int j=0;j<samples;j++) {
					State randomState = newState.simulateToEndRandom();
					value += randomState.winning;
				}
				value = value/((double)samples);
				double[] outputList = new double[] {value};
				Example ex = new Example(inputList,outputList);
				exampleList[i] = ex;
			}
			
			network.setErrorThreshold(.1);
			network.train(exampleList, 20);
			network.save(name);
		}
		
	}
	
	/*An attempt at reinforcement learning**/
	public static void trainNetwork5(String name) {
		NeuralNetwork network = new NeuralNetwork(new int[] {64,64,64,1});
		network.setActivationFunction("TANH");
		network.setErrorThreshold(.1);
		network.setLearningRate(.1);
		Random r = new Random();
		double e = .1;
		int numBatches = 400;
		int samples = 70;
		for (int batch=0;batch<numBatches;batch++) {
			network.load(name);
			AI computer = new AI(network);
			ArrayList<Example> exampleList = new ArrayList<Example>();
			for (int i=0;i<samples;i++) {
				System.out.println((double)(batch*samples + i+1)/(numBatches*samples)*100);
				State newState = new State();
				while (!newState.gameOver) {
					double randVal = r.nextDouble();
					int[] xy;
					double[] input = newState.getNetworkArray();
					if (randVal<e) {
						xy = computer.getRandomMove(newState);
						
					} else {
						xy = computer.getNextMove(newState);
					}
					newState = new State(newState,xy[0],xy[1]);
					double outputVal;
					if (newState.gameOver) {
						outputVal = (double)newState.winning;
					} else {
						outputVal = network.calculate(new Example(newState.getNetworkArray()))[0];
					}
					Example ex = new Example(input,new double[] {outputVal});
					exampleList.add(ex);
				}
				//taking care of the winning state
				double[] input = newState.getNetworkArray();
				double outputVal = newState.winning;
				Example ex = new Example(input,new double[] {outputVal});
				exampleList.add(ex);
				
			}
			Example[] exampleArray = new Example[exampleList.size()];
			for (int j=0;j<exampleList.size();j++) {
				exampleArray[j] = exampleList.get(j);
			}
			network.train(exampleArray,10);
			network.save(name);
			double a = (double)(batch)/numBatches;
			//e = 1.5/(40.0*a+10);
		}
		
	}
	
	public static void geneticAlgorithm() {
		NeuralNetwork net1 = new NeuralNetwork(new int[] {64,64,1});
		NeuralNetwork net2 = new NeuralNetwork(new int[] {64,64,1});
		net1.load("reversiNetwork4.txt");
		net2.load("reversiNetwork4.txt");
		Random r = new Random();
		int n = r.nextInt(20);
		net2.adjustWeights(n, .1);
		AI ai1 = new AI(net1); //white
		AI ai2 = new AI(net2); //black
		State state = new State();
		while (!state.gameOver) {
			if (state.nextTurn==1) {
				int[] xy = ai1.getNextMove(state);
				state = new State(state,xy[0],xy[1]);
			} else if (state.nextTurn==-1) {
				int[] xy = ai2.getNextMove(state);
				state = new State(state,xy[0],xy[1]);
			}
		}
		if (state.winning==-1) { //if the new network won
			System.out.println("Network improved");
			net2.save("reversiNetwork4.txt");
		} else {
			//System.out.println("Network stayed the same");
		}
	}
	
	public static int compare() {
		NeuralNetwork net1 = new NeuralNetwork(new int[] {64,64,64,1});
		NeuralNetwork net2 = new NeuralNetwork(new int[] {64,64,64,1});
		net1.load("reversiNetwork2.txt");
		net2.load("reversiNetwork2.txt");
		AI ai1 = new AI(net1); //white
		AI ai2 = new AI(net2); //black
		State state = new State();
		GUI gg = new GUI();
		gg.startGUI(2);
		while (!state.gameOver) {
			System.out.println(state.blackScore+state.whiteScore);
			if (state.nextTurn==1) {
				System.out.print("First AI: ");
				int[] xy = ai1.getMiniMaxMove(state,3);
				state = new State(state,xy[0],xy[1]);
				gg.updateGUI(state); //should change the value of nextTurn
				gg.drawTurn();
				gg.drawScore();
			} else if (state.nextTurn==-1) {
				System.out.print("Second AI: ");
				int[] xy = ai2.getMiniMaxMove(state,3);
				state = new State(state,xy[0],xy[1]);
				gg.updateGUI(state); //should change the value of nextTurn
				gg.drawTurn();
				gg.drawScore();
			}
		}
		if (state.winning==-1) { //if the new network won
			int d = state.blackScore-state.whiteScore;
			System.out.println("Second AI won by " + d + " points.");
			return 2;
		} else {
			int d = -1*(state.blackScore-state.whiteScore);
			System.out.println("First AI won by " + d + " points.");
			return 1;
		}
		
	}
	
}
