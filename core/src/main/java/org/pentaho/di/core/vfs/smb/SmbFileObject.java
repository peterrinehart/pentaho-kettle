package org.pentaho.di.core.vfs.smb;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.msfscc.fileinformation.FileInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.util.UserAuthenticatorUtils;

import java.io.IOException;
import java.util.EnumSet;

public class SmbFileObject extends AbstractFileObject<SmbFileSystem> {
  DiskEntry smbFile;
  FileAllInformation fileInfo;
  FileType fileType;

  protected SmbFileObject( final AbstractFileName name, final SmbFileSystem fileSystem) throws FileSystemException {
    super(name, fileSystem);
    // this.fileName = UriParser.decode(name.getURI());
  }

  /**
   * Attaches this file object to its file resource.
   */
  @Override
  protected void doAttach() throws Exception {
    // Defer creation of the SmbFile to here
    if (smbFile == null) {
      smbFile = createSmbFile( getName() );
    }
  }

  @Override
  protected void doDetach() throws Exception {
    // file closed through content-streams
    smbFile = null;
  }

  private DiskEntry createSmbFile(final FileName fileName)
    throws FileSystemException {
    final SmbFileName smbFileName = (SmbFileName) fileName;

    final String path = smbFileName.getUriWithoutAuth();

    UserAuthenticationData authData = null;
    DiskEntry file = null;
    try {
      Connection conn = ((SmbFileSystem)getFileSystem()).conenctToHost();
      Session session = conn.authenticate( new AuthenticationContext( "devuser", "password".toCharArray(), "." ) );
      // assuming a disk share; shouldn't need to support pipe or printer shares
      DiskShare share = (DiskShare) session.connectShare( smbFileName.getShare() );
      // access mask, attributes, and other options SHOULD only matter if we're creating a new file, not reading an existing one
      if ( share.fileExists( smbFileName.getPath() ) ) {
        file = share.openFile( smbFileName.getPath(), null, null, null, null, null );
        fileType = FileType.FILE;
      } else if ( share.folderExists( smbFileName.getPath() ) ) {
        file = share.openDirectory( smbFileName.getPath(), null, null, null, null, null );
        fileType = FileType.FOLDER;
      } else {
        // new file, not a directory
        file = share.openFile( smbFileName.getPath(),
          EnumSet.of( AccessMask.GENERIC_ALL ),
          EnumSet.of( FileAttributes.FILE_ATTRIBUTE_NORMAL ),
          SMB2ShareAccess.ALL,
          SMB2CreateDisposition.FILE_CREATE,
          null );
        fileType = FileType.IMAGINARY;
      }
      fileInfo = file.getFileInformation();
      return file;
    } catch ( IOException e ) {
      throw new FileSystemException( e );
    }
  }

  /**
   * Determines the type of the file, returns null if the file does not exist.
   */
  @Override
  protected FileType doGetType() throws Exception {
    return fileType;
  }

  /**
   * Lists the children of the file. Is only called if {@link #doGetType} returns {@link FileType#FOLDER}.
   */
  @Override
  protected String[] doListChildren() throws Exception {
    // VFS-210: do not try to get listing for anything else than directories
    if (!file.isDirectory()) {
      return null;
    }

    return UriParser.encode(file.list());
  }

  /**
   * Determines if this file is hidden.
   */
  @Override
  protected boolean doIsHidden() throws Exception {
    return fileInfo.getBasicInformation().;
  }

  /**
   * Deletes the file.
   */
  @Override
  protected void doDelete() throws Exception {
    file.delete();
  }

  @Override
  protected void doRename(final FileObject newfile) throws Exception {
    file.renameTo(createSmbFile(newfile.getName()));
  }

  /**
   * Creates this file as a folder.
   */
  @Override
  protected void doCreateFolder() throws Exception {
    file.mkdir();
    file = createSmbFile(getName());
  }

  /**
   * Returns the size of the file content (in bytes).
   */
  @Override
  protected long doGetContentSize() throws Exception {
    return file.length();
  }

  /**
   * Returns the last modified time of this file.
   */
  @Override
  protected long doGetLastModifiedTime() throws Exception {
    return file.getLastModified();
  }

  /**
   * Creates an input stream to read the file content from.
   */
  @Override
  protected InputStream doGetInputStream(final int bufferSize) throws Exception {
    try {
      return new SmbFileInputStream(file);
    } catch (final SmbException e) {
      if (e.getNtStatus() == NtStatus.NT_STATUS_NO_SUCH_FILE) {
        throw new org.apache.commons.vfs2.FileNotFoundException(getName());
      }
      if (file.isDirectory()) {
        throw new FileTypeHasNoContentException(getName());
      }

      throw e;
    }
  }

  /**
   * Creates an output stream to write the file content to.
   */
  @Override
  protected OutputStream doGetOutputStream(final boolean bAppend) throws Exception {
    return new SmbFileOutputStream(file, bAppend);
  }

  /**
   * random access
   */
  @Override
  protected RandomAccessContent doGetRandomAccessContent(final RandomAccessMode mode) throws Exception {
    return new SmbFileRandomAccessContent(file, mode);
  }

  @Override
  protected boolean doSetLastModifiedTime(final long modtime) throws Exception {
    file.setLastModified(modtime);
    return true;
  }
}
