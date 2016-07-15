import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamNew {

	private Webcam webcam;
	BufferedImage imageWebcam;

	public WebcamNew() {
		JFrame webcamWindow = new JFrame("Test webcam panel");
		webcamWindow.setLayout(new BorderLayout());

		// get default webcam and open it
		webcam = Webcam.getDefault();
		webcam.setViewSize(WebcamResolution.VGA.getSize());
		webcam.open();

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setMirrored(true);
		webcamWindow.add(panel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		webcamWindow.add(buttonPanel, BorderLayout.SOUTH);

		JButton okWebcambtn = new JButton("Take a picture");
		buttonPanel.add(okWebcambtn);

		okWebcambtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				takePicture();
			}
		});

		JButton cancelWebcam = new JButton("Cancel");
		buttonPanel.add(cancelWebcam);

		cancelWebcam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				webcam.close();
				webcamWindow.setVisible(false);
			}
		});
		webcamWindow.add(panel);
		webcamWindow.setResizable(true);
		webcamWindow.pack();
		webcamWindow.setVisible(true);
	}

	protected BufferedImage takePicture() {

		imageWebcam = webcam.getImage();
		System.out.println("picture taken");
		imageWebcam = setImageWebcam(imageWebcam);
		// icon = scaleBufferedImage(imageWebcam, originalImageLabel);
		// originalImageLabel.setIcon(icon);
		return imageWebcam;
		
	}

	public BufferedImage getImageWebcam() {
		return imageWebcam;
	}

	public BufferedImage setImageWebcam(BufferedImage imageWebcam) {
		this.imageWebcam = imageWebcam;
		return imageWebcam;
	}

}
