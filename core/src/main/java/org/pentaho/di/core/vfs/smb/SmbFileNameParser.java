package org.pentaho.di.core.vfs.smb;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.FileNameParser;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.VfsComponentContext;
import org.apache.commons.vfs2.provider.url.UrlFileNameParser;
import org.apache.commons.vfs2.util.Cryptor;
import org.apache.commons.vfs2.util.CryptorFactory;

public class SmbFileNameParser extends UrlFileNameParser {
  private static final SmbFileNameParser INSTANCE = new SmbFileNameParser();
  private static final int SMB_PORT = 139;

  public SmbFileNameParser() {
    super();
  }

  public static FileNameParser getInstance() {
    return INSTANCE;
  }

  @Override
  public FileName parseUri( final VfsComponentContext context, final FileName base, final String filename )
    throws FileSystemException {
    final StringBuilder name = new StringBuilder();

    // Extract the scheme and authority parts
    final Authority auth = extractToPath( context, filename, name );

    // extract domain
    String username = auth.getUserName();
    final String domain = extractDomain( username );
    if ( domain != null ) {
      username = username.substring( domain.length() + 1 );
    }

    // Decode and adjust separators
    UriParser.canonicalizePath( name, 0, name.length(), this );
    UriParser.fixSeparators( name );

    // Extract the share
    final String share = UriParser.extractFirstElement( name );
    if ( share == null || share.isEmpty() ) {
      throw new FileSystemException( "vfs.provider.smb/missing-share-name.error", filename );
    }

    // Normalise the path. Do this after extracting the share name,
    // to deal with things like smb://hostname/share/..
    final FileType fileType = UriParser.normalisePath( name );
    final String path = name.toString();

    return new SmbFileName( auth.getScheme(), auth.getHostName(), auth.getPort(), username, auth.getPassword(),
      domain, share, path, fileType );
  }

  private String extractDomain( final String username ) {
    if ( username == null ) {
      return null;
    }

    for ( int i = 0; i < username.length(); i++ ) {
      if ( username.charAt( i ) == '\\' ) {
        return username.substring( 0, i );
      }
    }

    return null;
  }

  /**
   * Extracts the hostname from a URI.
   *
   * @param name string buffer with the "scheme://[userinfo@]" part has been removed already. Will be modified.
   * @return the host name or null.
   */
  protected String extractHostName( final StringBuilder name ) {
    final int maxlen = name.length();
    int pos = 0;
    for ( ; pos < maxlen; pos++ ) {
      final char ch = name.charAt( pos );
      if ( ch == '/' || ch == ';' || ch == '?' || ch == ':' || ch == '@' || ch == '&' || ch == '=' || ch == '+'
        || ch == '$' || ch == ',' ) {
        break;
      }
    }
    if ( pos == 0 ) {
      return null;
    }

    final String hostname = name.substring( 0, pos );
    name.delete( 0, pos );
    return hostname;
  }

  /**
   * Extracts the port from a URI.
   *
   * @param name string buffer with the "scheme://[userinfo@]hostname" part has been removed already. Will be
   *             modified.
   * @param uri  full URI for error reporting.
   * @return The port, or -1 if the URI does not contain a port.
   * @throws FileSystemException   if URI is malformed.
   * @throws NumberFormatException if port number cannot be parsed.
   */
  protected int extractPort( final StringBuilder name, final String uri ) throws FileSystemException {
    if ( name.length() < 1 || name.charAt( 0 ) != ':' ) {
      return -1;
    }

    final int maxlen = name.length();
    int pos = 1;
    for ( ; pos < maxlen; pos++ ) {
      final char ch = name.charAt( pos );
      if ( ch < '0' || ch > '9' ) {
        break;
      }
    }

    final String port = name.substring( 1, pos );
    name.delete( 0, pos );
    if ( port.isEmpty() ) {
      throw new FileSystemException( "vfs.provider/missing-port.error", uri );
    }

    return Integer.parseInt( port );
  }

  /**
   * Extracts the user info from a URI.
   *
   * @param name string buffer with the "scheme://" part has been removed already. Will be modified.
   * @return the user information up to the '@' or null.
   */
  protected String extractUserInfo( final StringBuilder name ) {
    final int maxlen = name.length();
    for ( int pos = 0; pos < maxlen; pos++ ) {
      final char ch = name.charAt( pos );
      if ( ch == '@' ) {
        // Found the end of the user info
        final String userInfo = name.substring( 0, pos );
        name.delete( 0, pos + 1 );
        return userInfo;
      }
      if ( ch == '/' || ch == '?' ) {
        // Not allowed in user info
        break;
      }
    }

    // Not found
    return null;
  }

