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
package eu.peppol.pubtools.project;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.FileSystemResource;

import eu.peppol.pubtools.TestData;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1ResourceType;

public final class ProjectReaderTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ProjectReaderTest.class);

  @Test
  public void testReadProjectXml ()
  {
    for (final File aDir : TestData.getTestScenarioDirs ())
    {
      final File aProjectXml = new File (aDir, "project.xml");
      assertTrue ("Project XML is missing: " + aDir.getAbsolutePath (), aProjectXml.exists ());

      LOGGER.info ("Reading " + aProjectXml.getAbsolutePath ());

      final P1ProjectType aProject = ProjectReader.read (new FileSystemResource (aProjectXml));
      assertNotNull (aProject);

      for (final P1ResourceType aRes : aProject.getResource ())
      {
        final String sPath = aRes.getPath ();
        final File aResFile = new File (aDir, sPath);
        assertTrue ("Referenced file is missing: " + aResFile.getAbsolutePath (), aResFile.exists ());
      }
    }
  }
}
