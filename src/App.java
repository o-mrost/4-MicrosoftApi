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
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bing.Data;
import bing.RootBing;
import gson.Captions;
import gson.JsonRoot;

public class App {

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
	private BufferedImage image = null, imgLabels = null;

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
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setSize((3 * screenSize.width / 4), (3 * screenSize.height / 4));
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setLayout(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		frame.getContentPane().add(fc);

		originalImageLabel = new JLabel();
		originalImageLabel.setBounds(23, 93, 300, 300);
		frame.getContentPane().add(originalImageLabel);

		btnTakePicture = new JButton("Take a picture with webcam");
		btnTakePicture.setBounds(66, 34, 212, 29);
		frame.getContentPane().add(btnTakePicture);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(118, 6, 117, 29);
		frame.getContentPane().add(btnBrowse);

		JButton btnAnalyse = new JButton("    Analyse image");
		btnAnalyse.setBounds(82, 406, 196, 29);
		frame.getContentPane().add(btnAnalyse);

		tagsField = new JTextArea();
		tagsField.setBounds(22, 472, 102, 89);
		tagsField.setLineWrap(true);
		tagsField.setWrapStyleWord(true);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);

		btnHelp = new JButton("");
		btnHelp.setBorderPainted(false);
		ImageIcon btnIcon = new ImageIcon("img/helpRed.png");
		btnHelp.setIcon(btnIcon);

		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO write help
				HelpFrame help = new HelpFrame();
			}
		});
		btnHelp.setBounds(847, 0, 77, 59);
		frame.getContentPane().add(btnHelp);

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

		urlField = new JTextField();
		urlField.setBounds(66, 69, 220, 26);
		frame.getContentPane().add(urlField);
		urlField.setColumns(10);

		lblImageFromWebcam = new JLabel();
		lblImageFromWebcam.setBounds(297, 79, -286, 384);
		frame.getContentPane().add(lblImageFromWebcam);
		lblImageFromWebcam.setIcon(icon);

		btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.setVisible(true);
		btnSearchForSimilar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println("==========================================");
				System.out.println("new search");
				System.out.println("==========================================");
				bingToken = tokenCache.getApiToken(imageSearchTokenFileName);

				// in case user edited description, update it
				text = descriptionField.getText();

				// in case user edited tags, we get the new info here and
				// make
				// it suitable for url, replace new line character with %20
				String newTags = tagsField.getText().replace("\n", "%20");
				// and replace spaces with %20
				newTags = newTags.replace(" ", "%20");

				searchParameters = newTags + text.replace(" ", "%20");
				System.out.println("search parameters: " + searchParameters);

				if (searchParameters.length() != 0) {
					searchForSimilarImages(searchParameters);

				} else {
					JOptionPane.showMessageDialog(null, "Please choose first an image to analyse or insert tags");
				}
			}
		});

		btnSearchForSimilar.setBounds(495, 18, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);

		// place for images from internet
		foundImagesLabel1 = new JLabel();
		foundImagesLabel1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(firstImageUrl);
				}
			}
		});
		foundImagesLabel1.setBounds(400, 49, 250, 250);
		frame.getContentPane().add(foundImagesLabel1);

		foundImagesLabel2 = new JLabel();
		foundImagesLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(secondImageUrl);
				}
			}
		});
		foundImagesLabel2.setBounds(400, 311, 250, 250);
		frame.getContentPane().add(foundImagesLabel2);

		foundImagesLabel3 = new JLabel();
		foundImagesLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(thirdImageUrl);
				}
			}
		});
		foundImagesLabel3.setBounds(673, 47, 250, 250);
		frame.getContentPane().add(foundImagesLabel3);

		foundImagesLabel4 = new JLabel();
		foundImagesLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					saveFileChooser(fourthImageUrl);
				}
			}
		});
		foundImagesLabel4.setBounds(673, 313, 250, 250);
		frame.getContentPane().add(foundImagesLabel4);

		JLabel stepOne = new JLabel("");
		stepOne.setToolTipText("here comes something");
		stepOne.setIcon(new ImageIcon("img/stepOne.png"));
		stepOne.setBounds(23, 0, 67, 49);
		frame.getContentPane().add(stepOne);

		stepTwo = new JLabel("");
		stepTwo.setToolTipText("here comes something else");
		stepTwo.setIcon(new ImageIcon("img/stepTwo.png"));
		stepTwo.setBounds(20, 386, 67, 49);
		frame.getContentPane().add(stepTwo);

		stepThree = new JLabel("");
		stepThree.setToolTipText("here comes something different");
		stepThree.setIcon(new ImageIcon("img/stepThree.png"));
		stepThree.setBounds(432, 0, 67, 49);
		frame.getContentPane().add(stepThree);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					openFilechooser();
				}
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

		ArrayList originalUrls = rootBing.getValue();

		int i = 0;

		// clear labels in case there were results of previous search
		foundImagesLabel1.setIcon(null);
		foundImagesLabel2.setIcon(null);
		foundImagesLabel3.setIcon(null);
		foundImagesLabel4.setIcon(null);

		System.out.println("here are 10 urls");

