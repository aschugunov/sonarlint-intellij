package org.sonarlint.intellij.ui;

import com.intellij.execution.ui.ConsoleView;

public class SonarLintConsoleTestImpl implements SonarLintConsole {

  private String lastMessage = "";

  public String getLastMessage() {
    return lastMessage;
  }

  @Override
  public void debug(String msg) {
    lastMessage = msg;
  }

  @Override
  public boolean debugEnabled() {
    return true;
  }

  @Override
  public void info(String msg) {
    lastMessage = msg;
  }

  @Override
  public void error(String msg) {
    lastMessage = msg;
  }

  @Override
  public void error(String msg, Throwable t) {
    lastMessage = msg;
  }

  @Override
  public void clear() {
    lastMessage = "";
  }

  @Override
  public ConsoleView getConsoleView() {
    return null;
  }

  @Override
  public void dispose() {

  }


}
