import javax.swing.*;
import java.awt.*;

/**
 * Titik awal program. Membuat jendela (JFrame) dan memasang dua tampilan di dalamnya:
 * menu utama dan layar permainan. Keduanya ditumpuk dengan CardLayout sehingga
 * hanya satu yang terlihat pada satu waktu.
 */
public class App {

    // Nama kartu untuk CardLayout
    private static final String CARD_MENU = "menu";
    private static final String CARD_GAME = "game";

    public static void main(String[] args) {
        // Semua komponen Swing sebaiknya dibuat di thread khusus Swing (Event Dispatch Thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildWindow();
            }
        });
    }

    /** Menyusun jendela, menu utama, dan layar permainan, lalu menampilkannya. */
    private static void buildWindow() {
        JFrame frame = new JFrame("FlappyBird");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Wadah dengan CardLayout: berisi dua "kartu" (menu dan game)
        CardLayout cardLayout = new CardLayout();
        JPanel cards = new JPanel(cardLayout);

        // Logic dan View saling terhubung: View membaca data dari Logic,
        // Logic memanggil View untuk menggambar ulang
        Logic logika = new Logic();
        View tampilan = new View(logika);
        logika.setView(tampilan);

        // Esc setelah game over: kembali menampilkan menu utama
        logika.setOnBackToMenu(new Runnable() {
            @Override
            public void run() {
                cardLayout.show(cards, CARD_MENU);
            }
        });

        // Menu utama: tombol Mulai berpindah ke layar game, tombol Keluar menutup program
        MainMenu menu = new MainMenu(
                e -> {
                    cardLayout.show(cards, CARD_GAME);
                    logika.startGame();
                    tampilan.requestFocusInWindow();  // fokus keyboard diarahkan ke layar game
                },
                e -> System.exit(0)
        );

        cards.add(menu, CARD_MENU);
        cards.add(tampilan, CARD_GAME);
        cardLayout.show(cards, CARD_MENU);  // program dibuka dengan menu utama

        frame.add(cards);
        frame.pack();                       // ukuran jendela mengikuti ukuran panel (360 x 640)
        frame.setLocationRelativeTo(null);  // di tengah layar; dipanggil setelah pack() agar posisinya tepat
        frame.setVisible(true);             // terakhir, setelah semua properti diatur
    }
}
