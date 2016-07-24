import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JComboBox;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bingImageSearchApi.Urls;
import bingImageSearchApi.RootBing;
import computerVisionApi.Captions;
import computerVisionApi.RootComputerVision;
import javax.swing.JList;

public class App {

	private JFileChooser fc;
	private File file;
	private ImageIcon icon;
	private JFrame frame;
	private JTextArea tagsField, descriptionField;
	private JTextField urlField;
	private JButton btnTurnWebcamOn, btnBrowse, btnHelp, btnSearchForSimilar, btnAnalyse;
	private JLabel originalImageLabel, lblTags, lblDescription, foundImagesLabel1, foundImagesLabel2, foundImagesLabel3,
			foundImagesLabel4, labelTryLinks, stepOne, stepTwo, stepThree;
	private JProgressBar progressBar;
	private BufferedImage imageWebcam, originalImage = null, imageResponses = null;

	private Webcam webcam;
	private WebcamPanel panel;

	private String text, tagsString = "", searchParameters, computerVisionImageToken, bingToken, imageTypeString,
			sizeTypeString, licenseTypeString, safeSearchTypeString;
	private String firstImageUrl = null, secondImageUrl = null, thirdImageUrl = null, fourthImageUrl = null;

	private ArrayList<String> workingUrls;
	private String[] linksResponse = null;
	DefaultListModel listModel = new DefaultListModel();

