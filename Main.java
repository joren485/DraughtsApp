import java.util.List;

/**
 * Created by Joren on 20-Apr-16.
 */
public class Main {
    public static void main(String[] args) {

        Board b = new Board(10);

        Tile dest = b.getTile(new Tuple(1, 4));

        b.movePiece(new Tuple(1, 6), new Tuple(2, 5));
        b.movePiece(new Tuple(3, 6), new Tuple(4, 5));

        b.movePiece(b.getTile(new Tuple(0, 3)), dest);

        b.movePiece(dest, b.getTile(new Tuple(3, 6)));

        System.out.println(b);

    }
}
