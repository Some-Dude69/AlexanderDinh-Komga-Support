package FilesUtil;

import Assets.GetAssets;
import FFU.Options;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GUI extends JFrame{
	ImageIcon PreviewIcon = new ImageIcon(new BufferedImage(4,4, BufferedImage.TYPE_INT_ARGB));
	BufferedImage BImage;
	int imgWidth;
	int imgHeight;
	public void drawLoop(){
		//noinspection InfiniteLoopStatement
		while (true){
			try {
				//noinspection BusyWait
				Thread.sleep(50);
			}catch(InterruptedException ignored) {}
			Thread.onSpinWait();
			try {
				double ratio = Math.min((double) MainPanel.getWidth() / (double) imgWidth,
						(double) MainPanel.getHeight() / (double) imgHeight);
				if (imgWidth > 0 && imgHeight > 0 && ratio > 0) {
					PreviewIcon = new ImageIcon(BImage.getScaledInstance((int) (imgWidth * ratio),
							(int) (imgHeight * ratio), Image.SCALE_AREA_AVERAGING));
					Preview.setIcon(PreviewIcon);
				}
			}catch(Exception ignored){}
			Split.setDividerLocation(Split.getMaximumDividerLocation());
		}
	}
	public void setPreview(String ImagePath){
		synchronized (this) {
			try {
				BImage = ImageIO.read(new File(ImagePath));

				imgWidth = BImage.getWidth();
				imgHeight = BImage.getHeight();
				try {
					double ratio = Math.min((double) MainPanel.getWidth() / (double) imgWidth,
							(double) MainPanel.getHeight() / (double) imgHeight);

					if (imgWidth > 0 && imgHeight > 0 && ratio > 0) {
						PreviewIcon = new ImageIcon(BImage.getScaledInstance((int) (imgWidth * ratio),
								(int) (imgHeight * ratio), Image.SCALE_AREA_AVERAGING));
						Preview.setIcon(PreviewIcon);
					}
				} catch (Exception ignored) {}
				Split.setDividerLocation(Split.getMaximumDividerLocation());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, """
						Show u Preview coz ur naming scheme bad
						Try Running the WebScraper
						Dont Worry it will continue in the background""");
			}

		}
	}
	public void setPreview(File image_file){
		synchronized (this) {
			try {
				BImage = ImageIO.read(image_file);

				imgWidth = BImage.getWidth();
				imgHeight = BImage.getHeight();
				try {
					double ratio = Math.min((double) MainPanel.getWidth() / (double) imgWidth,
							(double) MainPanel.getHeight() / (double) imgHeight);

					if (imgWidth > 0 && imgHeight > 0 && ratio > 0) {
						PreviewIcon = new ImageIcon(BImage.getScaledInstance((int) (imgWidth * ratio),
								(int) (imgHeight * ratio), Image.SCALE_AREA_AVERAGING));
						Preview.setIcon(PreviewIcon);
					}
				} catch (Exception ignored) {}
				Split.setDividerLocation(Split.getMaximumDividerLocation());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, """
						Show u Preview coz ur naming scheme bad
						Try Running the WebScraper
						Dont Worry it will continue in the background""");
			}

		}
	}
	public void Reset_bar(int Max){
		ProgressBar.setMaximum(Max);
		ProgressBar.setValue(0);
		ProgressBar.setString("Done with: 0/"+ ProgressBar.getMaximum());
	}
	public void Increment_bar(){
		int val = ProgressBar.getValue()+1;
		ProgressBar.setValue(val);
		ProgressBar.setString("Done with: "+val+"/"+ ProgressBar.getMaximum());
	}
	private JPanel MainPanel;
	public JLabel Preview;
	private JSplitPane Split;
	public JProgressBar ProgressBar;

	public GUI() throws IOException{
		Image Icon = ImageIO.read(new GetAssets("icon.bmp").assetFile());
		File Loading_Picture = new GetAssets("Loading Page.png").assetFile();


		setIconImage(Icon);
		setPreview(Loading_Picture);

		setSize(new Dimension(1280, 720));
		if(Options.Maximized)
			setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

		setTitle("File Fixer Util");
		setContentPane(MainPanel);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(Options.ShowGui);
		Split.setDividerLocation(Split.getMaximumDividerLocation()/2);
		ProgressBar.setMinimum(0);
		new Thread(this::drawLoop, "Draw Loop").start();
	}

	private void createUIComponents() throws IOException, FontFormatException {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException
		         | InstantiationException
		         | IllegalAccessException
		         | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}

		InputStream fontIS = new GetAssets("JetBrainsMono-Bold.ttf").assetIS();
		Font font = Font.createFont(Font.TRUETYPE_FONT, fontIS).deriveFont(22f);

		ProgressBar = new JProgressBar();

		ProgressBar.setFont(font);
	}
}
