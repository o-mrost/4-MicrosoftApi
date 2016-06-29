import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamAPI {

	BufferedImage imageWebcam = null;
	JButton btn = new JButton("Take a picture");
	Webcam webcam = Webcam.getDefault();
	JFrame window;

	public WebcamAPI() {
	}

	public BufferedImage turnWebcamOn() throws InterruptedException {

		webcam.setViewSize(WebcamResolution.VGA.getSize());

		webcam.addWebcamListener(new WebcamListener() {

			int imageNumber = 0;

			@Override
			public void webcamOpen(WebcamEvent arg0) {
			}

			@Override
			public void webcamImageObtained(WebcamEvent arg0) {
				imageNumber++;
				// System.out.println("Image " + imageNumber + " Captured!");
			}

			@Override
			public void webcamDisposed(WebcamEvent arg0) {
			}

			@Override
			public void webcamClosed(WebcamEvent arg0) {
			}
		});

		System.out.println("1 camera is turned on");
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(false);
		panel.setDisplayDebugInfo(false);
		panel.setImageSizeDisplayed(false);
		panel.setMirrored(false);

		JFrame window = new JFrame("Test webcam panel");
		window.setLayout(new BorderLayout());
		window.add(panel, BorderLayout.CENTER);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("taking a picture");
				imageWebcam = takePicture();
			}
		});
		window.add(btn, BorderLayout.SOUTH);

		window.setResizable(true);

		// this closes all our app, how to close only webcam and turn it off?
		// window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);

		if (imageWebcam == null) {
			System.out.println("image is null, take a picture");
			wait();
		}

		return imageWebcam;

	}

	protected BufferedImage takePicture() {

		// created date stamp to add to every picture taken with a
		// webcam
		BufferedImage image;

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
		String dateTime = dateFormat.format(date);

		// TODO make it more flexible, and not depending on the name of
		// user, may be create a folder on desktop
		String fileName = "/Users/olgamrost/Desktop/WebCam/" + "img-" + dateTime + ".png";

		image = webcam.getImage();

		try {

			ImageIO.write(image, "PNG", new File(fileName));
			System.out.println("image stored at " + fileName);
			// display(image);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return image;

	}

	// // does not work yet
	// public void display(BufferedImage img) {
	// JFrame frame = new JFrame();
	// JButton okbtn = new JButton("Use this image?");
	//
	// okbtn.addActionListener(new ActionListener() {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// webcam.close();
	// }
	// });
	// frame.setLayout(new BorderLayout());
	//
	// JLabel label = new JLabel(new ImageIcon(img));
	// frame.getContentPane().add(label, BorderLayout.CENTER);
	// frame.add(okbtn, BorderLayout.SOUTH);
	// frame.pack();
	// frame.setVisible(true);
	// }
}