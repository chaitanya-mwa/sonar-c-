/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2017 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
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
package org.sonar.cxx.checks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.sonar.cxx.CxxAstScanner;
import org.sonar.cxx.CxxConfiguration;
import org.sonar.squidbridge.api.SourceFile;
import org.sonar.squidbridge.checks.CheckMessagesVerifier;

public class FileRegularExpressionCheckTest {

  @Test
  public void fileRegExWithoutFilePattern() throws UnsupportedEncodingException, IOException {
    FileRegularExpressionCheck check = new FileRegularExpressionCheck();
    check.regularExpression = "stdafx\\.h";
    check.message = "Found 'stdafx.h' in file!";
    CxxFileTester tester = CxxFileTesterHelper.CreateCxxFileTester("src/test/resources/checks/FileRegEx.cc", ".");       
    SourceFile file = CxxAstScanner.scanSingleFile(tester.cxxFile, tester.sensorContext, check);    
    
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage(check.message)
      .noMore();
  }

  @Test
  public void fileRegExInvertWithoutFilePattern() throws UnsupportedEncodingException, IOException {
    FileRegularExpressionCheck check = new FileRegularExpressionCheck();
    Charset charset = Charset.forName("UTF-8");
    CxxConfiguration cxxConfig = new CxxConfiguration(charset);
    check.regularExpression = "stdafx\\.h";
    check.invertRegularExpression = true;
    check.message = "Found no 'stdafx.h' in file!";

    CxxFileTester tester = CxxFileTesterHelper.CreateCxxFileTester("src/test/resources/checks/FileRegExInvert.cc", ".");       
    
    SourceFile file = CxxAstScanner.scanSingleFileConfig(tester.cxxFile, cxxConfig, tester.sensorContext, check);    
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage(check.message)
      .noMore();
  }

  @Test
  public void fileRegExCodingErrorActionReplace() throws UnsupportedEncodingException, IOException {
    FileRegularExpressionCheck check = new FileRegularExpressionCheck();
    Charset charset = Charset.forName("US-ASCII");
    CxxConfiguration cxxConfig = new CxxConfiguration(charset);
    check.regularExpression = "stdafx\\.h";
    check.message = "Found 'stdafx.h' in file!";

    CxxFileTester tester = CxxFileTesterHelper.CreateCxxFileTester("src/test/resources/checks/FileRegEx.cc", ".", "US-ASCII");
      
    SourceFile file = CxxAstScanner.scanSingleFileConfig(tester.cxxFile, cxxConfig, tester.sensorContext, check);    
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage(check.message)
      .noMore();
  }

  @Test
  public void fileRegExWithFilePattern() throws UnsupportedEncodingException, IOException {
    FileRegularExpressionCheck check = new FileRegularExpressionCheck();
    Charset charset = Charset.forName("UTF-8");
    CxxConfiguration cxxConfig = new CxxConfiguration(charset);
    check.matchFilePattern = "/**/*.cc"; // all files with .cc file extension
    check.regularExpression = "#include\\s+\"stdafx\\.h\"";
    check.message = "Found '#include \"stdafx.h\"' in a .cc file!";

    CxxFileTester tester = CxxFileTesterHelper.CreateCxxFileTester("src/test/resources/checks/FileRegEx.cc", ".");    
    SourceFile file = CxxAstScanner.scanSingleFileConfig(tester.cxxFile, cxxConfig, tester.sensorContext, check); 

    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage(check.message)
      .noMore();
  }

  @Test
  public void fileRegExInvertWithFilePatternInvert() throws UnsupportedEncodingException, IOException {
    FileRegularExpressionCheck check = new FileRegularExpressionCheck();
    Charset charset = Charset.forName("UTF-8");
    CxxConfiguration cxxConfig = new CxxConfiguration(charset);
    check.matchFilePattern = "/**/*.h"; // all files with not .h file extension
    check.invertFilePattern = true;
    check.regularExpression = "#include\\s+\"stdafx\\.h\"";
    check.invertRegularExpression = true;
    check.message = "Found no '#include \"stdafx.h\"' in a file with not '.h' file extension!";
    
    CxxFileTester tester = CxxFileTesterHelper.CreateCxxFileTester("src/test/resources/checks/FileRegExInvert.cc", ".");
    SourceFile file = CxxAstScanner.scanSingleFileConfig(tester.cxxFile, cxxConfig, tester.sensorContext, check);    

    
    CheckMessagesVerifier.verify(file.getCheckMessages())
      .next().withMessage(check.message)
      .noMore();
  }

}
