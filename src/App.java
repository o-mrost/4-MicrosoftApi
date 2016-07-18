import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bing.Data;
import bing.RootBing;
import gson.Captions;
import gson.JsonRoot;

public class App {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon;
	private JFrame frame, progressFrame, webcamWindow;
	private JTextArea tagsField, descriptionField;
	private JTextField urlField;
	private JButton btnTakePicture, btnBrowse, btnHelp, btnSearchForSimilar, btnAnalyse;
	private JLabel originalImageLabel, lblTags, lblDescription, foundImagesLabel1, foundImagesLabel2, foundImagesLabel3,
			foundImagesLabel4;

	// private BufferedImage imgFromCam = null;
	BufferedImage imageWebcam;
	// = null;
	private BufferedImage image = null, imgLabels = null;

	private HttpDescribeImage httpLocal = new HttpDescribeImage();
	private HttpSimilarImagesSearch httpBingSearch = new HttpSimilarImagesSearch();

	private String analyseImageToken, bingToken;

	private JProgressBar aJProgressBar;

	private Token searchToken = new Token();
	private Token tokenCache = new Token();

	private Webcam webcam;

	String link, url, text, contentUrl, tagsString = "", searchParameters, labelInfo, labelTwoInfo;
	String[] tags;
	String firstImageUrl = null;
	String secondImageUrl = null;
	String thirdImageUrl = null;
	String fourthImageUrl = null;
	int numberOfTags, widthImage, heightImage, progressValue;

	String tagsTokenFileName = "APIToken.txt";
	String imageSearchTokenFileName = "SearchApiToken.txt";
	URL url1 = null, url2 = null, url3 = null, url4 = null;
	private JLabel stepTwo;
	private JLabel stepThree;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
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
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// set height of frame to 3/4 of screen height

		// Dimension screenSize = new
		// Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setSize(1000, 700);
		// (3 * screenSize.width / 4), (3 * screenSize.height / 4));
		frame.getContentPane().setLayout(null);
		// System.out.println("width " + screenSize.width);
		// System.out.println("height " + screenSize.height);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		frame.getContentPane().add(fc);

		JLabel stepOne = new JLabel("");
		stepOne.setToolTipText("here comes something");
		stepOne.setIcon(new ImageIcon("img/stepOne.png"));
		stepOne.setBounds(23, 0, 67, 49);
		frame.getContentPane().add(stepOne);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(66, 6, 117, 29);
		frame.getContentPane().add(btnBrowse);

		btnTakePicture = new JButton("Take a picture with webcam");
		btnTakePicture.setBounds(66, 34, 212, 29);
		frame.getContentPane().add(btnTakePicture);

		urlField = new JTextField();
		urlField.setBounds(66, 67, 220, 26);
		frame.getContentPane().add(urlField);
		urlField.setColumns(10);

		originalImageLabel = new JLabel();
		// edit to center the label
		originalImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		originalImageLabel.setBounds(23, 93, 300, 300);
		frame.getContentPane().add(originalImageLabel);

		stepTwo = new JLabel("");
		stepTwo.setToolTipText("here comes something else");
		stepTwo.setIcon(new ImageIcon("img/stepTwo.png"));
		stepTwo.setBounds(23, 397, 67, 49);
		frame.getContentPane().add(stepTwo);

		btnAnalyse = new JButton("Analyse image");
		btnAnalyse.setBounds(66, 405, 196, 29);
		frame.getContentPane().add(btnAnalyse);

