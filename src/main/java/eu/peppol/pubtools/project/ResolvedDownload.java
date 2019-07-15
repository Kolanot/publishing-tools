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

  @Nonnull
  public String getTitle ()
  {
    return m_aRes.getTitle ();
  }
}
