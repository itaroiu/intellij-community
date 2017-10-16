/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.naming;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.TypeConversionUtil;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import one.util.streamex.EntryStream;
import org.jetbrains.annotations.NotNull;

public class MethodNameSameAsClassNameInspectionBase extends BaseInspection {
  @Override
  @NotNull
  public String getDisplayName() {
    return InspectionGadgetsBundle.message(
      "method.name.same.as.class.name.display.name");
  }

  @Override
  @NotNull
  protected String buildErrorString(Object... infos) {
    return InspectionGadgetsBundle.message(
      "method.name.same.as.class.name.problem.descriptor");
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @Override
  public BaseInspectionVisitor buildVisitor() {
    return new MethodNameSameAsClassNameVisitor();
  }

  private static class MethodNameSameAsClassNameVisitor
    extends BaseInspectionVisitor {

    @Override
    public void visitMethod(@NotNull PsiMethod method) {
      // no call to super, so it doesn't drill down into inner classes
      if (method.isConstructor()) return;
      final String methodName = method.getName();
      final PsiClass containingClass = method.getContainingClass();
      if (containingClass == null) return;
      final String className = containingClass.getName();
      if (!methodName.equals(className)) return;

      PsiMethod[] constructors = containingClass.getConstructors();
      PsiParameter[] parameters = method.getParameterList().getParameters();
      for (PsiMethod constructor : constructors) {
        PsiParameter[] ctorParameters = constructor.getParameterList().getParameters();
        if(parameters.length == ctorParameters.length &&
           !EntryStream.zip(parameters, ctorParameters)
             .mapKeys(PsiParameter::getType).mapValues(PsiParameter::getType)
             .mapKeyValue((t1, t2) -> TypeConversionUtil.erasure(t1).equals(TypeConversionUtil.erasure(t2))).has(false)) {
          return;
        }
      }

      registerMethodError(method, isOnTheFly(), method.getBody() != null && !containingClass.isInterface());
    }
  }
}
