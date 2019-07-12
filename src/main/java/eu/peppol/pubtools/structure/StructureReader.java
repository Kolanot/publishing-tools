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
package eu.peppol.pubtools.structure;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;

import eu.peppol.pubtools.structure.v1.ObjectFactory;
import eu.peppol.pubtools.structure.v1.S1StructureType;

public class StructureReader
{
  private static final Logger LOGGER = LoggerFactory.getLogger (StructureReader.class);
  private static final ClassPathResource XSD = new ClassPathResource ("schemas/structure-1.xsd",
                                                                      StructureReader.class.getClassLoader ());

  private StructureReader ()
  {}

  @Nullable
  public static S1StructureType read (@Nonnull final IReadableResource aRes)
  {
    final GenericJAXBMarshaller <S1StructureType> m = new GenericJAXBMarshaller <> (S1StructureType.class,
                                                                                    new CommonsArrayList <> (XSD),
                                                                                    new ObjectFactory ()::createStructure);
    final ErrorList aErrorList = new ErrorList ();
    m.setValidationEventHandlerFactory (x -> new WrappedCollectingValidationEventHandler (aErrorList));
    try
    {
      final S1StructureType ret = m.read (aRes);
      return ret;
    }
    finally
    {
      for (final IError aError : aErrorList)
        LOGGER.error (aError.getAsString (Locale.US));
    }
  }

}
