import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Kanvas permainan (JPanel). Class ini hanya menggambar: latar, pipa, burung,
 * label skor, dan tulisan "Game Over". Semua data diambil dari Logic.
 */
public class View extends JPanel {

    int width = 360;
    int height = 640;

    private Logic logic;          // sumber data yang akan digambar
    private Image backgroundImage;
    private JLabel scoreLabel;    // label penampil skor di bagian atas layar

    /**
     * @param logic objek Logic yang menyimpan data permainan sekaligus menjadi KeyListener
     */
    public View(Logic logic) {
        this.logic = logic;
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();

        setPreferredSize(new Dimension(width, height));
        setBackground(Color.cyan);  // cadangan bila gambar latar gagal dimuat

        // Agar panel dapat menerima input keyboard, panel harus bisa fokus
        // dan Logic didaftarkan sebagai KeyListener-nya
        setFocusable(true);
        addKeyListener(logic);

        // Label skor diletakkan manual (layout null) di tengah atas layar
        setLayout(null);
        // JLabel dikustomisasi sedikit: teks digambar dua kali (bayangan gelap lalu teks putih)
        // supaya skor tetap terbaca saat berada di atas awan putih
        scoreLabel = new JLabel("0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(2, 2);               // geser sedikit untuk bayangan
                setForeground(new Color(0, 0, 0, 160));
                super.paintComponent(g2);
                g2.dispose();

                setForeground(Color.WHITE);       // teks utama di posisi asli
                super.paintComponent(g);
            }
        };
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 44));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(0, 20, width, 60);
        add(scoreLabel);
    }

    /** Memperbarui teks pada label skor. Dipanggil oleh Logic setiap skor berubah. */
    public void updateScore(int score) {
        scoreLabel.setText(String.valueOf(score));
    }

    /** Dipanggil Swing setiap kali panel perlu digambar (termasuk saat repaint()). */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /** Menggambar seluruh elemen permainan dengan urutan dari belakang ke depan. */
    public void draw(Graphics g) {
        // 1. Latar belakang
        g.drawImage(backgroundImage, 0, 0, width, height, null);

        // 2. Semua pipa
        ArrayList<Pipe> pipes = logic.getPipes();
        if (pipes != null) {
            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(),
                        pipe.getWidth(), pipe.getHeight(), null);
            }
        }

        // 3. Burung
        Player player = logic.getPlayer();
        if (player != null) {
            g.drawImage(player.getImage(), player.getPosX(), player.getPosY(),
                    player.getWidth(), player.getHeight(), null);
        }

        // 4. Tampilan game over di atas semuanya
        if (logic.isGameOver()) {
            drawGameOver(g);
        }
    }

    /** Menggambar lapisan gelap transparan beserta tulisan game over dan petunjuk tombol. */
    private void drawGameOver(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Lapisan gelap transparan agar tulisan mudah dibaca
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        drawCenteredText(g2, "GAME OVER", new Font("SansSerif", Font.BOLD, 40), 280);
        drawCenteredText(g2, "Skor: " + logic.getScore(), new Font("SansSerif", Font.BOLD, 26), 330);
        drawCenteredText(g2, "Tekan R untuk main lagi", new Font("SansSerif", Font.PLAIN, 18), 385);
        drawCenteredText(g2, "Tekan Esc untuk ke menu", new Font("SansSerif", Font.PLAIN, 18), 415);
        g2.dispose();
    }

    /** Menulis teks yang rata tengah secara horizontal pada ketinggian y tertentu. */
    private void drawCenteredText(Graphics2D g2, String text, Font font, int y) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (width - fm.stringWidth(text)) / 2, y);
    }
}
