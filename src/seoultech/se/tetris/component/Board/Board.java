package seoultech.se.tetris.component.Board;

import seoultech.se.tetris.GUI.HighScoreScreen;
import seoultech.se.tetris.GUI.NextBoard;
import seoultech.se.tetris.GUI.PlayScreen;
import seoultech.se.tetris.GUI.ScoreBoard;
import seoultech.se.tetris.blocks.Block;
import seoultech.se.tetris.component.GameScore;
import seoultech.se.tetris.component.NextGenerateBlock;
import seoultech.se.tetris.scoreData.dao.NormalScoreCsv;
import seoultech.se.tetris.scoreData.model.NormalScore;
import seoultech.se.tetris.settingScreen.FileInputOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;


public class Board extends JPanel {

    protected static final long serialVersionUID = 2434035659171694595L;

    public static final int HEIGHT = 20;
    public static final int WIDTH = 10;
    protected int gridCellSize;

    protected Color[][] background;

    //GameOver 설정
    protected JLabel text;

    protected KeyListener playerKeyListener;
    protected Timer timer;

    // 다른 클래스
    protected GameScore gameScore;
    protected ScoreBoard scoreBoard;
    protected NextGenerateBlock nextBlock;
    protected NextBoard nextBoard;
    protected NormalScoreCsv normalScoreCsv;
    protected PlayScreen playScreen;

    protected Block curr;

    protected FileInputOutput fileInputOutput;

    protected int[] keySettingArr;

    protected int initInterval = 1000;
    protected int completeLines = 0; // 완료 행 수
    protected int levelLines= 5; // 레벨 올라갈 때 필요한 줄 수
    protected int pluslevelLines = 5; // 필요한 줄 수 더하기

