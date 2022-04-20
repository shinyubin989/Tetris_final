package seoultech.se.tetris.main;


import seoultech.se.tetris.GUI.NextBoard;
import seoultech.se.tetris.GUI.PlayScreen;
import seoultech.se.tetris.GUI.ScoreBoard;
import seoultech.se.tetris.component.Board;
import seoultech.se.tetris.component.GameScore;
import seoultech.se.tetris.component.NextGenerateBlock;
import seoultech.se.tetris.settingScreen.FileInputOutput;
import seoultech.se.tetris.settingScreen.SettingScreen;
import seoultech.se.tetris.startScreen.StartScreen;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Tetris extends JFrame { // 게임 전체 화면

  private Board mainBoard;

  public static void main(String[] args) throws Exception {
//		Tetris tetris = new Tetris();
//		tetris.setVisible(true);

    // 시작 화면 틀고싶으면 밑에 주석을 풀어주세여

    EventQueue.invokeLater(new Runnable() {
      public void run() {
        StartScreen startScreen = new StartScreen();
      }
    });


//      EventQueue.invokeLater(new Runnable() {
//        public void run() {
//          SettingScreen settingScreen = new SettingScreen();
//        }
//      });


  }

  public Tetris() throws Exception {
    super("Tetris");
    System.out.println("시작");

    FileInputOutput fileInputOutput = new FileInputOutput();
    if (!(new File(("/Users/home/Desktop/colorSetting.ser")).exists())) {
      fileInputOutput.OutputColorFileNotForBlind();
    }
    if (!(new File(("/Users/home/Desktop/keySetting.ser")).exists())) {
      fileInputOutput.OutputKeySettingFileToArrow();
    }
    if (!(new File(("/Users/home/Desktop/screenSizeSetting.ser")).exists())) {
      fileInputOutput.OutputScreenSize800800();
    }


  }
}

