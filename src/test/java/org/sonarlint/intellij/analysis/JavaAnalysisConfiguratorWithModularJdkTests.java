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
package org.sonarlint.intellij.analysis;

import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaAnalysisConfiguratorWithModularJdkTests extends AbstractLightPlatformCodeInsightFixture4TestCase {

  private JavaAnalysisConfigurator underTest = new JavaAnalysisConfigurator();

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @Override
      public Sdk getSdk() {
        return addJrtFsJarTo(IdeaTestUtil.getMockJdk9());
      }
    };
  }

  @Test
  public void testAddJrtFsToClasspath() {
    final Map<String, String> props = underTest.configure(getModuleCompat());
    assertThat(props).containsKeys("sonar.java.libraries", "sonar.java.test.libraries");
    assertThat(props.get("sonar.java.libraries").split(","))
      .containsExactly(
        FAKE_JDK_ROOT_PATH.resolve("jdk9/lib/jrt-fs.jar").toString().replaceAll("\\\\", "/"));
    assertThat(props.get("sonar.java.test.libraries").split(","))
      .containsExactly(
        FAKE_JDK_ROOT_PATH.resolve("jdk9/lib/jrt-fs.jar").toString().replaceAll("\\\\", "/"));
  }

  private static Sdk addJrtFsJarTo(@NotNull Sdk jdk) {
    try {
      jdk = (Sdk) jdk.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
    SdkModificator sdkModificator = jdk.getSdkModificator();
    sdkModificator.setHomePath(FAKE_JDK_ROOT_PATH.resolve("jdk9").toString());
    sdkModificator.commitChanges();
    return jdk;
  }

}
