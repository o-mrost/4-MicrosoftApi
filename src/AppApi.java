import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

public class AppApi {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon, iconFromInternet;
	private JFrame frame, helpFrame;
	private JTextArea tagsField, descriptionField;
	private JTextField urlField;
	private JButton btnTakePicture, btnBrowse, btnSaveFile, btnHelp, btnSearchForSimilar;
	private JLabel originalImageLabel, lblTags, lblDescription, foundImagesLabel1, foundImagesLabel2, foundImagesLabel3,
			foundImagesLabel4, lblImageFromWebcam, helpLabel;

	private BufferedImage imgFromCam = null;
	private BufferedImage image = null;

	private HttpDescribeImage httpLocal = new HttpDescribeImage();
	private HttpSimilarImagesSearch httpBingSearch = new HttpSimilarImagesSearch();

	private String analyseImageToken, bingToken;

	private Token searchToken = new Token();
	private Token tokenCache = new Token();

	String link, url, text, contentUrl, tagsString = "", searchParameters, labelInfo, labelTwoInfo;
	String[] tags;
	String imageFirstLabel = null;
	String imageSecondLabel = null;
	String imageThirdLabel = null;
	String imageFourthLabel = null;
	int numberOfTags, widthImage, heightImage;

	String tagsTokenFileName = "APIToken.txt";
	String imageSearchTokenFileName = "SearchApiToken.txt";
	URL linkUrl1 = null, linkUrl2 = null, linkUrl3 = null, linkUrl4 = null;

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

