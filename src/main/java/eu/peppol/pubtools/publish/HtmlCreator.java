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
package eu.peppol.pubtools.publish;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.html.embedded.EHCCORSSettings;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.metadata.HCLink;
import com.helger.html.hc.html.metadata.HCMeta;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.script.HCScriptFile;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.sections.HCH4;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCBR;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.breadcrumb.BootstrapBreadcrumb;
import com.helger.photon.bootstrap4.dropdown.BootstrapDropdownMenu;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.listgroup.BootstrapListGroup;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarNav;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.utils.BootstrapPageHeader;

import eu.peppol.pubtools.codelist.v1.C1CodeType;
import eu.peppol.pubtools.project.ResolvedCodeList;
import eu.peppol.pubtools.project.ResolvedDownload;
import eu.peppol.pubtools.project.ResolvedProject;
import eu.peppol.pubtools.project.ResolvedSyntax;
import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.publish.html.HCSimpleTable;

public class HtmlCreator
{
  private static final Logger LOGGER = LoggerFactory.getLogger (HtmlCreator.class);
  private static final ICSSClassProvider CSS_CLASS_CODE_LIST_CODE = DefaultCSSClassProvider.create ("code-list-code");

  private HtmlCreator ()
  {}

  @Nonnull
  private static HCHtml _createEmptyPage (@Nonnull final ResolvedProject aProject,
                                          @Nonnegative final int nNestingLevel,
                                          @Nonnull final ResourceMap aResourceMap)
  {
    final String sLinkPrefix = StringHelper.getRepeated ("../", nNestingLevel);

    final HCHtml aHtml = new HCHtml ();
    aHtml.setLanguage ("en");

    // head
    final HCHead aHead = aHtml.head ();
    aHead.metaElements ().add (new HCMeta ().setCharset (StandardCharsets.UTF_8.name ()));
    aHead.metaElements ()
         .add (new HCMeta ().setName ("viewport").setContent ("width=device-width, initial-scale=1, shrink-to-fit=no"));
    aHead.addCSS (HCLink.createCSSLink (new SimpleURL ("https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"))
                        .setIntegrity ("sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T")
                        .setCrossOrigin (EHCCORSSettings.ANONYMOUS));
    aHead.addCSS (HCLink.createCSSLink (new SimpleURL (sLinkPrefix + "css/default.css")));

    // body
    final HCBody aBody = aHtml.body ();
    aBody.addChild (new HCScriptFile ().setSrc (new SimpleURL ("https://code.jquery.com/jquery-3.3.1.slim.min.js"))
                                       .setIntegrity ("sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo")
                                       .setCrossOrigin (EHCCORSSettings.ANONYMOUS));
    aBody.addChild (new HCScriptFile ().setSrc (new SimpleURL ("https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"))
                                       .setIntegrity ("sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1")
                                       .setCrossOrigin (EHCCORSSettings.ANONYMOUS));
    aBody.addChild (new HCScriptFile ().setSrc (new SimpleURL ("https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"))
                                       .setIntegrity ("sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM")
                                       .setCrossOrigin (EHCCORSSettings.ANONYMOUS));

    // Start NavBar
    {
      final ISimpleURL aLinkToStartPage = new SimpleURL (sLinkPrefix + "index.html");

      final BootstrapNavbar aNavbar = new BootstrapNavbar ();
      aNavbar.addBrand (new HCSpan ().addChild (aProject.getProject ().getName ()), aLinkToStartPage);

      final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();

      // Syntax
      if (aProject.hasSyntax ())
      {
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsOrderedMap <String, ResolvedSyntax> aMap = new CommonsLinkedHashMap <> ();
        aProject.forEachSyntax (x -> aMap.put (x.getTitle (), x));
        for (final Map.Entry <String, ResolvedSyntax> aEntry : aMap.entrySet ())
        {
          aDropDown.createAndAddItem ()
                   .setHref (new SimpleURL (sLinkPrefix + "syntax/" + aEntry.getValue ().getURLDir () + "/index.html"))
                   .addChild (aEntry.getKey ());
        }
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        aNav.addItem ().addNavDropDown ("Syntax", aDropDown);
      }

      // Rules
      if (aProject.hasRules ())
      {
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsList <P1ResourceType> aList = new CommonsArrayList <> ();
        aProject.forEachRule (aList::add);
        // Maintain order from project
        for (final P1ResourceType aRes : aList)
        {
          // TODO create link
          aDropDown.createAndAddItem ().addChild (aRes.getTitle ());
        }
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        aNav.addItem ().addNavDropDown ("Rules", aDropDown);
      }

      // Code Lists
      if (aProject.hasCodeLists ())
      {
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsOrderedMap <String, ResolvedCodeList> aMap = new CommonsLinkedHashMap <> ();
        aProject.forEachCodeList (x -> aMap.put (x.getTitle (), x));
        for (final Map.Entry <String, ResolvedCodeList> aEntry : aMap.entrySet ())
        {
          aDropDown.createAndAddItem ()
                   .setHref (new SimpleURL (sLinkPrefix +
                                            "codelist/" +
                                            aEntry.getValue ().getURLDir () +
                                            "/index.html"))
                   .addChild (aEntry.getKey ());
        }
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        aNav.addItem ().addNavDropDown ("Code lists", aDropDown);
      }

      // Downloads
      if (aProject.hasDownloads ())
      {
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsOrderedMap <String, ResolvedDownload> aMap = new CommonsLinkedHashMap <> ();
        aProject.forEachDownload (x -> aMap.put (x.getTitle (), x));
        for (final Map.Entry <String, ResolvedDownload> aEntry : aMap.entrySet ())
        {
          // TODO create link
          aDropDown.createAndAddItem ().addChild (aEntry.getKey ());
        }
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        aNav.addItem ().addNavDropDown ("Downloads", aDropDown);
      }

      aBody.addChild (aNavbar);
    }

    return aHtml;
  }

