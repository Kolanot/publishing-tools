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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.io.resource.inmemory.ReadableResourceString;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.html.EHTMLVersion;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.config.HCConversionSettings;
import com.helger.html.hc.config.HCSettings;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.render.HCRenderer;

public class ResourceMap extends CommonsLinkedHashMap <String, IReadableResource>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ResourceMap.class);

  public void addHtml (@Nonnull @Nonempty final String sRelativeFilename, @Nonnull final HCHtml aHtml)
  {
    final HCConversionSettings aConversionSettings = HCSettings.getMutableConversionSettings ();
    aConversionSettings.setHTMLVersion (EHTMLVersion.HTML5);
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

    final String sHtml = HCRenderer.getAsHTMLString (aHtml, aConversionSettings);
    put (sRelativeFilename, new ReadableResourceString (sHtml, StandardCharsets.UTF_8));
  }

  public void writeAll (@Nonnull final PublishingDestination aDest)
  {
    for (final Map.Entry <String, IReadableResource> aEntry : entrySet ())
    {
      if (StreamHelper.copyInputStreamToOutputStreamAndCloseOS (aEntry.getValue ().getBufferedInputStream (),
                                                                aDest.getOutputStream (aEntry.getKey ()))
                      .isFailure ())
      {
        LOGGER.error ("Faied to create '" + aEntry.getKey () + "'");
      }
    }
  }
}
