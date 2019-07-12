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

import javax.annotation.Nonnull;
import javax.annotation.WillClose;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.IHCConversionSettings;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.config.HCSettings;
import com.helger.html.hc.html.embedded.EHCCORSSettings;
import com.helger.html.hc.html.metadata.HCHead;
import com.helger.html.hc.html.metadata.HCLink;
import com.helger.html.hc.html.metadata.HCMeta;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.script.HCScriptFile;
import com.helger.html.hc.html.sections.HCBody;
import com.helger.html.hc.render.HCRenderer;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.utils.BootstrapPageHeader;

import eu.peppol.pubtools.project.ResolvedProject;

public class HtmlCreator
{
  public HtmlCreator ()
  {}

  @Nonnull
  public HCHtml createEmptyPage (@Nonnull final ResourceMap aResourceMap)
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

    return aHtml;
  }

  public void publishHome (@Nonnull final ResolvedProject aProject, @Nonnull final PublishingDestination aDest)
  {
    // Static resources
    final ResourceMap aResourceMap = new ResourceMap ();

    final HCHtml aHtml = createEmptyPage (aResourceMap);
    aHtml.head ().setPageTitle ("Home");
    final HCBody aBody = aHtml.body ();
    {
      final BootstrapContainer aMain = aBody.addAndReturnChild (new BootstrapContainer ());
      aMain.addChild (new BootstrapPageHeader ().addChild ("Home"));
    }

    // Write HTML
    writeToStream (aHtml, aDest.getOutputStream ("index.html"));

    // Write all resources
    aResourceMap.writeAll (aDest);
  }

  private static void writeToStream (@Nonnull final HCHtml aHtml, @Nonnull @WillClose final OutputStream aOS)
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
