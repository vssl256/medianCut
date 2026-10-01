import java.util.*;

public class Test {
    List<Pixel> list1 = new ArrayList<>();
    List<List<Pixel>> lists = new ArrayList<>();
    List<Pixel> selected;
    void main() {

        for ( int i = 0; i <= 32; i++ ) {
            list1.add( new Pixel( i, i, i ) );
        }
        lists.add( list1 );
        selected = lists.getLast();
        cutInHalf( selected );

        selected = lists.getLast();
        cutInHalf( selected );

        cutInHalf( selected );
        selected = lists.getLast();

        int count = 0;
        for ( List<Pixel> list : lists ) {
            for ( Pixel pixel : list ) {
                count++;
            }
        }
        Random random = new Random(1);
        selected = new ArrayList<>();
        for ( int i = 0; i < 64; i++ ) {
            int r = random.nextInt( 255 );
            int g = random.nextInt( 255 );
            int b = random.nextInt( 255 );
            Pixel pixel = new Pixel( r, g, b );
            selected.add( pixel );
        }
        sortBy("b");
        for ( Pixel pixel : selected ) {
            IO.println( pixel.r + " " + pixel.g + " " + pixel.b );
        }
    }

    void medianCut() {

    }

    void sortBy( String color ) {
        selected.sort( Comparator.comparing( o -> o.get( color ) ) );
    }

    void cutInHalf( List<Pixel> selected ) {
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
}
