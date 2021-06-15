package org.pentaho.di.core.vfs.smb;

import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.io.IOException;
import java.util.Collection;

public class SmbFileSystem extends AbstractFileSystem {

  private SMBClient client;
  private Connection connection;

  protected SmbFileSystem( final FileName rootName, final FileSystemOptions fileSystemOptions ) {
    super( rootName, null, fileSystemOptions );
  }
  @Override protected FileObject createFile( AbstractFileName name ) throws Exception {
    return new SmbFileObject( name, this );
  }

  @Override protected void addCapabilities( Collection<Capability> caps ) {
    caps.addAll( SmbFileProvider.capabilities );
  }

  protected Connection conenctToHost() throws IOException {
    if ( client == null ) {
      client = new SMBClient();
    }
    if ( connection == null ) {
      connection = client.connect( "172.20.42.142" );
    }
    return connection;
  }
}
