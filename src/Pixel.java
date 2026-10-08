public class Pixel {
    public int R, G, B;
    public float x, y, z;
    public float L, a, b;
    public Pixel( int R, int G, int B ) {
        this.R = R;
        this.G = G;
        this.B = B;
        getLab();
    }

    public Pixel() {
    }

    public void getLab() {
        xyzToLab();
    }

    private static final float REF_X = 95.047f;
    private static final float REF_Y = 100.000f;
    private static final float REF_Z = 108.883f;

    private static final float DELTA = 16f / 116f;
    private static final float EPSILON = 0.008856f;
    private static final float KAPPA = 7.787f;
    private void xyzToLab() {
        rgbToXyz();

        float varX = this.x / REF_X;
        float varY = this.y / REF_Y;
        float varZ = this.z / REF_Z;

        if ( varX > EPSILON ) varX = ( float ) Math.cbrt( varX );
        else varX = ( KAPPA * varX ) + DELTA;

        if ( varY > EPSILON ) varY = ( float ) Math.cbrt( varY );
        else varY = ( KAPPA * varY ) + DELTA;

        if ( varZ > EPSILON ) varZ = ( float ) Math.cbrt( varZ );
        else varZ = ( KAPPA * varZ ) + DELTA;

        this.L = ( 116f * varY ) - 16f;
        this.a = 500 * ( varX - varY );
        this.b = 200 * ( varY - varZ );
    }

    private static final float SRGB_THRESHOLD = 0.04045f;
    private static final float SRGB_LINEAR_SCALE = 12.92f;
    private static final float SRGB_OFFSET = 0.055f;
    private static final float SRGB_SCALE = 1.055f;
    private static final float SRGB_GAMMA = 2.4f;
    private void rgbToXyz() {
        float varR = ( this.R / 255f );
        float varG = ( this.G / 255f );
        float varB = ( this.B / 255f );

        if ( varR > SRGB_THRESHOLD ) varR = ( float ) Math.pow( ( ( varR + SRGB_OFFSET) / SRGB_SCALE), SRGB_GAMMA);
        else varR /= SRGB_LINEAR_SCALE;

        if ( varG > SRGB_THRESHOLD ) varG = ( float ) Math.pow( ( ( varG + SRGB_OFFSET) / SRGB_SCALE), SRGB_GAMMA);
        else varG /= SRGB_LINEAR_SCALE;

        if ( varB > SRGB_THRESHOLD ) varB = ( float ) Math.pow( ( ( varB + SRGB_OFFSET) / SRGB_SCALE), SRGB_GAMMA);
        else varB /= SRGB_LINEAR_SCALE;

        varR *= 100;
        varG *= 100;
        varB *= 100;

        this.x = varR * 0.4124f + varG * 0.3576f + varB * 0.1805f;
        this.y = varR * 0.2126f + varG * 0.7152f + varB * 0.0722f;
        this.z = varR * 0.0193f + varG * 0.1192f + varB * 0.9505f;
    }

    public Integer get( String key ) {
        switch ( key ) {
            case "R" -> {
                return this.R;
            }
            case "G" -> {
                return this.G;
            }
            case "B" -> {
                return this.B;
            }
            default -> {
                return -1;
            }
        }
    }

    public void print() {
        System.out.println( this.getColor() );
    }

    public int getColor() {
        return ( this.R << 16 ) | ( this.G << 8 ) | this.B;
    }

    public void set( int[] array ) {
        this.R = array[0];
        this.G = array[1];
        this.B = array[2];
        getLab();
    }

    public void set( Pixel pixel ) {
        this.R = pixel.R;
        this.G = pixel.G;
        this.B = pixel.B;
        getLab();
    }
}
