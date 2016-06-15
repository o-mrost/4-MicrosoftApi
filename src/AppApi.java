import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gson.Captions;
import gson.JsonRoot;

public class AppApi {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon;
	private JFrame frame;
	private JTextArea tagsField;
	private JTextArea descriptionField;
	private JTextField textField;
	private JLabel imageLabel;
	private JButton btnTakePicture;
	private JButton btnBrowse;
	private JButton btnSaveFile;
	private JButton btnHelp;
	private JLabel lblTags;
	private JLabel lblDescription;
	private JLabel foundImagesLabel;
	private JLabel lblImageFromWebcam;

	private BufferedImage imgFromCam = null;

	private String link = "https://i.kinja-img.com/gawker-media/image/upload/s--iIUOalRV--/c_scale,fl_progressive,q_80,w_800/qmehyq7rssourraj0au1.png";
	private String url = "{'url':'" + link + "'}";

	private HttpDescribe httpQueryDescribe = new HttpDescribe();
	private TagsToken tokenCache = new TagsToken();
	private String token = tokenCache.getApiToken();
	private ImageSearchToken searchToken = new ImageSearchToken();
	private HttpSearch newSearch = new HttpSearch();
	private String searchTokenApi = searchToken.getApiToken();

	String[] tags;
	String text;

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

		// set size of frame to 3/4 of screen size
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setSize((3 * screenSize.width / 4), (3 * screenSize.height / 4));
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		frame.getContentPane().add(fc);

		imageLabel = new JLabel();
		imageLabel.setBounds(23, 109, 305, 342);
		frame.getContentPane().add(imageLabel);

		btnTakePicture = new JButton("Take a picture with webcam");
		btnTakePicture.setBounds(30, 46, 212, 29);
		frame.getContentPane().add(btnTakePicture);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(79, 18, 117, 29);
		frame.getContentPane().add(btnBrowse);

		JButton btnAnalyse = new JButton("Analyse image");
		btnAnalyse.setBounds(361, 30, 196, 29);
		frame.getContentPane().add(btnAnalyse);

		tagsField = new JTextArea();
		tagsField.setBounds(352, 107, 205, 116);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);

		textField = new JTextField("Paste URL: ");
		textField.setBounds(22, 79, 220, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		btnSaveFile = new JButton("Save file");
		btnSaveFile.setBounds(828, 499, 117, 29);
		frame.getContentPane().add(btnSaveFile);

		btnHelp = new JButton("Help");
		btnHelp.setBounds(894, 0, 60, 29);
		frame.getContentPane().add(btnHelp);

		lblTags = new JLabel("Tags:");
		lblTags.setBounds(352, 79, 61, 16);
		frame.getContentPane().add(lblTags);

		descriptionField = new JTextArea();
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		descriptionField.setBounds(352, 263, 205, 43);
		frame.getContentPane().add(descriptionField);
		descriptionField.setColumns(10);

		lblDescription = new JLabel("Description:");
		lblDescription.setBounds(352, 235, 77, 16);
		frame.getContentPane().add(lblDescription);

		// place for images from internet
		foundImagesLabel = new JLabel();
		foundImagesLabel.setBounds(569, 71, 352, 379);
		frame.getContentPane().add(foundImagesLabel);

		lblImageFromWebcam = new JLabel();
		lblImageFromWebcam.setBounds(297, 79, -286, 384);
		frame.getContentPane().add(lblImageFromWebcam);

		lblImageFromWebcam.setIcon(icon);
		// create Label display returned BufferedImage, create Buttons (Use
		// Image / take new image)

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFilechooser();
			}
		});

		btnTakePicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				takePicture();
			}
		});

		btnAnalyse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				analyse();
			}

		});

	}

	protected void analyse() {
		
		String response = httpQueryDescribe.describeImageFromLink(url, token);

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JsonRoot root = gson.fromJson(response, JsonRoot.class);
		String[] tags = root.getDescription().getTags();
		System.out.println("=============");
		System.out.println("tags " + Arrays.toString(tags));

		for (String element : tags) {
			tagsField.append(element + "\n");
		}

		for (Captions currentCaption : root.getDescription().getCaptions()) {
			text = currentCaption.getText();
			System.out.println("caption: " + text);
			System.out.println("=============");
			descriptionField.setText(text);
		}
		searchForSimilarImages(searchTokenApi);
	}
	
	protected void takePicture() {

		WebcamAPI cam = new WebcamAPI();
		try {
			imgFromCam = cam.getPicture();
			System.out.println("taken image");
			// here null pointer exception
			icon = scaleImage(imgFromCam, lblImageFromWebcam);
			System.out.println("icon set");
			// doesn't work yet

			// return bufferedImage
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	protected void openFilechooser() {

		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			BufferedImage image = null;

			try {
				image = (BufferedImage) ImageIO.read(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			icon = scaleImage(file.getAbsolutePath(), imageLabel);
			imageLabel.setIcon(icon);
		}
	}

	

	protected void searchForSimilarImages(String searchToken) {

		System.out.println("search token =" + searchTokenApi + "=");
		String url222 = "https://bingapis.azure-api.net/api/v5/images/search?q=cats&count=4&offset=0&mkt=en-us&safeSearch=Moderate";

		newSearch.GetUrlContentAsString(searchToken);
		System.out.println("searched");
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

	protected ImageIcon scaleImage(BufferedImage img, JLabel label) {

		/** get height and width, if h > w set hScaledInstance = -1 usw. **/
		ImageIcon icon;
		int h = label.getHeight();
		int w = label.getWidth();
		if (h > w) {
			icon = new ImageIcon(
					new ImageIcon(img).getImage().getScaledInstance(label.getWidth(), -1, Image.SCALE_FAST));
		} else if (w > h) {
			icon = new ImageIcon(
					new ImageIcon(img).getImage().getScaledInstance(-1, label.getHeight(), Image.SCALE_FAST));

		} else {
			icon = new ImageIcon(new ImageIcon(img).getImage().getScaledInstance(label.getWidth(), label.getHeight(),
					Image.SCALE_FAST));
		}
		return icon;
	}
}
