/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/
package org.pentaho.di.trans.steps.fileinput.text;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public final class TextFileInputCsvReaderProviderFactory {

  private static final TextFileInputCsvReaderProvider LEGACY_PROVIDER = new LegacyTextFileInputCsvReaderProvider();

  private TextFileInputCsvReaderProviderFactory() {
  }

  public static TextFileInputCsvReaderProvider getLegacyProvider() {
    return LEGACY_PROVIDER;
  }

  public static List<TextFileInputCsvReaderProvider> getCsvReaderProviders() {
    Map<String, TextFileInputCsvReaderProvider> providersById = new LinkedHashMap<>();
    providersById.put( LEGACY_PROVIDER.getId(), LEGACY_PROVIDER );

    ServiceLoader<TextFileInputCsvReaderProvider> serviceLoader =
      ServiceLoader.load( TextFileInputCsvReaderProvider.class, Thread.currentThread().getContextClassLoader() );
    for ( TextFileInputCsvReaderProvider provider : serviceLoader ) {
      if ( provider != null && !StringUtils.isEmpty( provider.getId() ) ) {
        providersById.putIfAbsent( provider.getId(), provider );
      }
    }

    return new ArrayList<>( providersById.values() );
  }

  public static TextFileInputCsvReaderProvider findProvider( String providerId ) {
    String idToFind = StringUtils.isEmpty( providerId ) ? LEGACY_PROVIDER.getId() : providerId;
    for ( TextFileInputCsvReaderProvider provider : getCsvReaderProviders() ) {
      if ( StringUtils.equalsIgnoreCase( provider.getId(), idToFind ) ) {
        return provider;
      }
    }
    return null;
  }
}
