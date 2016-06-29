import java.awt.BorderLayout;
import java.awt.EventQueue;
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
import javax.swing.JLabel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamTry {

	BufferedImage imageWebcam = null;
	JButton btn = new JButton();
	Webcam webcam = Webcam.getDefault();
	JFrame window;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WebcamTry window = new WebcamTry();
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
	public WebcamTry() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblWebcam = new JLabel("Webcam");
		lblWebcam.setBounds(77, 52, 296, 172);
		frame.getContentPane().add(lblWebcam);

		JButton btnTakePicture = new JButton("Take picture");
		btnTakePicture.setBounds(172, 243, 117, 29);
		frame.getContentPane().add(btnTakePicture);

	}

	public BufferedImage getPicture() throws InterruptedException {

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

				// created date stamp to add to every picture taken with a
				// webcam
				Date date = new Date();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
				String dateTime = dateFormat.format(date);

				// TODO make it more flexible, and not depending on the name of
				// user, may be create a folder on desktop
				String fileName = "/Users/olgamrost/Desktop/WebCam/" + "img-" + dateTime + ".png";

				imageWebcam = webcam.getImage();

				try {

					ImageIO.write(imageWebcam, "PNG", new File(fileName));
					System.out.println("image stored at " + fileName);
					// display(image);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		window.add(btn, BorderLayout.SOUTH);

		window.setResizable(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);

		return imageWebcam;

	}
}
