import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Joren on 20-Apr-16.
 */
public class Board {

    private final Tuple[] KINGMOVES;

    private final int board_size;
    private final Piece[][] board;

    public Board(int size){
        this.board_size = size;
        this.board = new Piece[this.board_size][this.board_size];

        for (int y = 0; y < this.board_size; y++){
            for (int x = 0; x < this.board_size; x++){

                if ((x + y) % 2 == 0) {
                    this.board[x][y] = null;
                }
                else{
                    if (y < (this.board_size/2) - 1){
                        this.board[x][y] = new Piece(Piece.PieceColor.Black, new Tuple(x, y));
                    }
                    else if (y >= this.board_size - ((this.board_size/2) - 1)){
                        this.board[x][y] = new Piece(Piece.PieceColor.White, new Tuple(x, y));
                    }
                    else {
                        this.board[x][y] = null;
                    }
                }
            }
        }

        this.KINGMOVES = new Tuple[this.board_size * this.board_size];
        int index = 0;
        for(int x = -1*(this.board_size - 1); x < this.board_size; x++){
            for(int y = -1*(this.board_size - 1); y < this.board_size; y++){
                this.KINGMOVES[index] = new Tuple(x, y);
            }
        }

    }

    public Board(){

        // Default size is 10
        this(10);
    }
    public Piece getPiece(Tuple pos){
        return this.board[pos.x][pos.y];
    }

    public void setPiece(Piece p, Tuple cor){
        this.board[cor.x][cor.y] = new Piece(p, cor);
    }

    public boolean isEmpty(Tuple cor){
        return this.board[cor.x][cor.y] == null;
    }

    /**
     * Move a piece to a location. It is assumed that the move is a valid one,
     * there are methods for all valid moves and captures.
     */
    public void movePiece(Tuple src, Tuple dest){

        System.out.println(src + " to" + dest + "\n");
        Piece p = this.getPiece(src);
        this.setPiece(p, dest);
        this.board[src.x][src.y] = null;


        //TODO This code needs to be implemented after a capture chain
        //For removing pieces

            if (abs(dest.x - src.x) > 1 && abs(dest.y - src.y) > 1){
                Tuple dir = Tuple.getDirection(src, dest);

                Tuple new_empty_cor = new Tuple(dest.x + dir.x, dest.y + dir.y);
                this.getPiece(new_empty_cor).switchinChain();
            }
    }

    public List<Tuple> getPossibleCaptures(Piece piece){
        List<Tuple> captures = new ArrayList<>();

        Tuple[] moves;

        if (piece.isKing()){
            moves = this.KINGMOVES;
        }

        else{
            moves = new Tuple[4];
            moves[0] = new Tuple(1,1);
            moves[1] = new Tuple(-1,1);
            moves[2] = new Tuple(1,-1);
            moves[3] = new Tuple(-1,-1);
        }

        for (Tuple move_dir: moves) {

            Tuple cor = new Tuple(piece.getX() + move_dir.x, piece.getY() + move_dir.y);
            Tuple cor_behind = new Tuple(piece.getX() + 2* move_dir.x, piece.getY() + 2* move_dir.y);

            if (cor.x < this.board_size && cor.x >= 0 && cor.y < this.board_size && cor.y >= 0 &&
                    cor_behind.x < this.board_size && cor_behind.x >= 0 && cor_behind.y < this.board_size && cor_behind.y >= 0
                    && !this.isEmpty(cor)
                    && !this.getPiece(cor).getinChain()
                    && this.getPiece(cor).getColor() != piece.getColor()
                    && this.isEmpty(cor_behind)){

                captures.add(cor_behind);
            }
        }
            return captures;
    }

    private List<Tuple> getPossibleMoves(Piece piece){
        List<Tuple> moves = new ArrayList<>();

        Tuple[] move_dirs;
        if (piece.isKing()){
            move_dirs = this.KINGMOVES;
        }
        else{
            move_dirs = new Tuple[2];
            if (piece.getColor() == Piece.PieceColor.Black){
                move_dirs[0] = new Tuple(1,1);
                move_dirs[1] = new Tuple(-1,1);
            }

            else{
                move_dirs[0] = new Tuple(1,-1);
                move_dirs[1] = new Tuple(-1,-1);
            }
        }

        for (Tuple move_dir: move_dirs){

            Tuple cor = new Tuple(piece.getX() + move_dir.x, piece.getY() + move_dir.y);

            if (cor.x < this.board_size && cor.x >= 0 && cor.y < this.board_size && cor.y >= 0){

                if(this.isEmpty(cor))
                    moves.add(cor);
            }
        }

        return moves;
    }

    public void getLegalCaptures(Tuple src, Move node){

        Piece piece = this.getPiece(src);

        List<Tuple> captures = getPossibleCaptures(piece);

        ArrayList<Move> moves = new ArrayList<>();


        for (Tuple dest : captures){

            System.out.println(dest.toString());

            System.out.println(src);

            this.movePiece(src, dest);
            this.getPiece(dest);

            Move branch = new Move(dest);
            getLegalCaptures(dest, branch);

            moves.add(branch);
            this.movePiece(dest, src);
        }

        int maxium_height = 0;

        for (Move m : moves) {
            int height = m.getHeight();
            if (height > maxium_height) {
                maxium_height = height;
            }
        }

        for (Move m : moves) {
            if(m.getHeight() == maxium_height) {
                node.addMove(m);
            }
        }
    }

    public Move getLegalMoves(Piece piece){

        List<Tuple> captures = getPossibleCaptures(piece);
        Move root = new Move(piece.getPosition());

        if (captures.size() == 0){
            for(Tuple cor: getPossibleMoves(piece)){
                root.addMove(new Move(cor));
            }
            return root;
        }

        getLegalCaptures(piece.getPosition(), root);
        return root;
    }

    @Override
    public String toString(){

        Piece temp;
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < this.board_size; y++){
            for (int x = 0; x < this.board_size; x++){
                temp = this.board[x][y];

                if (temp == null) {
                    sb.append(" * ");
                }

                else if (temp.getColor() == Piece.PieceColor.White){
                    sb.append(" W ");
                }
                else {
                    sb.append(" B ");
                }

            }
            sb.append('\n');

        }
        return sb.toString();
    }
}
