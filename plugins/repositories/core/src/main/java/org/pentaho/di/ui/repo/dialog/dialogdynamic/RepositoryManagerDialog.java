package org.pentaho.di.ui.repo.dialog.dialogdynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.json.simple.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.SwtUniversalImage;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.BaseRepositoryMeta;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.repo.controller.RepositoryConnectController;
import org.pentaho.di.ui.util.SwtSvgImageUtil;

public class RepositoryManagerDialog extends Dialog {

  public static final int MARGIN = 15;

  protected Shell dialog;
  protected Display display;

  protected Label lblHeader;
  protected Button btnAdd;
  protected PropsUI props;

  // Provides a function that can be called to refresh the list of repositories
  protected Runnable refreshList;

  // Provides a function that reverts the UI back to the repository list
  protected Runnable setToList;

  protected BiConsumer<String, JSONObject> setToEditor;

  protected Runnable saveAction;

  protected Map<String, RepositoryInfo> repositoryInfos;

  private static final Image LOGO = GUIResource.getInstance().getImageLogoSmall();

  private LogChannelInterface log =
    KettleLogStore.getLogChannelInterfaceFactory().create( RepositoryManagerDialog.class );

  public RepositoryManagerDialog( Shell parent ) {
    super( parent );
    props=PropsUI.getInstance();
  }

  public void open( int width, int height ) {

    display = getParent().getDisplay();

    dialog = new Shell( display, SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.DIALOG_TRIM );

    dialog.setSize( width, height );
    dialog.setImage( LOGO );
    props.setLook( dialog );

    // TODO: Change to BaseMessages
    dialog.setText( "Repository Manager" );

    FormLayout fl = new FormLayout();
    fl.marginHeight = MARGIN;
    fl.marginWidth = MARGIN;
    dialog.setLayout( fl );


    lblHeader = new Label( dialog, SWT.NONE );
    Font headerFont = new Font( display, Arrays.stream( lblHeader.getFont().getFontData() ).<FontData> map( fd -> {
      fd.setHeight( 24 );
      fd.setStyle( SWT.BOLD );
      return fd;
    } ).toArray( FontData[]::new ) );
    lblHeader.setFont( headerFont );
    lblHeader.addDisposeListener( d -> headerFont.dispose() );
    lblHeader.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( 0, 0 ).result() );
    props.setLook( lblHeader );




    Label lblSep = new Label( dialog, SWT.HORIZONTAL | SWT.SEPARATOR );
    lblSep.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( lblHeader, MARGIN ).height( 2 ).result() );
      props.setLook( lblSep );


    Button btnHelp = new Button( dialog, SWT.PUSH );
