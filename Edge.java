
public class Edge {

	/*Class Edge provides a framework for an edge between 
	 * any two nodes in a neural network.
	 * 
	 * Attributes:
	 *		weight: [double] value in the range [-1,1] which represents
	 *			the edge weight multiplier.
	 *		start: [int] value denoting the index of the node which this edge leaves
	 *		end: [int] value denoting the index of the node which this edge enters**/
	double weight = .5;
	int start = 0;
	int end = 0;
	
	public Edge(double w, int s, int e) {
		weight = w;
		start = s;
		end = e;
	}
}
