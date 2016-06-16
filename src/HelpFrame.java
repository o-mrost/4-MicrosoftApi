import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class HelpFrame extends JFrame{

	public HelpFrame() {
		
		setLayout(new BorderLayout());
		setSize(400, 400);
		
		Container c = getContentPane();
		
		JLabel label1 = new JLabel ("Beginning of the text");
		JLabel label2 = new JLabel ("Text continues");
		c.add(label1, BorderLayout.NORTH);
		c.add(label2, BorderLayout.CENTER);
		
		setVisible(true);
	}
}
