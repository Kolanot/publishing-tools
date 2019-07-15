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

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.WillClose;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.IHCConversionSettings;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.config.HCSettings;
import com.helger.html.hc.html.embedded.EHCCORSSettings;
import com.helger.html.hc.html.grouping.HCDD;
import com.helger.html.hc.html.grouping.HCDL;
import com.helger.html.hc.html.grouping.HCDT;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.metadata.HCLink;
import com.helger.html.hc.html.metadata.HCMeta;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.script.HCScriptFile;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.render.HCRenderer;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.dropdown.BootstrapDropdownMenu;
import com.helger.photon.bootstrap4.grid.BootstrapGridSpec;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.listgroup.BootstrapListGroup;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarNav;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.utils.BootstrapPageHeader;

import eu.peppol.pubtools.codelist.v1.C1CodeListType;
import eu.peppol.pubtools.project.ResolvedCodeList;
import eu.peppol.pubtools.project.ResolvedProject;
import eu.peppol.pubtools.project.ResolvedStructure;
import eu.peppol.pubtools.project.v1.P1ResourceType;
import eu.peppol.pubtools.structure.v1.S1StructureType;

public class HtmlCreator
{
  public HtmlCreator ()
  {}

  @Nonnull
  public HCHtml createEmptyPage (@Nonnull final ResolvedProject aProject, @Nonnull final ResourceMap aResourceMap)
  {
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
    aHead.addCSS (HCLink.createCSSLink (new SimpleURL ("/css/structure.css")));
    aResourceMap.put ("css/structure.css", new ClassPathResource ("html/assets/structure.css"));

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
      final ISimpleURL aLinkToStartPage = new SimpleURL ("/index.html");

      final BootstrapNavbar aNavbar = new BootstrapNavbar ();
      aNavbar.addBrand (new HCSpan ().addChild (aProject.getProject ().getName ()), aLinkToStartPage);

      final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();

      // Syntax
      {
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsOrderedMap <String, S1StructureType> aMap = new CommonsLinkedHashMap <> ();
        aProject.forEachStructure (x -> aMap.put (x.getTitle (), x.getStructure ()));
        for (final Map.Entry <String, S1StructureType> aEntry : aMap.entrySet ())
        {
          // TODO create link
          aDropDown.createAndAddItem ().addChild (aEntry.getKey ());
        }
        aNav.addItem ().addNavDropDown ("Syntax", aDropDown);
      }

      // Rules
      {
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsList <P1ResourceType> aList = new CommonsArrayList <> ();
        aProject.forEachSchematron (aList::add);
        // Maintain order from project
        for (final P1ResourceType aRes : aList)
        {
          // TODO create link
          aDropDown.createAndAddItem ().addChild (aRes.getTitle ());
        }
        aNav.addItem ().addNavDropDown ("Rules", aDropDown);
      }

      // Code Lists
      {
        final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
        final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
        final ICommonsOrderedMap <String, C1CodeListType> aMap = new CommonsLinkedHashMap <> ();
        aProject.forEachCodeList (x -> aMap.put (x.getTitle (), x.getCodeList ()));
        for (final Map.Entry <String, C1CodeListType> aEntry : aMap.entrySet ())
        {
          // TODO create link
          aDropDown.createAndAddItem ().addChild (aEntry.getKey ());
        }
        aNav.addItem ().addNavDropDown ("Code lists", aDropDown);
      }

      aBody.addChild (aNavbar);
    }

