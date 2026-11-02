import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Pusat seluruh aturan permainan Flappy Bird ("otak" di balik layar).
 *
 * Tugas class ini:
 *  - menyimpan data permainan (pemain, daftar pipa, skor, status game over),
 *  - menjalankan game loop lewat Timer (gravitasi, pergerakan pipa, deteksi tabrakan, skor),
 *  - menerima input keyboard (spasi untuk melompat, R untuk mengulang, Esc untuk ke menu),
 *  - memutar musik latar dan efek suara.
 *
 * Class View hanya bertugas menggambar berdasarkan data yang dimiliki Logic.
 */
public class Logic implements ActionListener, KeyListener {

    // ---------- ukuran layar ----------
    int frameWidth = 360;
    int frameHeight = 640;

    // ---------- posisi dan ukuran awal pemain ----------
    int playerStartPosX = frameWidth / 2;
    int playerStartPosY = frameHeight / 2;
    int playerWidth = 34;
    int playerHeight = 24;

    // ---------- posisi dan ukuran awal pipa ----------
    int pipeStartPosX = frameWidth;  // pipa muncul tepat di luar sisi kanan layar
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // ---------- pengaturan fisika ----------
    int gravity = 1;         // tambahan kecepatan turun pada setiap frame
    int jumpVelocity = -10;  // kecepatan vertikal saat melompat (negatif = ke atas)
    int pipeVelocityX = -2;  // kecepatan pipa bergerak ke kiri pada setiap frame

    // ---------- timer ----------
    Timer gameLoop;       // memanggil actionPerformed ±60 kali per detik
    Timer pipesCooldown;  // memunculkan sepasang pipa baru setiap 1,5 detik

    // ---------- komponen permainan ----------
    View view;
    Image birdImage;
    Player player;

    Image lowerPipeImage;
    Image upperPipeImage;
    ArrayList<Pipe> pipes;

    // ---------- status permainan ----------
    private int score = 0;            // jumlah pasang pipa yang sudah dilewati
    private boolean gameOver = false; // true jika pemain sudah menabrak atau jatuh

    // ---------- audio ----------
    private Sound bgm;    // musik latar
    private Sound flap;   // efek saat melompat
    private Sound point;  // efek saat skor bertambah
    private Sound hit;    // efek saat game over

    // Dipanggil ketika pemain menekan Esc setelah game over (kembali ke menu utama)
    private Runnable onBackToMenu;

