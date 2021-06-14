package org.pentaho.di.core.vfs.smb;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.util.Collection;

public class SmbFileSystem extends AbstractFileSystem {

  protected SmbFileSystem( final FileName rootName, final FileSystemOptions fileSystemOptions ) {
    super( rootName, null, fileSystemOptions );
  }
  @Override protected FileObject createFile( AbstractFileName name ) throws Exception {
    return new SmbFileObject( name, this );
  }

  @Override protected void addCapabilities( Collection<Capability> caps ) {
    caps.addAll( SmbFileProvider.capabilities );
  }
}
