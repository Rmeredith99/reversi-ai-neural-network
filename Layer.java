
public class Layer {

	/*Represents a full layer of nodes in a neural network.
	 * 
	 * Attributes
	 *		nodeList: list of nodes in the layer
	 *		length: number of nodes in the layer**/

	Node[] nodeList;
	int length = 0;
	
	public Layer(Node[] nodes) {
		nodeList = nodes;
		length = nodes.length;
	}
	
	/*Constructor that creates a layer of n blank nodes**/
	public Layer(int n) {
		Node[] list = new Node[n];
		for (int i=0;i<n;i++) {
			Node node = new Node();
			list[i] = node;
		}
		nodeList = list;
		length = n;
	}
	
	/*Constructor that creates a layer of n nodes, each 
	 * with a black set of edges of proper size**/
	public Layer(int n, int previousSize, int nextSize) {
		Node[] list = new Node[n];
		for (int i=0;i<n;i++) {
			Edge[] edgeListIn = new Edge[previousSize];
			Edge[] edgeListOut = new Edge[nextSize];
			Node node = new Node(edgeListIn,edgeListOut);
			list[i] = node;
		}
		nodeList = list;
		length = n;
	}
	
	/*Returns the node at index i**/
	public Node getNode(int i) {
		return nodeList[i];
	}
	
	/*Returns an array of the node output values**/
	public double[] getOutputList() {
		double[] list = new double[length];
		for (int i=0;i<length;i++) {
			list[i] = nodeList[i].outValue;
		}
		return list;
	}
}