//    btnHelp.setImage( new Image(dialog.getDisplay(), "app/img/help.svg"));
    //TODO: BaseMessage
    btnHelp.setText( "Help" );
    btnHelp.setLayoutData( new FormDataBuilder().left( 0, 0 ).bottom( 100, 0 ).width( 80 ).height( 35 ).result() );
    props.setLook( btnHelp );
    btnHelp.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        //TODO: Call RepoConnectController.help();
      };
    } );


    Composite buttonsComposite = new Composite( dialog, SWT.NONE );
    buttonsComposite.setLayoutData( new FormDataBuilder().bottom( 100, 0 ).right( 100, 0 ).left( btnHelp, MARGIN ).height( 35 ).result() );
    StackLayout buttonStack = new StackLayout();
    buttonStack.marginHeight = 0;
    buttonStack.marginWidth = 0;
    buttonsComposite.setLayout( buttonStack );
    props.setLook( buttonsComposite );

    Composite closeComposite = new Composite( buttonsComposite, SWT.NONE );

    FormLayout closeLayout = new FormLayout();
    closeLayout.marginWidth = 0;
    closeLayout.marginHeight = 0;
    closeComposite.setLayout( closeLayout );
    props.setLook( closeComposite );


    Button btnClose = new Button( closeComposite, SWT.PUSH );
    btnClose.setLayoutData( new FormDataBuilder().bottom( 100, 0 ).right( 100, 0 ).top( 0, 0 ).width( 80 ).result() );
    props.setLook( btnClose );
    // TODO: Change to BaseMessages
    btnClose.setText( "Close" );
    dialog.setDefaultButton( btnClose );
    btnClose.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {
        dialog.dispose();
      }
    } );



    Composite editorButtonsComposite = new Composite( buttonsComposite, SWT.NONE );
    FormLayout flEditorButtons = new FormLayout();
    flEditorButtons.marginHeight = 0;
    flEditorButtons.marginWidth = 0;
    editorButtonsComposite.setLayout( flEditorButtons );
    props.setLook( editorButtonsComposite );


    Button btnCancel = new Button( editorButtonsComposite, SWT.PUSH );
    //TODO: BaseMessages
    btnCancel.setText( "Cancel" );
    btnCancel.setLayoutData( new FormDataBuilder().right( 100, 0 ).top( 0, 0 ).bottom( 100, 0 ).width( 80 ).result() );
    props.setLook( btnCancel );
    btnCancel.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        //Return back to the list view
        setToList.run();
      };
    });

    Button btnSave = new Button( editorButtonsComposite, SWT.PUSH );
    //TODO: BaseMessages
    btnSave.setText( "Save" );
    props.setLook( btnSave );
    btnSave.setLayoutData( new FormDataBuilder().right( btnCancel, -5 ).top(0, 0 ).bottom( 100, 0 ).width( 80 ).result() );
    btnSave.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        saveAction.run();
      };
    } );

    StackLayout sl = new StackLayout();

    // Stack Composite that will display various pages
    Composite stackComposite = new Composite( dialog, SWT.NONE );
    stackComposite.setLayout( sl );
    props.setLook( stackComposite );
    stackComposite.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( lblSep, Const.MARGIN )
        .bottom( buttonsComposite, -Const.FORM_MARGIN )
        .result() );
    stackComposite.setBackground( display.getSystemColor( SWT.COLOR_WHITE ) );


    Composite repoListComp = buildRepoListComposite( stackComposite );
    props.setLook( repoListComp );

    repositoryInfos = new LinkedHashMap<>();

    // TODO: Replace this with the code PluginRegistry.getInstance().getPlugins( RepositoryPluginType.class );,
    // we will no longer use RepositoryConnectController.getPlugins(),
    List<PluginInterface> repoPlugins = PluginRegistry.getInstance().getPlugins( RepositoryPluginType.class );

    /*
    repoPlugins.add( new PluginInterfaceAdapter( "KettleDatabaseRepository", "Database Repository", "db" ) );
    repoPlugins.add( new PluginInterfaceAdapter( "KettleFileRepository", "File Repository", "file" ) );
    repoPlugins.add(
      new PluginInterfaceAdapter( "PentahoEnterpriseRepository", "Pentaho Repository (Recommended)", "pentaho" ) );
*/
    // Sort the repositories in display order (Pentaho, File, Database), assign their editor composite, convert to
    // RepositoryInfo, and store in hash
    repositoryInfos =
      repoPlugins.stream()
        .sorted( Comparator.<PluginInterface, Integer> comparing( pi -> pi.getDescription().length() ).reversed() )
        .flatMap( pi -> {

          switch ( pi.getIds()[0] ) {
            case "PentahoEnterpriseRepository":
              return Stream.of(
                new RepositoryInfo( pi, new PentahoEnterpriseRepoFormComposite( stackComposite, SWT.NONE ) ) );
            // TODO: Add the proper composites for these
            case "KettleFileRepository":
              return Stream.of(
                new RepositoryInfo( pi, new KettleFileRepoFormComposite( stackComposite, SWT.NONE ) ) );
            case "KettleDatabaseRepository":
              return Stream.of( new RepositoryInfo( pi, new KettleDatabaseRepoFormComposite( stackComposite, SWT.NONE ) {
              } ) );
            default:
              return Stream.empty();
          }
        } ).collect( Collectors.toMap( ri -> ri.getId(), Function.identity(), ( r, i ) -> r, LinkedHashMap::new ) );

    SelectionListener menuSelListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        if ( e.widget.getData() != null && e.widget.getData() instanceof String ) {
          setToEditor.accept( (String) e.widget.getData(), null );
        }
      }
    };

    // Create menu items to drop down when a user presses the "Add" button
    Menu addPopUp = new Menu( btnAdd );
    for ( RepositoryInfo ri : repositoryInfos.values() ) {
      MenuItem mi = new MenuItem( addPopUp, SWT.MENU );
      mi.setText( ri.getName() );
      mi.setToolTipText( ri.getDescription() );
      mi.setData( ri.getId() );
      mi.addSelectionListener( menuSelListener );
    }

    btnAdd.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        Rectangle r = btnAdd.getBounds();
        Point p = btnAdd.getParent().toDisplay( r.x, r.y + r.height );
        addPopUp.setLocation( p );
        addPopUp.setVisible( true );
      }
    } );

    setToList = () -> {
      // TODO: Base Messages
      lblHeader.setText( "Repositories" );
      props.setLook( lblHeader );
      refreshList.run();

      buttonStack.topControl = closeComposite;
      //closeComposite.requestLayout();
      buttonsComposite.requestLayout();
      //buttonsComposite.pack();
      dialog.setDefaultButton( btnClose );

      sl.topControl = repoListComp;
      stackComposite.requestLayout();
      //stackComposite.pack();
    };

    setToEditor = ( type, data ) -> {
      RepositoryInfo ri = repositoryInfos.get( type );
      if ( ri != null ) {
        BaseRepoFormComposite repoComp = ri.getComposite();
        if ( data != null ) {
          //Editing
          saveAction = () -> onUpdateRepository( type, repoComp.toMap() );
          repoComp.populate( data );

        } else {
          //Creating
          saveAction = () -> onCreateRepository( type, repoComp.toMap() );
          repoComp.populate( new JSONObject() );
        }
        lblHeader.setText( ri.getName() );


        buttonStack.topControl = editorButtonsComposite;
        //editorButtonsComposite.requestLayout();
        buttonsComposite.requestLayout();
        //buttonsComposite.pack();
        dialog.setDefaultButton( btnSave );
        //Updates if the save button is active
        repoComp.updateSaveButton( btnSave );

        sl.topControl = repoComp;
        stackComposite.requestLayout();
        //stackComposite.pack();
      }
    };

    // Set to the list by default
    setToList.run();

    dialog.open();

    // TODO: Place this in the correct open area
    refreshList.run();

    while ( !dialog.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }

  }

  private Composite buildRepoListComposite( Composite parent ) {
    Composite comp = new Composite( parent, SWT.NONE );
    FormLayout fl = new FormLayout();
    fl.marginWidth = MARGIN;
    fl.marginHeight = MARGIN;
    comp.setLayout( fl );

    Composite btnComp = new Composite( comp, SWT.NONE );
    btnComp.setLayout( new FormLayout() );
    btnComp.setLayoutData( new FormDataBuilder().right( 100, 0 ).top( 0, 0 ).bottom( 100, 0 ).result() );
    props.setLook( btnComp );

    btnAdd = new Button( btnComp, SWT.PUSH );
    // TODO: BaseMessages
    btnAdd.setText( "Add" );
    btnAdd.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).result() );
    props.setLook( btnAdd );

    Button btnEdit = new Button( btnComp, SWT.PUSH );
    // TODO: BaseMessages
    btnEdit.setText( "Edit" );
    btnEdit.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( btnAdd, MARGIN ).result() );
    props.setLook( btnEdit );

    Button btnDelete = new Button( btnComp, SWT.PUSH );
    // TODO: BaseMessages
    btnDelete.setText( "Delete" );
    btnDelete.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( btnEdit, MARGIN ).result() );
    props.setLook( btnDelete );

    // Empty label to enforce a minimum width
    Label emptyLabel = new Label( btnComp, SWT.NONE );
    emptyLabel
      .setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( btnDelete, 0 ).width( 80 ).result() );
    props.setLook( emptyLabel );

    // TODO: PropsUI
    TableViewer repoList = new TableViewer( comp, SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION );
    repoList.getTable()
      .setLayoutData(
        new FormDataBuilder().left( 0, 0 ).right( btnComp, -MARGIN ).top( 0, 0 ).bottom( 100, 0 ).result() );
    repoList.setContentProvider( ArrayContentProvider.getInstance() );
    props.setLook( repoList.getControl() );

    TableViewerColumn repoColumn = new TableViewerColumn( repoList, SWT.NONE );
    repoColumn.getColumn().setWidth( 370 );

    Font repoNameFont =
      new Font( display, Arrays.stream( repoList.getTable().getFont().getFontData() ).<FontData> map( fd -> {
        fd.setHeight( 16 );
        return fd;
      } ).toArray( FontData[]::new ) );
    repoList.getTable().addDisposeListener( d -> repoNameFont.dispose() );

    repoColumn.setLabelProvider( new StyledCellLabelProvider() {

      public void update( ViewerCell cell ) {
        JSONObject o = (JSONObject) cell.getElement();
        String nameStr = (String) o.get( BaseRepositoryMeta.DISPLAY_NAME );
        Styler style = new Styler() {
          @Override
          public void applyStyles( TextStyle textStyle ) {
            textStyle.font = repoNameFont;
            textStyle.foreground = display.getSystemColor( SWT.COLOR_BLUE );
          }

        };
        StyledString name = new StyledString( nameStr + "\n", style );

        String description = (String) o.get( BaseRepositoryMeta.DESCRIPTION );
        if ( !Utils.isEmpty( description ) ) {
          int indexOf = description.indexOf( '\n' );
          if ( indexOf >= 0 ) {
            description = description.substring( 0, indexOf );
          }
        }

        name.append( description, StyledString.COUNTER_STYLER );
        cell.setText( name.toString() );
        cell.setStyleRanges( name.getStyleRanges() );
        super.update( cell );
      };

      protected void measure( Event event, Object element ) {
        super.measure( event, element );
        // There are better ways to measure this, but a constant should generally work
        event.height = 85;
      }
    } );

    // Load the image and render it for the default selected repository
    SwtUniversalImage swtImgCheck =
      SwtSvgImageUtil.getUniversalImage( dialog.getDisplay(), getClass().getClassLoader(), "SUC.svg" );
    final Image imgCheck = swtImgCheck.getAsBitmapForSize( dialog.getDisplay(), 30, 30 );
    repoList.getTable().addDisposeListener( d -> imgCheck.dispose() );

    TableViewerColumn defaultColumn = new TableViewerColumn( repoList, SWT.NONE );
    defaultColumn.getColumn().setWidth( 30 );
    defaultColumn.setLabelProvider( new ColumnLabelProvider() {

      @Override
      public String getText( Object element ) {
        return "";
      }

      @SuppressWarnings( "unchecked" )
      @Override
      public Image getImage( Object element ) {
        JSONObject o = (JSONObject) element;
        if ( (Boolean) o.getOrDefault( BaseRepositoryMeta.IS_DEFAULT, false ) ) {
          return imgCheck;
        } else {
          return super.getImage( element );
        }
      }

    } );

    // Listener for the edit button
    btnEdit.addSelectionListener( new SelectionAdapter() {
      @SuppressWarnings( "unchecked" )
      @Override
      public void widgetSelected( SelectionEvent e ) {
        IStructuredSelection sel = repoList.getStructuredSelection();
        if ( !sel.isEmpty() ) {
          JSONObject item = (JSONObject) sel.getFirstElement();
          String id = (String) item.getOrDefault( BaseRepositoryMeta.ID, "" );
          setToEditor.accept( id, item );
        }
      }
    } );

    // Listener for the delete button
    btnDelete.addSelectionListener( new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        IStructuredSelection sel = repoList.getStructuredSelection();
        if ( !sel.isEmpty() ) {
          JSONObject item = (JSONObject) sel.getFirstElement();
          String name = (String) item.getOrDefault( BaseRepositoryMeta.DISPLAY_NAME, "" );
          if ( !Utils.isEmpty( name ) ) {

            // TODO: Change to BaseMessages
            String deleteMessage = String.format( "Are you sure you wish to remove the '%s' repository?", name );

            MessageBox delBox = new MessageBox( dialog, SWT.ICON_WARNING | SWT.YES | SWT.NO );
            // TODO: Change to BaseMessages
            delBox.setText( "Delete Repository" );
            delBox.setMessage( deleteMessage );

            if ( SWT.YES == delBox.open() ) {
              onDeleteRepository( name );
              refreshList.run();
            }

          }
        }
      }
    } );

    ISelectionChangedListener scl = sce -> {
      boolean enabled = true;
      if ( repoList.getStructuredSelection().isEmpty() ) {
        enabled = false;
      } else {
        String id =
          (String) ( (JSONObject) repoList.getStructuredSelection().getFirstElement() ).get( BaseRepositoryMeta.ID );
        if ( !repositoryInfos.containsKey( id ) ) {
          enabled = false;
        }
      }

      btnEdit.setEnabled( enabled );
      btnDelete.setEnabled( enabled );
    };

    // Fire this once to disable things initially
    scl.selectionChanged( null );

    repoList.addPostSelectionChangedListener( scl );

    refreshList = () -> {

      // TODO: Call the RepoConnectController.getRepositories() here
      List<JSONObject> repos = new ArrayList<>();
      repos = RepositoryConnectController.getInstance().getRepositories();
      try {
      /*  repos.add( (JSONObject) new JSONParser().parse( "{\n" +
          "\"id\":\"PentahoEnterpriseRepository\",\n" +
          "\"displayName\":\"Pentaho Repo\",\n" +
          "\"description\":\"Pentaho Repository | http://localhost:8080\",\n" +
          "\"isDefault\":true,\n" +
          "\"url\":\"http://localhost:8080\"\n" +
          "}" ) );
        repos.add( (JSONObject) new JSONParser().parse( "{\n" +
          "\"id\":\"PentahoEnterpriseRepository\",\n" +
          "\"displayName\":\"Pentaho Repo 2\",\n" +
          "\"description\":\"Pentaho Repository | http://localhost:8081\",\n" +
          "\"isDefault\":false,\n" +
          "\"url\":\"http://localhost:8080\"\n" +
          "}" ) );
        repos.add( (JSONObject) new JSONParser().parse( "{\n" +
          "\"id\":\"KettleFileRepository\",\n" +
          "\"displayName\":\"File Repo\",\n" +
          "\"description\":\"File Repository\",\n" +
          "\"isDefault\":false,\n" +
          "\"location\":\"C:/Users/myuser/Desktop\",\n" +
          "\"doNotModify\": false,\n" +
          "\"showHiddenFolders\": true\n" +
          "}" ) );
        repos.add( (JSONObject) new JSONParser().parse( "{\n" +
          "\"id\":\"KettleDatabaseRepository\",\n" +
          "\"displayName\":\"Database Repo\",\n" +
          "\"description\":\"Database Repository\",\n" +
          "\"isDefault\":false,\n" +
          "\"databaseConnection\":\"repoConnection\"\n" +
          "}" ) );
*/
      } catch ( Exception e ) {
      }

      // Sets the input to the list of repositories
      repoList.setInput( repos );

    };
    return comp;
  }

  @SuppressWarnings( "unchecked" )
  private void onCreateRepository( String id, Map<String,Object> results ) {

    System.out.println("results :"+results);

    JSONObject j = new JSONObject();
    j.putAll( results );
    try{
      RepositoryConnectController.getInstance().createRepository(id,results);
      log.logBasic( "Repository: "+results.get( "displayName" )+ " creation successfully" );
    }
    catch ( Exception e) {
      log.logError( "Error creating repository", e );
    }
    //Switching back to the list view upon successful create
    setToList.run();
  }

  @SuppressWarnings( "unchecked" )
  private void onUpdateRepository( String id, Map<String,Object> results ) {

    System.out.println("results :"+results);

    JSONObject j = new JSONObject();
    j.putAll( results );

    try {
      RepositoryConnectController.getInstance()
        .updateRepository( id, results );
      log.logBasic( "Repository update successful");
    }
    catch ( Exception e) {
      log.logError( "Error updating repository", e );
    }
    //Switch back to the list view upon successful create
    setToList.run();
  }

  private void onDeleteRepository( String repoName ) {
    try {
      RepositoryConnectController.getInstance().deleteRepository( repoName );
      log.logBasic( "Repository delete successful" );
    }
    catch ( Exception e ){
      log.logError( "Error deleting repository ",e );
    }
    }


  protected static class RepositoryInfo {

    private final String id;
    private final String name;
    private final String description;
    private final BaseRepoFormComposite composite;

    @SuppressWarnings( "unchecked" )
    public RepositoryInfo( PluginInterface plugin, BaseRepoFormComposite composite ) {
      id = plugin.getIds()[0];
      name = plugin.getName();
      description = plugin.getDescription();
      this.composite = composite;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public BaseRepoFormComposite getComposite() {
      return composite;
    }

  }
}
