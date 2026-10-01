public class Pixel {
    public int r, g, b;
    public Pixel( int r, int g, int b ) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Pixel() {
    }

    public Integer get( String key ) {
        switch ( key ) {
            case "r" -> {
                return this.r;
            }
            case "g" -> {
                return this.g;
            }
            case "b" -> {
                return this.b;
            }
            default -> {
                return -1;
            }
        }
    }

    public void print() {
        System.out.println( this.getColor() );
    }

    public boolean equalTo( Pixel pixel ) {
        return this.r == pixel.r && this.g == pixel.g && this.b == pixel.b;
    }

    public void set( int r, int g, int b ) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getColor() {
        return ( this.r << 16 ) | ( this.g << 8 ) | this.b;
    }

    public void set( int[] array ) {
        this.r = array[0];
        this.g = array[1];
        this.b = array[2];
    }

    public void set( Pixel pixel ) {
        this.r = pixel.r;
        this.g = pixel.g;
        this.b = pixel.b;
    }
}