  @Nonnull
  private static BootstrapBreadcrumb _createBreadcrumbs (@Nonnull final String... aParams)
  {
    ValueEnforcer.isTrue ((aParams.length % 2) == 1, "Odd param count is needed");

    final BootstrapBreadcrumb aBreadcrumb = new BootstrapBreadcrumb ();

    final int nCount = aParams.length;
    for (int i = 0; i < nCount - 1; i += 2)
    {
      aBreadcrumb.getList ().addLink (new SimpleURL (aParams[i + 1]), aParams[i]);
    }
    aBreadcrumb.getList ().addActive (aParams[nCount - 1]);

    return aBreadcrumb;
  }

  private static void _createSyntax (@Nonnull final ResolvedProject aProject,
                                     @Nonnull final ResolvedSyntax aSyntax,
                                     @Nonnull final ResourceMap aResourceMap)
  {
    final HCHtml aHtml = _createEmptyPage (aProject, 2, aResourceMap);
    aHtml.head ().setPageTitle (aProject.getProject ().getName () + " | " + aSyntax.getTitle ());
    final HCBody aBody = aHtml.body ();
    aBody.addChild (_createBreadcrumbs ("Home", "../../index.html", "Syntaxes", "../index.html", aSyntax.getTitle ()));

    final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
    aMain.addChild (new BootstrapPageHeader ().addChild (aSyntax.getTitle ()));

    // TODO

    aResourceMap.addHtml ("syntax/" + aSyntax.getURLDir () + "/index.html", aHtml);
  }

  private static void _createSyntaxOverview (@Nonnull final ResolvedProject aProject,
                                             @Nonnull final ResourceMap aResourceMap)
  {
    final ICommonsList <ResolvedSyntax> aList = new CommonsArrayList <> ();
    aProject.forEachSyntax (aList::add);

    final HCHtml aHtml = _createEmptyPage (aProject, 1, aResourceMap);
    aHtml.head ().setPageTitle (aProject.getProject ().getName () + " | Syntaxes");

    final HCBody aBody = aHtml.body ();
    aBody.addChild (_createBreadcrumbs ("Home", "../index.html", "Syntaxes"));
    final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
    aMain.addChild (new BootstrapPageHeader ().addChild ("Syntaxes"));

    final HCUL aUL = aMain.addAndReturnChild (new HCUL ());
    for (final ResolvedSyntax aItem : aList)
      aUL.addItem (new HCA (new SimpleURL (aItem.getURLDir () + "/index.html")).addChild (aItem.getTitle ()));

    aResourceMap.addHtml ("syntax/index.html", aHtml);

    // Create all code lists
    for (final ResolvedSyntax aItem : aList)
      _createSyntax (aProject, aItem, aResourceMap);
  }

  private static void _createRule (@Nonnull final ResolvedProject aProject,
                                   @Nonnull final P1ResourceType aRule,
                                   @Nonnull final ResourceMap aResourceMap)
  {

  }

  private static void _createCodeList (@Nonnull final ResolvedProject aProject,
                                       @Nonnull final ResolvedCodeList aCodeList,
                                       @Nonnull final ResourceMap aResourceMap)
  {
    final HCHtml aHtml = _createEmptyPage (aProject, 2, aResourceMap);
    aHtml.head ().setPageTitle (aProject.getProject ().getName () + " | " + aCodeList.getTitle ());
    final HCBody aBody = aHtml.body ();
    aBody.addChild (_createBreadcrumbs ("Home",
                                        "../../index.html",
                                        "Code lists",
                                        "../index.html",
                                        aCodeList.getTitle ()));

    final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
    aMain.addChild (new BootstrapPageHeader ().addChild (aCodeList.getTitle ()));

    final BootstrapContainer aCont = aMain.addAndReturnChild (new BootstrapContainer ());
    final HCSimpleTable aTable = aCont.addAndReturnChild (new HCSimpleTable ());
    aTable.addRow ("Identifier", aCodeList.getCodeList ().getIdentifier ());
    aTable.addRow ("Agency", aCodeList.getCodeList ().getAgency ());
    aTable.addRow ("Version", aCodeList.getCodeList ().getVersion ());
    aTable.addRow ("Subset", aCodeList.getCodeList ().getSubset ());
    // TODO usage

    final HCNodeList aCodes = new HCNodeList ();
    for (final C1CodeType aCode : aCodeList.getCodeList ().getCode ())
    {
      final HCDiv aDiv = new HCDiv ().addClass (CSS_CLASS_CODE_LIST_CODE)
                                     .addChild (new HCCode ().addChild (aCode.getId ()))
                                     .addChild (new HCBR ())
                                     .addChild (new HCStrong ().addChild (aCode.getName ()));
      if (StringHelper.hasText (aCode.getDescription ()))
        aDiv.addChild (new HCBR ()).addChild (new HCP ().addChild (aCode.getDescription ()));
      aCodes.addChild (aDiv);
    }
    aTable.addRow ("Codes", aCodes);

    aResourceMap.addHtml ("codelist/" + aCodeList.getURLDir () + "/index.html", aHtml);
  }

