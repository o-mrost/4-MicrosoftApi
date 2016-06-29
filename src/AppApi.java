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
	private ImageIcon icon, iconOneFromInternet, iconTwoFromInternet, iconThreeFromInternet, iconFourFromInternet;
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
	String firstImageUrl = null;
	String secondImageUrl = null;
	String thirdImageUrl = null;
	String fourthImageUrl = null;
	int numberOfTags, widthImage, heightImage;

	String tagsTokenFileName = "APIToken.txt";
	String imageSearchTokenFileName = "SearchApiToken.txt";
	URL url1 = null, url2 = null, url3 = null, url4 = null;

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
		originalImageLabel.setBounds(23, 109, 300, 300);
		frame.getContentPane().add(originalImageLabel);

		btnTakePicture = new JButton("Take a picture with webcam");
		btnTakePicture.setBounds(30, 46, 212, 29);
		frame.getContentPane().add(btnTakePicture);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(78, 18, 117, 29);
		frame.getContentPane().add(btnBrowse);

		JButton btnAnalyse = new JButton("    Analyse image");
		ImageIcon analyseIcon = new ImageIcon("img/cloud-icon.png");
		btnAnalyse.setIcon(analyseIcon);
		btnAnalyse.setBounds(352, 18, 196, 29);
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
		btnSearchForSimilar.setBounds(726, 18, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);

		// place for images from internet
		foundImagesLabel1 = new JLabel();
		foundImagesLabel1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(firstImageUrl);
			}
		});

		foundImagesLabel1.setBounds(600, 50, 250, 250);
		frame.getContentPane().add(foundImagesLabel1);

		foundImagesLabel2 = new JLabel();
		foundImagesLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(secondImageUrl);
			}
		});
		foundImagesLabel2.setBounds(600, 310, 250, 250);
		frame.getContentPane().add(foundImagesLabel2);

		foundImagesLabel3 = new JLabel();
		foundImagesLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(thirdImageUrl);
			}
		});
		foundImagesLabel3.setBounds(920, 50, 250, 250);
		frame.getContentPane().add(foundImagesLabel3);

		foundImagesLabel4 = new JLabel();
		foundImagesLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				saveFileChooser(fourthImageUrl);
			}
		});
		foundImagesLabel4.setBounds(920, 310, 250, 250);
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
					String linkNew = urlField.getText();
					setImageAsImageIcon(linkNew, originalImageLabel);
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

			// TODO or may be better use here switch case thing?
			// get one element of arrayList

			// TODO scale images correctly
			if (i == 0) {
				firstImageUrl = currentData.getContentUrl();

				// get information about width and height of this image from
				// Bing API
				widthImage = currentData.getWidth();
				heightImage = currentData.getHeight();
				labelInfo = " width: " + widthImage + ", height: " + heightImage;

				// display width and height when user hovers over image
				foundImagesLabel1.setToolTipText(labelInfo);

				System.out.println("first url " + firstImageUrl);

				// set this image as icon on a JLabel
				setImageAsImageIcon(firstImageUrl, foundImagesLabel1);

			} else if (i == 1) {

				secondImageUrl = currentData.getContentUrl();

				widthImage = currentData.getWidth();
				heightImage = currentData.getHeight();
				labelInfo = " width: " + widthImage + ", height: " + heightImage;
				foundImagesLabel2.setToolTipText(labelInfo);

				System.out.println("second url " + secondImageUrl);
				setImageAsImageIcon(secondImageUrl, foundImagesLabel2);

			} else if (i == 2) {

				thirdImageUrl = currentData.getContentUrl();

				widthImage = currentData.getWidth();
				heightImage = currentData.getHeight();
				labelInfo = " width: " + widthImage + ", height: " + heightImage;
				foundImagesLabel3.setToolTipText(labelInfo);

				System.out.println("third url " + thirdImageUrl);
				setImageAsImageIcon(thirdImageUrl, foundImagesLabel3);

			} else if (i == 3) {

				fourthImageUrl = currentData.getContentUrl();

				widthImage = currentData.getWidth();
				heightImage = currentData.getHeight();
				labelInfo = " width: " + widthImage + ", height: " + heightImage;
				foundImagesLabel4.setToolTipText(labelInfo);

				System.out.println("fourth url " + fourthImageUrl);
				setImageAsImageIcon(fourthImageUrl, foundImagesLabel4);
			}
			i++;
		}
	}

	protected void setImageAsImageIcon(String link, JLabel label) {

		URL linkAsUrl = null;
		try {
			linkAsUrl = new URL(link);
			// set image as Buffered image
			image = ImageIO.read(linkAsUrl);
			icon = scaleBufferedImage(image, label);
			label.setIcon(icon);
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

			icon = scaleBufferedImage(image, originalImageLabel);
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

	protected ImageIcon scaleBufferedImage(BufferedImage img, JLabel label) {

		ImageIcon icon = new ImageIcon(img);

		double width = icon.getIconWidth();
		double height = icon.getIconHeight();
		
		System.out.println("image width = " + width);
		System.out.println("image height = " +  height);
		
		double labelWidth = label.getWidth();
		double labelHight = label.getHeight();
		System.out.println("label width is constant = " + labelWidth);
		System.out.println("label hight = " + labelHight);

		double scaleWidth = width / labelWidth;
		double scaleHeight = height / labelHight;
		System.out.println("scale according to width = " + scaleWidth);
		System.out.println("scale according to height = " + scaleHeight);

		// for horizontal images
		double newWidth = width / scaleWidth;
		
		// for vertical images
		double newHeight = height / scaleHeight;

		if (width >= height) {
			// horisontal image
			icon = new ImageIcon (icon.getImage().getScaledInstance((int) newWidth, -1, Image.SCALE_SMOOTH));
		} else {
			// vertical image
			icon = new ImageIcon (icon.getImage().getScaledInstance(-1, (int) newHeight, Image.SCALE_SMOOTH));
		}
		
		return icon;
	}
}
