package SetThumbs;

import Assets.GetAssets;
import FFU.Options;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import static FFU.funcs.Find_Month;

public class SetUp extends JFrame{
	private JPanel MainPanel;
	private JFormattedTextField MonthsExcluded;
	public JCheckBox Whitelist;
	private JEditorPane PathsList;
	private JButton Submit;
	private JLabel PreMonthsExcluded;
	private JTextField BlackListedTF;
	public ArrayList<String> BlackList = new ArrayList<>(12);
	public volatile boolean Submitted = false;
	public SetUp() throws IOException{
		Image Icon = ImageIO.read(new GetAssets("icon.bmp").assetFile());
		setIconImage(Icon);
		setSize(new Dimension(1280, 720));

		setTitle("Set Komga Thumbs");
		setContentPane(MainPanel);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
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
		String BaseExcludedText = "Enter a month to be excluded, then press enter(e.g: January*ENTER*)";

		BlackListedTF = new JTextField();
		PreMonthsExcluded = new JLabel();

		MonthsExcluded = new JFormattedTextField();
		MonthsExcluded.setTransferHandler(null);
		MonthsExcluded.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(MonthsExcluded.getText().equals(BaseExcludedText)){
					MonthsExcluded.setText("");
				}
				char c = e.getKeyChar();
				if(!(Character.isLetter(c))){
					if(c == '\n') {
						String month = Find_Month(MonthsExcluded.getText())[1];
						if(!month.equals("Nullity")){
							if (!BlackListedTF.getText().contains(month)) {
								BlackList.add(month);
								BlackList = new ArrayList<>(BlackList
										.stream()
										.sorted(((o1, o2) -> {
											int first = Integer.parseInt(
													Find_Month(o1)[0]);
											int second = Integer.parseInt(
													Find_Month(o2)[0]);

											return first-second;
										}))
										.toList());

								StringBuilder newText = new StringBuilder();

								if(Whitelist.isSelected())
									newText.append("WhiteListed Months:");
								else
									newText.append("BlackListed Months:");

								for(int i = 0; i < BlackList.size(); i++) {
									if(i == 0)
										newText.append(BlackList.get(i));
									else
										newText.append(", ")
												.append(BlackList.get(i));
								}
								BlackListedTF.setText(newText.toString());
							}
							MonthsExcluded.setText("");
						}
						else {
							MonthsExcluded.setText("NOT A MONTH!");
							MonthsExcluded.setFocusable(false);
							MonthsExcluded.setFocusable(true);
						}
					}
					e.consume();
				}else super.keyTyped(e);
			}
		});
		MonthsExcluded.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(
						MonthsExcluded.getText().equals(BaseExcludedText)
						|| MonthsExcluded.getText().equals("NOT A MONTH!")
				){
					MonthsExcluded.setText("");
				}else super.mouseClicked(e);
			}
		});

		PathsList = new JEditorPane();
		PathsList.setText("Click to add folders");

		PathsList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (
						PathsList.getText().equals("CANT BE BLANK, Click to add folders")
								|| PathsList.getText().equals("Click to add folders")
				) {
					PathsList.setText("");
				}
				Preferences preferences = Preferences.userRoot().node(getClass().getName());
				JFileChooser chooser = new JFileChooser(preferences.get(
						"Last Folder Location",
						new File(".").getAbsolutePath())
				);
				chooser.setDialogTitle("Choose Year Paths");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = chooser.getSelectedFile().getAbsolutePath();
					String StringNum = path.replaceAll("[^0-9]", "");
					short Year;
					if (!StringNum.isBlank()) {
						Year = (short) Double.parseDouble(StringNum);
						if (!(Year >= 2018 && Year <= 2099)) {
							StringNum = JOptionPane.showInputDialog("What Year Is This???");
							StringNum = StringNum.replaceAll("[^0-9]", "");
							Year = Short.parseShort(StringNum);
							if (Year < 2018) {
								Year = 0;
								JOptionPane.showMessageDialog(
										null,
										"Year cant be lower than KDA (2018)");
							}
						}
					} else {
						StringNum = JOptionPane.showInputDialog("What Year Is This???");

						StringNum = StringNum == null ? "" : StringNum;

						StringNum = StringNum.replaceAll("[^0-9]", "");
						Year = Short.parseShort(StringNum);
						if (Year < 2018) {
							Year = 0;
							JOptionPane.showMessageDialog(
									null,
									"Year cant be lower than KDA (2018)");
						}
					}
					preferences.put(
							"Last Folder Location",
							chooser.getSelectedFile().getParent());
					if ((!Options.YearPaths.containsKey(Year)) && !(Year == 0)) {
						Options.YearPaths.put(Year, Paths.get(path).toAbsolutePath());
						String Text = path + ", Year:" + Year + ";\n";
						PathsList.setText(PathsList.getText() + Text);
					}
				}
			}
		});


		Whitelist = new JCheckBox();
		Whitelist.addActionListener(e -> {
			if(Whitelist.isSelected()){
				PreMonthsExcluded.setText("Enter WhiteListed Month:");
				BlackListedTF.setText(
						BlackListedTF.getText().replace("BlackListed", "WhiteListed")
				);
			}else{
				PreMonthsExcluded.setText("Enter BlackListed Month:");
				BlackListedTF.setText(
						BlackListedTF.getText().replace("WhiteListed", "BlackListed")
				);
			}
		});

		try(InputStream fontIS = new GetAssets("JetBrainsMono-Bold.ttf").assetIS()){
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontIS).deriveFont(16f);
			Whitelist.setFont(font);
		}catch (IOException | FontFormatException e){
			throw new RuntimeException(e);
		}
		try(InputStream fontIS = new GetAssets("JetBrainsMono-Regular.ttf").assetIS()){
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontIS).deriveFont(18f);
			PathsList.setFont(font);
			MonthsExcluded.setFont(font);
			PreMonthsExcluded.setFont(font);
			BlackListedTF.setFont(font.deriveFont(17.4f));
		}catch (IOException | FontFormatException e){
			throw new RuntimeException(e);
		}



		Submit = new JButton();
		Submit.addActionListener(e -> {
			if(!(PathsList.getText().strip().isBlank()
					|| PathsList.getText().equals("CANT BE BLANK, Click to add folders")
					|| PathsList.getText().equals("Click to add folders")
			)){
				Submitted = true;
			}else{
				PathsList.setText("CANT BE BLANK, Click to add folders");
			}
		});
	}
}
