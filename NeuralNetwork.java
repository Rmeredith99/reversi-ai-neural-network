import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NeuralNetwork {

	/*Represents a network of perceptrons for the purpose of machine learning.
	 * 
	 * Attributes:
	 * 		inputLayer: a Layer object that represents the first layer of the network
	 * 		outputLayer: a Layer object that represents the last layer of the network
	 * 		hiddenLayers: an array of Layer objects for the hidden layers
	 * 		layerList: an array with all layers. Useful for propagation loops
	 * 		dimensions: an array of ints that determines the number of hidden 
	 * 			layers and their sizes
	 * 		error: the stopping criterion for training an example. If the error method 
	 * 			returns a value lower than error, it will stop running that example.
	 * 			A value between zero and one
	 * 		learningRate: the factor by which the weights are changed with every iteration.
	 * 			A value between zero and one
	 * 		activationFunction: String and either "SIGMOID" or "TANH" **/
	
	Layer inputLayer;
	Layer outputLayer;
	Layer[] hiddenLayers;
	Layer[] layerList;
	double[][] constantWeights;
	
	int[] dimensions;
	double error = .1;
	Random r = new Random();
	double learningRate = .2;
	String activationFunction = "TANH";
	
	public NeuralNetwork(int[] dim) {
		dimensions = dim;
		int length = dim.length;
		layerList = new Layer[length];
		inputLayer = new Layer(dim[0],0,dim[1]);
		outputLayer = new Layer(dim[length-1],dim[length-2],0);
		Layer[] layers = new Layer[length-2];
		for (int i=1;i<length-1;i++) {
			Layer layer = new Layer(dim[i],dim[i-1],dim[i+1]);
			layers[i-1] = layer;
			layerList[i] = layer;
		}		
		layerList[0] = inputLayer;
		layerList[length-1] = outputLayer;
		hiddenLayers = layers;
		
		/*
		layerList = new Layer[dimensions.length];
		layerList[0] = inputLayer;
		layerList[layerList.length-1] = outputLayer;
		for (int i=1;i<dimensions.length-1;i++) {
			layerList[i+1] = hiddenLayers[i];
		}
		**/
		
		//connecting the nodes to one another with edges
		if (dimensions.length==2) {
			for (int i=0;i<inputLayer.length;i++) {
				for (int j=0;j<outputLayer.length;j++) {
					double randVal = getRandom(-1,1);
					Edge edge = new Edge(randVal,i,j);
					Node from = inputLayer.getNode(i);
					Node to = outputLayer.getNode(j);
					from.output[j] = edge;
					to.input[i] = edge;
				}
			}
		} else {
			for (int i=0;i<inputLayer.length;i++) {
				for (int j=0;j<layerList[1].length;j++) {
					double randVal = getRandom(-1,1);
					Edge edge = new Edge(randVal,i,j);
					Node from = inputLayer.getNode(i);
					Node to = layerList[1].getNode(j);
					from.output[j] = edge;
					to.input[i] = edge;
				}
			}
			
			for (int k=1;k+1<layerList.length-1;k++) {
				for (int i=0;i<layerList[k].length;i++) {
					for (int j=0;j<layerList[k+1].length;j++) {
						double randVal = getRandom(-1,1);
						Edge edge = new Edge(randVal,i,j);
						Node from = layerList[k].getNode(i);
						Node to = layerList[k+1].getNode(j);
						from.output[j] = edge;
						to.input[i] = edge;
					}
				}
			}
		
			for (int i=0;i<layerList[layerList.length-2].length;i++) {
				for (int j=0;j<outputLayer.length;j++) {
					double randVal = getRandom(-.5,.5);
					Edge edge = new Edge(randVal,i,j);
					Node from = layerList[layerList.length-2].getNode(i);
					Node to = outputLayer.getNode(j);
					from.output[j] = edge;
					to.input[i] = edge;
				}
			}
		}
		
		constantWeights = new double[length-1][];
		for (int i=0;i<length-1;i++) {
			double[] list = new double[dim[i+1]];
			for (int j=0;j<list.length;j++) {
				list[j] = getRandom(-.5,.5);
			}
			constantWeights[i] = list;
		}
		
	}
	
	/*Returns a random double between min and max**/
	private double getRandom(double min, double max) {
		return (max - min)/2.0 * r.nextGaussian()/Math.sqrt(dimensions[0]);
	}
	
	/*Acts as the activation function**/
	private double g(double z) {
		if (activationFunction.equals("SIGMOID")) {
			double a = 1+ Math.pow(Math.E,-z);
			return 1/a;
		} else if (activationFunction.equals("TANH")) {
			return Math.tanh(z/2);
		}
		return 0;
	}
	
	/*Derivative of the activation function. Takes in the last value 
	 * of the activation function, rather than the value which the
	 * activation function takes as input.**/
	private double gPrime(double g) {
		if (activationFunction.equals("SIGMOID")) {
			return g*(1-g);
		} else if (activationFunction.equals("TANH")) {
			return .5*(1-Math.pow(g,2));
		}
		return 0;
	}
	
	/*Sets the value of acceptable error in training results**/
	public void setErrorThreshold(double alpha) {
		error = alpha;
	}
	
	/*Set the function g to either a Sigmoid or a Tanh.
	 * Inputs are limited to either "SIGMOID" or "TANH".
	 * Anything else does nothing.**/
	public void setActivationFunction(String name) {
		if (name.equals("SIGMOID")) {
			activationFunction = "SIGMOID";
		} else if (name.equals("TANH")) {
			activationFunction = "TANH";
		}
	}
	
	/*Takes in two arrays of doubles and returns a double that represents
	 * the mean error of the all the elements**/
	private double getError(double[] input, double[] output) {
		double sum = 0;
		int length = input.length;
		for (int i=0;i<length;i++) {
			sum += (input[i]-output[i])*(input[i]-output[i]);
		}
		sum /= length;
		sum = Math.pow(sum, .5);
		return sum;
	}
	
	/*Sets the learning rate of the network**/
	public void setLearningRate(double rate) {
		learningRate = rate;
	}
	
	/*Takes in an iterable object of examples and trains the neural network based on them.
	 * After this is run, the weights will have been altered to reflect the 
	 * change in the neural network.**/
	public void train(Example[] examples, int iterations) {
		for (int iterate=0;iterate<iterations;iterate++) {
			int size = examples.length;
			for (int w=0;w<size;w++) {
				int b = r.nextInt(size);
				Example example = examples[b];
				while (getError(example.output,outputLayer.getOutputList())>error) {
					// This point starts the feed-forward and back-prop loops
					
					//Feed Forward
					////////////////////////////////////////////
					for (int i=0;i<inputLayer.length;i++) { //for each node in the input layer
						Node node = inputLayer.getNode(i);
						double inputValue = example.getInputValue(i);
						node.setOutputValue(inputValue);
					}
					
					for (int l=1;l<dimensions.length;l++) { //for each layer after the input layer
						Layer layer = layerList[l];
						Layer previousLayer = layerList[l-1];
						for (int nodeIndex=0;nodeIndex<layer.nodeList.length;nodeIndex++) { //for each node in the layer
							double in = 0;
							Node node = layer.getNode(nodeIndex);
							for (int into=0;into<previousLayer.length;into++) {
								Node previousNode = previousLayer.getNode(into);
								in += previousNode.outValue*previousNode.output[nodeIndex].weight;
							}
							in += constantWeights[l-1][nodeIndex]; //adds in the constant node
							node.setOutputValue(g(in));
						}
					}
					
					////////////////////////////////////////////
					
					
					//Back-Propagation
					////////////////////////////////////////////
					for (int n=0;n<outputLayer.length;n++) { //for each node in output layer
						Node node = outputLayer.getNode(n);
						double delta = gPrime(node.outValue)*(example.getOutputValue(n)-node.outValue);
						node.setDelta(delta);
					}
					
					for (int l=layerList.length-2;l>=0;l--) {
						Layer layer = layerList[l];
						Layer higherLayer = layerList[l+1];
						for (int i=0;i<layer.length;i++) { //for each node in layer
							Node node = layer.getNode(i);
							double delta = gPrime(node.outValue);
							double sum = 0;
							for (int j=0;j<higherLayer.length;j++) { //for each node in the layer above
								Node higherNode = higherLayer.getNode(j);
								sum += (node.output[j].weight)*(higherNode.delta);
							}
							delta *= sum;
							node.setDelta(delta);
						}
					}
					
					//Adjusts the weights of the edges after training
					for (int l=0;l<layerList.length-1;l++) {
						Layer thisLayer = layerList[l];
						Layer nextLayer = layerList[l+1];
						for (int i=0;i<thisLayer.length;i++) {
							Node thisNode = thisLayer.getNode(i);
							for (int j=0;j<nextLayer.length;j++) {
								Node nextNode = nextLayer.getNode(j);
								double newWeight = (learningRate*thisNode.outValue*nextNode.delta);
								thisNode.output[j].weight += newWeight;
							}
						}
					}
					
					int dim = dimensions.length;
					for (int i=0;i<dim-1;i++) {
						int l = i+1;
						for (int j=0;j<constantWeights[i].length;j++) {
							constantWeights[i][j] += learningRate * layerList[l].getNode(j).delta;
						}
					}
					
					////////////////////////////////////////////
					
				}
			}
		}
	}
	
	/*Takes in an example and returns the value when the neural network runs that example.
	 * This does not train the network, but simply returns the results of running the input**/
	public double[] calculate(Example example) {
		for (int i=0;i<inputLayer.length;i++) { //for each node in the input layer
			Node node = inputLayer.getNode(i);
			double inputValue = example.getInputValue(i);
			node.setOutputValue(inputValue);
		}
		
		for (int l=1;l<dimensions.length;l++) { //for each layer after the input layer
			Layer layer = layerList[l];
			Layer previousLayer = layerList[l-1];
			for (int nodeIndex=0;nodeIndex<layer.nodeList.length;nodeIndex++) { //for each node in the layer
				double in = 0;
				Node node = layer.getNode(nodeIndex);
				for (int into=0;into<previousLayer.length;into++) {
					Node previousNode = previousLayer.getNode(into);
					in += previousNode.outValue*previousNode.output[nodeIndex].weight;
				}
				in += constantWeights[l-1][nodeIndex]; //adds in the constant node
				node.setOutputValue(g(in));
			}
		}
		
		return outputLayer.getOutputList();
	}
	
	/*Returns a one dimensional array with all the weights in the network.
	 * Used for writing to the save file.**/
	public double[] getWeights() {
		ArrayList<Double> list = new ArrayList<Double>();
		for (int l=0;l<dimensions.length-1;l++) {
			for (int n=0;n<layerList[l].length;n++) {
				Node node = layerList[l].getNode(n);
				for (Edge e : node.output) {
					list.add(e.weight);
				}
			}
		}
		for (int i=0;i<constantWeights.length;i++) {
			for (int j=0;j<constantWeights[i].length;j++) {
				list.add(constantWeights[i][j]);
			}
		}
		Double[] array = list.toArray(new Double[list.size()]);
		double[] a = new double[array.length];
		for (int i=0;i<array.length;i++) {
			a[i] = (double) array[i];
		}
		return a;
	}
	
	/*Once the network has been read, this takes the array of weights and
	 * sets the neural network to those values.**/
	public void setWeights(double[] weightsArray) {
		int counter = 0;
		for (int l=0;l<dimensions.length-1;l++) {
			for (int n=0;n<layerList[l].length;n++) {
				Node node = layerList[l].getNode(n);
				for (Edge e : node.output) {
					e.weight = weightsArray[counter];
					counter += 1;
				}
			}
		}
		
		for (int i=0;i<constantWeights.length;i++) {
			for (int j=0;j<constantWeights[i].length;j++) {
				constantWeights[i][j] = weightsArray[counter];
				counter += 1;
			}
		}
		
	}
	
	/*Loads a saved neural network and returns a NeuralNetwork object**/
	public void load(String fileName) {
		File file = new File(fileName);
        BufferedReader reader = null;
        
        ArrayList<Double> array = new ArrayList<Double>();
        int[] dim;
        int size1;
        int size2;
        
        try {
            reader = new BufferedReader(new FileReader(file));

            String text = reader.readLine();
            String parts[];
            parts=text.split(" ");
            dim = new int[parts.length];
            for (int s=0;s<parts.length;s++) {
            	dim[s] = Integer.parseInt(parts[s]);
            }
            
            text = reader.readLine();
            parts = text.split(" ");
            size1 = Integer.parseInt(parts[0]);
            size2 = Integer.parseInt(parts[1]);
            
            for (int i=0;i<size1+size2+2;i++){
                text=reader.readLine();
                parts=text.split(" ");
                array.add(Double.parseDouble(parts[0]));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            int[] d = new int[]{1,2,3}; 
            //randomly initiating dim so that the constructor can be called
            //without errors
            dim = d;
        }
        
		//NeuralNetwork myNetwork = new NeuralNetwork(dim);
		
		Double[] newArray = array.toArray(new Double[array.size()]);
		double[] a = new double[newArray.length];
		for (int i=0;i<newArray.length;i++) {
			a[i] = (double) newArray[i];
		}
		
		setWeights(a);
		//return myNetwork;
	}
	
	/*Writes the current weights to a file with name filename**/
	public void save(String fileName) {
		int size2 = 0;
		for (int i=0;i<constantWeights.length;i++) {
			for (int j=0;j<constantWeights[i].length;j++) {
				size2 += 1;
			}
		}
		
		double[] weightList = getWeights();
		int size1 = weightList.length - 2 - size2; 
		
		try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            
            String dimString = "";
            for (int i : dimensions) {
            	dimString = dimString + i + " ";
            }
            writer.println(dimString);
            
            writer.println("" + size1 + " " + size2);
            
            for (Double w : weightList) {
            	writer.println(w);
            }
            

            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	
	/*Randomly adjusts n weights by a random range of -delta to delta**/
	public void adjustWeights(int n, double delta) {
		double[] weights = getWeights();
		Random r =  new Random();
		int length = weights.length;
		for (int i=0;i<n;i++) {
			int m = r.nextInt(length);
			double d = 2*delta*r.nextDouble()-delta;
			weights[m] = weights[m] + d;
		}
		setWeights(weights);
	}
	
	
}
