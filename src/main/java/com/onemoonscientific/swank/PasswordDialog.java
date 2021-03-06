package com.onemoonscientific.swank;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author brucejohnson
 */
public class PasswordDialog extends JDialog implements ActionListener, KeyListener {

    /**
     *
     */
    protected JTextField nameField;
    /**
     *
     */
    protected JPasswordField passwordField;
    /**
     *
     */
    protected JButton okButton;
    /**
     *
     */
    protected JButton cancelButton;
    /**
     *
     */
    protected JLabel nameLabel;
    /**
     *
     */
    protected JLabel passwordLabel;
    /**
     *
     */
    protected JLabel msgLabel;
    /**
     *
     */
    protected JPanel buttonPanel;

    protected String userName = "";
    protected String msg = "";

    @Override
    public void setName(String name) {
        this.nameField.setText(name);
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    /**
     *
     * @param pass
     */
    public void setPassword(String pass) {
        this.passwordField.setText(pass);
    }

    /**
     *
     * @return
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void setMsg(String msg) {
        this.msgLabel.setText(msg);
    }

    /**
     *
     * @return
     */
    public boolean okPressed() {
        return okButtonPressed;
    }
    private boolean okButtonPressed = false;

    /**
     *
     * @param parent
     * @param title
     */
    public PasswordDialog(Frame parent, String title) {
        super(parent, title, true);
        
    }

    //Handle clicks on the Set and Cancel buttons.
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            okButtonPressed = true;
        }
        PasswordDialog.this.setVisible(false);

    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            PasswordDialog.this.setVisible(false);
        } else {
            if ((e.getSource() == passwordField) || (e.getSource() == okButton)) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okButtonPressed = true;
                    PasswordDialog.this.setVisible(false);
                } else {
                    if (e.getSource() == passwordField) {
                        okButton.setEnabled(true);
                    }
                }
            }
        }
    }

    private void layoutPanel(JPanel buttonPanel) {
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        JPanel mainPanel = new JPanel(gridbag);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));

        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridy = 0;

        gridbag.setConstraints(msgLabel, c);
        mainPanel.add(msgLabel);

        c.gridwidth = 1;
        c.gridy = 1;

        c.anchor = GridBagConstraints.EAST;
        gridbag.setConstraints(nameLabel, c);
        gridbag.setConstraints(nameField, c);

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);

        c.gridy = 2;
        gridbag.setConstraints(passwordLabel, c);
        gridbag.setConstraints(passwordField, c);

        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        gridbag.setConstraints(buttonPanel, c);
        mainPanel.add(buttonPanel);

        getContentPane().add(mainPanel);
        pack();
    }

    private void makeControls() {
        msgLabel = new JLabel("                       ");
        nameLabel = new JLabel("Name     ");
        nameField = new JTextField("", 20);
        nameField.addKeyListener(this);

        passwordLabel = new JLabel("Password ");
        passwordField = new JPasswordField("", 20);
        passwordField.addKeyListener(this);

        okButton = new JButton("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(this);
        okButton.addKeyListener(this);
        okButton.setActionCommand("OK");

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
    }

    @Override
    protected void dialogInit() {
        makeControls();
        super.dialogInit();
        layoutPanel(buttonPanel);
    }

    /**
     *
     * @return
     */
    public boolean showDialog() {
        setVisible(true);
        return okPressed();
    }
    /**
     *
     * @return
     */
    public boolean showDialog(final int x, final int y) {
        setLocation(x,y);
        setVisible(true);
        return okPressed();
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        PasswordDialog p = new PasswordDialog(null, "Test");
        if (p.showDialog()) {
            System.out.println("Name: " + p.getName());
            System.out.println("Pass: " + new String(p.getPassword()));
        } else {
            System.out.println("User selected cancel");
        }
    }
}
