package org.pentaho.di.ui.repo.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.ui.repo.controller.RepositoryConnectController;

public class RepositoryManagerSWT extends Shell {

    private Display display;
    private Shell shell;

    public RepositoryManagerSWT( Shell shell, RepositoryConnectController controller ) {
        //  this.controller = controller;
        this.shell = shell;
        this.display = shell.getDisplay();
    }

    public void createDialog(RepositoryConnectController controller) {
        try {
        //    Display display = Display.getDefault();
            RepositoryManagerSWT shell = new RepositoryManagerSWT(display,controller);
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
    public RepositoryManagerSWT(Display display, RepositoryConnectController newcontroller) {
        super(display, SWT.SHELL_TRIM);

        TabFolder tabFolder = new TabFolder(this, SWT.NONE);
        tabFolder.setBounds(0, 10, 1001, 593);

        TabItem tbtmTab_create = new TabItem(tabFolder, SWT.NONE);
        tbtmTab_create.setText("   Create   ");


        TabItem tbtmTab_update = new TabItem(tabFolder, SWT.NONE);
        tbtmTab_update.setText("   Update   ");

        TabItem tbtmTab_delete = new TabItem(tabFolder, SWT.NONE);
        tbtmTab_delete.setText("   Delete   ");



        Label lblNewLabel_create = new Label(tabFolder, SWT.NONE);
        lblNewLabel_create.setText("create a repository");
        tbtmTab_create.setControl(lblNewLabel_create);



//        Group groupcreate = new Group(tabFolder, SWT.NONE);
        //group.setText("Group in Tab 2");
        this.setText("Group in Tab 2");
        Button button = new Button(this, SWT.NONE);
        button.setText("Button in Tab 2");
        button.setBounds(10, 50, 130, 30);

        Text text = new Text(this, SWT.BORDER);
        text.setText("Text in Tab 2");
        text.setBounds(10, 90, 200, 20);
  //      tbtmTab_create.setControl(groupcreate);

//----------------------------------------- create tab components start------------

        Label lblRepoName_create = new Label(this, SWT.NONE);
        lblRepoName_create.setBounds(37, 134, 240, 25);
        lblRepoName_create.setText("Repo name");
        tbtmTab_create.setControl(lblRepoName_create);

        Text text_create = new Text(this, SWT.BORDER);
        text_create.setBounds(37, 165, 297, 31);
        tbtmTab_create.setControl(text_create);

        Label lblRepoUrl_create = new Label(this, SWT.NONE);
        lblRepoUrl_create.setBounds(37, 218, 81, 25);
        lblRepoUrl_create.setText("Repo url");
        tbtmTab_create.setControl(lblRepoUrl_create);

        Text text_1_create = new Text(this, SWT.BORDER);
        text_1_create.setBounds(37, 249, 297, 31);
        tbtmTab_create.setControl(text_1_create);


        Label lblDescription_create = new Label(this, SWT.NONE);
        lblDescription_create.setBounds(37, 297, 174, 25);
        lblDescription_create.setText("Description");
        tbtmTab_create.setControl(lblDescription_create);

        Text text_2_create = new Text(this, SWT.BORDER);
        text_2_create.setBounds(37, 328, 297, 31);
        tbtmTab_create.setControl(text_2_create);

        Button btnCreate = new Button(this, SWT.NONE);
        btnCreate.setBounds(37, 388, 105, 35);
        btnCreate.setText("create");
        tbtmTab_create.setControl(btnCreate);

        //------------------------------------------------- create tab components ends --------





        Label lblNewLabel_update = new Label(tabFolder, SWT.NONE);
        tbtmTab_update.setControl(lblNewLabel_update);
        lblNewLabel_update.setText("update a repository");

        Label lblNewLabel_delete = new Label(tabFolder, SWT.NONE);
        tbtmTab_delete.setControl(lblNewLabel_delete);
        lblNewLabel_delete.setText("Delete repository");
    //    Button delbutton = new Button(tabFolder, SWT.NONE);
  //      delbutton.setText("del button");
//        tbtmTab_delete.setControl(delbutton);




        createContents();
    }

    /**
     * Create contents of the shell.
     */
    protected void createContents() {
        setText("SWT Application");
        setSize(1010, 646);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
