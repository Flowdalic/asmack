/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.harmony.auth.callback;

import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class DialogCallbackHandler implements CallbackHandler {
    
    public DialogCallbackHandler(){
        
    }
    
    public DialogCallbackHandler(Component parentComponent){
    }
    
    private static class DialogCallbackHandlerDialoge extends Dialog{
        
        private static final long serialVersionUID = -598231925400058563L;

        private Label nameLabel = new Label();
        
        private Label passwordLabel = new Label();
        
        private TextField nameText = new TextField("",16);
        
        private TextField passwordText = new TextField("",20);
        
        private Button okButton = new Button("OK");
        
        private Button cancelButton = new Button("Cancel");
        
        private NameCallback nameCallback;
        
        private PasswordCallback passwordCallback;
        
        public void exitDialog(){
            this.dispose();
        }
        
        public DialogCallbackHandlerDialoge(Callback[] callbacks) throws UnsupportedCallbackException{
            
            super((Frame)null,"Confirmation");
            this.setResizable(true);
            this.setBounds(320, 250, 350, 120);
            int layoutColumn = 1;
            okButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(nameCallback != null)
                        nameCallback.setName(nameText.getText());
                    if(passwordCallback != null)
                        passwordCallback.setPassword(passwordText.getText().toCharArray());
                    exitDialog();
                }
            });
            
            cancelButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    exitDialog();
                }
            });
            
            for(int i=0;i<callbacks.length;i++){
                if(callbacks[i] instanceof NameCallback){
                    nameCallback = (NameCallback)callbacks[i];
                    nameLabel.setText(nameCallback.getPrompt());
                    this.add(nameLabel);
                    this.add(nameText);
                    layoutColumn++;
                }
                else if(callbacks[i] instanceof PasswordCallback){
                    passwordCallback = (PasswordCallback)callbacks[i];
                    passwordLabel.setText(passwordCallback.getPrompt());
                    this.add(passwordLabel);
                    this.add(passwordText);
                    layoutColumn++;
                }
                else{
                    throw new UnsupportedCallbackException(callbacks[i]);
                }
            }
            passwordText.setEchoChar('*');
            this.add(okButton);
            this.add(cancelButton);
            this.setModal(true);
            this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e) {
                    
                    dispose();
                }
            });
            this.setLayout(new GridLayout(layoutColumn,1));
        }
    }

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        Dialog df = new DialogCallbackHandlerDialoge(callbacks);
        df.setModal(true);
        df.setVisible(true);
    }
}
