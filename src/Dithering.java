import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dithering {

    static final String INPUT = "i.png";
    static final String OUTPUT = "dither.png";
    static final List<Pixel> GB_PALETTE = new ArrayList<>();
    static final List<Pixel> BW_PALETTE = new ArrayList<>();
    static final float[][] BAYER_2X2 = new float[][] {
            { 0f, 2f },
            { 3f, 1f }
    };

    void main() {
        initPalette();
        BufferedImage image = readImage( INPUT );

        image = bayer( image );

        writeImage( OUTPUT, image );
    }

    private static BufferedImage bayer( BufferedImage image ) {
        WritableRaster raster = image.getRaster();
        for ( int y = 0; y < raster.getHeight(); y++ ) {
            for ( int x = 0; x < raster.getWidth(); x++ ) {
                Pixel pixel = new Pixel();
                int[] pixelValue = new int[4];
                raster.getPixel( x, y, pixelValue );
                pixel.set( pixelValue );

                Pixel color = getColor( y, x, pixel, BAYER_2X2, BW_PALETTE );
                raster.setPixel( x, y, new int[] { color.R, color.G, color.B } );
            }
        }
        return image;
    }

    private static Pixel getColor(int y, int x, Pixel pixel, float[][] matrix, List<Pixel> palette ) {
        float threshold = ( matrix[ y % matrix.length ][ x % matrix.length ] + 0.5f ) / ( float ) ( matrix.length * matrix.length );
        float brightness = pixel.L / 100f;

        float scaledBrightness = brightness * ( palette.size() - 1 );
        int baseIdx = ( int ) scaledBrightness;
        float fraction = scaledBrightness - baseIdx;

        if ( fraction > threshold ) baseIdx++;

        baseIdx = Math.min( baseIdx, palette.size() - 1 );

        Pixel color = palette.get( baseIdx );
        return color;
    }

    private static void initPalette() {
        GB_PALETTE.add( new Pixel( 15, 56, 15 ) );
        GB_PALETTE.add( new Pixel( 48, 98, 48 ) );
        GB_PALETTE.add( new Pixel( 139, 172, 15 ) );
        GB_PALETTE.add( new Pixel( 155, 188, 15 ) );

        BW_PALETTE.add( new Pixel( 0, 0, 0 ) );
        //BW_PALETTE.add( new Pixel( 85, 85, 85 ) );
        //BW_PALETTE.add( new Pixel( 170, 170, 170 ) );
        BW_PALETTE.add( new Pixel( 255, 255, 255 ) );

    }

    private static BufferedImage readImage( String path ) {
        BufferedImage bufferedImage = null;
        try {
            File imageFile = new File( path );
            bufferedImage = ImageIO.read( imageFile );
        } catch ( IOException e ) {
            System.err.println( e.getMessage() );
        }
        return bufferedImage;
    }

    private static void writeImage( String path, BufferedImage image ) {
        try {
            File imageFile = new File( path );
            ImageIO.write( image, "png", imageFile );
        } catch ( IOException e ) {
            System.err.println( e.getMessage() );
        }
    }
}
