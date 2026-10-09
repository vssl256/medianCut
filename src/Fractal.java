import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class Fractal {

    static final String OUTPUT = "fractal3.png";
    static final double X_OFFSET = -0.7436438870371587;
    static final double Y_OFFSET = 0.131825940205312;
    static final double SIZE = 0.000003;
    static final int RESOLUTION = 2160;

    static final double X_MIN = X_OFFSET - SIZE, X_MAX = X_OFFSET + SIZE;
    static final double Y_MIN = Y_OFFSET - SIZE, Y_MAX = Y_OFFSET + SIZE;
    static final int MAX_ITER = 1000;

    void main() {
        long startTime = System.nanoTime();

        createImage( OUTPUT, RESOLUTION, RESOLUTION );

        long endTime = System.nanoTime() - startTime;
        System.out.println( endTime / 1_000_000 + " ms" );
    }

    void createImage( String name, int width, int height ) {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        WritableRaster raster = image.getRaster();

        double colorScale = MAX_ITER / 255d;

        IntStream.range( 0, height ).parallel().forEach( screenY -> {
            for ( int screenX = 0; screenX < width; screenX++ ) {
                double x = remap( screenX, 0, width, X_MIN, X_MAX );
                double y = remap( screenY, 0, height, Y_MIN, Y_MAX );

                double zx = 0d, zy = 0d;

                int i = 0;

                double x2, y2;

                while ( i < MAX_ITER ) {
                    x2 = zx * zx;
                    y2 = zy * zy;

                    if ( x2 + y2 > 4d ) break;

                    double yNew = Math.fma( 2d * zx, zy, y );
                    zx = Math.fma( -zy, zy, x2 + x );
                    zy = yNew;
                    i++;
                }

                int color = ( int ) ( i / colorScale );
                raster.setPixel( screenX, screenY, new int[] { color * 2, color / 2, color } );
            }
        } );
        long startTime = System.nanoTime();
        writeImage( name, image );
        long endTime = System.nanoTime() - startTime;
        System.out.println( endTime / 1_000_000 + " ms to write image" );
    }

    double remap( double x, double x1, double x2, double y1, double y2 ) {
        return y1 + ( ( x - x1 ) * ( y2 - y1 ) ) / ( x2 - x1 );
    }

    void writeImage( String path, BufferedImage image ) {
        try {
            File imageFile = new File( path );
            ImageIO.write( image, "png", imageFile );
        } catch ( IOException e ) {
            System.err.println( e.getMessage() );
        }
    }
}