    public Board() {

    }
    public Board(GameScore gameScore, ScoreBoard scoreBoard) throws Exception {
        this.gameScore = gameScore;
        this.scoreBoard = scoreBoard;

        fileInputOutput = new FileInputOutput();

        int[] locationArr = fileInputOutput.InputScreenSizeFile();

        //보드 설정
        setBounds(locationArr[2], locationArr[3], 350, 700);
        this.gameScore = gameScore;
        this.scoreBoard = scoreBoard;
        setBackground(Color.BLACK);

        gridCellSize = getBounds().width / WIDTH; //네모네모 크기 설정

        /*컴포넌트 설정*/
        text = new JLabel("Game Over"); // 글자
        text.setBounds(100,300, 250,120);

        /*폰트 설정*/
        Font font = new Font("Roboto", Font.BOLD, 60); // 폰트 설정
        text.setForeground(Color.RED);
        text.setFont(font);
        text.setVisible(false);
        this.add(text); // 글자 표시


        background = new Color[HEIGHT][WIDTH];

        //Set timer for block drops.
        timer = new Timer(initInterval, e -> {
            try {
                moveBlockDown(); // 블럭 내려보내기
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        timer.start();
        spawnBlock();

        //키 리스너
        playerKeyListener = new PlayerKeyListener();
        addKeyListener(playerKeyListener);
        setFocusable(true);
        requestFocus();


        fileInputOutput = new FileInputOutput();
        keySettingArr = fileInputOutput.InputKeyFile();

        System.out.println("Normal");
    }
    public Board(PlayScreen playScreen, GameScore gameScore, ScoreBoard scoreBoard, NextGenerateBlock nextGBlock, NextBoard nextBoard, NormalScoreCsv normalScoreCsv) throws Exception{
        this.playScreen = playScreen;
        this.gameScore = gameScore;
        this.scoreBoard = scoreBoard;
        this.normalScoreCsv = normalScoreCsv;

        fileInputOutput = new FileInputOutput();

        int[] locationArr = fileInputOutput.InputScreenSizeFile();

        //보드 설정
        setBounds(locationArr[2], locationArr[3], 350, 700);
        this.gameScore = gameScore;
        this.scoreBoard = scoreBoard;
        setBackground(Color.BLACK);

        gridCellSize = getBounds().width / WIDTH; //네모네모 크기 설정

        /*컴포넌트 설정*/
        text = new JLabel("Game Over"); // 글자
        text.setBounds(100,300, 250,120);

        /*폰트 설정*/
        Font font = new Font("Roboto", Font.BOLD, 60); // 폰트 설정
        text.setForeground(Color.RED);
        text.setFont(font);
        text.setVisible(false);
        this.add(text); // 글자 표시


        background = new Color[HEIGHT][WIDTH];

        //Set timer for block drops.
        timer = new Timer(initInterval, e -> {
            try {
                moveBlockDown(); // 블럭 내려보내기
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        timer.start();
        this.nextBlock = nextGBlock;
        this.nextBoard = nextBoard;
        spawnBlock();

        //키 리스너
        playerKeyListener = new PlayerKeyListener();
        addKeyListener(playerKeyListener);
        setFocusable(true);
        requestFocus();


        fileInputOutput = new FileInputOutput();
        keySettingArr = fileInputOutput.InputKeyFile();

        System.out.println("Normal");
    }

    @Override
    public void paintComponent(Graphics g) { //컴포넌트 그리기
        super.paintComponent(g);

        drawBackground(g);

        if(!isBlockOutOfBounds()) placeBlock(g);

    }

    public void clearLines() throws InterruptedException {
        boolean lineFilled;
        int completeRows =0;

        for (int row = HEIGHT -1; row >=0; row--){

            lineFilled = true;

            for(int col = 0; col < WIDTH; col++)
            {
                if(background[row][col] ==null)
                {
                    lineFilled = false;
                    break;
                }
            }

            if(lineFilled)
            {

                clearEvent(row);
                this.paint(this.getGraphics());
                Thread.sleep(100);
                clearLine(row);
                shiftDown(row);
                clearLine(0);
                row++;

                //스코어 증가
                gameScore.line();
                completeLines++;
                completeRows++;
                repaint();
                setInterval();
            }
        }

        if (completeRows >=2) gameScore.multiLine(completeRows);
    }

    protected void clearLine(int row) {
        for(int i = 0; i < WIDTH; i++)
        {
            background[row][i] = null;
        }
    }

    protected void clearEvent(int row) {
        for(int i = 0; i < WIDTH; i++)
        {
            background[row][i] = Color.LIGHT_GRAY;
        }

        System.out.println("ClearEvent");

    }

    protected void shiftDown(int row) {
        for(int r = row; r >0; r--){
            for (int col = 0; col < WIDTH; col++)
            {
                background[r][col] = background[r-1][col];
            }
        }
    }

    protected void setInterval() {
        if (completeLines >= levelLines) {
            initInterval -= 100;
            levelLines += pluslevelLines;
            pluslevelLines += 2;
            timer.setDelay(initInterval);
            gameScore.setPlus(2);
            System.out.println("Delay : " + timer.getDelay());
        }
    }

    protected void moveBlockToBackground(){ //블럭 background로 보내기
        int[][] shape = curr.getShape();
        int h = curr.height();
        int w = curr.width();

        int xPos = curr.getX();
        int yPos = curr.getY();

        Color color = curr.getColor();

        for (int row=0; row< h; row++)
        {
            for (int col = 0; col < w; col++)
            {
                if(shape[row][col]==1)
                {
                    background[row + yPos][col + xPos] = color;
                }
            }
        }
    }

    protected void placeBlock(Graphics g) { // 블럭 그리기

        Color color = curr.getColor();
        int[][] shape = curr.getShape();

        for (int row = 0; row < curr.height(); row++) {
            for (int col = 0; col < curr.width(); col++) {
                if (shape[row][col]==1) {
                    int x = (curr.getX() + col) * gridCellSize;
                    int y = (curr.getY() + row) * gridCellSize;

                    drawGridSquare(g,color, x, y);
                }
            }
        }
    }

    protected void drawBackground(Graphics g) { // background 그리기
        Color color;

        for (int row = 0; row < HEIGHT; row++)
        {
            for (int col = 0; col < WIDTH; col++)
            {
                color = background[row][col];

                if (color != null)
                {
                    int x = col * gridCellSize;
                    int y = row * gridCellSize;

                    drawGridSquare(g, color, x, y );
                }
            }
        }
    }

    protected void drawGridSquare(Graphics g, Color color, int x , int y) { //블럭 그리기(painting)
        g.setColor(color);
        g.fillRect(x, y, gridCellSize, gridCellSize); //블럭 그리고
        g.setColor(Color.BLACK);
        g.drawRect(x, y, gridCellSize, gridCellSize); // 테두리 그리기
    }

    public void spawnBlock() throws Exception{ // 새로운 블럭 스폰
        curr = nextBlock.getNextblock(); // 새로운 블럭 스폰
        nextBlock.generateBlock();
        nextBoard.updateBlock();
    }

    public void makeGameOverbackground(){
        Color color;

        for (int row = 0; row < HEIGHT; row++)
        {
            for (int col = 0; col < WIDTH; col++)
            {
                color = background[row][col];

                if (color != null)
                    background[row][col] = Color.gray;
            }
        }
    }

    public boolean isBlockOutOfBounds(){

        for(int col = 0; col < WIDTH; col++)
        {
            if(background[2][col] != null) return true;
        }

        return false;
    }

    // 스코어 받고 저장하는 메소드
    public void gameOverScore(){
        int temp = gameScore.getTotal_score();
        if(normalScoreCsv.getRecords().isEmpty()){ // 비어 있으면
            // 이름 입력 및 예외 처리
            String name = inputDialog();
            String difficulty = normalScoreCsv.getLevel();
            NormalScore normalScore = new NormalScore(name,temp,difficulty);

            this.normalScoreCsv = new NormalScoreCsv(normalScore);
            normalScoreCsv.finalScoreEmpty(); // 파일에 저장
        }
        else{
            int isRank = normalScoreCsv.isRank(temp);
            if(isRank != 11){
                //저장 가능하면
                String name = inputDialog();
                String difficulty = normalScoreCsv.getLevel();
                NormalScore normalScore = new NormalScore(name,temp,difficulty);

                this.normalScoreCsv = new NormalScoreCsv(normalScore, isRank);
                normalScoreCsv.finalScoreNotEmpty(); // 파일에 저장
            }
            else{
                // 불가능하면
                String name = "";
                String difficulty = "";
                NormalScore normalScore = new NormalScore(name,temp,difficulty);

                this.normalScoreCsv = new NormalScoreCsv(normalScore, isRank);
                normalScoreCsv.finalScoreNotEmpty(); // 파일에 저장
            }
        }
    }

    // 이름을 사용자 입력에 대한 예외 처리
    public String inputDialog(){
        String name = JOptionPane.showInputDialog(this,"Congratulations! Enter your English name!"); // 입력 요구
        while(name == null || name.equals(name.toUpperCase())){ // null 값과 한글 입력의 경우
            name = JOptionPane.showInputDialog(this, "영어 이름 입력하라구요! 왜 말을 안 들어!", "이럴 줄 알았다", JOptionPane.WARNING_MESSAGE);
        }
        return name;
    }

    protected void moveBlockDown() throws Exception { //블럭 내리기
        if(!checkBottom()) {
            if(isBlockOutOfBounds())
            {
                timer.stop();
                repaint();
                System.out.println("Game Over");
                this.makeGameOverbackground(); // 종료
                text.setVisible(true);
                gameOverScore(); // 스코어 처리
                // 스코어 보드 화면 보여주기
                HighScoreScreen highScoreScreen = new HighScoreScreen(normalScoreCsv);
                highScoreScreen.setVisible(true);
                playScreen.setVisible(false);

                return;
            }
            moveBlockToBackground();
            spawnBlock();
            clearLines();
            repaint();

        }
        curr.moveDown();
        gameScore.playScore(); // 스코어 증가
        scoreBoard.updateScore(); // 점수 보여주기~
        repaint();
    }

    protected void moveBlockRight() { // 오른쪽 이동
        if(isBlockOutOfBounds()) return;

        if(!checkRight()) return;
        curr.moveRight();
        repaint();
    }

    protected void moveBlockLeft() { // 왼쪽 이동
        if(isBlockOutOfBounds()) return;
        if(!checkLeft()) return;
        curr.moveLeft();
        repaint();
    }

    protected void dropBlock() throws Exception {
        while (checkBottom()) {
            moveBlockDown();
        }
        repaint();
    }

    protected void rotateBlock() { // 블럭 회전
        if(!checkBottom())return;
        if(isBlockOutOfBounds()) return;
        if (checkRotate(curr.rotate())) curr.setShape(curr.rotate());
        if(!checkRight())
        {
            if(!checkLeft()) return;
        }
        repaint();

    }

    //rotate 자리에 !null 있는지 체크
    protected boolean checkRotate(int[][] shape) {

        int w = curr.width();
        int h = curr.height();

        for(int row =0; row < w; row++ )
        {
            for(int col = 0; col < h; col++)
            {
                if(shape[row][col] !=0)
                {
                    int x = col + curr.getX();
                    int y = row + curr.getY();
                    if(x < WIDTH && y<HEIGHT){
                        if(background[y][x] != null) return false;
                    }
                    else if(x >= WIDTH && checkLeft()) moveBlockLeft();
                    else return false;

                }
            }
        }

        return true;
    }

    //바닥 체크
    protected boolean checkBottom() {
        if (curr.getBottomEdge() == HEIGHT){
            return false;
        }

        int[][]shape = curr.getShape();
        int w = curr.width();
        int h = curr.height();

        for(int col =0; col < w; col++ )
        {
            for(int row = h-1; row >= 0; row--)
            {
                if(shape[row][col] !=0)
                {
                    int x = col + curr.getX();
                    int y = row + curr.getY() +1;
                    if(background[y][x] != null) return false;
                }
            }
        }

        return true;
    }

    //왼쪽 체크
    protected boolean checkLeft() {
        if(curr.getLeftEdge() ==0) return false;

        int[][]shape = curr.getShape();
        int w = curr.width();
        int h = curr.height();

        for(int row =0; row < h; row++ )
        {
            for(int col = 0; col < w; col++)
            {
                if(shape[row][col] !=0)
                {
                    int x = col + curr.getX() -1 ;
                    int y = row + curr.getY();
                    if(background[y][x] != null) return false;
                }
            }
        }
        return true;
    }

    //오른쪽 체크
    protected boolean checkRight() {
        if(curr.getRightEdge() == WIDTH ) return false;

        int[][]shape = curr.getShape();
        int w = curr.width();
        int h = curr.height();

        for(int row =0; row < h; row++ )
        {
            for(int col = w-1; col >=0; col--)
            {
                if(shape[row][col] !=0)
                {
                    int x = col + curr.getX() +1 ;
                    int y = row + curr.getY();
                    if(background[y][x] != null) return false;
                }
            }
        }

        return true;
    }

    public void showPopup() {
        int input = JOptionPane.showConfirmDialog(this, "게임을 중단하시겠습니까? 중단될 경우, 게임의 데이터가 유실됩니다.", "confirm", JOptionPane.YES_NO_OPTION);
        if (input == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            repaint();
        }
    }



    /* 사용자의 키보드 입력에 대한 메소드 */
    public class PlayerKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == keySettingArr[1]) {
                try {
                    if(!isBlockOutOfBounds()) moveBlockDown();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (e.getKeyCode() == keySettingArr[3]) {
                moveBlockRight();
            } else if (e.getKeyCode() == keySettingArr[2]) {
                moveBlockLeft();
            } else if (e.getKeyCode() == keySettingArr[0]) {
                rotateBlock();
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    if(!isBlockOutOfBounds()){
                        dropBlock();
                        moveBlockToBackground();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                timer.stop();
                repaint();
                showPopup();
                timer.start();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }
}