	private JList list;

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
	 * 
	 * @param string
	 */
	public App() {
		super();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 750);
		frame.getContentPane().setLayout(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		frame.getContentPane().add(fc);

		stepOne = new JLabel("");
		stepOne.setToolTipText("here comes something");
		stepOne.setIcon(new ImageIcon("img/stepOne.png"));
		stepOne.setBounds(266, -4, 67, 49);
		frame.getContentPane().add(stepOne);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(66, 6, 117, 29);
		frame.getContentPane().add(btnBrowse);

		btnTurnWebcamOn = new JButton("Take a picture with webcam");
		btnTurnWebcamOn.setBounds(66, 34, 212, 29);
		frame.getContentPane().add(btnTurnWebcamOn);

		JButton btnTakePictureWithWebcam = new JButton("Take a picture");
		btnTakePictureWithWebcam.setBounds(430, 324, 117, 29);
		frame.getContentPane().add(btnTakePictureWithWebcam);
		btnTakePictureWithWebcam.setVisible(false);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(542, 324, 117, 29);
		frame.getContentPane().add(btnCancel);
		btnCancel.setVisible(false);

		JButton btnSaveImage = new JButton("Save image");
		btnSaveImage.setBounds(497, 357, 117, 29);
		frame.getContentPane().add(btnSaveImage);
		btnSaveImage.setVisible(false);

		urlField = new JTextField();
		urlField.setBounds(66, 67, 220, 26);
		frame.getContentPane().add(urlField);
		urlField.setColumns(10);

		originalImageLabel = new JLabel();
		originalImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		originalImageLabel.setBounds(33, 98, 300, 300);
		frame.getContentPane().add(originalImageLabel);

		stepTwo = new JLabel("");
		stepTwo.setToolTipText("here comes something else");
		stepTwo.setIcon(new ImageIcon("img/stepTwo.png"));
		stepTwo.setBounds(266, 413, 67, 49);
		frame.getContentPane().add(stepTwo);

		btnAnalyse = new JButton("Analyse image");
		btnAnalyse.setBounds(68, 423, 196, 29);
		frame.getContentPane().add(btnAnalyse);

		tagsField = new JTextArea();
		tagsField.setBounds(23, 479, 102, 89);
		tagsField.setLineWrap(true);
		tagsField.setWrapStyleWord(true);
		frame.getContentPane().add(tagsField);
		tagsField.setColumns(10);

		lblTags = new JLabel("Tags:");
		lblTags.setBounds(46, 451, 61, 16);
		frame.getContentPane().add(lblTags);

		descriptionField = new JTextArea();
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		descriptionField.setBounds(137, 479, 187, 89);
		frame.getContentPane().add(descriptionField);
		descriptionField.setColumns(10);

		lblDescription = new JLabel("Description:");
		lblDescription.setBounds(163, 451, 77, 16);
		frame.getContentPane().add(lblDescription);

		stepThree = new JLabel("");
		stepThree.setToolTipText("here comes something different");
		stepThree.setIcon(new ImageIcon("img/stepThree.png"));
		stepThree.setBounds(266, 685, 67, 49);
		frame.getContentPane().add(stepThree);

		JLabel lblImageType = new JLabel("Image type");
		lblImageType.setBounds(23, 580, 102, 16);
		frame.getContentPane().add(lblImageType);

		String[] imageTypes = { "unspecified", "AnimategGif", "Clipart", "Line", "Photo" };
		JComboBox imageTypeBox = new JComboBox(imageTypes);
		imageTypeBox.setBounds(137, 580, 187, 23);
		frame.getContentPane().add(imageTypeBox);

		JLabel lblSizeType = new JLabel("Size");
		lblSizeType.setBounds(23, 608, 102, 16);
		frame.getContentPane().add(lblSizeType);

		String[] sizeTypes = { "unspecified", "Small", "Medium", "Large", "Wallpaper" };
		JComboBox sizeBox = new JComboBox(sizeTypes);
		sizeBox.setBounds(137, 608, 187, 23);
		frame.getContentPane().add(sizeBox);

		JLabel lblLicenseType = new JLabel("License");
		lblLicenseType.setBounds(23, 636, 102, 16);
		frame.getContentPane().add(lblLicenseType);

		String[] licenseTypes = { "unspecified", "Public", "Share", "ShareCommercially", "Modify" };
		JComboBox licenseBox = new JComboBox(licenseTypes);
		licenseBox.setBounds(137, 636, 187, 23);
		frame.getContentPane().add(licenseBox);

		JLabel lblSafeSearchType = new JLabel("Safe search");
		lblSafeSearchType.setBounds(23, 664, 102, 16);
		frame.getContentPane().add(lblSafeSearchType);

		String[] safeSearchTypes = { "Strict", "Moderate", "Off" };
		JComboBox safeSearchBox = new JComboBox(safeSearchTypes);
		safeSearchBox.setBounds(137, 664, 187, 23);
		frame.getContentPane().add(safeSearchBox);

		btnSearchForSimilar = new JButton("Search for similar images");
		btnSearchForSimilar.setVisible(true);
		btnSearchForSimilar.setBounds(66, 695, 189, 29);
		frame.getContentPane().add(btnSearchForSimilar);

		// label to try urls to display images, not shown on the main frame
		labelTryLinks = new JLabel();
		labelTryLinks.setBounds(0, 0, 100, 100);

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

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setBounds(440, 602, 440, 29);

		Border border = BorderFactory
				.createTitledBorder("We are checking every image, pixel by pixel, it may take a while...");
		progressBar.setBorder(border);
		frame.getContentPane().add(progressBar);
		progressBar.setVisible(false);

		btnHelp = new JButton("");
		btnHelp.setBorderPainted(false);
		ImageIcon btnIcon = new ImageIcon("img/helpRed.png");
		btnHelp.setIcon(btnIcon);
		btnHelp.setBounds(917, 4, 77, 59);
		frame.getContentPane().add(btnHelp);

		// all action listeners
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					openFilechooser();
				}
			}
		});

		btnTurnWebcamOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				setAllFoundImagesLabelsAndPreviewsToNull();
				btnTakePictureWithWebcam.setVisible(true);
				btnCancel.setVisible(true);
				turnCameraOn();
			}
		});

		btnTakePictureWithWebcam.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				btnSaveImage.setVisible(true);
				// take a photo with web camera
				imageWebcam = webcam.getImage();
				originalImage = imageWebcam;

				// to mirror the image we create a temporary file, flip it
				// horizontally and then delete

				// get user's name to store file in the user directory
				String user = System.getProperty("user.home");
				String fileName = user + "/webCamPhoto.jpg";
				File newFile = new File(fileName);

				try {
					ImageIO.write(originalImage, "jpg", newFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					originalImage = (BufferedImage) ImageIO.read(newFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				newFile.delete();
				originalImage = mirrorImage(originalImage);
				icon = scaleBufferedImage(originalImage, originalImageLabel);
				originalImageLabel.setIcon(icon);
			}
		});

		btnSaveImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
					try {

						file = fc.getSelectedFile();
						File output = new File(file.toString());
						// check if image already exists
						if (output.exists()) {
							int response = JOptionPane.showConfirmDialog(null, //
									"Do you want to replace the existing file?", //
									"Confirm", JOptionPane.YES_NO_OPTION, //
									JOptionPane.QUESTION_MESSAGE);
							if (response != JOptionPane.YES_OPTION) {
								return;
							}
						}
						ImageIO.write(toBufferedImage(originalImage), "jpg", output);
						System.out.println("Your image has been saved in the folder " + file.getPath());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				btnTakePictureWithWebcam.setVisible(false);
				btnCancel.setVisible(false);
				btnSaveImage.setVisible(false);
				webcam.close();
				panel.setVisible(false);
			}
		});

		urlField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlField.getText().length() > 0) {
					String linkNew = urlField.getText();
					getImageFromHttp(linkNew, originalImageLabel);
					originalImage = imageResponses;
				}
			}
		});

		btnAnalyse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Token computerVisionToken = new Token();
				String computerVisionTokenFileName = "APIToken.txt";

				try {
					computerVisionImageToken = computerVisionToken.getApiToken(computerVisionTokenFileName);
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

				// clear labels in case there were results of previous search
				setAllFoundImagesLabelsAndPreviewsToNull();

				System.out.println("==========================================");
				System.out.println("new search");
				System.out.println("==========================================");

				Token bingImageToken = new Token();
				String bingImageTokenFileName = "SearchApiToken.txt";

				bingToken = bingImageToken.getApiToken(bingImageTokenFileName);

				// in case user edited description or tags, update it and
				// replace new line character, spaces and breaks with %20
				text = descriptionField.getText().replace(" ", "%20").replace("\r", "%20").replace("\n", "%20");
				String tagsString = tagsField.getText().replace(" ", "%20").replace("\r", "%20").replace("\n", "%20");

				imageTypeString = imageTypeBox.getSelectedItem().toString();
				sizeTypeString = sizeBox.getSelectedItem().toString();
				licenseTypeString = licenseBox.getSelectedItem().toString();
				safeSearchTypeString = safeSearchBox.getSelectedItem().toString();

				searchParameters = tagsString + text;
				System.out.println("search parameters: " + searchParameters);

				if (searchParameters.length() != 0) {

					// add new thread for searching, so that progress bar and
					// searching could run simultaneously
					Thread t1 = new Thread(new Runnable() {
						@Override
						public void run() {

							progressBar.setVisible(true);
							searchForSimilarImages(searchParameters, imageTypeString, sizeTypeString, licenseTypeString,
									safeSearchTypeString);
						}

					});
					// start searching for similar images in a separate thread
					t1.start();
				} else {
					JOptionPane.showMessageDialog(null,
							"Please choose first an image to analyse or insert search parameters");
				}
			}
		});

		foundImagesLabel1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (foundImagesLabel1.getIcon() != null) {
					if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						saveFileChooser(firstImageUrl);
					}
				}
			}
		});

		foundImagesLabel2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (foundImagesLabel2.getIcon() != null) {
					if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						saveFileChooser(secondImageUrl);
					}
				}
			}
		});

		foundImagesLabel3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (foundImagesLabel3.getIcon() != null) {
					if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						saveFileChooser(thirdImageUrl);
					}
				}
			}
		});

		foundImagesLabel4.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (foundImagesLabel4.getIcon() != null) {
					if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						saveFileChooser(fourthImageUrl);
					}
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

	protected void setAllFoundImagesLabelsAndPreviewsToNull() {

		foundImagesLabel1.setIcon(null);
		foundImagesLabel2.setIcon(null);
		foundImagesLabel3.setIcon(null);
		foundImagesLabel4.setIcon(null);

		foundImagesLabel1.setToolTipText(null);
		foundImagesLabel2.setToolTipText(null);
		foundImagesLabel3.setToolTipText(null);
		foundImagesLabel4.setToolTipText(null);
	}

	protected void getImageFromHttp(String link, JLabel label) {

		HttpResponse response = null;
		InputStream is = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(link);
		try {
			response = client.execute(request);
			is = response.getEntity().getContent();
			imageResponses = ImageIO.read(is);
		} catch (ClientProtocolException e) {
			try {
				imageResponses = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			try {
				imageResponses = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		// TODO check the problem with bad urls to webpages, not images
		icon = scaleBufferedImage(imageResponses, label);
		label.setIcon(icon);
	}

	protected BufferedImage mirrorImage(BufferedImage imageToFlip) {

		// Flip the image horizontally
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-imageToFlip.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		imageToFlip = op.filter(imageToFlip, null);

		return imageToFlip;
	}

	protected void turnCameraOn() {

		// get default webcam and open it
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		panel = new WebcamPanel(webcam);
		panel.setMirrored(true);
		panel.setBounds(400, 50, 305, 229);
		frame.getContentPane().add(panel);
	}

	protected void searchForSimilarImages(String text, String imageType, String sizeType, String licenseType,
			String safeSearchType) {

		SearchForSimilarImages bingSearch = new SearchForSimilarImages();
		String responseBing = bingSearch.GetUrlContentAsString(bingToken, text, imageType, sizeType, licenseType,
				safeSearchType);

		GsonBuilder gsonBingBuilder = new GsonBuilder();
		Gson gsonBing = gsonBingBuilder.create();
		RootBing rootBing = gsonBing.fromJson(responseBing, RootBing.class);

		// created temp array list with all urls and width and height of images
		ArrayList<Urls> originalUrls = rootBing.getValue();

		// filtered only working links
		ArrayList<String> checkedUrls = checkLinks(originalUrls);
		// System.out.println("Links to display: ");

		int i = 0;
		for (String temp : checkedUrls) {

			if (i == 0) {
				firstImageUrl = temp;
				foundImagesLabel1.setToolTipText("<html><img src=\"" + firstImageUrl + "\">");

				getImageFromHttp(firstImageUrl, foundImagesLabel1);

			} else if (i == 1) {
				secondImageUrl = temp;
				foundImagesLabel2.setToolTipText("<html><img src=\"" + secondImageUrl + "\">");
				getImageFromHttp(secondImageUrl, foundImagesLabel2);

			} else if (i == 2) {
				thirdImageUrl = temp;
				foundImagesLabel3.setToolTipText("<html><img src=\"" + thirdImageUrl + "\">");
				getImageFromHttp(thirdImageUrl, foundImagesLabel3);

			} else if (i == 3) {
				fourthImageUrl = temp;
				foundImagesLabel4.setToolTipText("<html><img src=\"" + fourthImageUrl + "\">");
				getImageFromHttp(fourthImageUrl, foundImagesLabel4);

				progressBar.setVisible(false);
			}
			i++;
		}

		// if not enough images were found, display suggestion to edit search
		if (checkedUrls.size() <= 3) {
			try {
				System.out.println("found less than three images =============");
				imageResponses = (BufferedImage) ImageIO.read(new File("img/warning.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			icon = scaleBufferedImage(imageResponses, foundImagesLabel4);
			foundImagesLabel4.setIcon(icon);
			progressBar.setVisible(false);
		}
	}

	protected ArrayList<String> checkLinks(ArrayList<Urls> originalValue) {

		// here we convert our Data arrayList to a String arrayList
		ArrayList<String> stringArray = new ArrayList<String>();

		for (Urls currentData : originalValue) {
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
			// System.out.println("============");
			// System.out.println("CHECKING: " + strElement);

			try {
				linkAsUrl = new URL(strElement);
				imageResponses = ImageIO.read(linkAsUrl);
				getImageFromHttp(strElement, labelTryLinks);
				// System.out.println("OK");
				// count++;
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

	protected String analyse() {

		AnalyseImage computerVisionSearch = new AnalyseImage();
		String response = computerVisionSearch.describeImage(originalImage, computerVisionImageToken);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		RootComputerVision root = gson.fromJson(response, RootComputerVision.class);
		String[] tags = root.getDescription().getTags();

		System.out.println("=============");
		System.out.println("tags " + Arrays.toString(tags));

		int numberOfTags;
		// limit number of tags displayed to maximum first five
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
			originalImage = ImageIO.read(fileNameAsUrl);
			ImageIO.write(toBufferedImage(originalImage), "jpeg", output);

			System.out.println("image saved, in the folder: " + output.getAbsolutePath());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void openFilechooser() {

		originalImage = null;
		file = fc.getSelectedFile();

		try {
			originalImage = (BufferedImage) ImageIO.read(file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		icon = scaleBufferedImage(originalImage, originalImageLabel);
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

	protected ImageIcon scaleBufferedImageWithoutLabel(BufferedImage img) {

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(img);
			double width = icon.getIconWidth();
			double height = icon.getIconHeight();
			double labelWidth = 150;
			double labelHight = 150;
			double scaleWidth = width / labelWidth;
			double scaleHeight = height / labelHight;

			if (width >= height) {
				// horizontal image
				double newWidth = width / scaleWidth;
				icon = new ImageIcon(icon.getImage().getScaledInstance((int) newWidth, -1, Image.SCALE_SMOOTH));
			} else {
				// vertical image
				double newHeight = height / scaleHeight;
				icon = new ImageIcon(icon.getImage().getScaledInstance(-1, (int) newHeight, Image.SCALE_SMOOTH));
			}
		} catch (NullPointerException e) {
			try {
				originalImage = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}

		return icon;
	}

	protected ImageIcon scaleBufferedImage(BufferedImage img, JLabel label) {

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(img);
			double width = icon.getIconWidth();
			double height = icon.getIconHeight();
			double labelWidth = label.getWidth();
			double labelHight = label.getHeight();
			double scaleWidth = width / labelWidth;
			double scaleHeight = height / labelHight;

			if (width >= height) {
				// horizontal image
				double newWidth = width / scaleWidth;
				icon = new ImageIcon(icon.getImage().getScaledInstance((int) newWidth, -1, Image.SCALE_SMOOTH));
			} else {
				// vertical image
				double newHeight = height / scaleHeight;
				icon = new ImageIcon(icon.getImage().getScaledInstance(-1, (int) newHeight, Image.SCALE_SMOOTH));
			}
		} catch (NullPointerException e) {
			try {
				originalImage = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		}

		return icon;
	}
}