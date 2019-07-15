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
import eu.peppol.pubtools.project.v1.P1DocumentationType;
import eu.peppol.pubtools.project.v1.P1ProjectType;
import eu.peppol.pubtools.project.v1.P1PropertyType;
import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.structure.v1.S1StructureType;

public class ResolvedProject
{
  private final P1ProjectType m_aProject;
  private final ICommonsOrderedMap <String, ResolvedSyntax> m_aSyntax = new CommonsLinkedHashMap <> ();
  private final ICommonsOrderedMap <String, ResolvedCodeList> m_aCodeLists = new CommonsLinkedHashMap <> ();
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
    final String sKey = aRes.getPath ();
    if (m_aCodeLists.containsKey (sKey))
      throw new IllegalArgumentException ("Another CodeList with key '" + sKey + "' is already contained!");
    m_aCodeLists.put (sKey, new ResolvedCodeList (aRes, aResolved));
  }

  public void addSyntax (@Nonnull final P1ResourceType aRes, @Nonnull final S1StructureType aResolved)
  {
    final String sKey = aRes.getPath ();
    if (m_aSyntax.containsKey (sKey))
      throw new IllegalArgumentException ("Another Structure with key '" + sKey + "' is already contained!");
    m_aSyntax.put (sKey, new ResolvedSyntax (aRes, aResolved));
  }

  public void addSchematron (@Nonnull final P1ResourceType aRes)
  {
    final String sKey = aRes.getPath ();
    if (m_aSchematrons.containsKey (sKey))
      throw new IllegalArgumentException ("Another Schematron with key '" + sKey + "' is already contained!");
    m_aSchematrons.put (sKey, aRes);
  }

  public void addDownload (@Nonnull final P1ResourceType aRes)
  {
    ValueEnforcer.notNull (aRes, "Res");

    // Download is mandatory
    final P1PropertyType aDownload = CollectionHelper.findFirst (aRes.getProperty (),
                                                                 x -> x.getKey ().equals ("download"));
    if (aDownload == null)
      throw new IllegalArgumentException ("Property 'download' is missing");

    // Filename is optional
    P1PropertyType aFilename = CollectionHelper.findFirst (aRes.getProperty (), x -> x.getKey ().equals ("filename"));
    if (aFilename == null)
      aFilename = aDownload;

    final String sKey = aFilename.getValue ();
    if (m_aDownloads.containsKey (sKey))
      throw new IllegalArgumentException ("Another Download with key '" + sKey + "' is already contained!");
    m_aDownloads.put (sKey, new ResolvedDownload (aRes, sKey, aDownload.getValue ()));
  }

  public boolean hasDocumentation ()
  {
    return m_aProject.hasDocumentationEntries ();
  }

  public void forEachDocumentation (@Nonnull final Consumer <? super P1DocumentationType> aConsumer)
  {
    // Maintain order "as is"
    for (final P1DocumentationType x : m_aProject.getDocumentation ())
      aConsumer.accept (x);
  }

  public boolean hasSyntax ()
  {
    return m_aSyntax.isNotEmpty ();
  }

  public void forEachSyntax (@Nonnull final Consumer <? super ResolvedSyntax> aConsumer)
  {
    // Sort
    for (final ResolvedSyntax aEntry : CollectionHelper.getSorted (m_aSyntax.values (),
                                                                   Comparator.comparing (ResolvedSyntax::getTitle)))
      aConsumer.accept (aEntry);
  }

  public boolean hasCodeLists ()
  {
    return m_aCodeLists.isNotEmpty ();
  }

  public void forEachCodeList (@Nonnull final Consumer <? super ResolvedCodeList> aConsumer)
  {
    // Sort
    for (final ResolvedCodeList aEntry : CollectionHelper.getSorted (m_aCodeLists.values (),
                                                                     Comparator.comparing (ResolvedCodeList::getTitle)))
      aConsumer.accept (aEntry);
  }

  public boolean hasRules ()
  {
    return m_aSchematrons.isNotEmpty ();
  }

  public void forEachRule (@Nonnull final Consumer <? super P1ResourceType> aConsumer)
  {
    // Maintain order "as is"
    for (final P1ResourceType x : m_aSchematrons.values ())
      aConsumer.accept (x);
  }

  public boolean hasDownloads ()
  {
    return m_aDownloads.isNotEmpty ();
  }

  public void forEachDownload (@Nonnull final Consumer <? super ResolvedDownload> aConsumer)
  {
    // Maintain order "as is"
    for (final ResolvedDownload x : m_aDownloads.values ())
      aConsumer.accept (x);
  }
}
