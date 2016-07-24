import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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

import bingImageSearchApi.RootBing;
import bingImageSearchApi.Urls;
import computerVisionApi.Captions;
import computerVisionApi.RootComputerVision;

public class AppSpringLayout {

	private JButton btnTurnCameraOn, btnCancel, btnSearchForSimilar, btnBrowse, btnAnalyseImage, btnTakeAPicture,
			btnSave;
	private JComboBox safeSearchBox, licenseBox, sizeBox, imageTypeBox;
	private JLabel lblFoundLinks;
	private JFrame frame;
	private JLabel lblSafeSearch_1, originalImageLabel, lblTags, lblDescription, lblImageType, lblSize, lblLicense,
			lblSafeSearch, labelTryLinks;
	private JTextArea tagsTextArea, descriptionTextArea;
	private JTextField urlTextField;
	private JList list;
	private JProgressBar progressBar;
	private JFileChooser fc;
	private File file;
	private ImageIcon icon;

	private SpringLayout springLayout;

	private Webcam webcam;
	private WebcamPanel panel;

	private BufferedImage imageWebcam, originalImage = null, imageResponses = null;

	private String computerVisionImageToken, bingToken, tagsString = "", text, imageTypeString, sizeTypeString,
			licenseTypeString, safeSearchTypeString, searchParameters;
	private ArrayList<String> workingUrls;

	private int foundLinks;
	DefaultListModel listModel = new DefaultListModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppSpringLayout window = new AppSpringLayout();
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
	public AppSpringLayout() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(0, 0, 850, 750);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		frame.setLocationRelativeTo(null);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png");
		fc = new JFileChooser();
		fc.setFileFilter(filter);
		// frame.getContentPane().add(fc);

		btnTurnCameraOn = new JButton("Turn camera on");
		springLayout.putConstraint(SpringLayout.WEST, btnTurnCameraOn, 51, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnTurnCameraOn, -581, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(btnTurnCameraOn);

		urlTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.SOUTH, btnTurnCameraOn, -6, SpringLayout.NORTH, urlTextField);
		springLayout.putConstraint(SpringLayout.WEST, urlTextField, 0, SpringLayout.WEST, btnTurnCameraOn);
		springLayout.putConstraint(SpringLayout.EAST, urlTextField, 274, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(urlTextField);
		urlTextField.setColumns(10);

		originalImageLabel = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, originalImageLabel, 102, SpringLayout.NORTH,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, originalImageLabel, 34, SpringLayout.WEST,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, originalImageLabel, -326, SpringLayout.SOUTH,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, originalImageLabel, -566, SpringLayout.EAST,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, urlTextField, -6, SpringLayout.NORTH, originalImageLabel);
		frame.getContentPane().add(originalImageLabel);

		btnAnalyseImage = new JButton("Analyse image");
		springLayout.putConstraint(SpringLayout.NORTH, btnAnalyseImage, 6, SpringLayout.SOUTH, originalImageLabel);
		springLayout.putConstraint(SpringLayout.WEST, btnAnalyseImage, 58, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnAnalyseImage, 252, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(btnAnalyseImage);

		lblTags = new JLabel("Tags:");
		frame.getContentPane().add(lblTags);

		lblDescription = new JLabel("Description:");
		springLayout.putConstraint(SpringLayout.WEST, lblDescription, 84, SpringLayout.EAST, lblTags);
		springLayout.putConstraint(SpringLayout.NORTH, lblTags, 0, SpringLayout.NORTH, lblDescription);
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 6, SpringLayout.SOUTH, btnAnalyseImage);
		springLayout.putConstraint(SpringLayout.SOUTH, lblDescription, -269, SpringLayout.SOUTH,
				frame.getContentPane());
		frame.getContentPane().add(lblDescription);

