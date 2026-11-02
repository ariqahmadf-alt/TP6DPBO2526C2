import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Pembungkus sederhana untuk memutar satu file audio berformat WAV.
 * Satu objek Sound mewakili satu berkas suara yang dimuat ke memori sekali
 * di awal, sehingga pemutaran berikutnya tidak perlu membaca berkas lagi.
 *
 * Jika audio gagal dimuat (berkas tidak ada atau perangkat suara tidak tersedia),
 * objek tetap dapat dipakai dan semua method-nya tidak melakukan apa-apa,
 * sehingga game tetap bisa dimainkan tanpa suara.
 */
public class Sound {
    private Clip clip;  // data audio di memori; null jika gagal dimuat

    /**
     * Memuat berkas audio dari classpath.
     *
     * @param path lokasi berkas relatif terhadap classpath, misalnya "assets/flap.wav"
     */
    public Sound(String path) {
        try (InputStream raw = getClass().getResourceAsStream(path)) {
            if (raw == null) {
                System.err.println("Berkas audio tidak ditemukan: " + path);
                return;
            }
            // BufferedInputStream diperlukan agar AudioSystem dapat mengenali format berkas
            AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(raw));
            clip = AudioSystem.getClip();
            clip.open(stream);
        } catch (Exception e) {
            // Perangkat suara tidak ada atau format tidak didukung: lanjut tanpa suara
            System.err.println("Audio tidak dapat dimuat (" + path + "): " + e.getMessage());
            clip = null;
        }
    }

    /** Memutar suara dari awal satu kali. Jika masih berbunyi, diulang dari awal. */
    public void play() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Memutar suara dari awal secara berulang tanpa henti (dipakai untuk musik latar). */
    public void loop() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** Menghentikan suara yang sedang berbunyi. */
    public void stop() {
        if (clip == null) return;
        clip.stop();
    }

    /**
     * Mengatur volume suara.
     *
     * @param volume nilai 0.0 (hening) sampai 1.0 (volume asli)
     */
    public void setVolume(float volume) {
        if (clip == null || !clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        // Konversi skala linear 0..1 ke desibel agar perubahan volume terdengar wajar
        float db = (float) (20.0 * Math.log10(Math.max(volume, 0.0001f)));
        gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
    }
}
