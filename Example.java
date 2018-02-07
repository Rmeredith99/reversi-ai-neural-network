
public class Example {

	/*Represents a sample input/output array for training or just
	 * an input array for retrieving results from the network 
	 * 
	 * Attributes:
	 * 		input: the input array of doubles, each constrained to [-1,1]
	 * 		output: the output array of doubles, each constrained to [-1,1]
	 * **/
	
	double[] input;
	double[] output;
	
	public Example(double[] in, double[] out) {
		input = in;
		output = out;
	}
	
	public Example(double[]in) {
		input = in;
		output = null;
	}
	
	/*Returns the value at index i of the example**/
	public double getInputValue(int i) {
		return input[i];
	}
	
	/*Returns the value at index i of the example**/
	public double getOutputValue(int i) {
		return output[i];
	}
	
	
}
