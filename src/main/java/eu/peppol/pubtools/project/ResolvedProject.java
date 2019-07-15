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

import java.util.Comparator;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;

import eu.peppol.pubtools.codelist.v1.C1CodeListType;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1PropertyType;
import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.structure.v1.S1StructureType;

public class ResolvedProject
{
  private final P1ProjectType m_aProject;
  private final ICommonsOrderedMap <String, ResolvedStructure> m_aResolvedStructure = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, ResolvedCodeList> m_aResolvedCodeLists = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, P1ResourceType> m_aSchematrons = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, ResolvedDownload> m_aDownloads = new CommonsLinkedHashMap <> ();

  public ResolvedProject (@Nonnull final P1ProjectType aProject)
  {
    ValueEnforcer.notNull (aProject, "Project");
    m_aProject = aProject;
  }

  @Nonnull
  public P1ProjectType getProject ()
  {
    return m_aProject;
  }

  public void addCodeList (@Nonnull final P1ResourceType aRes, @Nonnull final C1CodeListType aResolved)
  {
    m_aResolvedCodeLists.put (aRes.getPath (), new ResolvedCodeList (aRes, aResolved));
  }

  public void addStructure (@Nonnull final P1ResourceType aRes, @Nonnull final S1StructureType aResolved)
  {
    m_aResolvedStructure.put (aRes.getPath (), new ResolvedStructure (aRes, aResolved));
  }

  public void addSchematron (@Nonnull final P1ResourceType aRes)
  {
    final String sKey = aRes.getPath ();
    if (m_aSchematrons.containsKey (sKey))
      throw new IllegalArgumentException ("Another schematron with key '" + sKey + "' is already contained!");
    m_aSchematrons.put (aRes.getPath (), aRes);
  }

  public void addDownload (@Nonnull final P1ResourceType aRes)
  {
    ValueEnforcer.notNull (aRes, "Res");

    final P1PropertyType aDownload = CollectionHelper.findFirst (aRes.getProperty (),
                                                                 x -> x.getKey ().equals ("download"));
    if (aDownload == null)
      throw new IllegalArgumentException ("Property 'download' is missing");

    P1PropertyType aFilename = CollectionHelper.findFirst (aRes.getProperty (), x -> x.getKey ().equals ("filename"));
    if (aFilename == null)
    {
      // Fallback
      aFilename = aDownload;
    }

    final String sKey = aFilename.getValue ();
    if (m_aDownloads.containsKey (sKey))
      throw new IllegalArgumentException ("Another download with key '" + sKey + "' is already contained!");
    m_aDownloads.put (sKey, new ResolvedDownload (aRes, sKey, aDownload.getValue ()));
  }

  public void forEachStructure (@Nonnull final Consumer <? super ResolvedStructure> aConsumer)
  {
    // Sort
    for (final ResolvedStructure aEntry : CollectionHelper.getSorted (m_aResolvedStructure.values (),
                                                                      Comparator.comparing (ResolvedStructure::getTitle)))
      aConsumer.accept (aEntry);
  }

  public void forEachCodeList (@Nonnull final Consumer <? super ResolvedCodeList> aConsumer)
  {
    // Sort
    for (final ResolvedCodeList aEntry : CollectionHelper.getSorted (m_aResolvedCodeLists.values (),
                                                                     Comparator.comparing (ResolvedCodeList::getTitle)))
      aConsumer.accept (aEntry);
  }

  public void forEachSchematron (@Nonnull final Consumer <? super P1ResourceType> aConsumer)
  {
    // Maintain order "as is"
    for (final P1ResourceType r : m_aSchematrons.values ())
      aConsumer.accept (r);
  }

  public void forEachDownload (@Nonnull final Consumer <? super ResolvedDownload> aConsumer)
  {
    // Maintain order "as is"
    for (final ResolvedDownload r : m_aDownloads.values ())
      aConsumer.accept (r);
  }
}
