import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ImageTry {

	private JFrame frame;

	private ImageIcon icon;
	BufferedImage image, flippedImage;

	// normal links
	String link1 = "http://www.blirk.net/wallpapers/1920x1080/sea-wallpaper-6.jpg";
	String link2 = "https://upload.wikimedia.org/wikipedia/commons/c/c0/Gingerbread_House_Essex_CT.jpg";
	String link3 = "http://r.ddmcdn.com/s_f/o_1/cx_633/cy_0/cw_1725/ch_1725/w_720/APL/uploads/2014/11/too-cute-doggone-it-video-playlist.jpg";

	// problem links:

	// java.lang.NullPointerException
	String linkNull1 = "http://upload.wikimedia.org/wikipedia/commons/1/13/Green_Grass.JPG";
	String linkNull2 = "http://upload.wikimedia.org/wikipedia/commons/7/7a/Caribbean_sea_-_Morrocoy_National_Park_-_Playa_escondida.jpg";

	String linkNull3 = "http://lghttp.21049.nexcesscdn.net/809F1B/mage/media/catalog/product/cache/1/image/1500x/9df78eab33525d08d6e5fb8d27136e95/3/1/314740mbb321990-17oz-oil-bottle-bormioli-rocco.jpg";

	String linkNull4 = "http://upload.wikimedia.org/wikipedia/commons/8/8f/2009-03-20_Red_car_NB_on_S_Lasalle_St_in_Durham.jpg";

	String linkNull5 = "http://benjamindeibertphotography.files.wordpress.com/2010/02/wine-glass-1.jpg";
	String linkNull6 = "http://thenaiveobserver.files.wordpress.com/2011/04/ear.jpg";

	// javax.imageio.IIOException: Can't get input stream from URL!
	// Caused by: java.net.UnknownHostException: upmcrelocation.co.in
	String linkIIO1 = "http://upmcrelocation.co.in/superscapes/wp-content/uploads/2014/07/hackberry_tree_20131230_1040936985.png";

	// Caused by: java.io.FileNotFoundException:
	// http://newsroom.ucla.edu/_gallery/get_file/\?file_id=52e7c0f3f6091d782f000b49&file_ext=.jpg
	String linkIIO2 = "http://newsroom.ucla.edu/portal/ucla/artwork/0/9/3/4/0/209340/Sun_credit_The_satellite_Solar_and_Heliospheric_observatory_an_international_project_of_ESA_and_NASA_.jpg";

	// Caused by: java.io.IOException: Server returned HTTP response code: 403
	// for URL:
	// http://swcrown.com/wp-content/uploads/2014/02/Stunning-blue-car-wallpaper.jpg
	String linkIIO3 = "http://swcrown.com/wp-content/uploads/2014/02/Stunning-blue-car-wallpaper.jpg";

	// Caused by: java.io.IOException: Server returned HTTP response code: 403
	// for URL: http://peacechild.org/wp-content/uploads/2013/06/water.jpg
	String linkIIO4 = "http://peacechild.org/wp-content/uploads/2013/06/water.jpg";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ImageTry window = new ImageTry();
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
	public ImageTry() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lbl1st = new JLabel();
		lbl1st.setSize(150, 150);
		frame.getContentPane().add(lbl1st, BorderLayout.NORTH);

		JLabel lbl2nd = new JLabel();
		lbl2nd.setSize(150, 150);
		frame.getContentPane().add(lbl2nd, BorderLayout.CENTER);

		JLabel lbl3rd = new JLabel();
		lbl3rd.setSize(150, 150);
		frame.getContentPane().add(lbl3rd, BorderLayout.SOUTH);

		setImageAsImageIcon(link3, lbl1st);

		setImageAsImageIconMirrored(link3, lbl2nd);

		getImageHttp(linkIIO4, lbl3rd);
	}

	private void getImageHttp(String link, JLabel label) {

		HttpResponse response = null;
		InputStream is = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(link);
		System.out.println("===========");
		System.out.println("link: " + link);
		try {
			response = client.execute(request);
			is = response.getEntity().getContent();
			image = ImageIO.read(is);
			System.out.println("done using stream");
		} catch (ClientProtocolException e) {

			try {
				// display fake error message
				image = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			e.printStackTrace();
		} catch (IOException e) {
			try {
				// display fake error message
				image = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}

		icon = scaleBufferedImage(image, label);
		label.setIcon(icon);
	}

	private void setImageAsImageIconMirrored(String linkToFlip, JLabel lblFlip) {

		URL linkAsUrl = null;
		try {

			linkAsUrl = new URL(linkToFlip);
			System.out.println("===========");
			System.out.println("link: " + linkAsUrl);
			image = ImageIO.read(linkAsUrl);

			// Flip the image horizontally
			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-image.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);

			icon = scaleBufferedImage(image, lblFlip);
			lblFlip.setIcon(icon);
			System.out.println("mirrored image set as icon");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
			try {
				// display fake error message
				image = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			icon = scaleBufferedImage(image, lblFlip);
			lblFlip.setIcon(icon);
			e.printStackTrace();
		}
	}

	protected void setImageAsImageIcon(String link, JLabel label) {

		URL linkAsUrl = null;
		try {

			linkAsUrl = new URL(link);
			System.out.println("===========");
			System.out.println("link: " + linkAsUrl);

			image = ImageIO.read(linkAsUrl);
			icon = scaleBufferedImage(image, label);
			label.setIcon(icon);
			System.out.println("image set as icon");
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
			try {
				// display fake error message
				image = (BufferedImage) ImageIO.read(new File("img/error.png"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			icon = scaleBufferedImage(image, label);
			label.setIcon(icon);
			e.printStackTrace();
		}
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
			double newHight = height / scaleWidth;
			icon = new ImageIcon(icon.getImage().getScaledInstance((int) newWidth, (int) newHight, Image.SCALE_SMOOTH));
		} else {
			// vertical image
			double newHeight = height / scaleHeight;
			icon = new ImageIcon(icon.getImage().getScaledInstance(-1, (int) newHeight, Image.SCALE_SMOOTH));
		}
		return icon;
	}
}
