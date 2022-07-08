package org.pentaho.di.ui.repo.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;

public class CreateRepoManager extends Shell {
	private Text text;
	private Text text_1;
	private Text text_2;

	public CreateRepoManager() {

		System.out.println("default constructor called");
	}
	/**
	 * Launch the application.
	 * @param
	 */
	public  void createArepoManager() {
		System.out.println("method called createArepoManager");
		try {
			Display display = Display.getDefault();
			CreateRepoManager shell = new CreateRepoManager(display);
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
	public CreateRepoManager(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		Label lblRepoName = new Label(this, SWT.NONE);
		lblRepoName.setBounds(37, 134, 240, 25);
		lblRepoName.setText("Repo name");
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(37, 165, 297, 31);
		
		Label lblRepoUrl = new Label(this, SWT.NONE);
		lblRepoUrl.setBounds(37, 218, 81, 25);
		lblRepoUrl.setText("Repo url");
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(37, 249, 297, 31);
		
		Label lblDescription = new Label(this, SWT.NONE);
		lblDescription.setBounds(37, 297, 174, 25);
		lblDescription.setText("Description");
		
		text_2 = new Text(this, SWT.BORDER);
		text_2.setBounds(37, 328, 297, 31);
		
		Button btnCreate = new Button(this, SWT.NONE);
		btnCreate.setBounds(37, 388, 105, 35);
		btnCreate.setText("create");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(1071, 634);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
