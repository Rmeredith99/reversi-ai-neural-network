import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import java.util.concurrent.TimeUnit;


public class GUI extends JFrame implements ActionListener{

	State state = new State();
	Container cp= getContentPane();
	Box titleBox = new Box(BoxLayout.X_AXIS);
	Box eastBox = new Box(BoxLayout.Y_AXIS);
	Box b= new Box(BoxLayout.X_AXIS);
	Box c1 = new Box(BoxLayout.Y_AXIS);
	Box c2 = new Box(BoxLayout.Y_AXIS);
	Box c3 = new Box(BoxLayout.Y_AXIS);
	Box c4 = new Box(BoxLayout.Y_AXIS);
	Box c5 = new Box(BoxLayout.Y_AXIS);
	Box c6 = new Box(BoxLayout.Y_AXIS);
	Box c7 = new Box(BoxLayout.Y_AXIS);
	Box c8 = new Box(BoxLayout.Y_AXIS);
	//JButton swapColor = new JButton("Switch Color");
	Square[][] squareList = new Square[8][8];
	int nextTurn = -1;
	boolean gameOver = false;
	AI computer = null;
	int numPlayers = 2;
	int humanColor = -1;
	
	MouseEvents me= new MouseEvents();
	
	int whiteScore = 2;
	int blackScore = 2;
	
	public GUI() {
		//////////////Setting up all the squares
		for (int i=0;i<8;i++) {
			for (int j=0;j<8;j++) {
				Square sq = new Square(i,j);
				sq.addMouseListener(me);
				squareList[i][j] = sq;
			}
		}
		
		Box[] boxList = new Box[8];
		boxList[0]=c1;
		boxList[1]=c2;
		boxList[2]=c3;
		boxList[3]=c4;
		boxList[4]=c5;
		boxList[5]=c6;
		boxList[6]=c7;
		boxList[7]=c8;
		
		for (int j=0;j<8;j++) {
			for (int i=0;i<8;i++) {
				boxList[j].add(squareList[j][i]);
			}
		}
		
		b.add(c1);
		b.add(c2);
		b.add(c3);
		b.add(c4);
		b.add(c5);
		b.add(c6);
		b.add(c7);
		b.add(c8);
		///////////////
		
		
		
		//Setting up the title at the top of the window
		String reversi = "REVERSI";
		for (int i=0;i<7;i++) {
			String s = reversi.substring(i,i+1);
			JTextField txt = new JTextField();
			Font font = new Font("Verdana", Font.BOLD, 60);
			txt.setFont(font);
			txt.setBackground(new Color(100,100,100));
			if (i%2==0) {
				txt.setForeground(Color.BLACK);
			} else {
				txt.setForeground(Color.WHITE);
			}
			txt.setText(s);
			titleBox.add(txt);
		}
		
		
		//Setting up the side bar with score
		JTextField sideText1 = new JTextField();
		Font font = new Font("Verdana", Font.BOLD, 20);
		String s = "    White: " + state.whiteScore;
		sideText1.setFont(font);
		sideText1.setBackground(new Color(180,180,180));
		sideText1.setForeground(Color.BLACK);
		sideText1.setText(s);
		eastBox.add(sideText1);
		
		JTextField sideText2 = new JTextField();
		s = "    Black: " + state.blackScore;
		sideText2.setFont(font);
		sideText2.setBackground(new Color(180,180,180));
		sideText2.setForeground(Color.BLACK);
		sideText2.setText(s);
		eastBox.add(sideText2);
		
		JTextField sideText3 = new JTextField();
		if (nextTurn==1) {
			s = "  White's Turn  ";
			sideText3.setBackground(new Color(255,255,255));
			sideText3.setForeground(Color.BLACK);
		} else {
			s = "  Black's Turn  ";
			sideText3.setBackground(new Color(0,0,0));
			sideText3.setForeground(Color.WHITE);
		}
		sideText3.setFont(font);
		sideText3.setText(s);
		eastBox.add(sideText3);
		
		
		
		//Adding everything to the content pane
		cp.add(eastBox, BorderLayout.EAST);
		cp.add(titleBox, BorderLayout.NORTH);
		cp.add(b,BorderLayout.CENTER);
		//cp.add(swapColor,BorderLayout.EAST);
		
		pack();
		setVisible(true);

		//swapColor.addActionListener(this);
		
	}
	
	/*Starts the gui with **/
	public void startGUI(int players) {
		numPlayers = players;
		if (players==1) {
			NeuralNetwork net = new NeuralNetwork(new int[] {64,64,64,1});
			net.setActivationFunction("TANH");
			net.setErrorThreshold(.1);
			net.setLearningRate(.1);
			net.load("reversiNetwork2.txt");
			AI computer = new AI(net);
			setAI(computer);
			humanColor = -1;
		}
		updateGUI(state);
		this.setResizable(false);
	}
	