    return aHtml;
  }

  private void _createStructure (@Nonnull final ResolvedStructure aStruct, @Nonnull final PublishingDestination aDest)
  {

  }

  private void _createRule (@Nonnull final P1ResourceType aStruct, @Nonnull final PublishingDestination aDest)
  {

  }

  private void _createCodeList (@Nonnull final ResolvedCodeList aCodeList, @Nonnull final PublishingDestination aDest)
  {

  }

  public void publishHome (@Nonnull final ResolvedProject aProject, @Nonnull final PublishingDestination aDest)
  {
    // Static resources
    final ResourceMap aResourceMap = new ResourceMap ();

    // Create child pages
    aProject.forEachStructure (x -> _createStructure (x, aDest));
    aProject.forEachSchematron (x -> _createRule (x, aDest));
    aProject.forEachCodeList (x -> _createCodeList (x, aDest));

    // Create home
    final HCHtml aHtml = createEmptyPage (aProject, aResourceMap);
    aHtml.head ().setPageTitle ("Home");
    final HCBody aBody = aHtml.body ();

    {
      final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
      aMain.addChild (new BootstrapPageHeader ().addChild ("Home"));

      final BootstrapGridSpec aGSLeft = BootstrapGridSpec.create (2);
      final BootstrapGridSpec aGSRight = BootstrapGridSpec.create (10);

      final BootstrapContainer aCont = aMain.addAndReturnChild (new BootstrapContainer ());

      {
        final HCDL aDL = aCont.addAndReturnChild (new HCDL ().addClass (CBootstrapCSS.ROW));
        final BootstrapListGroup aLG = new BootstrapListGroup ();
        aProject.forEachStructure (x -> {
          aLG.addItem (x.getTitle ());
        });
        if (aLG.hasChildren ())
        {
          aDL.addChild (aGSLeft.applyTo (new HCDT ().addChild ("Syntax")));
          aDL.addChild (aGSRight.applyTo (new HCDD ().addChild (aLG)));
        }
      }

      {
        final HCDL aDL = aCont.addAndReturnChild (new HCDL ().addClass (CBootstrapCSS.ROW));
        final BootstrapListGroup aLG = new BootstrapListGroup ();
        aProject.forEachSchematron (x -> {
          aLG.addItem (x.getTitle ());
        });
        if (aLG.hasChildren ())
        {
          aDL.addChild (aGSLeft.applyTo (new HCDT ().addChild ("Rules")));
          aDL.addChild (aGSRight.applyTo (new HCDD ().addChild (aLG)));
        }
      }

      {
        final HCDL aDL = aCont.addAndReturnChild (new HCDL ().addClass (CBootstrapCSS.ROW));
        final BootstrapListGroup aLG = new BootstrapListGroup ();
        aProject.forEachCodeList (x -> {
          aLG.addItem (x.getTitle ());
        });
        if (aLG.hasChildren ())
        {
          aDL.addChild (aGSLeft.applyTo (new HCDT ().addChild ("Code lists")));
          aDL.addChild (aGSRight.applyTo (new HCDD ().addChild (aLG)));
        }
      }

      {
        final HCDL aDL = aCont.addAndReturnChild (new HCDL ().addClass (CBootstrapCSS.ROW));
        final BootstrapListGroup aLG = new BootstrapListGroup ();
        aProject.forEachDownload (x -> {
          aLG.addItem (x.getDownloadFilename ());
        });
        if (aLG.hasChildren ())
        {
          aDL.addChild (aGSLeft.applyTo (new HCDT ().addChild ("Downloads")));
          aDL.addChild (aGSRight.applyTo (new HCDD ().addChild (aLG)));
        }
      }
    }

    // Write HTML
    _writeToStream (aHtml, aDest.getOutputStream ("index.html"));

    // Write all resources
    aResourceMap.writeAll (aDest);
  }

  private static void _writeToStream (@Nonnull final HCHtml aHtml, @Nonnull @WillClose final OutputStream aOS)
  {
    final IHCConversionSettings aConversionSettings = HCSettings.getConversionSettings ();
    HCRenderer.prepareForConversion (aHtml, aHtml.body (), aConversionSettings);

    // Extract and merge all inline out-of-band nodes
    if (aConversionSettings.isExtractOutOfBandNodes ())
    {
      final ICommonsList <IHCNode> aOOBNodes = aHtml.getAllOutOfBandNodesWithMergedInlineNodes ();
      aHtml.addAllOutOfBandNodesToHead (aOOBNodes);
    }

    // Move scripts to body? If so, after aggregation!
    if (HCSettings.isScriptsInBody ())
      aHtml.moveScriptElementsToBody ();

    HCRenderer.writeHtmlTo (aHtml, aConversionSettings, aOS);
  }
}
