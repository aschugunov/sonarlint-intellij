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

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonarlint.intellij.AbstractSonarLintLightTests;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class EditorChangeTriggerTest extends AbstractSonarLintLightTests {
  private SonarLintSubmitter submitter = mock(SonarLintSubmitter.class);
  private FileDocumentManager docManager = mock(FileDocumentManager.class);
  private EditorChangeTrigger underTest;

  @Before
  public void prepare() {
    replaceProjectService(SonarLintSubmitter.class, submitter);
    getGlobalSettings().setAutoTrigger(true);
    underTest = new EditorChangeTrigger(getProject());
    underTest.onProjectOpened();
  }

  @After
  public void cleanup() {
    underTest.onProjectClosed();
  }

  @Test
  public void should_trigger() {
    VirtualFile file = createAndOpenTestFile("MyClass.java", Language.findLanguageByID("JAVA"), "");

    underTest.documentChanged(createEvent(file));

    assertThat(underTest.getEvents()).hasSize(1);
    verify(submitter, timeout(3000)).submitFiles(Collections.singleton(file), TriggerType.EDITOR_CHANGE, true);
    verifyNoMoreInteractions(submitter);
  }

  @Test
  public void dont_trigger_if_auto_disabled() {
    VirtualFile file = createAndOpenTestFile("MyClass.java", Language.findLanguageByID("JAVA"), "");
    getGlobalSettings().setAutoTrigger(false);

    underTest.documentChanged(createEvent(file));
    verifyZeroInteractions(submitter);
  }

//  @Test
//  public void dont_trigger_if_check_fails() {
//    Module m1 = mock(Module.class);
//    VirtualFile file = mock(VirtualFile.class);
//    Document doc = mock(Document.class);
//    DocumentEvent event = createEvent(file);
//
//    when(file.isValid()).thenReturn(true);
//    when(event.getDocument()).thenReturn(doc);
//    when(docManager.getFile(doc)).thenReturn(file);
////    when(utils.guessProjectForFile(file)).thenReturn(getProject());
////    when(utils.findModuleForFile(file, getProject())).thenReturn(m1);
//
//    underTest.documentChanged(event);
//    verifyZeroInteractions(submitter);
//  }

// XXX how to close project ?
//  @Test
//  public void dont_trigger_if_project_is_closed() {
//    VirtualFile file = createAndOpenTestFile("MyClass.java", Language.findLanguageByID("JAVA"), "");
//
//    ProjectManager.getInstance().closeProject(getProject());
//
//    underTest.documentChanged(createEvent(file));
//    verifyZeroInteractions(submitter);
//  }

// XXX how to make DocumentManager.getFile return null ?
//  @Test
//  public void dont_trigger_if_no_vfile() {
//    VirtualFile file = createAndOpenTestFile("MyClass.java", Language.findLanguageByID("JAVA"), "");
//
//    Document doc = mock(Document.class);
//    DocumentEvent event = createEvent(file);
//
//    when(event.getDocument()).thenReturn(doc);
//    when(docManager.getFile(doc)).thenReturn(null);
//
//    underTest.documentChanged(event);
//    verifyZeroInteractions(submitter);
//  }

  @Test
  public void nothing_to_do_before_doc_change() {
    underTest.beforeDocumentChange(null);
    verifyZeroInteractions(submitter);
  }

  @Test
  public void clear_and_dispose() {
    VirtualFile file = createAndOpenTestFile("MyClass.java", Language.findLanguageByID("JAVA"), "");

    underTest.documentChanged(createEvent(file));
    underTest.onProjectClosed();

    assertThat(underTest.getEvents()).isEmpty();
  }

  private DocumentEvent createEvent(VirtualFile file) {
    DocumentEvent mock = mock(DocumentEvent.class);
    when(mock.getDocument()).thenReturn(FileDocumentManager.getInstance().getDocument(file));
    return mock;
  }
}
