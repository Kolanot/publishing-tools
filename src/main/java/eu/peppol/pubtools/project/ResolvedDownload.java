package eu.peppol.pubtools.project;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;

import eu.peppol.pubtools.project.v1.P1ResourceType;

public class ResolvedDownload
{
  private final P1ResourceType m_aRes;
  private final String m_sFilename;
  private final String m_sDownload;

  public ResolvedDownload (@Nonnull final P1ResourceType aRes,
                           @Nonnull @Nonempty final String sFilename,
                           @Nonnull @Nonempty final String sDownload)
  {
    ValueEnforcer.notNull (aRes, "Res");
    ValueEnforcer.notEmpty (sFilename, "Filename");
    ValueEnforcer.notEmpty (sDownload, "Download");
    m_aRes = aRes;
    m_sFilename = sFilename;
    m_sDownload = sDownload;
  }

  @Nonnull
  @Nonempty
  public String getFilename ()
  {
    return m_sFilename;
  }

  @Nonnull
  @Nonempty
  public String getDownloadFilename ()
  {
    return m_sDownload;
  }
}
