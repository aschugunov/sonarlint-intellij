package org.sonarlint.intellij.ui;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.sonarlint.intellij.util.SonarLintUtils;

public interface SonarLintConsole {
  static SonarLintConsole get(@NotNull Project p) {
    return SonarLintUtils.getService(p, SonarLintConsole.class);
  }

  void debug(String msg);

  boolean debugEnabled();

  void info(String msg);

  void error(String msg);

  void error(String msg, Throwable t);

  void clear();

  ConsoleView getConsoleView();

  void dispose();
}