    /**
     * Memuat gambar dan audio, lalu menyiapkan objek pemain, daftar pipa, dan kedua Timer.
     * Timer belum dijalankan di sini; permainan baru dimulai saat startGame() dipanggil
     * (yaitu ketika tombol "Mulai" di menu utama ditekan).
     */
    public Logic() {
        // Memuat gambar dari folder assets
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();

        // Memuat audio
        bgm = new Sound("assets/bgm.wav");
        flap = new Sound("assets/flap.wav");
        point = new Sound("assets/point.wav");
        hit = new Sound("assets/hit.wav");
        bgm.setVolume(0.35f);  // musik latar dibuat lebih pelan agar efek suara tetap jelas

        // Timer yang memunculkan pipa baru setiap 1500 ms
        pipesCooldown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                placePipes();
            }
        });

        // Timer game loop: 1000/60 ms per tick, listener-nya adalah class ini sendiri
        gameLoop = new Timer(1000 / 60, this);
    }

    // =====================================================================
    // Getter / setter yang dipakai class lain
    // =====================================================================

    public void setView(View view) {
        this.view = view;
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Pipe> getPipes() {
        return pipes;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    /** Mendaftarkan aksi yang dijalankan saat pemain menekan Esc setelah game over. */
    public void setOnBackToMenu(Runnable onBackToMenu) {
        this.onBackToMenu = onBackToMenu;
    }

    // =====================================================================
    // Siklus permainan: mulai, atur ulang, selesai
    // =====================================================================

    /**
     * Memulai permainan dari awal: mengembalikan semua data ke kondisi awal,
     * menjalankan kedua Timer, dan memutar musik latar.
     * Dipakai untuk permainan pertama maupun saat restart dengan tombol R.
     */
    public void startGame() {
        // Pemain kembali ke posisi tengah dengan kecepatan nol
        player.setPosX(playerStartPosX);
        player.setPosY(playerStartPosY);
        player.setVelocityY(0);

        // Semua pipa dihapus dan skor dikembalikan ke nol
        pipes.clear();
        score = 0;
        gameOver = false;
        if (view != null) {
            view.updateScore(score);
            view.repaint();
        }

        // restart() menjalankan Timer dari awal, baik saat sedang berhenti maupun berjalan
        pipesCooldown.restart();
        gameLoop.restart();

        bgm.loop();
    }

    /**
     * Mengakhiri permainan: menghentikan Timer agar semua objek berhenti bergerak,
     * mematikan musik latar, dan memutar efek tabrakan.
     * Pengecekan gameOver di awal mencegah efek suara terputar dua kali.
     */
    private void endGame() {
        if (gameOver) return;
        gameOver = true;
        gameLoop.stop();
        pipesCooldown.stop();
        bgm.stop();
        hit.play();
    }

    // =====================================================================
    // Pembuatan dan pergerakan pipa
    // =====================================================================

    /**
     * Membuat sepasang pipa (atas dan bawah) di sisi kanan layar.
     * Posisi pipa atas diacak, sedangkan celah di antara kedua pipa selalu sama besar
     * (seperempat tinggi layar) supaya pemain masih dapat melewatinya.
     */
    public void placePipes() {
        // Pipa atas digeser ke atas sebanyak 1/4 sampai 3/4 tinggi pipa (acak)
        int randomPosY = (int) (pipeStartPosY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = frameHeight / 4;  // tinggi celah di antara kedua pipa

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        // Pipa bawah diletakkan di bawah pipa atas, dipisahkan oleh celah
        Pipe lowerPipe = new Pipe(pipeStartPosX, randomPosY + openingSpace + pipeHeight,
                pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    /**
     * Memperbarui seluruh isi permainan untuk satu frame:
     * gravitasi pada pemain, pergerakan pipa, deteksi tabrakan, penghitungan skor,
     * dan pembuangan pipa yang sudah keluar layar.
     */
    public void move() {
        // --- Pemain: kecepatan bertambah karena gravitasi, lalu posisi mengikuti kecepatan ---
        player.setVelocityY(player.getVelocityY() + gravity);
        player.setPosY(player.getPosY() + player.getVelocityY());
        player.setPosY(Math.max(player.getPosY(), 0));  // burung tidak bisa keluar dari sisi atas layar

        // --- Game over jika burung menyentuh batas bawah layar ---
        if (player.getPosY() + player.getHeight() >= frameHeight) {
            player.setPosY(frameHeight - player.getHeight());  // burung berhenti tepat di tepi bawah
            endGame();
            return;
        }

        // --- Pipa: dicek dari indeks terakhir agar aman saat ada yang dihapus ---
        for (int i = pipes.size() - 1; i >= 0; i--) {
            Pipe pipe = pipes.get(i);
            pipe.setPosX(pipe.getPosX() + pipeVelocityX);

            // Game over jika burung bersentuhan dengan pipa
            if (isColliding(player, pipe)) {
                endGame();
                return;
            }

            // Skor +1 saat burung sudah melewati tepi kanan pipa.
            // Hanya pipa atas yang dihitung, sehingga satu pasang pipa bernilai satu poin.
            if (pipe.getImage() == upperPipeImage && !pipe.isPassed()
                    && player.getPosX() > pipe.getPosX() + pipe.getWidth()) {
                pipe.setPassed(true);
                score++;
                point.play();
                if (view != null) view.updateScore(score);
            }

            // Buang pipa yang sudah sepenuhnya keluar dari sisi kiri layar
            if (pipe.getPosX() + pipe.getWidth() < 0) {
                pipes.remove(i);
            }
        }
    }

    /**
     * Mengecek tabrakan antara pemain dan sebuah pipa dengan membandingkan
     * dua persegi panjang (axis-aligned bounding box). Keduanya bertabrakan jika
     * saling tumpang tindih pada sumbu X sekaligus sumbu Y.
     *
     * @return true jika pemain menyentuh pipa
     */
    private boolean isColliding(Player p, Pipe pipe) {
        return p.getPosX() < pipe.getPosX() + pipe.getWidth()    // sisi kiri pemain di kiri sisi kanan pipa
                && p.getPosX() + p.getWidth() > pipe.getPosX()   // sisi kanan pemain di kanan sisi kiri pipa
                && p.getPosY() < pipe.getPosY() + pipe.getHeight() // sisi atas pemain di atas sisi bawah pipa
                && p.getPosY() + p.getHeight() > pipe.getPosY();   // sisi bawah pemain di bawah sisi atas pipa
    }

    // =====================================================================
    // ActionListener: dipanggil oleh gameLoop pada setiap tick
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        if (view != null) {
            view.repaint();  // meminta View menggambar ulang dengan data terbaru
        }
    }

    // =====================================================================
    // KeyListener: input keyboard
    // =====================================================================

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_SPACE && gameLoop.isRunning()) {
            // Spasi: burung melompat. Hanya berlaku saat permainan sedang berjalan.
            player.setVelocityY(jumpVelocity);
            flap.play();
        } else if (code == KeyEvent.VK_R && gameOver) {
            // R: mengulang permainan. Hanya berlaku setelah game over.
            startGame();
        } else if (code == KeyEvent.VK_ESCAPE && gameOver && onBackToMenu != null) {
            // Esc: kembali ke menu utama. Hanya berlaku setelah game over.
            onBackToMenu.run();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
