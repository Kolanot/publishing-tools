/**
 * Copyright (C) 2019 OpenPEPPOL AISBL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.peppol.pubtools;

import java.io.File;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class TestData
{
  private TestData ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <File> getTestScenarioDirs ()
  {
    final File aBase = new File ("src/test/resources/testcases").getAbsoluteFile ();
    return new CommonsArrayList <> (new File (aBase, "billing"), new File (aBase, "upgrade3"));
  }
}
