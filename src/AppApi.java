import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

import bing.Data;
import bing.RootBing;
import gson.Captions;
import gson.JsonRoot;
import net.coobird.thumbnailator.Thumbnails;

public class AppApi {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon, iconFromInternet;
	private JFrame frame, helpFrame;
	private JTextArea tagsField, descriptionField;
	private JTextField urlField;
	private JButton btnTakePicture, btnBrowse, btnSaveFile, btnHelp, btnSearchForSimilar;
	private JLabel imageLabel, lblTags, lblDescription, foundImagesLabel, lblImageFromWebcam, helpLabel;

	private BufferedImage imgFromCam = null;
	private BufferedImage image = null;

	private HttpDescribeLocal httpLocal = new HttpDescribeLocal();
	private HttpBing httpBingSearch = new HttpBing();

	private TagsToken tokenCache = new TagsToken();
	private String token = tokenCache.getApiToken();
	private ImageSearchToken searchToken = new ImageSearchToken();
	private String searchTokenApi = searchToken.getApiToken();

	String link, url, text, contentUrl, tagsString = "", searchParameters;
	String[] tags;

	int numberOfTags;

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

		JButton btnAnalyse = new JButton("    Analyse image");
		ImageIcon analyseIcon = new ImageIcon("img/cloud-icon.png");
		btnAnalyse.setIcon(analyseIcon);
		btnAnalyse.setBounds(361, 30, 196, 29);
		frame.getContentPane().add(btnAnalyse);

		tagsField = new JTextArea();
		tagsField.setBounds(352, 107, 205, 116);
		tagsField.setLineWrap(true);
		tagsField.setWrapStyleWord(true);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);

		btnSaveFile = new JButton("Save file");
		btnSaveFile.setBounds(828, 499, 117, 29);
		frame.getContentPane().add(btnSaveFile);

		btnHelp = new JButton("");
		btnHelp.setBorderPainted(false);
		ImageIcon btnIcon = new ImageIcon("img/help-icon.png");
		btnHelp.setIcon(btnIcon);

		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HelpFrame help = new HelpFrame();
			}
		});
		btnHelp.setBounds(895, 10, 50, 49);
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

		urlField = new JTextField();
		urlField.setBounds(22, 79, 220, 26);
		frame.getContentPane().add(urlField);
		urlField.setColumns(10);

		lblImageFromWebcam = new JLabel();
		lblImageFromWebcam.setBounds(297, 79, -286, 384);
		frame.getContentPane().add(lblImageFromWebcam);

		// buffer image first
		lblImageFromWebcam.setIcon(icon);

		btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// TODO empty tags and description from previous search

				// change description to actual text
				text = descriptionField.getText();
				System.out.println("new description " + text);

				// TODO tags get and change to string

				System.out.println("old tags string " + tagsString);
				
				System.out.println("tags field " + tagsField.getText());
				String newTags = tagsField.getText().replace("\n", "%20");
				newTags = newTags.replace(" ", "%20");
				
				System.out.println("new tags " + newTags);

				searchParameters = newTags + text.replace(" ", "%20");
				System.out.println(" search params " + searchParameters);
				searchForSimilarImages(searchParameters);
			}
		});
		btnSearchForSimilar.setBounds(628, 30, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);
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

				try {
					analyse();
				} catch (NullPointerException e1) {
					e1.printStackTrace();
				}
			}
		});

		urlField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlField.getText().length() > 0) {
					setImageFromUrlAsImageIcon();
				}
			}
		});
	}
	

	protected void searchForSimilarImages(String text) {

		String responseBing = httpBingSearch.GetUrlContentAsString(searchTokenApi, text);

		GsonBuilder gsonBingBuilder = new GsonBuilder();
		Gson gsonBing = gsonBingBuilder.create();

		RootBing rootBing = gsonBing.fromJson(responseBing, RootBing.class);

		for (Data currentData : rootBing.getValue()) {
			contentUrl = currentData.getContentUrl();
			System.out.println("content url " + contentUrl);

			// TODO modify setImageAsImageIcon so that it accepts url, label
			// name and scaling proportions
			// and sets the image as label accordingly

			// we can add one image
			// TODO think how to insert several, loop through coordinates?
			URL linkUrl = null;
			url = "{'url':'" + contentUrl + "'}";

			try {
				linkUrl = new URL(contentUrl);
				BufferedImage imgFromUrl = ImageIO.read(linkUrl);
				iconFromInternet = scaleBufferedImage(imgFromUrl, foundImagesLabel);
				foundImagesLabel.setIcon(iconFromInternet);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void getThumbnail() {
		// TODO Auto-generated method stub
		try {

			Thumbnails.of(file).size(400, 00).toFile(new File("thumbnail.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void setImageFromUrlAsImageIcon() {
		URL link2 = null;
		link = urlField.getText();
		url = "{'url':'" + link + "'}";

		try {
			link2 = new URL(link);
			// set image as Buffered image
			image = ImageIO.read(link2);
			icon = scaleBufferedImage(image, imageLabel);
			imageLabel.setIcon(icon);
		} catch (MalformedURLException e2) {
			tagsField.setText("please enter a valid link or choose an image with button 'Browse' ");
			e2.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
	}


	protected String analyse() {

		// in case user uploads image from hard drive
		String response = httpLocal.describeImageFromFilechooser(image, token);

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JsonRoot root = gson.fromJson(response, JsonRoot.class);
		String[] tags = root.getDescription().getTags();
		System.out.println("=============");
		System.out.println("tags " + Arrays.toString(tags));

		// for (String element : tags) {
		// tagsField.append(element + "\n");
		// }

		if (tags.length >= 6) {
			numberOfTags = 6;
		} else {
			numberOfTags = tags.length;
		}

		tagsField.setText("");

		for (int i = 0; i < numberOfTags; i++) {
			tagsField.append(tags[i] + "\n");
			// get tags from string array to a string variable
			tagsString = tagsString + tags[i] + "%20";
		}

		for (Captions currentCaption : root.getDescription().getCaptions()) {
			text = currentCaption.getText();
			System.out.println("old description: " + text);
			System.out.println("=============");
			descriptionField.setText(text);
		}
		String textString = text.replace(" ", "%20");

		System.out.println("text with replaced spaces " + textString);
		// searchForSimilarImages(searchTokenApi);

		System.out.println("complete text " + tagsString + textString);
		return tagsString + textString;
	}

	protected void takePicture() {

		WebcamAPI cam = new WebcamAPI();
		try {
			imgFromCam = cam.getPicture();
			System.out.println("taken image");
			// here null pointer exception
			icon = scaleBufferedImage(imgFromCam, lblImageFromWebcam);
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
			image = null;

			try {
				image = (BufferedImage) ImageIO.read(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// getThumbnail();

			// added to try with thumbnailator
			// icon = new ImageIcon("thumbnail.jpg");

			icon = scaleImage(file.getAbsolutePath(), imageLabel);
			imageLabel.setIcon(icon);

			// pass this BufferedImage image for the analysis

			// how to make analyse button response differently when either
			// uploading
			// file or using url? switch case thing?
		}
	}

	protected ImageIcon scaleImage(String string1, JLabel label) {

		// this method displays image from filechooser
		/** get height and width, if h > w set hScaledInstance = -1 usw. **/
		// TODO understand how it works!
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

	protected ImageIcon scaleBufferedImage(BufferedImage img, JLabel label) {

		// TODO understand it too
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