		tagsTextArea = new JTextArea();
		springLayout.putConstraint(SpringLayout.NORTH, tagsTextArea, 459, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, tagsTextArea, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, tagsTextArea, -727, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(tagsTextArea);

		descriptionTextArea = new JTextArea();
		springLayout.putConstraint(SpringLayout.NORTH, descriptionTextArea, 0, SpringLayout.SOUTH, lblDescription);
		springLayout.putConstraint(SpringLayout.WEST, descriptionTextArea, 18, SpringLayout.EAST, tagsTextArea);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		frame.getContentPane().add(descriptionTextArea);

		lblImageType = new JLabel("Image type");
		springLayout.putConstraint(SpringLayout.WEST, lblTags, 0, SpringLayout.WEST, lblImageType);
		springLayout.putConstraint(SpringLayout.NORTH, lblImageType, 10, SpringLayout.SOUTH, tagsTextArea);
		springLayout.putConstraint(SpringLayout.WEST, lblImageType, 20, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblImageType);

		lblSize = new JLabel("Size");
		springLayout.putConstraint(SpringLayout.NORTH, lblSize, 21, SpringLayout.SOUTH, lblImageType);
		springLayout.putConstraint(SpringLayout.WEST, lblSize, 20, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblSize);

		lblLicense = new JLabel("License");
		springLayout.putConstraint(SpringLayout.WEST, lblLicense, 20, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblLicense);

		lblSafeSearch = new JLabel("Safe search");
		springLayout.putConstraint(SpringLayout.WEST, lblSafeSearch, 10, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblSafeSearch, 139, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(lblSafeSearch);

		String[] licenseTypes = { "unspecified", "Public", "Share", "ShareCommercially", "Modify" };
		licenseBox = new JComboBox(licenseTypes);
		springLayout.putConstraint(SpringLayout.WEST, licenseBox, 28, SpringLayout.EAST, lblLicense);
		frame.getContentPane().add(licenseBox);

		String[] sizeTypes = { "unspecified", "Small", "Medium", "Large", "Wallpaper" };
		sizeBox = new JComboBox(sizeTypes);
		springLayout.putConstraint(SpringLayout.WEST, sizeBox, 50, SpringLayout.EAST, lblSize);
		springLayout.putConstraint(SpringLayout.EAST, sizeBox, -556, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, licenseBox, 0, SpringLayout.EAST, sizeBox);
		frame.getContentPane().add(sizeBox);

		String[] imageTypes = { "unspecified", "AnimategGif", "Clipart", "Line", "Photo" };
		imageTypeBox = new JComboBox(imageTypes);
		springLayout.putConstraint(SpringLayout.SOUTH, descriptionTextArea, -6, SpringLayout.NORTH, imageTypeBox);
		springLayout.putConstraint(SpringLayout.NORTH, imageTypeBox, 547, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, tagsTextArea, -6, SpringLayout.NORTH, imageTypeBox);
		springLayout.putConstraint(SpringLayout.WEST, imageTypeBox, 6, SpringLayout.EAST, lblImageType);
		springLayout.putConstraint(SpringLayout.EAST, imageTypeBox, -556, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, sizeBox, 10, SpringLayout.SOUTH, imageTypeBox);
		frame.getContentPane().add(imageTypeBox);

		btnBrowse = new JButton("Browse");
		springLayout.putConstraint(SpringLayout.WEST, btnBrowse, 0, SpringLayout.WEST, btnTurnCameraOn);
		springLayout.putConstraint(SpringLayout.EAST, btnBrowse, -583, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(btnBrowse);

		lblSafeSearch_1 = new JLabel("Safe search");
		springLayout.putConstraint(SpringLayout.WEST, lblSafeSearch_1, 20, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblLicense, -17, SpringLayout.NORTH, lblSafeSearch_1);
		frame.getContentPane().add(lblSafeSearch_1);

		String[] safeSearchTypes = { "Strict", "Moderate", "Off" };
		safeSearchBox = new JComboBox(safeSearchTypes);
		springLayout.putConstraint(SpringLayout.SOUTH, licenseBox, -6, SpringLayout.NORTH, safeSearchBox);
		springLayout.putConstraint(SpringLayout.WEST, safeSearchBox, 4, SpringLayout.EAST, lblSafeSearch_1);
		springLayout.putConstraint(SpringLayout.EAST, safeSearchBox, 0, SpringLayout.EAST, licenseBox);
		frame.getContentPane().add(safeSearchBox);

		btnSearchForSimilar = new JButton("Search for similar images");
		springLayout.putConstraint(SpringLayout.SOUTH, lblSafeSearch_1, -13, SpringLayout.NORTH, btnSearchForSimilar);
		springLayout.putConstraint(SpringLayout.SOUTH, safeSearchBox, -6, SpringLayout.NORTH, btnSearchForSimilar);
		springLayout.putConstraint(SpringLayout.NORTH, btnSearchForSimilar, 689, SpringLayout.NORTH,
				frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, btnSearchForSimilar, 36, SpringLayout.WEST,
				frame.getContentPane());
		frame.getContentPane().add(btnSearchForSimilar);

		btnCancel = new JButton("Cancel");
		springLayout.putConstraint(SpringLayout.NORTH, btnCancel, 0, SpringLayout.NORTH, btnBrowse);
		btnCancel.setVisible(false);
		frame.getContentPane().add(btnCancel);

		btnSave = new JButton("Save");
		springLayout.putConstraint(SpringLayout.WEST, btnCancel, 6, SpringLayout.EAST, btnSave);
		springLayout.putConstraint(SpringLayout.NORTH, btnSave, 0, SpringLayout.NORTH, btnBrowse);
		btnSave.setVisible(false);
		frame.getContentPane().add(btnSave);

		btnTakeAPicture = new JButton("Take a picture");
		springLayout.putConstraint(SpringLayout.WEST, btnTakeAPicture, 114, SpringLayout.EAST, btnBrowse);
		springLayout.putConstraint(SpringLayout.WEST, btnSave, 6, SpringLayout.EAST, btnTakeAPicture);
		springLayout.putConstraint(SpringLayout.NORTH, btnTakeAPicture, 0, SpringLayout.NORTH, btnBrowse);
		btnTakeAPicture.setVisible(false);
		frame.getContentPane().add(btnTakeAPicture);

		//// JScrollPane scroll = new JScrollPane(list);
		// Constraints c = springLayout.getConstraints(list);
		// frame.getContentPane().add(scroll, c);

		list = new JList();
		JScrollPane scroll = new JScrollPane(list);
		springLayout.putConstraint(SpringLayout.EAST, descriptionTextArea, -89, SpringLayout.WEST, list);
		springLayout.putConstraint(SpringLayout.EAST, list, -128, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, list, 64, SpringLayout.EAST, licenseBox);
		springLayout.putConstraint(SpringLayout.NORTH, list, 0, SpringLayout.NORTH, btnTurnCameraOn);
		springLayout.putConstraint(SpringLayout.SOUTH, list, -35, SpringLayout.SOUTH, lblSafeSearch_1);

		Constraints c = springLayout.getConstraints(list);
		frame.getContentPane().add(scroll, c);

		// frame.getContentPane().add(list);

		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.NORTH, progressBar, 15, SpringLayout.SOUTH, list);
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 24, SpringLayout.EAST, safeSearchBox);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, 389, SpringLayout.WEST, btnTakeAPicture);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		Border border = BorderFactory
				.createTitledBorder("We are checking every image, pixel by pixel, it may take a while...");
		progressBar.setBorder(border);
		frame.getContentPane().add(progressBar);

