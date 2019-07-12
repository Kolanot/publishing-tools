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
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1ResourceType;

public class ResolvedProject
{
  private final P1ProjectType m_aProject;
  private final ICommonsOrderedMap <String, ResolvedResource> m_aResolvedResources = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, P1ResourceType> m_aNativeResources = new CommonsLinkedHashMap <> ();

  public ResolvedProject (@Nonnull final P1ProjectType aProject)
  {
    ValueEnforcer.notNull (aProject, "Project");
    m_aProject = aProject;
  }

  public void addResolvedResource (@Nonnull final P1ResourceType aRes, @Nonnull final Object aResolved)
  {
    m_aResolvedResources.put (aRes.getPath (), new ResolvedResource (aRes, aResolved));
  }

  public void addNativeResource (@Nonnull final P1ResourceType aRes)
  {
    m_aNativeResources.put (aRes.getPath (), aRes);
  }
}
