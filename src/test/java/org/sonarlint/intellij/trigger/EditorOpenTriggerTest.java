/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015-2020 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.trigger;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.sonarlint.intellij.AbstractSonarLintLightTests;
import org.sonarlint.intellij.config.global.SonarLintGlobalSettings;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class EditorOpenTriggerTest extends AbstractSonarLintLightTests {
  private SonarLintSubmitter submitter = mock(SonarLintSubmitter.class);

  private SonarLintGlobalSettings globalSettings = new SonarLintGlobalSettings();
  private EditorOpenTrigger editorTrigger;

  @Before
  public void start() {
    editorTrigger = new EditorOpenTrigger(getProject(), submitter, globalSettings);
    globalSettings.setAutoTrigger(true);
  }

  @Test
  public void should_trigger() {
    VirtualFile f1 = mock(VirtualFile.class);
    editorTrigger.fileOpened(mock(FileEditorManager.class), f1);
    verify(submitter).submitFiles(Collections.singleton(f1), TriggerType.EDITOR_OPEN, true);
  }

  @Test
  public void should_not_trigger_if_auto_disabled() {
    globalSettings.setAutoTrigger(false);
    VirtualFile f1 = mock(VirtualFile.class);
    editorTrigger.fileOpened(mock(FileEditorManager.class), f1);
    verifyZeroInteractions(submitter);
  }

  @Test
  public void should_do_nothing_closed() {
    VirtualFile f1 = mock(VirtualFile.class);
    FileEditorManager mock = mock(FileEditorManager.class);
    editorTrigger.fileClosed(mock, f1);
    editorTrigger.selectionChanged(new FileEditorManagerEvent(mock, null, null, null, null));
  }
}
