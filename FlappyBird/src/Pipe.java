import java.awt.*;

/**
 * Model data untuk satu pipa (rintangan).
 * Satu "pasang" pipa di layar terdiri dari dua objek Pipe: pipa atas dan pipa bawah.
 */
public class Pipe {
    private int posX;        // posisi horizontal pipa
    private int posY;        // posisi vertikal pipa
    private int width;       // lebar pipa
    private int height;      // tinggi pipa
    private Image image;     // gambar pipa (pipa atas atau pipa bawah)
    private int velocityX;   // kecepatan horizontal (tidak dipakai saat ini; kecepatan diatur Logic)
    private boolean passed;  // true jika pipa sudah dilewati pemain (agar skor tidak dihitung dua kali)

    /**
     * Membuat pipa baru yang belum dilewati pemain.
     *
     * @param posX   posisi X awal
     * @param posY   posisi Y awal
     * @param width  lebar pipa
     * @param height tinggi pipa
     * @param image  gambar pipa
     */
    public Pipe(int posX, int posY, int width, int height, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.image = image;
        this.velocityX = 0;
        this.passed = false;
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

    public int getVelocityX() { return velocityX; }
    public void setVelocityX(int velocityX) { this.velocityX = velocityX; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
}
