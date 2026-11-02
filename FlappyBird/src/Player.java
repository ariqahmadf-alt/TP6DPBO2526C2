import java.awt.*;

/**
 * Model data untuk burung yang dimainkan pemain.
 * Class ini hanya menyimpan posisi, ukuran, gambar, dan kecepatan vertikal;
 * seluruh aturan permainan (gravitasi, tabrakan, dsb.) ada di class Logic.
 */
public class Player {
    private int posX;       // posisi horizontal (piksel dari sisi kiri panel)
    private int posY;       // posisi vertikal (piksel dari sisi atas panel)
    private int width;      // lebar gambar burung
    private int height;     // tinggi gambar burung
    private Image image;    // gambar burung
    private int velocityY;  // kecepatan vertikal; negatif = naik, positif = turun

    /**
     * Membuat pemain baru dengan kecepatan awal nol.
     *
     * @param posX   posisi X awal
     * @param posY   posisi Y awal
     * @param width  lebar burung
     * @param height tinggi burung
     * @param image  gambar burung
     */
    public Player(int posX, int posY, int width, int height, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.velocityY = 0;
    }

    // ---------- getter dan setter ----------

    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }

    public int getPosY() { return posY; }
    public void setPosY(int posY) { this.posY = posY; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public Image getImage() { return image; }
    public void setImage(Image image) { this.image = image; }

    public int getVelocityY() { return velocityY; }
    public void setVelocityY(int velocityY) { this.velocityY = velocityY; }
}
