package controller;

import javax.swing.SwingUtilities;

import view.AuthView;

public class CDriver {
public static void main(String[] args) {
	
	SwingUtilities.invokeLater(new Runnable()
    {
		public void run() 
        {
        	
            new AuthView().show();
        }
	
    });
}
}
