import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageInTooltip {

	public static void main(String[] args) {

		JFrame frame = new JFrame();
		JLabel label = new JLabel("Label with image in Tooltip!");
		label.setToolTipText("<html><img src=\"" + ImageInTooltip.class.getResource("1.jpg") + "\">");
		label.setHorizontalAlignment(JLabel.CENTER);
		frame.setContentPane(label);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 200, 100);
		frame.setVisible(true);

	}
}