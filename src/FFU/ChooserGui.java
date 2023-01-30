package FFU;

import Assets.GetAssets;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ChooserGui extends JFrame{
	private JButton SetThumbnails;
	private JButton FileFixerUtil;
	private JPanel Panel;
	public int Chosen = 0;

	public ChooserGui() throws IOException {
		Image Icon = ImageIO.read(new GetAssets("icon.bmp").assetFile());
		setIconImage(new ImageIcon(Icon).getImage());
		setSize(240, 140);
		setResizable(false);
		setTitle("Choose a Utility");
		setContentPane(Panel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	private void createUIComponents() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException
		         | InstantiationException
		         | IllegalAccessException
		         | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		FileFixerUtil = new JButton();
		SetThumbnails = new JButton();

		FileFixerUtil.addActionListener(e -> {Chosen = 1;dispose();});
		SetThumbnails.addActionListener(e -> {Chosen = 2;dispose();});
	}
}
