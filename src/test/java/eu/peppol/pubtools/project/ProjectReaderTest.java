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
import com.helger.schematron.pure.exchange.PSReader;
import com.helger.schematron.pure.exchange.SchematronReadException;
import com.helger.schematron.pure.model.PSSchema;

import eu.peppol.pubtools.TestData;
import eu.peppol.pubtools.codelist.v1.C1CodeListType;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.structure.v1.S1StructureType;

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

      LOGGER.info ("Reading project file " + aProjectXml.getAbsolutePath ());

      final P1ProjectType aProject = ProjectReader.read (new FileSystemResource (aProjectXml));
      assertNotNull (aProject);

      for (final P1ResourceType aRes : aProject.getResource ())
      {
        // Check that resource exists
        final String sPath = aRes.getPath ();
        final File aResFile = new File (aDir, sPath);
        assertTrue ("Referenced file is missing: " + aResFile.getAbsolutePath (), aResFile.exists ());

        // Check that it is readable
        switch (aRes.getType ())
        {
          case CODE_LIST_1:
          {
            LOGGER.info ("Reading codelist file " + aResFile.getAbsolutePath ());
            final C1CodeListType aCL = CodeListReader.read (new FileSystemResource (aResFile));
            assertNotNull ("Failed to read code list file: " + aResFile.getAbsolutePath (), aCL);
            break;
          }
          case STRUCTURE_1:
          {
            LOGGER.info ("Reading structure file " + aResFile.getAbsolutePath ());
            final S1StructureType aStruct = StructureReader.readStructure (new FileSystemResource (aResFile));
            assertNotNull ("Failed to read structure file: " + aResFile.getAbsolutePath (), aStruct);
            break;
          }
          case NAMESPACE_1:
            // Can be ignored according to Erlend
            break;
          case NATIVE_FILE:
            break;
          case NATIVE_SCHEMATRON:
          {
            LOGGER.info ("Reading Schematron file " + aResFile.getAbsolutePath ());
            PSSchema aSchema;
            try
            {
              aSchema = new PSReader (new FileSystemResource (aResFile)).readSchema ();
            }
            catch (final SchematronReadException ex)
            {
              LOGGER.error ("oops", ex);
              aSchema = null;
            }
            assertNotNull ("Failed to read Schematrom file: " + aResFile.getAbsolutePath (), aSchema);
            break;
          }
          default:
            throw new IllegalStateException ("Unsupported type " + aRes.getType ());
        }
      }
    }
  }
}
