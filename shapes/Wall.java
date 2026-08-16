/**
 * Pared del laberinto de RobotMaze. Trabaja en coordenadas de casilla.
 *
 * @author (AlejoOsp)
 * @version 1.0.0
 */
public class Wall
{
    private int posX;            // columna
    private int posY;            // fila
    private boolean hit;         // true si el robot ya chocó contra ella
    private boolean isVisible;
    private Rectangle shape;

    private int cellSize;
    private int originX;
    private int originY;

    private static final String COLOR_OK = "black";
    private static final String COLOR_HIT = "red";
    private static final int DEFAULT_CELL = 20;

    /**
     * Constructor para una pared en la casilla indicada.
     *
     * @param posX columna de la pared.
     * @param posY fila de la pared.
     */
    public Wall(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
        this.hit = false;
        this.isVisible = false;
        this.cellSize = DEFAULT_CELL;
        this.originX = 0;
        this.originY = 0;
        this.shape = new Rectangle();
        this.shape.changeColor(COLOR_OK);
        relocate();
    }

    /**
     * Devuelve la casilla que ocupa la pared.
     *
     * @return arreglo [posX, posY].
     */
    public int[] coordinates()
    {
        return new int[] { this.posX, this.posY };
    }

    /**
     * Marca la pared como golpeada y la cambia de color.
     */
    public void hit()
    {
        hit = true;
        shape.changeColor(COLOR_HIT);
    }

    /**
     * Restaura el estado visual y lógico de la pared tras deshacer un movimiento.
     */
    public void resetHit()
    {
        hit = false;
        shape.changeColor(COLOR_OK);
    }

    /**
     * Indica si la pared ya fue golpeada.
     *
     * @return true si el robot chocó contra ella alguna vez.
     */
    public boolean isHit()
    {
        return hit;
    }

    /**
     * Ajusta la geometría del tablero sobre el que se dibuja la pared.
     *
     * @param cellSize tamaño en píxeles por casilla.
     * @param originX pixel donde empieza la columna 0.
     * @param originY pixel donde empieza la fila 0.
     */
    public void changeGrid(int cellSize, int originX, int originY)
    {
        this.cellSize = cellSize;
        this.originX = originX;
        this.originY = originY;
        relocate();
    }

    /**
     * Hace visible la pared.
     */
    public void makeVisible()
    {
        isVisible = true;
        shape.makeVisible();
    }

    /**
     * Hace invisible la pared.
     */
    public void makeInvisible()
    {
        shape.makeInvisible();
        isVisible = false;
    }

    /**
     * Reubica la figura en la posición de la casilla actual.
     */
    private void relocate()
    {
        shape.changeSize(cellSize, cellSize);
        shape.moveTo(originX + (posX * cellSize), originY + (posY * cellSize));
    }
}
