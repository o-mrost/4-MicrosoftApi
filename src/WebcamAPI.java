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

	BufferedImage image;

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
				System.out.println("picture taken");
				setImageWebcam(imageWebcam);
			}
		});
		window.add(btn, BorderLayout.SOUTH);

		window.setResizable(true);

		// this closes all our app, how to close only webcam and turn it off?
		// window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);

		System.out.println("alles gut");

		if (imageWebcam == null) {
			System.out.println("image is null, take a picture");
		}

		System.out.println("height 2 " + imageWebcam.getHeight());
		return imageWebcam;
	}

	protected BufferedImage takePicture() {

		// date stamp to add to every picture taken with a webcam

		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
		String dateTime = dateFormat.format(date);

		// TODO make it more flexible, and not depending on the name of
		// user, may be create a folder on desktop
		String fileName = "/Users/olgamrost/Desktop/WebCam/" + "img-" + dateTime + ".png";

		image = webcam.getImage();
		setImage(webcam.getImage());

		try {

			ImageIO.write(image, "PNG", new File(fileName));
			System.out.println("image stored at " + fileName);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Height " + image.getHeight());
		return image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImageWebcam() {
		return imageWebcam;
	}

	public void setImageWebcam(BufferedImage imageWebcam) {
		this.imageWebcam = imageWebcam;
	}
}