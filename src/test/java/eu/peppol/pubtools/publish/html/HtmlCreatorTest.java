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
package eu.peppol.pubtools.publish.html;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.timing.StopWatch;

import eu.peppol.pubtools.TestData;
import eu.peppol.pubtools.project.ProjectReader;
import eu.peppol.pubtools.project.ResolvedProject;
import eu.peppol.pubtools.publish.PublishingDestination;
import eu.peppol.pubtools.publish.html.HtmlCreator;

public final class HtmlCreatorTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (HtmlCreatorTest.class);

  @Test
  public void testReadProjectXml ()
  {
    for (final File aDir : TestData.getTestScenarioDirs ())
    {
      LOGGER.info ("Publishing home for " + aDir.getAbsolutePath ());

      final StopWatch aSW = StopWatch.createdStarted ();
      final ResolvedProject aResolvedProject = ProjectReader.createResolvedProject (aDir);

      final PublishingDestination aDest = new PublishingDestination (new File ("temp", aDir.getName ()));

      HtmlCreator.publishHome (aResolvedProject, aDest);
      LOGGER.info ("Publishing took " + aSW.stopAndGetMillis () + "ms");
    }
  }
}