	public void startGUI(int players, String human, AI comp) {
		numPlayers = players;
		updateGUI(state);
		if (human.equals("Black")) {
			setAI(comp);
			updateGUI(state);
			humanColor = -1;
		} else if (human.equals("White")) {
			setAI(comp);
			int[] xy = comp.getMiniMaxMove(state,3);
			state = new State(state,xy[0],xy[1]);
			updateGUI(state);
			humanColor = 1;
		}
		this.setResizable(false);
	}
	
	public void updateGUI(State state) {
		
		this.state = state;
		nextTurn = state.nextTurn;
		
		for (int x=0;x<8;x++) {
			for (int y=0;y<8;y++) {
				Square sq = squareList[x][y];
				sq.circleColor = state.board[y][x];
				drawSquare(x,y,sq.circleColor);
				
			}
		}
		
		int[] possibleMoves = state.getMoves();
		for (int move : possibleMoves) {
			int[] xy = state.getDoubleInt(move);
			int x = xy[0];
			int y = xy[1];
			Square sq = squareList[x][y];
			sq.circleColor = 2;
		}
		
		for (int x=0;x<8;x++) {
			for (int y=0;y<8;y++) {
				Square sq = squareList[x][y];
				drawSquare(x,y,sq.circleColor);
			}
		}
		this.whiteScore = state.whiteScore;
		this.blackScore = state.blackScore;
		
		this.gameOver = state.gameOver;
		
		drawScore();
		drawTurn();
		
	}
	
	public void drawSquare(int x, int y, int color) {
		Square sq = squareList[x][y];
		sq.circleColor = color;
		sq.complementDisk();
		sq.repaint();
	}
	
	public void drawTurn() {
		JTextField jtf = (JTextField) eastBox.getComponent(2);
		String s = "";
		if (gameOver) {
			if (whiteScore>blackScore) {
				s = "  White Wins!  ";
				jtf.setBackground(new Color(255,255,255));
				jtf.setForeground(Color.BLACK);
			} else if (blackScore>whiteScore) {
				s = "  Black Wins!  ";
				jtf.setBackground(new Color(0,0,0));
				jtf.setForeground(Color.WHITE);
			} else {
				s = "  Tie Game  ";
				jtf.setBackground(new Color(150,150,150));
				jtf.setForeground(Color.BLACK);
			}
			
		}
		else if (nextTurn==1) {
			s = "  White's Turn  ";
			jtf.setBackground(new Color(255,255,255));
			jtf.setForeground(Color.BLACK);
		} else {
			s = "  Black's Turn  ";
			jtf.setBackground(new Color(0,0,0));
			jtf.setForeground(Color.WHITE);
		}
		Font font = new Font("Verdana", Font.BOLD, 20);
		jtf.setFont(font);
		jtf.setText(s);
		jtf.revalidate();
	}
	
	public void drawScore() {
		JTextField sideText1 = (JTextField) eastBox.getComponent(0);
		Font font = new Font("Verdana", Font.BOLD, 20);
		String s = "    White: " + state.whiteScore;
		sideText1.setFont(font);
		if (gameOver) {
			if (whiteScore>blackScore) {
				sideText1.setBackground(new Color(0,200,0));
			} else if (whiteScore<blackScore) {
				sideText1.setBackground(new Color(200,0,0));
			} else {
				sideText1.setBackground(new Color(180,180,180));
			}
			
		} else {
			sideText1.setBackground(new Color(180,180,180));
		}
		sideText1.setForeground(Color.BLACK);
		sideText1.setText(s);
		sideText1.revalidate();
		
		JTextField sideText2 = (JTextField) eastBox.getComponent(1);
		s = "    Black: " + state.blackScore;
		sideText2.setFont(font);
		if (gameOver) {
			if (whiteScore<blackScore) {
				sideText2.setBackground(new Color(0,200,0));
			} else if (whiteScore>blackScore) {
				sideText2.setBackground(new Color(200,0,0));
			} else {
				sideText2.setBackground(new Color(180,180,180));
			}
			
		} else {
			sideText2.setBackground(new Color(180,180,180));
		}
		sideText2.setForeground(Color.BLACK);
		sideText2.setText(s);
		sideText2.revalidate();
	}

	public void setAI(AI computer) {
		this.computer = computer;
		numPlayers = 1;
	}
	
	public void actionPerformed (ActionEvent e) {
		
	}
	
	public class MouseEvents extends MouseInputAdapter {
		public void mouseClicked(MouseEvent e) {
			Object ob= e.getSource();
			if (ob instanceof Square) {
				((Square)ob).complementDisk();
				int x = ((Square)ob).x;
				int y = ((Square)ob).y;
				int tempNextTurn = nextTurn; //color of the human player
				state = new State(state,x,y); 
				
			
				updateGUI(state); //should change the value of nextTurn
				drawTurn();
				drawScore();
				
				
				//if computer player is enabled
				if (numPlayers==1 && state.nextTurn == -1*humanColor) {
					while (state.nextTurn == -1*humanColor && !gameOver) {
						
						
						int[] xy = computer.getMiniMaxMove(state,3);
						if (xy != null) {
							state = new State(state,xy[0],xy[1]);
						}
						
						updateGUI(state);
						drawTurn();
						drawScore();
					}
				}
				
			}
			

		}
	}
}