		labelTryLinks = new JLabel();
		labelTryLinks.setVisible(false);
		springLayout.putConstraint(SpringLayout.NORTH, labelTryLinks, -16, SpringLayout.SOUTH, btnSearchForSimilar);
		springLayout.putConstraint(SpringLayout.WEST, labelTryLinks, -61, SpringLayout.EAST, licenseBox);
		springLayout.putConstraint(SpringLayout.SOUTH, labelTryLinks, 0, SpringLayout.SOUTH, btnSearchForSimilar);
		springLayout.putConstraint(SpringLayout.EAST, labelTryLinks, 0, SpringLayout.EAST, licenseBox);
		frame.getContentPane().add(labelTryLinks);

		lblFoundLinks = new JLabel("");
		springLayout.putConstraint(SpringLayout.NORTH, lblFoundLinks, -29, SpringLayout.NORTH, progressBar);
		springLayout.putConstraint(SpringLayout.WEST, lblFoundLinks, 6, SpringLayout.EAST, scroll);
		springLayout.putConstraint(SpringLayout.SOUTH, lblFoundLinks, -13, SpringLayout.NORTH, progressBar);
		springLayout.putConstraint(SpringLayout.EAST, lblFoundLinks, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(lblFoundLinks);

		// label to get coordinates for web camera panel
		// lblNewLabel_1 = new JLabel("New label");
		// springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 0,
		// SpringLayout.NORTH, btnTurnCameraOn);
		// springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 98,
		// SpringLayout.EAST, originalImagesLabel);
		// springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel_1, -430,
		// SpringLayout.SOUTH, lblSafeSearch_1);
		// springLayout.putConstraint(SpringLayout.EAST, lblNewLabel_1, 0,
		// SpringLayout.EAST, btnCancel);
		// frame.getContentPane().add(lblNewLabel_1);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					openFilechooser();
				}
			}
		});

		btnTurnCameraOn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("turn camera on");
				turnCameraOn();
				btnCancel.setVisible(true);
				btnTakeAPicture.setVisible(true);
			}
		});

		btnTakeAPicture.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSave.setVisible(true);
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

		btnSave.addActionListener(new ActionListener() {
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
			public void actionPerformed(ActionEvent e) {
				btnTakeAPicture.setVisible(false);
				btnCancel.setVisible(false);
				btnSave.setVisible(false);
				webcam.close();
				panel.setVisible(false);
			}
		});

		urlTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (urlTextField.getText().length() > 0) {
					String linkNew = urlTextField.getText();
					displayImage(linkNew, originalImageLabel);
					originalImage = imageResponses;
				}
			}
		});

		btnAnalyseImage.addActionListener(new ActionListener() {
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

				listModel.clear();

				Token bingImageToken = new Token();
				String bingImageTokenFileName = "SearchApiToken.txt";

				bingToken = bingImageToken.getApiToken(bingImageTokenFileName);

				// in case user edited description or tags, update it and
				// replace new line character, spaces and breaks with %20
				text = descriptionTextArea.getText().replace(" ", "%20").replace("\r", "%20").replace("\n", "%20");
				String tagsString = tagsTextArea.getText().replace(" ", "%20").replace("\r", "%20").replace("\n",
						"%20");

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
							workingUrls = searchToDisplayOnJList(searchParameters, imageTypeString, sizeTypeString,
									licenseTypeString, safeSearchTypeString);
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

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				System.out.println("selected item " + list.getSelectedIndex() + "filename");
				// image icon
				System.out.println("hopefully file url " + list.getSelectedValue());
				
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

//					URL fileNameAsUrl = new URL(fileUrl);
					originalImage = ImageIO.read(fileNameAsUrl);
					ImageIO.write(toBufferedImage(originalImage), "jpeg", output);

					System.out.println("image saved, in the folder: " + output.getAbsolutePath());

				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	protected ArrayList<String> searchToDisplayOnJList(String text, String imageType, String sizeType,
			String licenseType, String safeSearchType) {

		SearchForSimilarImages bingSearch = new SearchForSimilarImages();
		String responseBing = bingSearch.GetUrlContentAsString(bingToken, text, imageType, sizeType, licenseType,
				safeSearchType);

		GsonBuilder gsonBingBuilder = new GsonBuilder();
		Gson gsonBing = gsonBingBuilder.create();
		RootBing rootBing = gsonBing.fromJson(responseBing, RootBing.class);

		// created temporary array list with all urls
		ArrayList<Urls> originalUrls = rootBing.getValue();

		// filtered only working links
		ArrayList<String> checkedUrls = checkLinks(originalUrls);
		// System.out.println("Links to display: ");

		// converted it to a string array
		String[] linksResponse = new String[checkedUrls.size()];
		linksResponse = checkedUrls.toArray(linksResponse);

		listModel.clear();

		for (String item : linksResponse) {
			String listItemText = item;
			// System.out.println("current link: " + listItemText);
			displayImageOnJList(item);

			// get width and height of image
			int height = imageResponses.getHeight();
			int width = imageResponses.getWidth();

			// shows only for the last one
			// TODO modify - may be with ListCellRenderer
			// subclass DefaultListCellRenderer and override the
			// getListCellRendererComponent() method
			// list.setToolTipText("<html><img src=\"" + item + "\">");
			listModel.addElement(icon);
		}
		list.setModel(listModel);

		System.out.println("found all links");
		progressBar.setVisible(false);
		lblFoundLinks.setText("");
		return checkedUrls;
	}

	protected ArrayList<String> checkLinks(ArrayList<Urls> originalValue) {

		// here we convert our Data arrayList to a String arrayList
		ArrayList<String> stringArray = new ArrayList<String>();

		for (Urls currentData : originalValue) {
			stringArray.add(currentData.getContentUrl());
		}

		// get an iterator
		Iterator iter = stringArray.iterator();
		String strElement = "";
		URL linkAsUrl = null;

		foundLinks = 0;
		while (iter.hasNext()) {
			strElement = (String) iter.next();
			// System.out.println("============");
			// System.out.println("CHECKING: " + strElement);
			try {
				linkAsUrl = new URL(strElement);
				imageResponses = ImageIO.read(linkAsUrl);
				displayImage(strElement, labelTryLinks);
				foundLinks++;
				// System.out.println("OK");
				lblFoundLinks.setText(foundLinks + " images found");
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
		}
		return stringArray;
	}

	protected void displayImageOnJList(String link) {

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
		icon = scaleBufferedImageWithoutLabel(imageResponses);
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

		tagsTextArea.setText("");

		for (int i = 0; i < numberOfTags; i++) {
			tagsTextArea.append(tags[i] + "\n");
			// get tags from string array to a string variable
			tagsString = tagsString + tags[i] + "%20";
		}

		for (Captions currentCaption : root.getDescription().getCaptions()) {
			text = currentCaption.getText();
			System.out.println("description: " + text);
			System.out.println("=============");
			descriptionTextArea.setText(text);
		}
		String textString = text.replace(" ", "%20");

		return tagsString + textString;
	}

	protected void displayImage(String link, JLabel label) {

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

	private BufferedImage toBufferedImage(Image imageToGetBuffered) {

		if (imageToGetBuffered instanceof BufferedImage) {
			return (BufferedImage) imageToGetBuffered;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(imageToGetBuffered.getWidth(null), imageToGetBuffered.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);

		return bimage;
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

	protected ImageIcon scaleBufferedImageWithoutLabel(BufferedImage img) {

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(img);
			double width = icon.getIconWidth();
			double height = icon.getIconHeight();
			double labelWidth = 300;
			double labelHight = 300;
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

	protected void turnCameraOn() {

		// get default webcam and open it
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		panel = new WebcamPanel(webcam);
		panel.setMirrored(true);

		springLayout.putConstraint(SpringLayout.NORTH, panel, 0, SpringLayout.NORTH, btnTurnCameraOn);
		springLayout.putConstraint(SpringLayout.WEST, panel, 98, SpringLayout.EAST, originalImageLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, panel, -430, SpringLayout.SOUTH, lblSafeSearch_1);
		springLayout.putConstraint(SpringLayout.EAST, panel, 0, SpringLayout.EAST, btnCancel);

		frame.getContentPane().add(panel);
		System.out.println("set the camera");
	}
}