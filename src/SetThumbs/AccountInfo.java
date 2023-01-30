package SetThumbs;

import Assets.GetAssets;
import javax.swing.*;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;



public class AccountInfo extends JFrame implements ActionListener {
	public static boolean Submit = false;
	public JPanel panel1;
	public JTextField UserField;
	public JTextField IPField;
	public JTextField HashField;
	public JPasswordField passwordField;
	public JButton submitButton;
	public JPanel ServerPane;
	public JPanel AccountPane;

	public AccountInfo() throws IOException {
		Image Icon = ImageIO.read(new GetAssets("icon.bmp").assetFile());
		setIconImage(new ImageIcon(Icon).getImage());
		setSize(new Dimension(690, 296));
		setResizable(false);
		submitButton.addActionListener(this);
		setTitle("Komga Thumbs util");
		setContentPane(panel1);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AccountInfo.Submit = true;
	}
}

