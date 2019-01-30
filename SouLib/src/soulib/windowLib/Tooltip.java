package soulib.windowLib;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Tooltip{
	public JDialog frame;
	private JLabel label;
	public Tooltip() {
		this(null);
	}
	public Tooltip(Window owner) {
		frame=new JDialog(owner);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setVisible(false);
		frame.setAlwaysOnTop(true);
		label=new JLabel();
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				int keycode=e.getKeyCode();
				if (keycode==KeyEvent.VK_C&&e.isControlDown()){
					try{
						Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents( new StringSelection(label.getText()),null);
					}catch(Exception ex) {

					}
				}
			}
		});
		JPanel labelPanel = new JPanel();
		labelPanel.add(label);
		frame.getContentPane().add(labelPanel, BorderLayout.PAGE_START);
	}
	public void show(int x,int y,String text) {
		if(frame.isVisible())return;
		frame.setLocation(x,y);
		label.setText(text);
		setVisible(true);
		frame.pack();
	}
	public void hide() {
		setVisible(false);
	}
	public void setVisible(boolean b) {
		frame.setVisible(b);
	}
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
}
