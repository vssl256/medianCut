import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

private static final String imageInputPath = "i.png";
private static final String imageOutputPath = "output.png";

private static final List<Pixel> palette = new ArrayList<>();

private static List<List<Pixel>> lists = new ArrayList<>();
private static List<Pixel> selected;
private static String longestAxis = "r";
private static int axisLength = -1;

private static final int DEPTH = 16;

void main() {
    BufferedImage image = readImage( imageInputPath );
    getPixels( image );
    medianCut();
    getAverageColors();
    IO.println( palette.size() );
    image = fuckImage( image );

    writeImage( imageOutputPath, image );
}

private static void getAverageColors() {
    for ( List<Pixel> list : lists ) {
        int rSum = 0, gSum = 0, bSum = 0;
        int size = list.size();
        for ( Pixel pixel : list ) {
            rSum += pixel.r;
            gSum += pixel.g;
            bSum += pixel.b;
        }
        int rAvg = rSum / size;
        int gAvg = gSum / size;
        int bAvg = bSum / size;
        Pixel avgColor = new Pixel( rAvg, gAvg, bAvg );
        palette.add( avgColor );
    }
}

private static void medianCut() {
    cutInHalf();
    while ( lists.size() < DEPTH ) {
        getLongestAxis();
        sort();
        cutInHalf();
    }
}

private static void getLongestAxis() {
    int rL, gL, bL;
    axisLength = 0;
    for ( List<Pixel> list : lists ) {
        int maxR = 0, maxG = 0, maxB = 0;
        int minR = 256, minG = 256, minB = 256;
        for (Pixel pixel : list) {
            int r = pixel.r, g = pixel.g, b = pixel.b;
            if ( r > maxR ) maxR = r;
            if ( g > maxG ) maxG = g;
            if ( b > maxB ) maxB = b;

            if ( r < minR ) minR = r;
            if ( g < minG ) minG = g;
            if ( b < minB ) minB = b;
        }
        rL = maxR - minR;
        gL = maxG - minG;
        bL = maxB - minB;
        if ( rL > axisLength ) {
            axisLength = rL;
            longestAxis = "r";
            selected = list;
        }
        if ( gL > axisLength ) {
            axisLength = gL;
            longestAxis = "g";
            selected = list;
        }
        if ( bL > axisLength ) {
            axisLength = bL;
            longestAxis = "b";
            selected = list;
        }
    }
}

private static void sort() {
    selected.sort( Comparator.comparing( o -> o.get( longestAxis ) ) );
}

private static void cutInHalf() {
    int listSize = ( int ) Math.floor( selected.size() / 2.0 );
    List<Pixel> left = new ArrayList<>();
    List<Pixel> right = new ArrayList<>();
    for ( int i = 0; i < listSize; i++ ) {
        left.add( selected.get( i ) );
        right.add( selected.get( i + listSize ) );
    }
    lists.remove(selected);
    lists.add( left );
    lists.add( right );
}

private static Pixel getClosestColor( Pixel pixel ) {
    Pixel closestColor = null;
    double closestDistance = Double.MAX_VALUE;
    int sR = pixel.r, sG = pixel.g, sB = pixel.b;
    for ( Pixel paletteColor : palette ) {
        double distance = colorDistance( pixel, paletteColor );

        if ( distance < closestDistance ) {
            closestDistance = distance;
            closestColor = paletteColor;
        }
    }
    return closestColor;
}

private static void getPixels( BufferedImage image ) {
    List<Pixel> list = new ArrayList<>();
    HashSet<Integer> allPixels = new HashSet<>();
    Raster raster = image.getData();
    for ( int y = 0; y < raster.getHeight(); y++ ) {
        for ( int x = 0; x < raster.getWidth(); x++ ) {
            Pixel pixel = new Pixel();
            int[] pixelValue = new int[4];
            raster.getPixel( x, y, pixelValue );
            pixel.set( pixelValue );

            int colorValue = pixel.getColor();
            if ( !allPixels.contains( colorValue ) ) list.add( pixel );
            allPixels.add( colorValue );
        }
    }
    selected = list;
    lists.add( list );
    IO.println( list.size() );
}

private static BufferedImage fuckImage( BufferedImage image ) {
    Raster raster = image.getData();
    BufferedImage newImage = new BufferedImage( raster.getWidth(), raster.getHeight(), BufferedImage.TYPE_INT_RGB );
    for ( int y = 0; y < raster.getHeight(); y++ ) {
        for ( int x = 0; x < raster.getWidth(); x++ ) {
            Pixel pixel = new Pixel();
            int[] pixelValue = new int[4];
            raster.getPixel( x, y, pixelValue );
            pixel.set( pixelValue );

            pixel.set( getClosestColor( pixel ) );

            int color = pixel.getColor();

            newImage.setRGB( x, y, color );
        }
    }

    return newImage;
}

private static double colorDistance( Pixel pixel1, Pixel pixel2 ) {
    int sR1 = pixel1.r, sG1 = pixel1.g, sB1 = pixel1.b;
    int sR2 = pixel2.r, sG2 = pixel2.g, sB2 = pixel2.b;
    return Math.sqrt( Math.pow( ( sR1 - sR2 ), 2 ) + Math.pow( ( sG1 - sG2 ), 2 ) + Math.pow( ( sB1 - sB2 ), 2 ) );
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