//		checkLinks(originalUrls);

		for (Data currentData : rootBing.getValue()) {

			// check if link is valid

			// and then set it as icon

			if (i == 0) {
				firstImageUrl = currentData.getContentUrl();
				foundImagesLabel1.setToolTipText("<html><img src=\"" + firstImageUrl + "\">");
				System.out.println("first url " + firstImageUrl);
				setImageAsImageIcon(firstImageUrl, foundImagesLabel1);

			} else if (i == 1) {

				secondImageUrl = currentData.getContentUrl();
				foundImagesLabel2.setToolTipText("<html><img src=\"" + secondImageUrl + "\">");
				System.out.println("second url " + secondImageUrl);
				setImageAsImageIcon(secondImageUrl, foundImagesLabel2);

			} else if (i == 2) {

				thirdImageUrl = currentData.getContentUrl();
				System.out.println("third url " + thirdImageUrl);
				foundImagesLabel3.setToolTipText("<html><img src=\"" + thirdImageUrl + "\">");
				setImageAsImageIcon(thirdImageUrl, foundImagesLabel3);

			} else if (i == 3) {

				fourthImageUrl = currentData.getContentUrl();
				foundImagesLabel4.setToolTipText("<html><img src=\"" + fourthImageUrl + "\">");
				System.out.println("fourth url " + fourthImageUrl);
				setImageAsImageIcon(fourthImageUrl, foundImagesLabel4);
			}
			i++;
		}
	}

//	private ArrayList checkLinks(ArrayList<Data> originalValue) {
//
//		String[] stringArray = null;
//		
//		int i = 0;
//		
//		for (Data currentData : originalValue) {
//			stringArray [i] = currentData.getContentUrl();
//			i++;
//		}
//			System.out.println("10 urls as string " + Arrays.toString(stringArray));
//		
//		
//		// print out original array
//
//		System.out.println("original array");
//		for (Data currentData : originalValue) {
//			System.out.println(currentData.getContentUrl());
//		}
//
//		// for (int i = 0; i < originalValue.size(); i++) {
//		// System.out.println(Arrays.toString.originalValue.get(i));
//		// }
//		//
//		//
//		for (Data currentData : originalValue) {
//			String currentLink = currentData.getContentUrl();
//
//			try {
//				URL linkAsUrl = new URL(currentLink);
//				// set image as Buffered image
//				image = ImageIO.read(linkAsUrl);
//				icon = scaleBufferedImage(image, foundImagesLabel1);
//				foundImagesLabel1.setIcon(icon);
//			} catch (IOException e1) {
//				// remove item from array list
//				originalValue.remove(currentLink);
//				System.out.println("removed element: " + currentLink);
//				// currentData
//				e1.printStackTrace();
//			} catch (NullPointerException e) {
//				// remove item from array list
//				originalValue.remove(currentLink);
//				System.out.println("removed element: " + currentLink);
//				e.printStackTrace();
//			}
//
//		}
//
//		// print out final array
//		System.out.println("final array");
//
//		for (Data currentData : originalValue) {
//			System.out.println(currentData.getContentUrl());
//		}
//
//		// TODO change return
//		return originalValue;
//
//	}

	protected void setImageAsImageIcon(String link, JLabel label) {

		URL linkAsUrl = null;
		try {
			linkAsUrl = new URL(link);
			// set image as Buffered image
			imgLabels = ImageIO.read(linkAsUrl);
			icon = scaleBufferedImage(imgLabels, label);
			label.setIcon(icon);
		} catch (MalformedURLException e2) {

			// TODO new error message as picture - wrong url
			e2.printStackTrace();
		} catch (IOException e1) {
			try {
				// TODO here is actually IIO exception, may be make new error
				// message
				imgLabels = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			icon = scaleBufferedImage(imgLabels, label);
			label.setIcon(icon);
			e1.printStackTrace();
			// added catch for null pointer exception, now app finds some more
			// images
		} catch (NullPointerException e) {
			 try {
			 // display fake error message
				 imgLabels = (BufferedImage) ImageIO.read(new File("img/error.png"));
			 } catch (IOException e2) {
			 e2.printStackTrace();
			 }
			 icon = scaleBufferedImage(imgLabels, label);
			 label.setIcon(icon);
			e.printStackTrace();
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

		// limit number of tags displayed to max first six
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
	

	protected void takePicture() {

		WebcamAPI camera = new WebcamAPI();

		try {

			camera.turnWebcamOn();
			System.out.println("camera works");

			imgFromCam = camera.getImageWebcam();
			
			// here null pointer exception

			// imgFromCam is null, how to pass BufferedImage to it?
			icon = scaleBufferedImage(imgFromCam, originalImageLabel);
			originalImageLabel.setIcon(icon);
			
			System.out.println("width " + imgFromCam.getWidth());
			// return bufferedImage
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	protected void saveFileChooser(String fileUrl) {

		fc.setDialogTitle("Specify name of the file to save");

//		// get name of file without url things, but with extension
//		String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());
//
//		// some files have name like this: 1581_003.jpg?imgmax=512
//		// leave just the part up to .jpg
//		if (fileName.contains("?")) {
//			fileName = fileName.substring(0, fileName.lastIndexOf('?'));
//		}
//
//		System.out.println("filename " + fileName);
//
//		String pathToFile = fc.getCurrentDirectory().toString();
//		File output = new File(pathToFile + "/" + fileName);

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
		// }
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
		System.out.println("image height = " + height);

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