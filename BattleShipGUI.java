package BattleChicks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class BattleShipGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final int PORT = 8989;
	private static final String INSTRUCTIONS = "Instructions\nEnter your username.\nPlace all five ships on your grid "
			+ "and hit the START button.\nThe ships will be placed in the order listed below. Select horizontal"
			+ " or vertical to change the direction they are being placed.\n\n"
			+ "Your username will be added and you will be connected to the game.\n"
			+ "In order to make a hit, press the appropriate button on your opponents grid.\n"
			+ "2 - Two Square  2 - Three Square  1 - Four Square  1 - Five Square";

	private PrintWriter writer;
	private JTextField userNameTextField;
	private JTextArea chatTextArea, textTextArea, updateTextArea;
	private JButton[][] gridButtons = new JButton[10][10];
	private JButton[][] grid2Buttons = new JButton[10][10];
	private JButton[][] myBoard;
	private JButton[][] opponentBoard;
	private JRadioButton verticalRadio, horizontalRadio;
	private ArrayList<String> battleshipButtons = new ArrayList<String>();
	private  boolean turn = false;
	private int countShips = 1;
	private char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' };
	private int row = 0;
	private int column = 0;

	private Socket socket;

	public BattleShipGUI() {
		super("Battle Ship");
	}

	public void showGUI() {
		JPanel mainPanel = new JPanel();
		mainPanel.setVisible(true);
		mainPanel.setLayout(new GridLayout(3, 2));

		// instruction Panel
		JPanel onePanel = new JPanel(new GridLayout(2, 1));
		onePanel.setVisible(true);
		JPanel instructionPanel = new JPanel();
		instructionPanel.setVisible(true);
		JTextArea instructionTextArea = new JTextArea(20, 40);
		instructionTextArea.append(INSTRUCTIONS);
		instructionTextArea.setLineWrap(true);
		instructionTextArea.setEditable(false);
		instructionPanel.add(instructionTextArea);
		JPanel shipPanel = new JPanel(new GridLayout(2, 3));

		ButtonGroup group = new ButtonGroup();
		verticalRadio = new JRadioButton("Vertical Ship", true);
		verticalRadio.setBorder(new EmptyBorder(10, 50, 10, 0));
		JLabel label = new JLabel("");
		horizontalRadio = new JRadioButton("Horizontal Ship", false);
		horizontalRadio.setBorder(new EmptyBorder(10, 0, 10, 50));
		group.add(verticalRadio);
		group.add(horizontalRadio);
		shipPanel.add(verticalRadio);
		shipPanel.add(label);
		shipPanel.add(horizontalRadio);
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButtonActionPerformed();
			}
		});
		JButton resetButton = new JButton("Reset Boards");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetButtonActionPerformed();
			}
		});
		JButton restartButton = new JButton("Restart Game");
		restartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartButtonActionPerformed();
			}
		});
		shipPanel.add(startButton);
		shipPanel.add(restartButton);
		shipPanel.add(resetButton);
		onePanel.add(instructionPanel);
		onePanel.add(shipPanel);

		// header Panel
		JPanel twoPanel = new JPanel(new GridLayout(1, 1));
		twoPanel.setVisible(true);
		JPanel headPanel = new JPanel(new GridLayout(3, 1));
		headPanel.setVisible(true);
		JLabel headLabel = new JLabel("Battle Ship", SwingConstants.CENTER);
		headLabel.setFont(headLabel.getFont().deriveFont(32.0f));
		headLabel.setBorder(new EmptyBorder(25, 100, 25, 100));
		headPanel.add(headLabel);

		JPanel usernamePanel = new JPanel();
		usernamePanel.setVisible(true);
		usernamePanel.setSize(12, 40);
		JLabel userNameLabel = new JLabel("Username: ");
		usernamePanel.add(userNameLabel);
		JTextField userNameTextField = new JTextField(25);
		usernamePanel.add(userNameTextField);
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginButtonActionPerformed();
			}
		});
		usernamePanel.add(loginButton);
		headPanel.add(usernamePanel);

		JPanel winLosePanel = new JPanel();
		winLosePanel.setVisible(true);
		JTextArea updateTextArea = new JTextArea(20, 40);
		updateTextArea.setEditable(false);
		winLosePanel.add(updateTextArea);
		headPanel.add(winLosePanel);
		twoPanel.add(headPanel);

		// opponent Panel
		JPanel threePanel = new JPanel(new GridLayout());
		threePanel.setVisible(true);
		JPanel opponentPanel = new JPanel(new GridLayout(10, 10));
		opponentPanel.setVisible(true);
		opponentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		opponentBoard = buildOpponentBoard(opponentPanel);

		threePanel.add(opponentPanel);

		// chat Panel
		JPanel fourPanel = new JPanel(new GridLayout());
		fourPanel.setVisible(true);
		JPanel chatPanel = new JPanel();
		JTextArea chatTextArea = new JTextArea(20, 40);
		chatTextArea.setLineWrap(true);
		chatTextArea.setEditable(false);

		JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
		chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatPanel.add(chatScrollPane);
		chatPanel.setVisible(true);
		fourPanel.add(chatPanel);

		// myGrid Panel
		JPanel fivePanel = new JPanel(new GridLayout());
		fivePanel.setVisible(true);
		fivePanel.setSize(50, 50);
		JPanel myGridPanel = new JPanel(new GridLayout(10, 10));
		myGridPanel.setVisible(true);
		myGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		myBoard = buildMyBoard(myGridPanel);

		fivePanel.add(myGridPanel);

		// send text Panel
		JPanel sixPanel = new JPanel(new GridLayout(2, 1));
		sixPanel.setVisible(true);
		JPanel textPanel = new JPanel();
		textTextArea = new JTextArea(10, 40);
		textTextArea.setLineWrap(true);
		textTextArea.setEditable(true);
		JScrollPane textScrollPane = new JScrollPane(textTextArea);
		textScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		textPanel.add(textScrollPane);
		textPanel.setVisible(true);
		sixPanel.add(textPanel);

		JPanel sendPanel = new JPanel();
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendButtonActionPerformed();
			}

		});
		sendPanel.add(sendButton);
		sixPanel.add(sendPanel);

		mainPanel.add(onePanel);
		mainPanel.add(twoPanel);
		mainPanel.add(threePanel);
		mainPanel.add(fourPanel);
		mainPanel.add(fivePanel);
		mainPanel.add(sixPanel);
		
		add(mainPanel);

		setLayout(new GridLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Battle Ship");
		setSize(new Dimension(1050, 1000));
		setVisible(true);
	}

	public JButton[][] buildOpponentBoard(JPanel panel) {
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				grid2Buttons[r][c] = new JButton("" + letters[r] + c);
				grid2Buttons[r][c].setPreferredSize(new Dimension(15, 15));
				grid2Buttons[r][c].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						opponentButtonActionPerformed(e);
					}
				});
				grid2Buttons[r][c].setBackground(Color.GRAY);
				panel.add(grid2Buttons[r][c]);
			}
		}
		return grid2Buttons;
	}

	public void clearOpponentBoard(JButton[][] grid2Buttons) {
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				grid2Buttons[r][c].setBackground(Color.GRAY);
			}
		}
	}

	public JButton[][] buildMyBoard(JPanel panel) {
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				gridButtons[r][c] = new JButton("" + letters[r] + c);
				gridButtons[r][c].setPreferredSize(new Dimension(15, 15));
				gridButtons[r][c].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						myButtonActionPerformed(e);
					}
				});
				gridButtons[r][c].setBackground(Color.GRAY);
				panel.add(gridButtons[r][c]);
			}
		}
		return gridButtons;
	}

	public void clearMyBoard(JButton[][] gridButtons) {
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				gridButtons[r][c].setBackground(Color.GRAY);
			}
		}
	}

	public void opponentButtonActionPerformed(ActionEvent e) {
		String clickedButton = ((JButton) e.getSource()).getText();
		System.out.println(clickedButton);

		if (turn) {
			writer.println(OutgoingHandlerInterface.fire(clickedButton));
			writer.flush();
		} else if (!turn) {
			writer.flush();
		}
	}

	public void findCordinates(String coordinate) {
		char[] coords = coordinate.toCharArray();
		Character[] charArray = { coords[0], coords[1] };

		char[] numbers = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		for (int x = 0; x < 10; x++) {
			if (charArray[0].equals(letters[x])) {

				row = x;
			} else {
				System.out.print("row else " + x + " ,");
			}

			if (charArray[1].equals(numbers[x])) {
				column = x;
			} else {
				System.out.print("column else " + x + " ,");
			}
		}
	}

	public void myButtonActionPerformed(ActionEvent e) {
		String coordinate = ((JButton) e.getSource()).getText();
		findCordinates(coordinate);

		addShipsToBoard(row, column);
	}

	public void addShipsToBoard(int r, int c) {
		int size;

		switch (countShips) {
		case 1:
			size = 2;
			buildShip(r, c, size);
			break;
		case 2:
			size = 2;
			buildShip(r, c, size);
			break;
		case 3:
			size = 3;
			buildShip(r, c, size);
			break;
		case 4:
			size = 3;
			buildShip(r, c, size);
			break;
		case 5:
			size = 4;
			buildShip(r, c, size);
			break;
		case 6:
			size = 5;
			buildShip(r, c, size);
			break;

		}

		countShips++;
	}

	public ActionListener buildShip(int r, int c, int size) {
		if (verticalRadio.isSelected()) {
			for (int i = 0; i < size; i++) {
				myBoard[r + i][c].setBackground(Color.PINK);
				String coord = myBoard[r + i][c].getText();
				battleshipButtons.add(coord);
			}
		} else if (horizontalRadio.isSelected()) {
			for (int i = 0; i < size; i++) {
				myBoard[r][c + i].setBackground(Color.PINK);
				String coord = myBoard[r][c + i].getText();
				battleshipButtons.add(coord);
			}
		}

		return null;
	}

	public void sendButtonActionPerformed() {
		String chat = textTextArea.getText();
		String username = userNameTextField.getText();
		writer.println(OutgoingHandlerInterface.sendChat(chat));
		writer.flush();

		chatTextArea.append(username + ": " + chat + "\n");
		textTextArea.setText("");
	}

	public void startButtonActionPerformed() {
		writer.println(OutgoingHandlerInterface.sendGameBoard(battleshipButtons));
		writer.flush();
	}

	public void loginButtonActionPerformed() {
		String username = userNameTextField.getText();
		try {
			socket = new Socket(InetAddress.getByName("ec2-52-41-213-54.us-west-2.compute.amazonaws.com"), PORT);
			writer = new PrintWriter(socket.getOutputStream());

			writer.println(OutgoingHandlerInterface.login(username));
			writer.flush();

			new Thread(new MessageReader(socket, this)).start();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void resetButtonActionPerformed() {
		clearOpponentBoard(grid2Buttons);
		clearMyBoard(gridButtons);
		battleshipButtons.clear();
		System.out.println("CLEARED ARRAY: " + battleshipButtons);
		countShips = 1;
	}

	public void restartButtonActionPerformed() {
		writer.println(OutgoingHandlerInterface.restart());
		writer.flush();

	}

	public void setChatMessage(String send) {
		chatTextArea.setText(send + "\n");
	}

	public void updateTextArea(String update) {
		updateTextArea.setText(update + "\n");
	}

	public void setTurn(Boolean myTurn) {
		turn = myTurn;
		if (turn) {
			updateTextArea.append("Your Turn\n");
		} else {
			updateTextArea.append("NOT your turn\n");
		}
	}

	public void hitMiss(Boolean hit, String coordinate) {
		findCordinates(coordinate);
		if (turn && hit) {
			updateTextArea.setText("   HIT\n");
			opponentBoard[row][column].setBackground(Color.MAGENTA);
		} else if (turn && !hit) {
			updateTextArea.setText("   MISS\n");
			opponentBoard[row][column].setBackground(Color.BLACK);
		} else if (!turn && hit) {
			updateTextArea.setText("   HIT\n");
			myBoard[row][column].setBackground(Color.MAGENTA);
		} else if (!turn && !hit) {
			updateTextArea.setText("   MISS\n");
			myBoard[row][column].setBackground(Color.BLACK);
		}

	}

}
