package org.pentaho.di.ui.repo.dialog.dialogdynamic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.json.simple.JSONObject;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.BaseRepositoryMeta;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.PropsUI;

import java.util.Map;

public class KettleFileRepoFormComposite extends BaseRepoFormComposite {

  protected Text txtLocation;
  protected  Button showHidden;
  protected  Button doNotModify;


  public KettleFileRepoFormComposite( Composite parent, int style )
  {
    super( parent, style );
  }
  
  @Override
  protected Control uiAfterDisplayName() {
    PropsUI props = PropsUI.getInstance();

    Label lLoc = new Label( this, SWT.NONE );
    lLoc.setText( "Location" );
    lLoc.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( txtDisplayName, CONTROL_MARGIN ).result() );
    props.setLook( lLoc );

    txtLocation = new Text( this, SWT.BORDER );
    txtLocation.setLayoutData( new FormDataBuilder().left( 0, 0 ).top( lLoc, LABEL_CONTROL_MARGIN ).width( MEDIUM_WIDTH ).result() );
    txtLocation.addModifyListener( lsMod );
    props.setLook( txtLocation );

    Button browseBtn = new Button( this,SWT.PUSH );
    // TODO: BaseMessages
    browseBtn.setText( "Browse" );
    browseBtn.setLayoutData( new FormDataBuilder().left( txtLocation, LABEL_CONTROL_MARGIN ).top( lLoc, LABEL_CONTROL_MARGIN ).result() );
    props.setLook( browseBtn );



  browseBtn.addSelectionListener( new SelectionAdapter() {
    @Override public void widgetSelected( SelectionEvent selectionEvent ) {
      System.out.println("browse button clicked");

      DirectoryDialog dialog = new DirectoryDialog(getShell());
      dialog.setFilterPath("c:\\"); // Windows specific
      String selectedDir= dialog.open();
      if ( !Utils.isEmpty( selectedDir ) ) {
        txtLocation.setText( selectedDir );
      }
      else{
        MessageBox messageBox = new MessageBox( getParent().getShell(), SWT.OK |
          SWT.ICON_ERROR | SWT.CANCEL );
        messageBox.setMessage( " select a directory" );
        messageBox.open();
      }
    }
  } );
    doNotModify = new Button( this, SWT.CHECK );
    // TODO: BaseMessages
    doNotModify.setText( "Do not modify items in this location" );
    doNotModify.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).top( txtLocation, CONTROL_MARGIN ).result() );
    props.setLook( doNotModify );
    doNotModify.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
        lsMod.modifyText( null );
      };
    } );

    showHidden = new Button( this, SWT.CHECK );
    // TODO: BaseMessages
    showHidden.setText( "Show hidden files" );
    showHidden.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).top( doNotModify, CONTROL_MARGIN ).result() );
    props.setLook( showHidden );
    showHidden.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
        lsMod.modifyText( null );
      };
    } );

    return showHidden;
  }

  @Override
  public Map<String,Object> toMap() {
    Map<String,Object> ret = super.toMap();
    
    //TODO: Change to PurRepositoryMeta.REPOSITORY_TYPE_ID
    ret.put( BaseRepositoryMeta.ID, "KettleFileRepository" );
    //TODO: Change to PurRepositoryMeta.URL
    ret.put( KettleFileRepositoryMeta.LOCATION, txtLocation.getText() );
    ret.put( KettleFileRepositoryMeta.SHOW_HIDDEN_FOLDERS,showHidden.getSelection() );
    ret.put( KettleFileRepositoryMeta.DO_NOT_MODIFY,doNotModify.getSelection());

    return ret;
  }
  
  @SuppressWarnings( "unchecked" )
  @Override
  public void populate( JSONObject source ) {
    super.populate( source );
    txtLocation.setText( (String) source.getOrDefault( KettleFileRepositoryMeta.LOCATION, "" ) );
    showHidden.setSelection( (Boolean) source.getOrDefault( KettleFileRepositoryMeta.SHOW_HIDDEN_FOLDERS, false ) );
    doNotModify.setSelection( (Boolean) source.getOrDefault( KettleFileRepositoryMeta.DO_NOT_MODIFY, false ) );
  }
  
  @Override
  protected boolean validateSaveAllowed() {
    return super.validateSaveAllowed() && !Utils.isEmpty( txtLocation.getText() );
  }

}
