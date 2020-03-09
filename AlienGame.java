/*
-Program definition: You are a Space Alien Hunter.  The goal of this game is to capture 
as many space aliens by clicking on the correct position 
of the alien. The alien appears for a small timer frame and then disappears. Each incorrect click takes away one of your lives
*/

//import classes
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AlienGame extends JPanel implements ActionListener { //main class

	static class FrameListener extends WindowAdapter { //sub class for listening to window operations
		/**windowClosing method
		 * Procedural method used to listen to the game grid getting closed and the restartFrame getting closed
		 * 
		 * @return void
		 */
		public void windowClosing(WindowEvent e) { //listens to when windows is getting closed
			if (e.getWindow() == grid) { //if the tab they are closing is the main game
				int exit = JOptionPane.showConfirmDialog(null, "Exit the game? You will lose all your current progress."); //ufp
				if (exit == JOptionPane.OK_OPTION) {
					System.exit(0); //exits if user clicks yes
				}
			} 

			else {
				JOptionPane.showMessageDialog(null, "You cannot close this tab"); //only for restart game frame
			}
		}
	}

	final static byte R=20, C=20; 
	public static JFrame grid;
	
	static String name; 
	static JButton[][] buttons; 
	static JButton restartYes, restart, restartNo; 
	static JButton startGame, exitGame, highScoreBtn;
	static JFrame restartFrame, highScorePage;
	static JFrame endMenu, menu; 
	static GridLayout menuLayout; 

	static ImageIcon alien = new ImageIcon ("H:/alien.png"); 
	static ImageIcon alienDead = new ImageIcon ("H:/alien dead.png"); 

	static Font titleFont = new Font("Calibri", Font.BOLD, 20);
	static Font normalFont = new Font("Calibri", Font.PLAIN, 14);
	static DecimalFormat df = new DecimalFormat("0.00");

	static double averageTime, totalTime, accuracy, time;
	static int points = 0, gameCount, lives = 3, currentPoints = 0;
	static int alienPos[][] = new int [R][C];
	static double startTime, endTime, clickRight, clickWrong;
	static byte x,y;
	static String highScoreFile = "highscore.txt"; //file location
	
	static Timer alienTimer = new Timer();

	public static void main(String[] args) { //main method
		menu(); //create the menu
	}
	
/** drawGrid method
 * procedural method draws the main game grid using JFrame commands
 * calls fillBoard method to determine position of alien
 * include timer to make alien pic disappear
 * 
 * @returns void 
 * 
 * @throws IOException
 */
	public static void drawGrid() throws IOException { 
		alienPos = fillBoard();
		grid = new JFrame("Game Board");
		grid.add(new AlienGame(alienPos));
		grid.setSize(800,800);
		grid.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		grid.setVisible(true);
		startTime = System.nanoTime();
		grid.addWindowListener(new FrameListener());
		
		alienTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				buttons[x][y].setIcon(null);
			}
		}, 100);
		
	}
	
	/**menu method
	 * procedural method used to create starting menu
	 * JFrame commands
	 * includes JLabel of title, description of game and startGame JButton and exitGame JButton
	 * 
	 * @return void
	 */

	public static void menu() {

		startGame = new JButton();
		exitGame = new JButton();
		startGame.setText("Start");
		exitGame.setText("Exit");
		startGame.addActionListener(actionListener);
		exitGame.addActionListener(actionListener);

		menu = new JFrame("Menu");
		menuLayout = new GridLayout(5,0);
		menu.setLayout(menuLayout);
		JLabel title = new JLabel();
		JLabel description = new JLabel();
		title.setText("Alien Hunter");
		description.setText("<html>You are a Space Alien Hunter. The goal of this game is to capture as many space aliens by clicking on the correct position "
				+ "of the alien. The alien appears for 100ms and then disappears. Each incorrect click takes away one of your lives. You start with 3 lives<html>");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(titleFont);
		description.setFont(normalFont);
		description.setHorizontalAlignment(JLabel.CENTER);
		menu.setSize(600,500);
		menu.add(title);
		menu.add(description);
		menu.add(startGame);
		menu.add(exitGame);
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu.setVisible(true);

	}
	
	/**endMenu method
	 * Procedural method used to create the end menu
	 * includes 3 JButtons of highScoreBtn (to display highscore), restart (to restart entire game), exitGame to exit the game
	 * include JLabel of statistics (e.g. total points, average time, etc.)
	 * 
	 * @return void
	 * 
	 */

	public static void endMenu() {
		highScoreBtn = new JButton();
		highScoreBtn.setText("High Score");
		highScoreBtn.addActionListener(actionListener);

		restart = new JButton("Yes");
		restart.setText("Yes");
		restart.addActionListener(actionListener);

		averageTime = (totalTime/clickRight);

		JLabel playAgain = new JLabel();
		playAgain.setText("Do you want to play again?");

		endMenu = new JFrame("Good Game, Well Played");
		menuLayout = new GridLayout(6,0);
		endMenu.setLayout(menuLayout);

		JLabel endTitle = new JLabel();
		endTitle.setText("The End...");
		endTitle.setFont(titleFont);
		JLabel info = new JLabel();
		info.setFont(normalFont);
		info.setText("<html>You got " + points + " points this game, through " + gameCount + " games. Your average time is " 
				+ df.format(averageTime)+ " seconds <br> and your percentage of clicking on the alien is: " 
				+ df.format((clickRight/(clickRight+clickWrong)*100)) + "%<html>");
		endMenu.add(endTitle);
		endMenu.add(info);
		endTitle.setHorizontalAlignment(JLabel.CENTER);
		info.setHorizontalAlignment(JLabel.CENTER);
		playAgain.setHorizontalAlignment(JLabel.CENTER);
		endMenu.add(playAgain);
		endMenu.add(restart);
		endMenu.add(highScoreBtn);
		endMenu.add(exitGame);
		endMenu.setSize(600, 500);
		endMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		endMenu.setVisible(true);
	}
	
	/**
	 * creates all buttons on game grid
	 * add picture to alien position
	 * add actionLisenter to all buttons
	 * 
	 * @param alienPos - array of alien position in game grid
	 * @throws IOException
	 */
	public AlienGame(int alienPos[][]) throws IOException {
		setLayout(new GridLayout(R,C));
		buttons = new JButton[R][C];

		for (int row = 0; row < R; row++) {
			for (int col = 0; col < C; col++) {
				// create a button instance and store it in the array
				buttons[row][col] = new JButton();

				if (alienPos[row][col] == 1) {
					buttons[row][col].setText(" ");
					buttons[row][col].setIcon(alien);
				}

				// set its background color
				buttons[row][col].setBackground(Color.WHITE);

				// add the button to this JPanel 
				add(buttons[row][col]);

				// ask the button to register this object for notification of clicks on the button
				buttons[row][col].addActionListener(actionListener);
			}
		}
	}

	/**ActionPerformed method for listening to button clicks
	 * if startGame was pressed
	 * -get username
	 * -create game grid
	 * 
	 * if exitGame was pressed
	 * -exits application
	 * 
	 * if restartYes was pressed
	 * -dispose restartFrame and game grid
	 * -create another game grid with different coordinates
	 * 
	 * if restartNo was pressed
	 * -display endMenu JFrame
	 * 
	 * if restart was pressed
	 * -reset all variables
	 * -get username again
	 * -dispose endMenu
	 * -create game grid again
	 * 
	 * if highScoreBtn was pressed
	 * -create a another JFrame with the highscore list
	 * -read the highscore file
	 * -sort the score
	 * -write to the highscore file with the sorted scores and names
	 * 
	 * else 
	 * - if user clicked on correct alien position
	 * -show the points they got and the time they took
	 * -ask if user wants to play again or not
	 * 
	 * else - if the other buttons created are clicked
	 * -display they clicked on the wrong box
	 * -decrease 1 life
	 * -if life is at 0 then take them to the end menu
	 * -or else ask if user want to play again
	 * 
	 */
	static ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == startGame) {
				name = JOptionPane.showInputDialog("Enter your username");
				int n = JOptionPane.showConfirmDialog(null, "Click Yes if you are ready", "Ready", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION) {
					JOptionPane.showMessageDialog(null, "Goodbye");
					System.exit(0);
				}
				menu.dispose();
				try {
					drawGrid();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if (e.getSource() == exitGame) {
				System.exit(0);
			}

			else if (e.getSource() == restartYes) {
				grid.setEnabled(true);
				restartFrame.dispose();
				grid.dispose();
				try {
					drawGrid();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

			else if (e.getSource() == restartNo) {
				restartFrame.dispose();
				grid.dispose();
				endMenu();
			}
			else if (e.getSource() == restart) {
				name = JOptionPane.showInputDialog("Enter your username");
				lives = 3;
				points = 0;
				gameCount = 0;
				clickRight = 0;
				clickWrong = 0;
				endMenu.dispose();
				try {
					drawGrid();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else if (e.getSource() == highScoreBtn) {
				int score[] = new int[6];
				score[5] = points;
				JLabel yourName =  new JLabel();
				yourName.setText("Your Score: " + name + " - " + Integer.toString(points));
				menuLayout = new GridLayout(7,0);
				String data[] = new String[10];
				highScorePage = new JFrame("High Scores");
				highScorePage.setSize(400,400);
				JLabel name1 = new JLabel();
				JLabel name2 = new JLabel();
				JLabel name3 = new JLabel();
				JLabel name4 = new JLabel();
				JLabel name5 = new JLabel();
				try {
					data = readFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				int count = 0;
				for (int i = 0; i <= 8; i+=2) {
					score[count] = Integer.parseInt(data[i+1]);
					count++;
				}
				sortScore(score);
				if (score[5] != points) {
					String tempName = null, tempScore = null;
					for (int i = 0; i < 5; i++) {
						if (score[i] == points) {
							if (i == 3) { //if user got 4th
								data[i*2+2] = data[i*2]; //swap last with 2nd last
								data[i*2+3] = data[i*2+1];
							}
							else if (i != 4) { //if user didnt get 5th
								int j = 3;
								while (j > i) {
									tempName = data[j*2];
									tempScore = data[j*2+1];
									data[j*2] = data[j*2-2];
									data[j*2+1] = data[j*2-1];
									j--;
								}
								data[8] = tempName;
								data[9] = tempScore;
								break;
							}
							data[i*2] = name; //
							data[i*2+1] = Integer.toString(points); 
						}
					}		
				}
				
				try {
					writeFile(data);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				name1.setText("1st: " + data[0] + " - " + data[1]);
				name2.setText("2nd: " + data[2] + " - " + data[3]);
				name3.setText("3rd: " + data[4] + " - " + data[5]);
				name4.setText("4th: " + data[6] + " - " + data[7]);
				name5.setText("5th: " + data[8] + " - " + data[9]);
				yourName.setHorizontalAlignment(JLabel.CENTER);
				name1.setHorizontalAlignment(JLabel.CENTER);
				name2.setHorizontalAlignment(JLabel.CENTER);
				name3.setHorizontalAlignment(JLabel.CENTER);
				name4.setHorizontalAlignment(JLabel.CENTER);
				name5.setHorizontalAlignment(JLabel.CENTER);
				highScorePage.setLayout(menuLayout);
				highScorePage.add(yourName);
				highScorePage.add(name1);
				highScorePage.add(name2);
				highScorePage.add(name3);
				highScorePage.add(name4);
				highScorePage.add(name5);
				highScorePage.setVisible(true);
			}

			else {

				if (e.getActionCommand().equals(buttons[x][y].getText())) {

					endTime = System.nanoTime();
					time = ((endTime - startTime)/Math.pow(10, 9));

					if (time < 0.5) 
						currentPoints=10;
					else if (time > 0.5 && time < 1)
						currentPoints=5;
					else if (time > 1 && time < 2)
						currentPoints=2;
					else 
						currentPoints=1;

					points += currentPoints;
					
					buttons[x][y].setIcon(alienDead);
					grid.setEnabled(false);
					
					JOptionPane.showMessageDialog(null, "You killed the alien! You took: " + df.format(time)+ " seconds \n and got " 
							+ currentPoints + " points \n You currently have " + points + " points.");
					
					totalTime += time;
					gameCount++;
					clickRight++;
					restartGame();
				}
				else {
					buttons[x][y].setIcon(alien);
					grid.setEnabled(false);

					gameCount++;
					clickWrong ++;
					lives--;
					JOptionPane.showMessageDialog(null, "You didn't kill the Alien :( You have "+ lives +" lives left");
					if (lives == 0) {
						JOptionPane.showMessageDialog(null, "You have no lives left, game over :(");
						endMenu();
					}
					else {
						restartGame();
					}
				}
			}
		}
	};

	/**fillBoard method
	 * Functional method used to generate the alien position and fill the 2d array which includes 1 and 0 to differentiate between alien
	 * 
	 * local variable 
	 * x - x position of alien
	 * y - y position of alien
	 * 
	 * @return alienPos - the 2d array with alien position
	 */
	public static int[][] fillBoard(){
		int alienPos[][] = new int[R][C];
		for (byte r=0;r<R;r++){
			for (byte c=0;c<C;c++){
				alienPos[r][c]=0;
			}
		}
		x=(byte)(Math.round(Math.random()*(R-1)));
		y=(byte)(Math.round(Math.random()*(C-1)));
		alienPos[x][y]=1;
		return alienPos;   
	}

	/**restartGame method
	 * Procedural method used to create the frame asking user if they want to continue playing (1 round)
	 * includes restartYes and restartNo JButton 
	 * 
	 * @return void
	 * 
	 */
	public static void restartGame() {
		JLabel playAgain = new JLabel("Play Again?");
		playAgain.setText("Do you want to play again?");
		restartFrame = new JFrame("Restart Game");
		restartFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		restartYes = new JButton("Yes");
		restartNo = new JButton("No");
		playAgain.setBounds(0,0,200,50);
		restartFrame.setSize(300, 200);
		FlowLayout layout = new FlowLayout();
		restartFrame.setLayout(layout);
		restartFrame.add(playAgain);
		restartFrame.add(restartYes);
		restartFrame.add(restartNo);
		restartFrame.setVisible(true);   
		restartYes.addActionListener(actionListener);
		restartNo.addActionListener(actionListener);
		restartFrame.addWindowListener(new FrameListener());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**readFile method
	 * Functional method used to read highscore file
	 * 
	 * local variable 
	 * read - buffered reader variable used to read files
	 * highscore[] - data read from the highscore file
	 * 
	 * @return highscore - the highscore list read from the highscore file
	 * @throws IOException
	 */
	public static String[] readFile() throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(highScoreFile));
		String[] highScore = new String[10];

		for (int i = 0; i < 10; i++)
			highScore[i] = read.readLine();

		read.close();
		return highScore;
	}
	
	/**writeFile method 
	 * Procedural method used to write the sorted highscore list to the highscore file
	 * 
	 * @param data - the data array that contains the name and score of the highscore
	 * @return void
	 * @throws IOException
	 */
	public static void writeFile(String[] data) throws IOException {

		FileOutputStream fos = new FileOutputStream(highScoreFile);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for (int i = 0; i < 10; i++) {
			bw.write(data[i]);
			bw.newLine();
		}
	 
		bw.close();
	}

	/**sortScore method
	 * Functional method used to sort the score and the previous highscores
	 * the scores are sorted from large to small
	 * 
	 *List of Local Variables
	 *temp - the temporary var to hold the num to swap the 2 numbers
	 *max - the biggest number of the array
	 *
	 * @param score - the list of scores
	 * @return scores array - sorted
	 */
	public static int[] sortScore(int score[]) {
		Arrays.sort(score);
		// reverses the array
		for(int i = 0; i < score.length / 2; i++) {
			int temp = score[i];
			score[i] = score[score.length - i - 1];
			score[score.length - i - 1] = temp;
		}
		return score;
	}
}




