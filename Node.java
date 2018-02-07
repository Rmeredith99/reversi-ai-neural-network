
public class Node {

	/*Node class represents a perceptron node within the neural network.
	 * 
	 * Attributes:
	 * 		input: array of edges entering the node
	 * 		output: array of edges leaving the node
	 * 		outValue: [double] which represents the 
	 * 			value leaving the node for a given example
	 * **/
	
	Edge[] input;
	Edge[] output;
	double outValue = 0;
	double delta = 0;

	public Node(Edge[] in, Edge[] out) {
		input = in;
		output = out;
	}
	
	public Node() {
		
	}
	
	/*Sets the output value that leaves the node.
	 * Useful to be able to change frequently during
	 * feed-forward and back-propagation loops.**/
	public void setOutputValue(double value) {
		outValue = value;
	}
	
	/*Returns the Edge leading into this node at index i**/
	public Edge getInputEdge(int i) {
		return input[i];
	}
	
	/*Returns the Edge heading out of this node at index i**/
	public Edge getOutputEdge(int i) {
		return output[i];
	}
	
	/*Returns the Edge leading into this node at index i**/
	public void setInputEdge(Edge[] edges) {
		input = edges;
	}
	
	/*Returns the Edge heading out of this node at index i**/
	public void setOutputEdge(Edge[] edges) {
		output = edges;
	}
	
	/*Used to set the error at a given node during back-propagation**/
	public void setDelta(double change) {
		delta = change;
	}

}
