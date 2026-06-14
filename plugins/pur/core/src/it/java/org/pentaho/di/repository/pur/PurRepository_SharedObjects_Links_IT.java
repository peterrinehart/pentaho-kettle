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

package org.pentaho.di.repository.pur;

import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.shared.SharedObjectInterface;
import org.pentaho.di.trans.TransMeta;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class PurRepository_SharedObjects_Links_IT extends PurRepositoryTestBase {

  private static final String SHARED_NAME_SUFFIX = "_shared_links";

  public PurRepository_SharedObjects_Links_IT( Boolean lazyRepo ) {
    super( lazyRepo );
  }

  private interface GenericMeta {
    public AbstractMeta createFilled() throws Exception;

    public void loadFromXml( Node xmlNode ) throws Exception;

    public AbstractMeta createEmpty();
  }

  @Test
  public void testReadSharedObjects_Trans() throws Exception {
    testReadSharedObjects( new GenericMeta() {

      private TransMeta meta;

      @Override
      public void loadFromXml( Node xmlNode ) throws Exception {
        meta.loadXML( xmlNode, purRepository, true, null );
      }

      @Override
      public AbstractMeta createFilled() throws Exception {
        meta = createFilledTransMeta();
        return meta;
      }

      @Override
      public AbstractMeta createEmpty() {
        meta = new TransMeta();
        return meta;
      }
    } );
  }

  @Test
  public void testReadSharedObjects_Job() throws Exception {
    testReadSharedObjects( new GenericMeta() {

      private JobMeta meta;

      @Override
      public void loadFromXml( Node xmlNode ) throws Exception {
        meta.loadXML( xmlNode, purRepository, null );
      }

      @Override
      public AbstractMeta createFilled() throws Exception {
        meta = createFilledJobMeta();
        return meta;
      }

      @Override
      public AbstractMeta createEmpty() {
        meta = new JobMeta();
        return meta;
      }
    } );
  }

  @SuppressWarnings( "unchecked" )
  private void testReadSharedObjects( GenericMeta gMeta ) throws Exception {
    PurRepository pur = purRepository;

    SlaveServer slave1 = new SlaveServer();
    slave1.setName( "slave1" + SHARED_NAME_SUFFIX );

    SlaveServer slave2 = new SlaveServer();
    slave2.setName( "slave2" + SHARED_NAME_SUFFIX );

    pur.save( slave1, null, null );
    pur.save( slave2, null, null );

    AbstractMeta meta = gMeta.createFilled();

    meta.getSlaveServers().add( slave1 );
    meta.getSlaveServers().add( slave2 );

    RepositoryDirectoryInterface saveDir = pur.getDefaultSaveDirectory( meta );
    meta.setRepositoryDirectory( saveDir );
    pur.save( meta, null, null );
    String xmlText = meta.getXML();

    try {
      // import transformation from file
      meta = gMeta.createEmpty();
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse( IOUtils.toInputStream( xmlText, StandardCharsets.UTF_8 ) );
      gMeta.loadFromXml( doc.getParentNode() );

      List<SharedObjectInterface<?>> sharedObjects =
          (List<SharedObjectInterface<?>>) pur.loadAndCacheSharedObjects( false ).get( RepositoryObjectType.SLAVE_SERVER );

      for ( int i = 0; i < meta.getSlaveServers().size(); i++ ) {
        for ( int j = 0; j < sharedObjects.size(); j++ ) {
          SlaveServer s1 = meta.getSlaveServers().get( i );
          SlaveServer s2 = (SlaveServer) sharedObjects.get( j );
          if ( s1 == s2 ) {
            fail( "Trans/job has direct links on slave servers from cache" );
          }
        }
      }
    } finally {
      pur.deleteSlave( slave1.getObjectId() );
      pur.deleteSlave( slave2.getObjectId() );
      pur.clearSharedObjectCache();
    }
  }

  private TransMeta createFilledTransMeta() {
    TransMeta meta = new TransMeta();
    meta.setName( "shared_objects_trans" + SHARED_NAME_SUFFIX );
    return meta;
  }

  private JobMeta createFilledJobMeta() {
    JobMeta meta = new JobMeta();
    meta.setName( "shared_objects_job" + SHARED_NAME_SUFFIX );
    return meta;
  }

}
