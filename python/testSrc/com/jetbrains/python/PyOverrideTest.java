package com.jetbrains.python;

import com.jetbrains.python.codeInsight.override.PyMethodMember;
import com.jetbrains.python.codeInsight.override.PyOverrideImplementUtil;
import com.jetbrains.python.fixtures.PyLightFixtureTestCase;
import com.jetbrains.python.psi.LanguageLevel;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.impl.PythonLanguageLevelPusher;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;

import java.util.Collections;

/**
 * @author yole
 */
public class PyOverrideTest extends PyLightFixtureTestCase {
  private void doTest() {
    myFixture.configureByFile("override/" + getTestName(true) + ".py");
    PyFunction toOverride = getTopLevelClass(0).getMethods() [0];
    PyOverrideImplementUtil.overrideMethods(myFixture.getEditor(), getTopLevelClass(1),
                                            Collections.singletonList(new PyMethodMember(toOverride)));
    myFixture.checkResultByFile("override/" + getTestName(true) + "_after.py", true);
  }

  private PyClass getTopLevelClass(int index) {
    PyFile file = (PyFile) myFixture.getFile();
    return file.getTopLevelClasses().get(index);
  }

  public void testSimple() {
    doTest();
  }

  public void testClassmethod() {
    doTest();
  }

  public void testNewStyle() {
    doTest();
  }

  public void testReturnValue() {  // PY-1537
    doTest();
  }

  public void testClassmethodNewStyle() {  // PY-1811
    doTest();
  }

  public void testIndent() {  // PY-1796
    doTest();
  }

  public void testQualified() {  // PY-2171
    myFixture.configureByFile("override/" + getTestName(true) + ".py");
    PyClass dateClass = PyClassNameIndex.findClass("datetime.tmxxx", myFixture.getProject());
    assertNotNull(dateClass);
    PyFunction initMethod = dateClass.findMethodByName(PyNames.INIT, false);
    assertNotNull(initMethod);
    PyOverrideImplementUtil.overrideMethods(myFixture.getEditor(), getTopLevelClass(0),
                                            Collections.singletonList(new PyMethodMember(initMethod)));
    myFixture.checkResultByFile("override/" + getTestName(true) + "_after.py", true);
  }

  public void testPy3k() {
    PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), LanguageLevel.PYTHON31);
    try {
      doTest();
    }
    finally {
      PythonLanguageLevelPusher.setForcedLanguageLevel(myFixture.getProject(), null);
    }
  }
}
