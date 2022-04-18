package seoultech.se.tetris.GUI;

import seoultech.se.tetris.component.Board;
import seoultech.se.tetris.component.GameScore;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;


public class PlayScreen extends JFrame { // 게임 전체 화면을 그리는 곳
	private JTextPane pane;
	private SimpleAttributeSet styleSet;

	public static void main(String[] args) throws Exception {
		PlayScreen tetris = new PlayScreen();
		tetris.setVisible(true);

	}

	public PlayScreen() throws Exception {
		super("테스트"); // 게임 실행시 이름
		setSize(800,800); // 전체 화면 크기
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 버튼 설정
		setLayout(null); // 레이아웃 설정
		setBackground(Color.WHITE);


		GameScore score = new GameScore();
		ScoreBoard scoreBoard = new ScoreBoard(score);
		NextBoard nextBoard = new NextBoard();
		Board mainBoard = new Board(score, scoreBoard);

		add(mainBoard);
		add(scoreBoard);
		add(nextBoard);

	}
}