import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;

public class AppApi {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon;

	private JFrame frame;
	private JTextField tagsField;
	private JTextField descriptionField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppApi window = new AppApi();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppApi() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());

		frame.setSize((3 * screenSize.width / 4), (3 * screenSize.height / 4));
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);

		frame.getContentPane().setLayout(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);

		JLabel imageLabel = new JLabel();
		imageLabel.setBounds(6, 79, 305, 372);
		frame.getContentPane().add(imageLabel);

		// Filechooser fc = new Filechooser();
		frame.getContentPane().add(fc);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					BufferedImage image = null;

					try {
						image = (BufferedImage) ImageIO.read(file);
						// displayImage(file.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					icon = scaleImage(file.getAbsolutePath(), imageLabel);
					imageLabel.setIcon(icon);
				}
			}
		});
		btnBrowse.setBounds(86, 30, 117, 29);
		frame.getContentPane().add(btnBrowse);
		
		JButton btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.setBounds(342, 30, 196, 29);
		frame.getContentPane().add(btnSearchForSimilar);
		
		JButton btnSaveFile = new JButton("Save file");
		btnSaveFile.setBounds(699, 30, 117, 29);
		frame.getContentPane().add(btnSaveFile);
		
		JButton btnHelp = new JButton("Help");
		btnHelp.setBounds(885, 6, 60, 29);
		frame.getContentPane().add(btnHelp);
		
		// textField for tags
		tagsField = new JTextField();
		tagsField.setBounds(352, 148, 186, 75);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);
		
		JLabel lblTags = new JLabel("Tags:");
		lblTags.setBounds(352, 99, 61, 16);
		frame.getContentPane().add(lblTags);
		
		// textField for description
		descriptionField = new JTextField();
		descriptionField.setBounds(360, 307, 178, 99);
		frame.getContentPane().add(descriptionField);
		descriptionField.setColumns(10);
		
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setBounds(352, 279, 77, 16);
		frame.getContentPane().add(lblDescription);
		
		// place for images from internet
		JLabel foundImagesLabel = new JLabel();
		foundImagesLabel.setBounds(569, 71, 352, 379);
		frame.getContentPane().add(foundImagesLabel);

	}

	protected ImageIcon scaleImage(String string1, JLabel label) {

		/** get height and width, if h > w set hScaledInstance = -1 usw. **/
		ImageIcon icon;
		int h = label.getHeight();
		int w = label.getWidth();
		if (h > w) {
			icon = new ImageIcon(
					new ImageIcon(string1).getImage().getScaledInstance(label.getWidth(), -1, Image.SCALE_FAST));
		} else if (w > h) {
			icon = new ImageIcon(
					new ImageIcon(string1).getImage().getScaledInstance(-1, label.getHeight(), Image.SCALE_FAST));

		} else {
			icon = new ImageIcon(new ImageIcon(string1).getImage().getScaledInstance(label.getWidth(),
					label.getHeight(), Image.SCALE_FAST));
		}
		return icon;
	}
}
