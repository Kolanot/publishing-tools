package eu.peppol.pubtools.publish.asciidoc;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.string.StringHelper;

import eu.peppol.pubtools.TestData;
import eu.peppol.pubtools.project.ProjectReader;
import eu.peppol.pubtools.project.v1.P1DocumentationType;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.publish.PublishingDestination;

public final class PeppolAsciiDocTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PeppolAsciiDocTest.class);

  @Test
  public void testParseAll ()
  {
    for (final File aDir : TestData.getTestScenarioDirs ())
    {
      final File aProjectXml = new File (aDir, "project.xml");
      final P1ProjectType aProject = ProjectReader.read (new FileSystemResource (aProjectXml));
      assertNotNull (aProject);

      final PublishingDestination aDest = new PublishingDestination (new File ("temp", aDir.getName ()));

      for (final P1DocumentationType aItem : aProject.getDocumentation ())
        if (StringHelper.hasText (aItem.getPath ()))
        {
          File aSrcFile = new File (aDir, "guide/" + aItem.getPath () + "/main.adoc");
          if (!aSrcFile.exists ())
            aSrcFile = new File (aDir, "guides/" + aItem.getPath () + "/main.adoc");
          if (!aSrcFile.isFile ())
            throw new IllegalArgumentException ("File not existing: " + aSrcFile.getAbsolutePath ());

          LOGGER.info ("Parsing " + aSrcFile.getAbsolutePath ());
          final File aDstDir = new File (aDest.getBaseDir (), aItem.getPath ());
          PeppolAsciiDoc.createHTML (aSrcFile, aDstDir);
          if (false)
            PeppolAsciiDoc.createPDF (aSrcFile, aDstDir);
        }
    }
  }
}