		// set height of frame to 3/4 of screen height
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setSize(screenSize.width, (3 * screenSize.height / 4));
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setLayout(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		frame.getContentPane().add(fc);

		originalImageLabel = new JLabel();
		originalImageLabel.setBounds(23, 109, 305, 342);
		frame.getContentPane().add(originalImageLabel);

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

		btnHelp = new JButton("");
		btnHelp.setBorderPainted(false);
		ImageIcon btnIcon = new ImageIcon("img/help-icon.png");
		btnHelp.setIcon(btnIcon);

		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO write help
				HelpFrame help = new HelpFrame();
			}
		});
		btnHelp.setBounds(1195, 10, 50, 49);
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

		urlField = new JTextField();
		urlField.setBounds(22, 79, 220, 26);
		frame.getContentPane().add(urlField);
		urlField.setColumns(10);

		lblImageFromWebcam = new JLabel();
		lblImageFromWebcam.setBounds(297, 79, -286, 384);
		frame.getContentPane().add(lblImageFromWebcam);
		lblImageFromWebcam.setIcon(icon);

		btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				bingToken = tokenCache.getApiToken(imageSearchTokenFileName);

				// in case user edited description, update it
				text = descriptionField.getText();

				// in case user edited tags, we get the new info here and make
				// it suitable for url, replace new line character with %20
				String newTags = tagsField.getText().replace("\n", "%20");
				// and replace spaces with %20
				newTags = newTags.replace(" ", "%20");

				searchParameters = newTags + text.replace(" ", "%20");
				searchForSimilarImages(searchParameters);
			}
		});
		btnSearchForSimilar.setBounds(725, 30, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);

		// place for images from internet
		foundImagesLabel1 = new JLabel();
		
		foundImagesLabel1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(imageFirstLabel);
			}
		});
		
		// hover over information
		
		foundImagesLabel1.setBounds(600, 107, 200, 200);
		frame.getContentPane().add(foundImagesLabel1);

		foundImagesLabel2 = new JLabel();
		foundImagesLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(imageSecondLabel);
			}
		});
		foundImagesLabel2.setBounds(600, 327, 200, 200);
		frame.getContentPane().add(foundImagesLabel2);

		foundImagesLabel3 = new JLabel();
		foundImagesLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(imageThirdLabel);
			}
		});
		foundImagesLabel3.setBounds(820, 107, 200, 200);
		frame.getContentPane().add(foundImagesLabel3);

		foundImagesLabel4 = new JLabel();
		foundImagesLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(imageFourthLabel);
			}
		});
		foundImagesLabel4.setBounds(820, 327, 200, 200);
		frame.getContentPane().add(foundImagesLabel4);

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
					analyseImageToken = searchToken.getApiToken(tagsTokenFileName);
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

		String responseBing = httpBingSearch.GetUrlContentAsString(bingToken, text);

		GsonBuilder gsonBingBuilder = new GsonBuilder();
		Gson gsonBing = gsonBingBuilder.create();

		RootBing rootBing = gsonBing.fromJson(responseBing, RootBing.class);

		int i = 0;

		// clear labels in case there were results of previous search
		foundImagesLabel1.setIcon(null);
		foundImagesLabel2.setIcon(null);
		foundImagesLabel3.setIcon(null);
		foundImagesLabel4.setIcon(null);

		for (Data currentData : rootBing.getValue()) {

			try {
				// TODO or may be better use here switch case thing?

				// TODO scale images correctly
				if (i == 0) {

					imageFirstLabel = currentData.getContentUrl();
					
					// get information about width and height of image
					widthImage = currentData.getWidth();
					heightImage = currentData.getHeight();
					
					labelInfo = " width: " + widthImage + ", height: " + heightImage;
					
					// display it when user hovers over image
					foundImagesLabel1.setToolTipText(labelInfo);
					
					System.out.println("first url " + imageFirstLabel);
					linkUrl1 = new URL(imageFirstLabel);
					BufferedImage imgFromUrl1 = ImageIO.read(linkUrl1);
					iconFromInternet = scaleBufferedImage(imgFromUrl1, foundImagesLabel1);
					foundImagesLabel1.setIcon(iconFromInternet);

				} else if (i == 1) {

					imageSecondLabel = currentData.getContentUrl();
					
					widthImage = currentData.getWidth();
					heightImage = currentData.getHeight();
					labelInfo = " width: " + widthImage + ", height: " + heightImage;
					foundImagesLabel2.setToolTipText(labelInfo);
					
					System.out.println("second url " + imageSecondLabel);
					linkUrl2 = new URL(imageSecondLabel);
					BufferedImage imgFromUrl2 = ImageIO.read(linkUrl2);
					iconFromInternet = scaleBufferedImage(imgFromUrl2, foundImagesLabel2);
					foundImagesLabel2.setIcon(iconFromInternet);

				} else if (i == 2) {

					imageThirdLabel = currentData.getContentUrl();
					
					widthImage = currentData.getWidth();
					heightImage = currentData.getHeight();
					labelInfo = " width: " + widthImage + ", height: " + heightImage;
					foundImagesLabel3.setToolTipText(labelInfo);
					
					System.out.println("third url " + imageThirdLabel);
					linkUrl3 = new URL(imageThirdLabel);
					BufferedImage imgFromUrl3 = ImageIO.read(linkUrl3);
					iconFromInternet = scaleBufferedImage(imgFromUrl3, foundImagesLabel3);
					foundImagesLabel3.setIcon(iconFromInternet);

				} else if (i == 3) {

					imageFourthLabel = currentData.getContentUrl();
					
					widthImage = currentData.getWidth();
					heightImage = currentData.getHeight();
					labelInfo = " width: " + widthImage + ", height: " + heightImage;
					foundImagesLabel4.setToolTipText(labelInfo);
					
					System.out.println("fourth url " + imageFourthLabel);
					linkUrl4 = new URL(imageFourthLabel);
					BufferedImage imgFromUrl4 = ImageIO.read(linkUrl4);
					iconFromInternet = scaleBufferedImage(imgFromUrl4, foundImagesLabel4);
					foundImagesLabel4.setIcon(iconFromInternet);
				}

				i++;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void setImageFromUrlAsImageIcon() {
		URL link2 = null;
		link = urlField.getText();

		try {
			link2 = new URL(link);
			// set image as Buffered image
			image = ImageIO.read(link2);
			icon = scaleBufferedImage(image, originalImageLabel);
			originalImageLabel.setIcon(icon);
		} catch (MalformedURLException e2) {
			tagsField.setText("please enter a valid link or choose an image with button 'Browse' ");
			e2.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected String analyse() {

		String response = httpLocal.describeImage(image, analyseImageToken);

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JsonRoot root = gson.fromJson(response, JsonRoot.class);
		String[] tags = root.getDescription().getTags();
		System.out.println("=============");
		System.out.println("tags " + Arrays.toString(tags));

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
			System.out.println("description: " + text);
			System.out.println("=============");
			descriptionField.setText(text);
		}
		String textString = text.replace(" ", "%20");

		return tagsString + textString;
	}

	protected void takePicture() {

		WebcamAPI camera = new WebcamAPI();

		try {

			camera.turnWebcamOn();
			System.out.println("camera works");

			// here null pointer exception
			
			// imgFromCam is null, how to pass BufferedImage to it?
			icon = scaleBufferedImage(imgFromCam, originalImageLabel);

			// return bufferedImage
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	protected void saveFileChooser(String fileUrl) {

		fc.setDialogTitle("Specify name of the file to save");

		if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {

			try {

				// file = fc.getSelectedFile();

				// get name of file without url things, but with extension
				String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());
				System.out.println("filename " + fileName);

				// how to suggest the filename

				File output = new File(" " + fileName);
				fc.setSelectedFile(output);

				// tino
				// File outputfile = new File("GREY_" + fileName1);
				// fileChooserS.setSelectedFile(outputfile);
				// fileChooserS.setDialogTitle("Speichern unter...");
				// int saveresult = fileChooserS.showSaveDialog(btnSpeichern);
				//
				URL fileNameAsUrl = new URL(fileUrl);
				image = ImageIO.read(fileNameAsUrl);
				ImageIO.write(toBufferedImage(image), "jpeg", output);
				System.out.println("image saved");

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void openFilechooser() {

		image = null;

		if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();

			try {
				image = (BufferedImage) ImageIO.read(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			icon = scaleImage(file.getAbsolutePath(), originalImageLabel);
			originalImageLabel.setIcon(icon);
		}
	}

	private BufferedImage toBufferedImage(Image imageToGetBuffered) {

		if (imageToGetBuffered instanceof BufferedImage) {
			return (BufferedImage) imageToGetBuffered;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(imageToGetBuffered.getWidth(null), imageToGetBuffered.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		return bimage;

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
