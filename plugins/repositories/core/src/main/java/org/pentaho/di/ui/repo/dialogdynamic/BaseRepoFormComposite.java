package org.pentaho.di.ui.repo.dialogdynamic;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.json.simple.JSONObject;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.BaseRepositoryMeta;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.PropsUI;

public abstract class BaseRepoFormComposite extends Composite {

  protected static final int MEDIUM_WIDTH = 300;
  protected static final int LABEL_CONTROL_MARGIN = 5;
  protected static final int CONTROL_MARGIN = 15;

  protected FormLayout layout;

  // Passed by the outer control
  protected Button btnSave;

  protected Text txtDisplayName;
  protected Text txtDescription;
  protected Button chkDefault;
  protected String originalName;
  protected PropsUI props;

  protected boolean changed = false;
  protected ModifyListener lsMod = ( e ) -> {
    changed = true;
    setSaveButtonEnabled();
  };

  public BaseRepoFormComposite( Composite parent, int style ) {
    super( parent, style );
    this.props = PropsUI.getInstance();

    props.setLook( this );

    layout = new FormLayout();
    setLayout( layout );

    Label lDispName = new Label( this, SWT.NONE );
    // TODO: BaseMessages
    lDispName.setText( "Display name" );
    lDispName.setLayoutData( new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).result() );
    props.setLook( lDispName );

    txtDisplayName = new Text( this, SWT.BORDER );
    props.setLook( txtDisplayName );
    txtDisplayName.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).top( lDispName, LABEL_CONTROL_MARGIN ).width( MEDIUM_WIDTH ).result() );
    txtDisplayName.addModifyListener( lsMod );

    Label lDescription = new Label( this, SWT.None );
    // TODO: BaseMessages
    lDescription.setText( "Description" );
    lDescription.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( uiAfterDisplayName(), CONTROL_MARGIN ).result() );
    props.setLook( lDescription );

    txtDescription = new Text( this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
    txtDescription.setLayoutData( new FormDataBuilder().left( 0, 0 ).top( lDescription, LABEL_CONTROL_MARGIN )
        .width( MEDIUM_WIDTH ).height( 100 ).result() );
    txtDescription.addModifyListener( lsMod );
    props.setLook( txtDescription );

    chkDefault = new Button( this, SWT.CHECK );
    // TODO: BaseMessages
    chkDefault.setText( "Launch connection on startup" );
    chkDefault.setLayoutData(
      new FormDataBuilder().left( 0, 0 ).right( 100, 0 ).top( txtDescription, CONTROL_MARGIN ).result() );
    props.setLook( chkDefault );
    chkDefault.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
        lsMod.modifyText( null );
      };
    } );


  }

  protected Control uiAfterDisplayName() {
    return txtDisplayName;
  }

  @SuppressWarnings( "unchecked" )
  public void populate( JSONObject source ) {
    String displayName = (String) source.getOrDefault( BaseRepositoryMeta.DISPLAY_NAME, "" );
    txtDisplayName.setText( displayName );
    originalName = displayName; // Store the originalName (in case this is an edit)

    txtDescription.setText( (String) source.getOrDefault( BaseRepositoryMeta.DESCRIPTION, "" ) );
    chkDefault.setSelection( (Boolean) source.getOrDefault( BaseRepositoryMeta.IS_DEFAULT, false ) );

  }

  public void updateSaveButton( Button btnSave ) {
    this.btnSave = btnSave;
    changed = false;
    setSaveButtonEnabled();
  }

  protected void setSaveButtonEnabled() {
    if ( btnSave != null ) {
      btnSave.setEnabled( changed && validateSaveAllowed() );
    }
  }

  protected boolean validateSaveAllowed() {
    return !Utils.isEmpty( txtDisplayName.getText() );
  }

  public Map<String, Object> toMap() {

    Map<String, Object> res = new HashMap<>();
    if ( !Utils.isEmpty( originalName ) ) {
      // TODO: Change this to RepoConnectController.ORIGINAL_NAME constant
      res.put( "originalName", originalName );
    }
    res.put( BaseRepositoryMeta.DISPLAY_NAME, txtDisplayName.getText() );
    res.put( BaseRepositoryMeta.DESCRIPTION, txtDescription.getText() );
    res.put( BaseRepositoryMeta.IS_DEFAULT, chkDefault.getSelection() );

    return res;
  }

}
