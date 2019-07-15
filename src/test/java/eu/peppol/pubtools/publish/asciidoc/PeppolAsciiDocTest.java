package eu.peppol.pubtools.publish.asciidoc;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.helger.commons.io.resource.FileSystemResource;

import eu.peppol.pubtools.TestData;
import eu.peppol.pubtools.project.ProjectReader;
import eu.peppol.pubtools.project.v1.P1DocumentationType;
import eu.peppol.pubtools.project.v1.P1ProjectType;

public final class PeppolAsciiDocTest
{
  @Test
  public void testParseAll ()
  {
    for (final File aDir : TestData.getTestScenarioDirs ())
    {
      final File aProjectXml = new File (aDir, "project.xml");
      final P1ProjectType aProject = ProjectReader.read (new FileSystemResource (aProjectXml));
      assertNotNull (aProject);

      for (final P1DocumentationType aItem : aProject.getDocumentation ())
      {

      }
    }
  }
}
