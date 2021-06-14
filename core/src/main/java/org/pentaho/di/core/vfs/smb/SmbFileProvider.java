package org.pentaho.di.core.vfs.smb;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SmbFileProvider extends AbstractOriginatingFileProvider {

  static final Collection<Capability> capabilities = Collections.unmodifiableCollection(
    Arrays.asList( Capability.CREATE, Capability.DELETE, Capability.RENAME, Capability.GET_TYPE,
      Capability.GET_LAST_MODIFIED, Capability.SET_LAST_MODIFIED_FILE,
      Capability.SET_LAST_MODIFIED_FOLDER, Capability.LIST_CHILDREN, Capability.READ_CONTENT,
      Capability.URI, Capability.WRITE_CONTENT, Capability.APPEND_CONTENT, Capability.RANDOM_ACCESS_READ,
      Capability.RANDOM_ACCESS_WRITE ) );

  @Override protected FileSystem doCreateFileSystem( FileName rootName, FileSystemOptions fileSystemOptions )
    throws FileSystemException {
    return new SmbFileSystem( rootName, fileSystemOptions );
  }

  @Override public Collection<Capability> getCapabilities() {
    return capabilities;
  }
}
