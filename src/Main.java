import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;

private static final String imageInputPath = "Cover.jpg";
private static final String OUTPUT_NAME = "output/output";
private static String imageOutputPath = "output/output.png";

private static final List<Pixel> palette = new ArrayList<>();

private static List<List<Pixel>> lists = new ArrayList<>();
private static List<Pixel> selected;
private static String longestAxis = "R";
private static int axisLength = -1;

private static final int DEPTH = 4;

void main() {
    getFreeOutputName();
    long startTime = System.nanoTime();
    BufferedImage image = readImage( imageInputPath );
    getPixels( image );
    medianCut();
    getAverageColors();
    //palette.clear();
    //palette.add(new Pixel( 0, 0, 0 ));
    //palette.add(new Pixel(255, 255, 255));
    //getOpenComputersPalette();
    IO.println( palette.size() );
    image = fuckImage( image );

    writeImage( imageOutputPath, image );
    long endTime = System.nanoTime() - startTime;
    System.out.println( endTime / 1_000_000 + " ms" );

    Pixel p1 = new Pixel( 0, 0, 255 );
    Pixel p2 = new Pixel( 254, 255, 255 );
    IO.println(p1.L + " " + p1.a + " " + p1.b);
}

private static void getOpenComputersPalette() {
    for ( int r = 0; r <= 5; r++ ) {
        for ( int g = 0; g <= 7; g++ ) {
            for ( int b = 0; b <= 4; b++ ) {
                int red = r * 0x33;
                int green = g * 0x24;
                int blue = b * 0x3F;

                palette.add( new Pixel( red, green, blue ) );
            }
        }
    }
}

private static int files = 1;
private static void getFreeOutputName() {
    File file = new File( imageOutputPath );
    if ( file.exists() && !file.isDirectory() ) {
        imageOutputPath = OUTPUT_NAME + files + ".png";
        files++;
        getFreeOutputName();
    }
}

private static void getAverageColors() {
    for ( List<Pixel> list : lists ) {
        int rSum = 0, gSum = 0, bSum = 0;
        int size = list.size();
        for ( Pixel pixel : list ) {
            rSum += pixel.R;
            gSum += pixel.G;
            bSum += pixel.B;
        }
        int rAvg = rSum / size;
        int gAvg = gSum / size;
        int bAvg = bSum / size;
        Pixel avgColor = new Pixel( rAvg, gAvg, bAvg );
        palette.add( avgColor );
    }
}

private static void medianCut() {
    split();
    while ( lists.size() < DEPTH ) {
        getLongestAxis();
        sort();
        split();
    }
}

private static void getLongestAxis() {
    int rL, gL, bL;
    axisLength = 0;
    for ( List<Pixel> list : lists ) {
        int maxR = 0, maxG = 0, maxB = 0;
        int minR = 256, minG = 256, minB = 256;
        for (Pixel pixel : list) {
            int r = pixel.R, g = pixel.G, b = pixel.B;
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
            longestAxis = "R";
            selected = list;
        }
        if ( gL > axisLength ) {
            axisLength = gL;
            longestAxis = "G";
            selected = list;
        }
        if ( bL > axisLength ) {
            axisLength = bL;
            longestAxis = "B";
            selected = list;
        }
    }
}

private static void sort() {
    selected.sort( Comparator.comparing( o -> o.get( longestAxis ) ) );
}

private static void split() {
    int middle = ( int ) Math.floor( selected.size() / 2.0 );
    List<Pixel> left = new ArrayList<>();
    List<Pixel> right = new ArrayList<>();
    for ( int i = 0; i < middle; i++ ) {
        left.add( selected.get( i ) );
        right.add( selected.get( i + middle ) );
    }
    lists.remove(selected);
    lists.add( left );
    lists.add( right );
}

private static void splitByLargest() {
    int largestGap = -1;
    int splitIdx = 0;

    for ( int i = 0; i < selected.size() - 1; i++ ) {
        int current = selected.get( i ).get( longestAxis );
        int next = selected.get( i + 1 ).get( longestAxis );
        int gap = next - current;

        if ( gap > largestGap ) {
            largestGap = gap;
            splitIdx = i;
        }
    }

    int middle = splitIdx + 1;

    List<Pixel> left = new ArrayList<>();
    List<Pixel> right = new ArrayList<>();

    for ( int i = 0; i < middle ; i++ ) {
        left.add( selected.get( i ) );
    }

    for ( int i = middle; i < selected.size(); i++ ) {
        right.add( selected.get( i ) );
    }

    lists.remove( selected );
    lists.add( left );
    lists.add( right );
}

private static Pixel getClosestColor( Pixel pixel ) {
    Pixel closestColor = null;
    double closestDistance = Double.MAX_VALUE;
    for ( Pixel paletteColor : palette ) {
        double distance = colorDistanceRGB( pixel, paletteColor );

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

private static double colorDistanceLab( Pixel p1, Pixel p2 ) {
    float L1 = p1.L, a1 = p1.a, b1 = p1.b;
    float L2 = p2.L, a2 = p2.a, b2 = p2.b;
    return Math.pow( ( L1 - L2 ), 2 ) + Math.pow( ( a1 - a2 ), 2 ) + Math.pow( ( b1 - b2 ), 2 );
}

private static double colorDistanceRGB( Pixel p1, Pixel p2 ) {
    int sR1 = p1.R, sG1 = p1.G, sB1 = p1.B;
    int sR2 = p2.R, sG2 = p2.G, sB2 = p2.B;
    return Math.pow( ( sR1 - sR2 ), 2 ) + Math.pow( ( sG1 - sG2 ), 2 ) + Math.pow( ( sB1 - sB2 ), 2 );
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