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
import com.helger.commons.io.file.FilenameHelper;

import eu.peppol.pubtools.codelist.v1.C1CodeListType;
import eu.peppol.pubtools.project.v1.P1ResourceType;

public final class ResolvedCodeList
{
  private final P1ResourceType m_aRes;
  private final C1CodeListType m_aCodeList;
  private final String m_sURLDir;

  public ResolvedCodeList (@Nonnull final P1ResourceType aRes, @Nonnull final C1CodeListType aCodeList)
  {
    ValueEnforcer.notNull (aRes, "Res");
    ValueEnforcer.notNull (aCodeList, "CodeList");
    m_aRes = aRes;
    m_aCodeList = aCodeList;
    // Ensure that it is a valid filename
    m_sURLDir = FilenameHelper.getAsSecureValidASCIIFilename (aCodeList.getIdentifier ());
  }

  @Nonnull
  public P1ResourceType getResource ()
  {
    return m_aRes;
  }

  @Nonnull
  public C1CodeListType getCodeList ()
  {
    return m_aCodeList;
  }

  @Nonnull
  public String getTitle ()
  {
    return m_aCodeList.getTitle ();
  }

  @Nonnull
  public String getURLDir ()
  {
    return m_sURLDir;
  }
}
