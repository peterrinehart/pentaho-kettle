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


package org.pentaho.di.ui.spoon;

import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.xul.swt.tab.TabItem;

public class TabMapEntry {
  public enum ObjectType {
    TRANSFORMATION_GRAPH, JOB_GRAPH, SLAVE_SERVER, BROWSER, PLUGIN,
  }

  private TabItem tabItem;

  private String filename;

  private String objectName;

  private RepositoryDirectoryInterface repositoryDirectory;

  private String connection;

  private String versionLabel;

  private TabItemInterface object;

  private ObjectType objectType;

  private boolean showingLocation;

  /**
   * @param tabName
   * @param objectName
   * @param objectType
   * @param object
   */
  public TabMapEntry( TabItem tabItem, String filename, String objectName,
                      RepositoryDirectoryInterface repositoryDirectory, String versionLabel, TabItemInterface object,
                      ObjectType objectType, String connection ) {
    this.tabItem = tabItem;
    this.filename = filename;
    this.objectName = objectName;
    this.repositoryDirectory = repositoryDirectory;
    this.versionLabel = versionLabel;
    this.object = object;
    this.objectType = objectType;
    this.connection = connection;
  }

  /**
   * @param tabName
   * @param objectName
   * @param objectType
   * @param object
   */
  public TabMapEntry( TabItem tabItem, String filename, String objectName,
    RepositoryDirectoryInterface repositoryDirectory, String versionLabel, TabItemInterface object,
    ObjectType objectType ) {
    this( tabItem, filename, objectName, repositoryDirectory, versionLabel, object, objectType, null );
  }

  public boolean equals( Object obj ) {
    TabMapEntry entry = (TabMapEntry) obj;

    boolean sameType = objectType.equals( entry.objectType );
    boolean sameName = objectName != null && entry.objectName != null && objectName.equals( entry.objectName );
    boolean sameFile =
      ( filename == null && entry.filename == null ) || ( filename != null && filename.equals( entry.filename ) );
    boolean sameVersion =
      ( versionLabel == null && entry.versionLabel == null )
        || ( versionLabel != null && versionLabel.equals( entry.versionLabel ) );
    boolean sameDirectory =
      ( repositoryDirectory == null && entry.repositoryDirectory == null )
        || ( repositoryDirectory != null && entry.repositoryDirectory != null && repositoryDirectory
          .getPath().equals( entry.repositoryDirectory.getPath() ) );

    return sameType && sameName && sameVersion && sameFile && sameDirectory;
  }

  /**
   * @return the objectName
   */
  public String getObjectName() {
    return objectName;
  }

  /**
   * @param objectName
   *          the objectName to set
   */
  public void setObjectName( String objectName ) {
    this.objectName = objectName;
  }

  public String getVersionLabel() {
    return versionLabel;
  }

  public void setVersionLabel( String versionLabel ) {
    this.versionLabel = versionLabel;
  }

  /**
   * @return the object
   */
  public TabItemInterface getObject() {
    return object;
  }

  /**
   * @param object
   *          the object to set
   */
  public void setObject( TabItemInterface object ) {
    this.object = object;
  }

  /**
   * @return the tabItem
   */
  public TabItem getTabItem() {
    return tabItem;
  }

  /**
   * @param tabItem
   *          the tabItem to set
   */
  public void setTabItem( TabItem tabItem ) {
    this.tabItem = tabItem;
  }

  /**
   * @return the objectType
   */
  public ObjectType getObjectType() {
    return objectType;
  }

  /**
   * @param objectType
   *          the objectType to set
   */
  public void setObjectType( ObjectType objectType ) {
    this.objectType = objectType;
  }

  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * @param filename
   *          the filename to set
   */
  public void setFilename( String filename ) {
    this.filename = filename;
  }

  /**
   * @return the repositoryDirectory
   */
  public RepositoryDirectoryInterface getRepositoryDirectory() {
    return repositoryDirectory;
  }

  /**
   * @param repositoryDirectory
   *          the repositoryDirectory to set
   */
  public void setRepositoryDirectory( RepositoryDirectoryInterface repositoryDirectory ) {
    this.repositoryDirectory = repositoryDirectory;
  }

  /**
   * @return the showingLocation
   */
  public boolean isShowingLocation() {
    return showingLocation;
  }

  /**
   * @param showingLocation
   *          the showingLocation to set
   */
  public void setShowingLocation( boolean showingLocation ) {
    this.showingLocation = showingLocation;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection( String connection ) {
    this.connection = connection;
  }
}
