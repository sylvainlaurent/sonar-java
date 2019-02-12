/*
 * SonarQube Java
 * Copyright (C) 2012-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.checks.security;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.check.Rule;
import org.sonar.java.checks.helpers.ConstantUtils;
import org.sonar.java.checks.methods.AbstractMethodDetection;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.model.ExpressionUtils;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;

@Rule(key = "S1523")
public class DynamicCodeCheck extends AbstractMethodDetection {

  private static final String JAVA_LANG_CLASS = "java.lang.Class";

  @Override
  protected List<MethodMatcher> getMethodInvocationMatchers() {
    return ImmutableList.of(
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("forName").withAnyParameters(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getMethod").withAnyParameters(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getMethods").withoutParameter(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getField").withAnyParameters(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getFields").withoutParameter(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getDeclaredField").withAnyParameters(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getDeclaredFields").withoutParameter(),
      MethodMatcher.create().typeDefinition(JAVA_LANG_CLASS).name("getDeclaredClasses").withoutParameter(),
      MethodMatcher.create().typeDefinition("java.lang.ClassLoader").name("loadClass").withAnyParameters());
  }

  @Override
  protected void onMethodInvocationFound(MethodInvocationTree mit) {
    Arguments arguments = mit.arguments();
    // if at least one argument is provided the first argument is always the name
    if (arguments.isEmpty() || ConstantUtils.resolveAsStringConstant(arguments.get(0)) == null) {
      reportIssue(ExpressionUtils.methodName(mit), "Make sure that this dynamic injection or execution of code is safe.");
    }
  }
}