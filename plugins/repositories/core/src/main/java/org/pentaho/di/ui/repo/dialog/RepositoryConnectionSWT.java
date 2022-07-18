package org.pentaho.di.ui.repo.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.repo.controller.RepositoryConnectController;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.function.Supplier;

public class RepositoryConnectionSWT extends Shell {

    private LogChannelInterface log =
            KettleLogStore.getLogChannelInterfaceFactory().create( RepositoryConnectionSWT.class );

    private static Class<?> PKG = RepositoryConnectionSWT.class;


    private Text txt_username;
    private Text txt_passwd;
    private String str_repoName;
    private String str_username;
    private String str_passwd;
    //private RepositoryConnectController controller;
    private Shell shell;
    private Display display;
    private static final Image LOGO = GUIResource.getInstance().getImageLogoSmall();
    //private static final String MANAGER_TITLE = BaseMessages.getString( PKG, "RepositoryDialog.Dialog.Manager.Title" );
    private static final String LOGIN_TITLE = BaseMessages.getString( PKG, "RepositoryDialog.Dialog.Login.Title" );

    private Supplier<Spoon> spoonSupplier;
    public static final String HELP_URL =
            Const.getDocUrl( BaseMessages.getString( PKG, "RepositoryDialog.Dialog.Help" ) );
    //public static final String HELP_URL ="Products/Use_a_Pentaho_Repository_in_PDI";

    public RepositoryConnectionSWT(Shell shell, RepositoryConnectController controller ) {
      //  this.controller = controller;
        this.shell = shell;
        this.display = shell.getDisplay();
    }


    public void createDialog(RepositoryConnectController controller) {
        try {
//            Display display = Display.getDefault();
            System.out.println("create dialog swt try block");

            RepositoryConnectionSWT shell = new RepositoryConnectionSWT(display,controller);
            shell.open();
            shell.layout();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the shell.
     * @param display
     */
    public RepositoryConnectionSWT(Display display, RepositoryConnectController newcontroller) {
        super(display, SWT.SHELL_TRIM);


        System.out.println("in conn manager rcvd selected repos: "+display.getData().toString());
        this.str_repoName = display.getData().toString();
        // reponame to repo url
        /*RepositoryConnectController repocontroller =  new RepositoryConnectController();
         repo_name = repocontroller.getRepository(display.getData().toString());
        System.out.println("got repo name : "+repo_name);
        //JSONObject obj = new JSONObject(repo_url);
        Object obj= JSONValue.parse(repo_name);
        JSONObject jsonObject = (JSONObject) obj;
        repo_name = (String) jsonObject.get("displayName");

        System.out.println("after json processing :"+repo_name);
*/
        //setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));

        Label lblRepositoryConnection = new Label(this, SWT.CENTER);
        lblRepositoryConnection.setText("Repository Connection");
       // lblRepositoryConnection.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
        lblRepositoryConnection.setBounds(249, 10, 320, 42);


        Label lblConnectTo = new Label(this, SWT.NONE);
        lblConnectTo.setBounds(52, 96, 121, 25);
        lblConnectTo.setText("Connect to :");

        Label lblRepoName = new Label(this, SWT.NONE);
        lblRepoName.setBounds(52, 140, 439, 25);
        //lblRepoName.setText(processed_url);
        lblRepoName.setText(str_repoName);

        Label lblUserName = new Label(this, SWT.NONE);
        lblUserName.setBounds(52, 186, 109, 25);
        lblUserName.setText("User name:");

        txt_username = new Text(this, SWT.BORDER);
        txt_username.setBounds(52, 217, 357, 31);

        Label lblPassword = new Label(this, SWT.NONE);
        lblPassword.setBounds(52, 273, 81, 25);
        lblPassword.setText("Password:");

        txt_passwd = new Text(this, SWT.BORDER);
        txt_passwd.setBounds(52, 304, 357, 31);

        Button btnConnect_1 = new Button(this, SWT.NONE);
        btnConnect_1.setBounds(52, 363, 105, 35);
        btnConnect_1.setText("login");

//******************** HELP **********************************
        Button btnHelp = new Button(this, SWT.ICON_INFORMATION);
        btnHelp.setBounds(599, 611, 105, 35);
        btnHelp.setText("help");
        btnHelp.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                System.out.println("help button clicked");
                System.out.println("help url :"+HELP_URL);
                Program.launch( HELP_URL );

              }
        });


        createContents();

        btnConnect_1.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                System.out.println("button pressed");

                str_username=txt_username.getText();
                str_passwd=txt_passwd.getText();
                //System.out.println("processed_repo_name :"+repo_name);
                //str_repoURL = repo_name;

                System.out.println("rcvd url, id and password");

                System.out.println("rcvd reponame :"+str_repoName);
                System.out.println("rcvd username :"+str_username);
                System.out.println("rcvd password :"+str_passwd);

                if(str_repoName.isEmpty()){
                    System.out.println("blank ip reponame");

                }
                if(str_username.isEmpty()){
                    System.out.println("blank ip username");

                }
                if(str_passwd.isEmpty()){
                    System.out.println("blank password");

                }
                else{
                    System.out.println("not blank ip username and password");
                    callLoginEndPoint(str_repoName, str_username, str_passwd,newcontroller);

                }
            }
        });

    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText(LOGIN_TITLE);
        setSize(739, 707);
        setImage(LOGO);

    }

    void callLoginEndPoint(String str_repoName, String str_username, String str_passwd, RepositoryConnectController controller) {

        System.out.println("login end points called:");
        System.out.println("repo name : "+str_repoName);
        System.out.println("username : "+ str_username);
        System.out.println("password : "+str_passwd);
        try {
            System.out.println("try block controller connect to repo");
//            RepositoryConnectController newcontroller = new RepositoryConnectController();

        //below logic should be replacing active logic //do not delete
            /*if ( controller.isRelogin() ) {
                System.out.println("here in relogin");
                controller
                        .reconnectToRepository( repositoryName, repositoryName, password );
            } else {
                System.out.println("here in login");
                controller
                        .connectToRepository(repositoryName, repositoryName, password );
            }*/

            controller
                    .connectToRepository(str_repoName, str_username, str_passwd);

            System.out.println("repo connection successful");
            getShell().close();
        }
        catch (Exception e){
            System.out.println(e);
            System.out.println("catch block of repoendpoints");
        }
        System.out.println("login end points calls ended");
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
