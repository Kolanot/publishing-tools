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

import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.structure.v1.S1StructureType;

public final class ResolvedSyntax
{
  private final P1ResourceType m_aRes;
  private final S1StructureType m_aStructure;

  public ResolvedSyntax (@Nonnull final P1ResourceType aRes, @Nonnull final S1StructureType aStructure)
  {
    ValueEnforcer.notNull (aRes, "Res");
    ValueEnforcer.notNull (aStructure, "Structure");
    m_aRes = aRes;
    m_aStructure = aStructure;
  }

  @Nonnull
  public P1ResourceType getResource ()
  {
    return m_aRes;
  }

  @Nonnull
  public S1StructureType getStructure ()
  {
    return m_aStructure;
  }

  @Nonnull
  public String getTitle ()
  {
    return m_aRes.getTitle ();
  }
}
