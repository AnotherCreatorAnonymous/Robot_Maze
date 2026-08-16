import javax.swing.JOptionPane;

/**
 * Mini aplicación para RobotMaze.
 * Permite crear un laberinto, poner paredes, iniciar el juego y mover el robot.
 */
public class RobotMaze
{
    private int size;
    private int entryX;
    private int entryY;
    private int exitX;
    private int exitY;
    private int cellSize;
    private int originX;
    private int originY;

    private Wall[][] walls;
    private Robot robot;
    private boolean started;
    private boolean finished;
    private boolean ok;
    private int life;

    /**
     * Constructor principal del laberinto.
     *
     * @param size tamaño del tablero cuadrado.
     */
    public RobotMaze(int size)
    {
        if (size < 3) {
            throw new IllegalArgumentException("El tamaño mínimo del laberinto es 3.");
        }

        this.size = size;
        this.cellSize = 20;
        this.originX = 20;
        this.originY = 20;
        this.started = false;
        this.finished = false;
        this.ok = true;
        this.life = 10;
        this.walls = new Wall[size][size];

        entryX = 0;
        entryY = (int)(Math.random() * size);
        exitX = size - 1;
        exitY = (int)(Math.random() * size);

        if (entryX == exitX && entryY == exitY) {
            exitY = (exitY + 1) % size;
        }
    }

    /**
     * Agrega una pared en la posición indicada.
     * Solo se permite antes de iniciar el juego.
     *
     * @param row fila de la pared.
     * @param column columna de la pared.
     */
    public void addWall(int row, int column)
    {
        if (started) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No se pueden agregar paredes después de iniciar.");
            return;
        }

        if (!isValidCell(row, column)) {
            ok = false;
            JOptionPane.showMessageDialog(null, "La posición de la pared no es válida.");
            return;
        }

        if ((row == entryX && column == entryY) || (row == exitX && column == exitY)) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No puedes poner una pared sobre la entrada o la salida.");
            return;
        }

        walls[row][column] = new Wall(row, column);
        walls[row][column].changeGrid(cellSize, originX, originY);
        ok = true;
    }

    /**
     * Inicia el juego colocando al robot en la entrada.
     */
    public void start()
    {
        if (started) {
            ok = false;
            JOptionPane.showMessageDialog(null, "El juego ya comenzó.");
            return;
        }

        robot = new Robot(entryX, entryY);
        robot.changeGrid(cellSize, originX, originY);
        robot.makeVisible();
        started = true;
        finished = false;
        life = 10;
        ok = true;
    }

    /**
     * Mueve el robot una casilla en la dirección actual.
     */
    public void move()
    {
        if (!started || finished) {
            ok = false;
            JOptionPane.showMessageDialog(null, "El juego no ha comenzado o ya terminó.");
            return;
        }

        int[] pos = robot.coordinates();
        int nextX = pos[0];
        int nextY = pos[1];

        char dir = robot.direction();
        if (dir == 'N') {
            nextY--;
        }
        else if (dir == 'S') {
            nextY++;
        }
        else if (dir == 'E') {
            nextX++;
        }
        else if (dir == 'W') {
            nextX--;
        }

        if (!isValidCell(nextX, nextY)) {
            life--;
            ok = false;
            JOptionPane.showMessageDialog(null, "Chocaste contra el borde. Perdiste 1 vida.");
            if (life <= 0) {
                finished = true;
                JOptionPane.showMessageDialog(null, "¡Te quedaste sin vida!");
            }
            return;
        }

        if (walls[nextX][nextY] != null) {
            walls[nextX][nextY].hit();
            life--;
            ok = false;
            JOptionPane.showMessageDialog(null, "Chocaste con una pared. Perdiste 1 vida.");
            if (life <= 0) {
                finished = true;
                JOptionPane.showMessageDialog(null, "¡Te quedaste sin vida!");
            }
            return;
        }

        robot.move(1);
        ok = robot.isOK();

        if (nextX == exitX && nextY == exitY) {
            finished = true;
            JOptionPane.showMessageDialog(null, "¡Llegaste a la salida!");
        }
    }

    /**
     * Gira el robot hacia la dirección indicada.
     *
     * @param direction dirección destino (N, S, E, W).
     */
    public void turn(char direction)
    {
        if (!started || finished) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No puedes girar porque el juego no comenzó o ya terminó.");
            return;
        }

        robot.turn(direction);
        ok = robot.isOK();
    }

    /**
     * Devuelve la vida disponible del robot.
     *
     * @return puntos de vida restantes.
     */
    public int life()
    {
        return life;
    }

    /**
     * Indica si la última operación fue válida.
     *
     * @return true si fue válida.
     */
    public boolean ok()
    {
        return ok;
    }

    /**
     * Indica si el juego terminó.
     *
     * @return true si ya terminó.
     */
    public boolean finished()
    {
        return finished;
    }

    /**
     * Termina el juego.
     */
    public void finish()
    {
        finished = true;
        ok = true;
    }

    /**
     * Hace visibles el robot y las paredes.
     */
    public void makeVisible()
    {
        if (robot != null) {
            robot.makeVisible();
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (walls[i][j] != null) {
                    walls[i][j].makeVisible();
                }
            }
        }
    }

    /**
     * Hace invisibles el robot y las paredes.
     */
    public void makeInvisible()
    {
        if (robot != null) {
            robot.makeInvisible();
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (walls[i][j] != null) {
                    walls[i][j].makeInvisible();
                }
            }
        }
    }

    /**
     * Verifica si una posición es válida dentro del tablero.
     *
     * @param row fila.
     * @param column columna.
     * @return true si está dentro del rango.
     */
    private boolean isValidCell(int row, int column)
    {
        return row >= 0 && row < size && column >= 0 && column < size;
    }
}
