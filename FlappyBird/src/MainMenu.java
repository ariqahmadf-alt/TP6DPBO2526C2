import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Tampilan menu utama yang muncul saat program dibuka.
 * Berisi judul dan dua tombol: memulai permainan dan keluar dari program.
 * Aksi kedua tombol ditentukan oleh class yang membuat menu ini (lihat App).
 */
public class MainMenu extends JPanel {

    private int width = 360;
    private int height = 640;

    private Image backgroundImage;
    private Image birdImage;

    /**
     * @param startAction aksi yang dijalankan saat tombol "Mulai" ditekan
     * @param exitAction  aksi yang dijalankan saat tombol "Keluar" ditekan
     */
    public MainMenu(ActionListener startAction, ActionListener exitAction) {
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();

        setPreferredSize(new Dimension(width, height));
        setLayout(null);  // posisi komponen diatur manual dengan setBounds

        JButton startButton = createButton("Mulai Main", 340);
        startButton.addActionListener(startAction);
        add(startButton);

        JButton exitButton = createButton("Keluar", 410);
        exitButton.addActionListener(exitAction);
        add(exitButton);
    }

    /** Membuat tombol dengan gaya seragam pada posisi vertikal y. */
    private JButton createButton(String text, int y) {
        JButton button = new JButton(text);
        button.setBounds((width - 200) / 2, y, 200, 50);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setFocusPainted(false);               // hilangkan garis fokus di sekitar teks
        button.setBackground(new Color(245, 190, 50));
        button.setForeground(new Color(60, 40, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Menggambar latar, burung besar, dan judul; tombol digambar otomatis oleh Swing. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, width, height, null);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Burung dibesarkan tiga kali lipat sebagai ilustrasi menu
        g2.drawImage(birdImage, (width - 34 * 3) / 2, 120, 34 * 3, 24 * 3, null);

        // Judul dengan bayangan tipis agar terbaca di atas latar terang
        Font titleFont = new Font("SansSerif", Font.BOLD, 46);
        g2.setFont(titleFont);
        String title = "Flappy Bird";
        int x = (width - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.setColor(new Color(0, 0, 0, 90));
        g2.drawString(title, x + 3, 263);
        g2.setColor(Color.WHITE);
        g2.drawString(title, x, 260);
        g2.dispose();
    }
}
