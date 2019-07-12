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
package eu.peppol.pubtools.codelist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.jaxb.GenericJAXBMarshaller;

import eu.peppol.pubtools.codelist.v1.C1CodeListType;
import eu.peppol.pubtools.codelist.v1.ObjectFactory;

public class CodeListReader
{
  private static final ClassPathResource XSD = new ClassPathResource ("schemas/codelist-1.xsd",
                                                                      CodeListReader.class.getClassLoader ());

  private CodeListReader ()
  {}

  @Nullable
  public static C1CodeListType read (@Nonnull final IReadableResource aRes)
  {
    final GenericJAXBMarshaller <C1CodeListType> m = new GenericJAXBMarshaller <> (C1CodeListType.class,
                                                                                   new CommonsArrayList <> (XSD),
                                                                                   new ObjectFactory ()::createCodeList);
    return m.read (aRes);
  }

}