  /**
   * Extracts the scheme, userinfo, hostname and port components of a generic URI.
   *
   * @param context component context.
   * @param uri     The absolute URI to parse.
   * @param name    Used to return the remainder of the URI.
   * @return Authority extracted host authority, never null.
   * @throws FileSystemException if authority cannot be extracted.
   */
  protected Authority extractToPath( final VfsComponentContext context, final String uri, final StringBuilder name )
    throws FileSystemException {
    final Authority auth = new Authority();

    final FileSystemManager fsm;
    if ( context != null ) {
      fsm = context.getFileSystemManager();
    } else {
      fsm = VFS.getManager();
    }

    // Extract the scheme
    auth.scheme = UriParser.extractScheme( fsm.getSchemes(), uri, name );

    // Expecting "//"
    if ( name.length() < 2 || name.charAt( 0 ) != '/' || name.charAt( 1 ) != '/' ) {
      throw new FileSystemException( "vfs.provider/missing-double-slashes.error", uri );
    }
    name.delete( 0, 2 );

    // Extract userinfo, and split into username and password
    final String userInfo = extractUserInfo( name );
    final String userName;
    final String password;
    if ( userInfo != null ) {
      final int idx = userInfo.indexOf( ':' );
      if ( idx == -1 ) {
        userName = userInfo;
        password = null;
      } else {
        userName = userInfo.substring( 0, idx );
        password = userInfo.substring( idx + 1 );
      }
    } else {
      userName = null;
      password = null;
    }
    auth.userName = UriParser.decode( userName );
    auth.password = UriParser.decode( password );

    if ( auth.password != null && auth.password.startsWith( "{" ) && auth.password.endsWith( "}" ) ) {
      try {
        final Cryptor cryptor = CryptorFactory.getCryptor();
        auth.password = cryptor.decrypt( auth.password.substring( 1, auth.password.length() - 1 ) );
      } catch ( final Exception ex ) {
        throw new FileSystemException( "Unable to decrypt password", ex );
      }
    }

    // Extract hostname, and normalise (lowercase)
    final String hostName = extractHostName( name );
    if ( hostName == null ) {
      throw new FileSystemException( "vfs.provider/missing-hostname.error", uri );
    }
    auth.hostName = hostName.toLowerCase();

    // Extract port
    auth.port = extractPort( name, uri );

    // Expecting '/' or empty name
    if ( name.length() > 0 && name.charAt( 0 ) != '/' ) {
      throw new FileSystemException( "vfs.provider/missing-hostname-path-sep.error", uri );
    }

    return auth;
  }


  /**
   * Parsed authority info (scheme, hostname, username/password, port).
   */
  protected static class Authority {
    private String hostName;
    private String password;
    private int port;
    private String scheme;
    private String userName;

    /**
     * Gets the host name.
     *
     * @return the host name.
     * @since 2.0
     */
    public String getHostName() {
      return hostName;
    }

    /**
     * Gets the user password.
     *
     * @return the password or null.
     * @since 2.0
     */
    public String getPassword() {
      return password;
    }

    /**
     * Gets the port.
     *
     * @return the port or -1.
     * @since 2.0
     */
    public int getPort() {
      return port;
    }

    /**
     * Get the connection schema.
     *
     * @return the connection scheme.
     * @since 2.0
     */
    public String getScheme() {
      return scheme;
    }

    /**
     * Gets the user name.
     *
     * @return the user name or null.
     * @since 2.0
     */
    public String getUserName() {
      return userName;
    }

    /**
     * Sets the host name.
     *
     * @param hostName the host name.
     * @since 2.0
     */
    public void setHostName( final String hostName ) {
      this.hostName = hostName;
    }

    /**
     * Sets the user password.
     *
     * @param password the user password.
     * @since 2.0
     */
    public void setPassword( final String password ) {
      this.password = password;
    }

    /**
     * Sets the connection port.
     *
     * @param port the port number or -1.
     * @since 2.0
     */
    public void setPort( final int port ) {
      this.port = port;
    }

    /**
     * Sets the connection schema.
     *
     * @param scheme the connection scheme.
     * @since 2.0
     */
    public void setScheme( final String scheme ) {
      this.scheme = scheme;
    }

    /**
     * Sets the user name.
     *
     * @param userName the user name.
     * @since 2.0
     */
    public void setUserName( final String userName ) {
      this.userName = userName;
    }
  }
}
