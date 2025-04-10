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


package org.pentaho.di.core.injection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.injection.bean.BeanInjectionInfo;
import org.pentaho.di.core.injection.bean.BeanInjector;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaString;

/**
 * Base class for test metadata injection.
 */
@Ignore
public abstract class BaseMetadataInjectionTest<T> {
  protected BeanInjectionInfo info;
  protected BeanInjector injector;
  protected T meta;
  protected Set<String> nonTestedProperties;
  protected boolean isDoingChecks;

  protected void setup( T meta ) {
    KettleLogStore.init();
    this.meta = meta;
    info = new BeanInjectionInfo( meta.getClass() );
    injector = new BeanInjector( info );
    nonTestedProperties = new HashSet<>( info.getProperties().keySet() );
    isDoingChecks = true;
  }

  @After
  public void after() {
    if ( isDoingChecks ) {
      assertTrue( "Some properties where not tested: " + nonTestedProperties, nonTestedProperties.isEmpty() );
    }
  }

  protected List<RowMetaAndData> setValue( ValueMetaInterface valueMeta, Object... values ) {
    RowMeta rowsMeta = new RowMeta();
    rowsMeta.addValueMeta( valueMeta );
    List<RowMetaAndData> rows = new ArrayList<>();
    if ( values != null ) {
      for ( Object v : values ) {
        rows.add( new RowMetaAndData( rowsMeta, v ) );
      }
    }
    return rows;
  }

  protected void skipPropertyTest( String propertyName ) {
    nonTestedProperties.remove( propertyName );
  }

  /**
   * Check boolean property.
   */
  protected void check( String propertyName, BooleanGetter getter ) throws KettleException {
    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "Y" ), "f" );
    assertEquals( true, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "N" ), "f" );
    assertEquals( false, getter.get() );

    ValueMetaInterface valueMetaBoolean = new ValueMetaBoolean( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaBoolean, true ), "f" );
    assertEquals( true, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaBoolean, false ), "f" );
    assertEquals( false, getter.get() );

    skipPropertyTest( propertyName );
  }

  /**
   * Check string property.
   */
  protected void check( String propertyName, StringGetter getter, String... values ) throws KettleException {
    ValueMetaInterface valueMeta = new ValueMetaString( "f" );

    if ( values.length == 0 ) {
      values = new String[] { "v", "v2", null };
    }

    String correctValue = null;
    for ( String v : values ) {
      injector.setProperty( meta, propertyName, setValue( valueMeta, v ), "f" );
      if ( v != null ) {
        // only not-null values injected
        correctValue = v;
      }
      assertEquals( correctValue, getter.get() );
    }

    skipPropertyTest( propertyName );
  }

  /**
   * Check enum property.
   */
  protected void check( String propertyName, EnumGetter getter, Class<?> enumType ) throws KettleException {
    ValueMetaInterface valueMeta = new ValueMetaString( "f" );

    Object[] values = enumType.getEnumConstants();

    for ( Object v : values ) {
      injector.setProperty( meta, propertyName, setValue( valueMeta, v ), "f" );
      assertEquals( v, getter.get() );
    }

    try {
      injector.setProperty( meta, propertyName, setValue( valueMeta, "###" ), "f" );
      fail( "Should be passed to enum" );
    } catch ( KettleException ex ) {
    }

    skipPropertyTest( propertyName );
  }

  /**
   * Check int property.
   */
  protected void check( String propertyName, IntGetter getter ) throws KettleException {
    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "1" ), "f" );
    assertEquals( 1, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "45" ), "f" );
    assertEquals( 45, getter.get() );

    ValueMetaInterface valueMetaInteger = new ValueMetaInteger( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaInteger, 1234L ), "f" );
    assertEquals( 1234, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaInteger, (long) Integer.MAX_VALUE ), "f" );
    assertEquals( Integer.MAX_VALUE, getter.get() );

    skipPropertyTest( propertyName );
  }

  /**
   * Check string-to-int property.
   */
  protected void checkStringToInt( String propertyName, IntGetter getter, String[] codes, int[] ids )
    throws KettleException {
    if ( codes.length != ids.length ) {
      throw new RuntimeException( "Wrong codes/ids sizes" );
    }
    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    for ( int i = 0; i < codes.length; i++ ) {
      injector.setProperty( meta, propertyName, setValue( valueMetaString, codes[i] ), "f" );
      assertEquals( ids[i], getter.get() );
    }

    skipPropertyTest( propertyName );
  }

  /**
   * Check long property.
   */
  protected void check( String propertyName, LongGetter getter ) throws KettleException {
    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "1" ), "f" );
    assertEquals( 1, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "45" ), "f" );
    assertEquals( 45, getter.get() );

    ValueMetaInterface valueMetaInteger = new ValueMetaInteger( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaInteger, 1234L ), "f" );
    assertEquals( 1234, getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaInteger, Long.MAX_VALUE ), "f" );
    assertEquals( Long.MAX_VALUE, getter.get() );

    skipPropertyTest( propertyName );
  }

  protected void check( String propertyName, ListGetter getter ) throws KettleException {
    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "foo", "bar" ), "f" );
    assertEquals( Arrays.asList( "foo", "bar" ), getter.get() );

    injector.setProperty( meta, propertyName, setValue( valueMetaString, "one", "two" ), "f" );
    assertEquals( Arrays.asList( "one", "two" ), getter.get() );

    skipPropertyTest( propertyName );
  }


  protected void checkStringToEnum( String propertyName, EnumGetter getter, Class enumType )
    throws KettleException {

    Object[] values = enumType.getEnumConstants();
    ValueMetaInterface valueMeta = new ValueMetaString( "f" );

    for ( Object v : values ) {
      injector.setProperty( meta, propertyName, setValue( valueMeta, v ), "f" );
      assertEquals( v, getter.get() );
    }

    skipPropertyTest( propertyName );
  }

  protected void checkPdiTypes( String propertyName, IntGetter getter ) throws KettleException {
    String[] supportedPdiTypes = ValueMetaFactory.getValueMetaNames();

    ValueMetaInterface valueMetaString = new ValueMetaString( "f" );

    for ( String pdiType : supportedPdiTypes ) {
      injector.setProperty( meta, propertyName, setValue( valueMetaString, pdiType ), "f" );
      assertEquals( ValueMetaFactory.getIdForValueMeta( pdiType ), getter.get() );
    }

    skipPropertyTest( propertyName );
  }

  public static int[] getTypeCodes( String[] typeNames ) {
    int[] typeCodes = new int[typeNames.length];
    for ( int i = 0; i < typeNames.length; i++ ) {
      typeCodes[i] = ValueMetaBase.getType( typeNames[i] );
    }
    return typeCodes;
  }

  public interface BooleanGetter {
    boolean get();
  }

  public interface StringGetter {
    String get();
  }

  public interface EnumGetter {
    Enum<?> get();
  }

  public interface IntGetter {
    int get();
  }

  public interface LongGetter {
    long get();
  }

  public interface ListGetter {
    List<?> get();
  }

  @Test
  public void testMetadataInjectionMessageElements( ) {
    isDoingChecks = false;
    String propertyMessageOmissions =
      ( new BeanInjectionInfo( meta.getClass() ) ).checkMetaDataInjectionBeanAgainstMessages();
    if ( propertyMessageOmissions != null ) {
      fail( propertyMessageOmissions );
    }
  }
}