		tagsField = new JTextArea();
		tagsField.setBounds(22, 472, 102, 89);
		tagsField.setLineWrap(true);
		tagsField.setWrapStyleWord(true);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);

		lblTags = new JLabel("Tags:");
		lblTags.setBounds(45, 444, 61, 16);
		frame.getContentPane().add(lblTags);

		descriptionField = new JTextArea();
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		descriptionField.setBounds(136, 472, 187, 89);
		frame.getContentPane().add(descriptionField);
		descriptionField.setColumns(10);

		lblDescription = new JLabel("Description:");
		lblDescription.setBounds(162, 444, 77, 16);
		frame.getContentPane().add(lblDescription);

		stepThree = new JLabel("");
		stepThree.setToolTipText("here comes something different");
		stepThree.setIcon(new ImageIcon("img/stepThree.png"));
		stepThree.setBounds(23, 579, 67, 49);
		frame.getContentPane().add(stepThree);

		btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.setVisible(true);
		btnSearchForSimilar.setBounds(66, 585, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);

		foundImagesLabel1 = new JLabel();
		foundImagesLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		foundImagesLabel1.setBounds(400, 49, 250, 250);
		frame.getContentPane().add(foundImagesLabel1);

		foundImagesLabel2 = new JLabel();
		foundImagesLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		foundImagesLabel2.setBounds(400, 313, 250, 250);
		frame.getContentPane().add(foundImagesLabel2);

		foundImagesLabel3 = new JLabel();
		foundImagesLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		foundImagesLabel3.setBounds(673, 49, 250, 250);
		frame.getContentPane().add(foundImagesLabel3);

		foundImagesLabel4 = new JLabel();
		foundImagesLabel4.setHorizontalAlignment(SwingConstants.CENTER);
		foundImagesLabel4.setBounds(673, 313, 250, 250);
		frame.getContentPane().add(foundImagesLabel4);

		btnHelp = new JButton("");
		btnHelp.setBorderPainted(false);
		ImageIcon btnIcon = new ImageIcon("img/helpRed.png");
		btnHelp.setIcon(btnIcon);
		btnHelp.setBounds(847, 0, 77, 59);
		frame.getContentPane().add(btnHelp);

		progressFrame = new JFrame("We are looking for images... It may take a while");
		JLabel progressLabel = new JLabel("progress");
		progressFrame.getContentPane().add(progressLabel);
		progressFrame.setSize(400, 100);
		progressFrame.setLocationRelativeTo(frame);

		// all action listeners
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					openFilechooser();
				}
			}
		});

		btnTakePicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				turnCameraOn();

				// TODO doesn't work yet, the image is reflected
				// image = flip(image);

			}
		});

		urlField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlField.getText().length() > 0) {
					String linkNew = urlField.getText();
					setImageAsImageIcon(linkNew, originalImageLabel);
					// added this line to fix the bug When analyse from link,
					// when click on Analyse image, popup “Please choose an
					// image” appears
					image = imgLabels;
				}
			}
		});

		btnAnalyse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					analyseImageToken = searchToken.getApiToken(tagsTokenFileName);
					try {
						analyse();
					} catch (NullPointerException e1) {
						// if user clicks on "analyze" button without uploading
						// image or posts a broken link
						JOptionPane.showMessageDialog(null, "Please choose an image");
						e1.printStackTrace();
					}
				} catch (NullPointerException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnSearchForSimilar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println("==========================================");
				System.out.println("new search");
				System.out.println("==========================================");
				bingToken = tokenCache.getApiToken(imageSearchTokenFileName);

				// in case user edited description or tags, update it and replace new
				// line character, spaces and breaks with %20
				text = descriptionField.getText().replace(" ", "%20").replace("\r", "%20").replace("\n", "%20");

				String tagsString = tagsField.getText().replace(" ", "%20").replace("\r", "%20").replace("\n", "%20");

				searchParameters = tagsString + text;
				System.out.println("search parameters: " + searchParameters);

				if (searchParameters.length() != 0) {
					searchForSimilarImages(searchParameters);
				} else {
					JOptionPane.showMessageDialog(null, "Please choose first an image to analyse or insert tags");
				}
			}
		});

		foundImagesLabel1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {

				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(firstImageUrl);
				}
			}
		});

		foundImagesLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(secondImageUrl);
				}
			}
		});

		foundImagesLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(thirdImageUrl);
				}
			}
		});

		foundImagesLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(fourthImageUrl);
				}
			}
		});

		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO write help
				HelpFrame help = new HelpFrame();
			}
		});
	}

	protected void turnCameraOn() {

		// webcamWindow = new JFrame("Test webcam panel");
		// webcamWindow.setLayout(new BorderLayout());

		// get default webcam and open it
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setMirrored(true);
		panel.setBounds(23, 130, 300, 210);

		// webcamWindow.add(panel, BorderLayout.CENTER);

		frame.getContentPane().add(panel);

		// TODO add buttons

		JPanel buttonPanel = new JPanel();
		webcamWindow.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton okWebcambtn = new JButton("Take a picture");
		buttonPanel.add(okWebcambtn);

		okWebcambtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				imageWebcam = webcam.getImage();
				System.out.println("picture taken");
				setImageWebcam(imageWebcam);
				icon = scaleBufferedImage(imageWebcam, originalImageLabel);
				originalImageLabel.setIcon(icon);
				image = getImageWebcam();
			}
		});

		JButton cancelWebcam = new JButton("Close");
		buttonPanel.add(cancelWebcam);

		cancelWebcam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				webcam.close();
				webcamWindow.setVisible(false);
			}
		});

		webcamWindow.getContentPane().add(panel);
		webcamWindow.setResizable(true);
		webcamWindow.pack();
		webcamWindow.setVisible(true);
	}

	public BufferedImage getImageWebcam() {
		return imageWebcam;
	}

	public void setImageWebcam(BufferedImage imageWebcam) {
		this.imageWebcam = imageWebcam;
	}

	// TODO make it work or delete
	private BufferedImage flip(BufferedImage image) {

		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));

		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return image;
	}

	protected void searchForSimilarImages(String text) {

		// Add progress bar
		progressFrame.setVisible(true);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// clear labels in case there were results of previous search
		foundImagesLabel1.setIcon(null);
		foundImagesLabel2.setIcon(null);
		foundImagesLabel3.setIcon(null);
		foundImagesLabel4.setIcon(null);

		String responseBing = httpBingSearch.GetUrlContentAsString(bingToken, text);

		GsonBuilder gsonBingBuilder = new GsonBuilder();
		Gson gsonBing = gsonBingBuilder.create();
		RootBing rootBing = gsonBing.fromJson(responseBing, RootBing.class);

		// created temp array list with all urls and width and height of images
		ArrayList<Data> originalUrls = rootBing.getValue();

		// filtered only working links
		ArrayList<String> checkedUrls = checkLinks(originalUrls);
		// System.out.println("Links to display: ");

		int i = 0;

		for (String temp : checkedUrls) {

			if (i == 0) {

				firstImageUrl = temp;
				foundImagesLabel1.setToolTipText("<html><img src=\"" + firstImageUrl + "\">");
				// System.out.println("first url: " + firstImageUrl);
				setImageAsImageIcon(firstImageUrl, foundImagesLabel1);

			} else if (i == 1) {

				secondImageUrl = temp;
				foundImagesLabel2.setToolTipText("<html><img src=\"" + secondImageUrl + "\">");
				// System.out.println("second url: " + secondImageUrl);
				setImageAsImageIcon(secondImageUrl, foundImagesLabel2);

			} else if (i == 2) {

				thirdImageUrl = temp;
				// System.out.println("third url: " + thirdImageUrl);
				foundImagesLabel3.setToolTipText("<html><img src=\"" + thirdImageUrl + "\">");
				setImageAsImageIcon(thirdImageUrl, foundImagesLabel3);

			} else if (i == 3) {

				fourthImageUrl = temp;
				foundImagesLabel4.setToolTipText("<html><img src=\"" + fourthImageUrl + "\">");
				// System.out.println("fourth url: " + fourthImageUrl);
				setImageAsImageIcon(fourthImageUrl, foundImagesLabel4);
			}

			i++;
			// progressFrame.setVisible(false);
		}

		// if not enough images were found, display suggestion to edit search
		if (checkedUrls.size() <= 2) {
			try {
				System.out.println("found less than two images =============");
				imgLabels = (BufferedImage) ImageIO.read(new File("img/warning.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			icon = scaleBufferedImage(imgLabels, foundImagesLabel1);
			foundImagesLabel1.setIcon(icon);
		}
	}

	protected ArrayList<String> checkLinks(ArrayList<Data> originalValue) {

		// here we convert our Data arrayList to a String arrayList
		ArrayList<String> stringArray = new ArrayList<String>();

		for (Data currentData : originalValue) {
			stringArray.add(currentData.getContentUrl());
		}

		// System.out.println("Original array with " + stringArray.size() + "
		// elements");
		// System.out.println("============");
		// stringArray.forEach(System.out::println);

		// get an iterator
		Iterator iter = stringArray.iterator();
		String strElement = "";
		URL linkAsUrl = null;

		// variable to count the number of valid urls
		int count = 0;

		while (iter.hasNext()) {
			strElement = (String) iter.next();
			System.out.println("============");
			System.out.println("CHECKING: " + strElement);

			try {
				linkAsUrl = new URL(strElement);
				imgLabels = ImageIO.read(linkAsUrl);
				setImageAsImageIcon(strElement, foundImagesLabel4);
				System.out.println("OK");
				count++;
			} catch (MalformedURLException e) {
				System.out.println("malformed exception with url " + strElement);
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("NullPointerException: element to be removed - " + strElement);
				iter.remove();
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("IIOException \nLink to be removed: " + strElement);
				iter.remove();
				e.printStackTrace();
			}
			// stop checking when 4 valid links are found
			if (count == 4) {
				break;
			}
		}

		// System.out.println("=======================");
		// System.out.println("Filtered array: ");
		// stringArray.forEach(System.out::println);
		// System.out.println("number of elements after the deletion " +
		// stringArray.size());

		return stringArray;
	}

	protected void setImageAsImageIcon(String link, JLabel label) {

		URL linkAsUrl = null;
		try {
			linkAsUrl = new URL(link);
			imgLabels = ImageIO.read(linkAsUrl);
			// icon = scaleBufferedImage(imgLabels, label);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();

		} catch (IOException e1) {
			try {
				imgLabels = (BufferedImage) ImageIO.read(new File("img/iioexception.png"));
				// icon = scaleBufferedImage(imgLabels, label);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e1.printStackTrace();

		} catch (NullPointerException e) {
			try {
				imgLabels = (BufferedImage) ImageIO.read(new File("img/error.png"));
				// icon = scaleBufferedImage(imgLabels, label);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			// icon = scaleBufferedImage(imgLabels, label);
			e.printStackTrace();
		}
		icon = scaleBufferedImage(imgLabels, label);
		label.setIcon(icon);
	}

	protected String analyse() {

		String response = httpLocal.describeImage(image, analyseImageToken);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		JsonRoot root = gson.fromJson(response, JsonRoot.class);
		String[] tags = root.getDescription().getTags();

		System.out.println("=============");
		System.out.println("tags " + Arrays.toString(tags));

		// limit number of tags displayed to max first five
		if (tags.length >= 5) {
			numberOfTags = 5;
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

	protected void saveFileChooser(String fileUrl) {

		fc.setDialogTitle("Specify name of the file to save");
		/*
		 * // get name of file without url things, but with extension String
		 * fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1,
		 * fileUrl.length());
		 * 
		 * // some files have name like this: 1581_003.jpg?imgmax=512 // leave
		 * just the part up to .jpg if (fileName.contains("?")) { fileName =
		 * fileName.substring(0, fileName.lastIndexOf('?')); }
		 * 
		 * System.out.println("filename " + fileName); // String pathToFile =
		 * fc.getCurrentDirectory().toString(); // File output = new
		 * File(pathToFile + "/" + fileName);
		 * 
		 */

		File output = new File(fc.getSelectedFile().toString());

		// check if file already exists, ask user if they wish to overwrite it
		if (output.exists()) {
			int response = JOptionPane.showConfirmDialog(null, //
					"Do you want to replace the existing file?", //
					"Confirm", JOptionPane.YES_NO_OPTION, //
					JOptionPane.QUESTION_MESSAGE);
			if (response != JOptionPane.YES_OPTION) {
				return;
			}
		}
		fc.setSelectedFile(output);

		try {

			URL fileNameAsUrl = new URL(fileUrl);
			image = ImageIO.read(fileNameAsUrl);
			ImageIO.write(toBufferedImage(image), "jpeg", output);

			System.out.println("image saved, in the folder: " + output.getAbsolutePath());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void openFilechooser() {

		image = null;
		file = fc.getSelectedFile();

		try {
			image = (BufferedImage) ImageIO.read(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		icon = scaleBufferedImage(image, originalImageLabel);
		originalImageLabel.setIcon(icon);
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
		double labelWidth = label.getWidth();
		double labelHight = label.getHeight();
		double scaleWidth = width / labelWidth;
		double scaleHeight = height / labelHight;

		if (width >= height) {
			// horisontal image
			double newWidth = width / scaleWidth;
			icon = new ImageIcon(icon.getImage().getScaledInstance((int) newWidth, -1, Image.SCALE_SMOOTH));
		} else {
			// vertical image
			double newHeight = height / scaleHeight;
			icon = new ImageIcon(icon.getImage().getScaledInstance(-1, (int) newHeight, Image.SCALE_SMOOTH));
		}
		return icon;
	}
}