  private static void _createCodeListsOverview (@Nonnull final ResolvedProject aProject,
                                                @Nonnull final ResourceMap aResourceMap)
  {
    final ICommonsList <ResolvedCodeList> aList = new CommonsArrayList <> ();
    aProject.forEachCodeList (aList::add);

    final HCHtml aHtml = _createEmptyPage (aProject, 1, aResourceMap);
    aHtml.head ().setPageTitle (aProject.getProject ().getName () + " | Code lists");

    final HCBody aBody = aHtml.body ();
    aBody.addChild (_createBreadcrumbs ("Home", "../index.html", "Code lists"));
    final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
    aMain.addChild (new BootstrapPageHeader ().addChild ("Code lists"));

    final HCUL aUL = aMain.addAndReturnChild (new HCUL ());
    for (final ResolvedCodeList aItem : aList)
      aUL.addItem (new HCA (new SimpleURL (aItem.getURLDir () + "/index.html")).addChild (aItem.getTitle ()));

    aResourceMap.addHtml ("codelist/index.html", aHtml);

    // Create all code lists
    for (final ResolvedCodeList aItem : aList)
      _createCodeList (aProject, aItem, aResourceMap);
  }

  private static void _createHome (@Nonnull final ResolvedProject aProject, @Nonnull final ResourceMap aResourceMap)
  {
    final HCHtml aHtml = _createEmptyPage (aProject, 0, aResourceMap);
    aHtml.head ().setPageTitle (aProject.getProject ().getName ());
    final HCBody aBody = aHtml.body ();

    final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
    aMain.addChild (new BootstrapPageHeader ().addChild ("Home"));

    final BootstrapContainer aCont = aMain.addAndReturnChild (new BootstrapContainer ());
    final HCSimpleTable aTable = aCont.addAndReturnChild (new HCSimpleTable ());

    if (aProject.hasDocumentation ())
    {
      final BootstrapListGroup aLG = new BootstrapListGroup ();
      aProject.forEachDocumentation (x -> {
        if (StringHelper.hasNoText (x.getPath ()))
          aLG.addItem (new HCH4 ().addChild (x.getValue ()));
        else
          aLG.addItem (x.getValue ());
      });
      aTable.addRow ("Documentation", aLG);
    }

    if (aProject.hasSyntax ())
    {
      final BootstrapListGroup aLG = new BootstrapListGroup ();
      aProject.forEachSyntax (x -> {
        aLG.addItem (x.getTitle ());
      });
      aTable.addRow ("Syntax", aLG);
    }

    if (aProject.hasRules ())
    {
      final BootstrapListGroup aLG = new BootstrapListGroup ();
      aProject.forEachRule (x -> {
        aLG.addItem (x.getTitle ());
      });
      aTable.addRow ("Rules", aLG);
    }

    if (aProject.hasCodeLists ())
    {
      final BootstrapListGroup aLG = new BootstrapListGroup ();
      aProject.forEachCodeList (x -> {
        aLG.addItem (x.getTitle ());
      });
      aTable.addRow ("Code lists", aLG);
    }

    if (aProject.hasDownloads ())
    {
      final BootstrapListGroup aLG = new BootstrapListGroup ();
      aProject.forEachDownload (x -> {
        aLG.addItem (x.getTitle ());
      });
      aTable.addRow ("Downloads", aLG);
    }

    aResourceMap.addHtml ("index.html", aHtml);
  }

  public static void publishHome (@Nonnull final ResolvedProject aProject, @Nonnull final PublishingDestination aDest)
  {
    // Static resources
    final ResourceMap aResourceMap = new ResourceMap ();
    aResourceMap.put ("css/default.css", new ClassPathResource ("template/default.css"));

    // Create child pages

    // Syntax files
    if (aProject.hasSyntax ())
      _createSyntaxOverview (aProject, aResourceMap);

    aProject.forEachRule (x -> _createRule (aProject, x, aResourceMap));

    // Create codelists
    if (aProject.hasCodeLists ())
      _createCodeListsOverview (aProject, aResourceMap);

    // Create home
    _createHome (aProject, aResourceMap);

    // Write all resources
    aResourceMap.writeAll (aDest);
    LOGGER.info ("Successfully wrote " + aResourceMap.size () + " files to " + aDest.getBaseDir ().getPath ());
  }
}
