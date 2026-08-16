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
    private Rectangle[][] background;
    private Robot robot;
    private Circle entryMarker;
    private Circle exitMarker;
    private boolean visible;
    private boolean started;
    private boolean finished;
    private boolean ok;
    private int life;

    private boolean hasLastMove;
    private int previousX;
    private int previousY;
    private char previousDirection;
    private int previousLife;
    private boolean previousWallHit;
    private int previousWallX;
    private int previousWallY;

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
        this.visible = false;
        this.started = false;
        this.finished = false;
        this.ok = true;
        this.life = 10;
        this.walls = new Wall[size][size];
        this.background = new Rectangle[size][size];
        createBackground();
        this.entryMarker = new Circle();
        this.exitMarker = new Circle();
        this.entryMarker.changeColor("green");
        this.exitMarker.changeColor("blue");
        this.entryMarker.changeSize(10);
        this.exitMarker.changeSize(10);

        entryX = 0;
        entryY = (int)(Math.random() * size);
        exitX = size - 1;
        exitY = (int)(Math.random() * size);

        if (entryX == exitX && entryY == exitY) {
            exitY = (exitY + 1) % size;
        }

        refreshMarkers();
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
        if (visible) {
            walls[row][column].makeVisible();
        }
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
        hasLastMove = false;
        refreshMarkers();
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

        previousX = pos[0];
        previousY = pos[1];
        previousDirection = dir;
        previousLife = robot.life();
        previousWallHit = false;
        previousWallX = -1;
        previousWallY = -1;
        hasLastMove = true;

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
            robot.hit();
            life = robot.life();
            ok = false;
            JOptionPane.showMessageDialog(null, "Chocaste contra el borde. Perdiste 1 vida.");
            if (!robot.isAlive()) {
                finished = true;
                JOptionPane.showMessageDialog(null, "¡Te quedaste sin vida!");
            }
            return;
        }

        if (walls[nextX][nextY] != null) {
            previousWallHit = true;
            previousWallX = nextX;
            previousWallY = nextY;
            walls[nextX][nextY].hit();
            robot.hit();
            life = robot.life();
            ok = false;
            JOptionPane.showMessageDialog(null, "Chocaste con una pared. Perdiste 1 vida.");
            if (!robot.isAlive()) {
                finished = true;
                JOptionPane.showMessageDialog(null, "¡Te quedaste sin vida!");
            }
            return;
        }

        robot.move(1);
        ok = robot.isOK();
        life = robot.life();

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
        life = robot.life();
    }

    /**
     * Devuelve la vida disponible del robot.
     *
     * @return puntos de vida restantes.
     */
    public int life()
    {
        return (robot == null) ? life : robot.life();
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
     * Devuelve la posición actual del robot dentro del tablero.
     *
     * @return arreglo [fila, columna] del robot.
     */
    public int[] robotCoordinates()
    {
        if (robot == null) {
            return new int[] { entryX, entryY };
        }
        return robot.coordinates();
    }

    /**
     * Devuelve la dirección actual del robot.
     *
     * @return 'N', 'S', 'E' o 'W'.
     */
    public char robotDirection()
    {
        if (robot == null) {
            return 'N';
        }
        return robot.direction();
    }

    /**
     * Hace visibles el robot, las paredes y las marcas de entrada y salida.
     */
    public void makeVisible()
    {
        this.visible = true;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (background[i][j] != null) {
                    background[i][j].makeVisible();
                }
            }
        }

        if (robot != null) {
            robot.makeVisible();
        }

        if (entryMarker != null) {
            entryMarker.makeVisible();
        }
        if (exitMarker != null) {
            exitMarker.makeVisible();
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
     * Hace invisibles el robot, las paredes y las marcas de entrada y salida.
     */
    public void makeInvisible()
    {
        this.visible = false;

        if (robot != null) {
            robot.makeInvisible();
        }

        if (entryMarker != null) {
            entryMarker.makeInvisible();
        }
        if (exitMarker != null) {
            exitMarker.makeInvisible();
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (walls[i][j] != null) {
                    walls[i][j].makeInvisible();
                }
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (background[i][j] != null) {
                    background[i][j].makeInvisible();
                }
            }
        }
    }

    /**
     * Decide automáticamente el mejor movimiento posible hacia la salida.
     * La estrategia prioriza la dirección que minimiza la distancia Manhattan
     * hasta la salida; si la mejor opción está bloqueada por una pared o un borde,
     * prueba el resto en orden de menor distancia resultante hasta encontrar una
     * jugada válida. Si todas fallan, ejecuta el intento mejor evaluado para
     * mantener el mismo comportamiento de choque/vida que un movimiento normal.
     */
    public void goodMove()
    {
        if (!started || finished) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No se puede hacer un buen movimiento porque el juego no inició o ya terminó.");
            return;
        }

        char[] order = orderDirectionsByExitDistance();
        for (int i = 0; i < order.length; i++) {
            char dir = order[i];
            int[] pos = robot.coordinates();
            int[] next = nextCell(pos[0], pos[1], dir);
            if (isValidCell(next[0], next[1]) && walls[next[0]][next[1]] == null) {
                if (robot.direction() != dir) {
                    turn(dir);
                }
                move();
                return;
            }
        }

        char fallback = order[0];
        if (robot.direction() != fallback) {
            turn(fallback);
        }
        move();
    }

    /**
     * Deshace el último movimiento realizado.
     * Decide revertir el daño de vida y la pared golpeada si el último movimiento
     * provocó un choque; si la última acción fue un movimiento válido, restaura la
     * posición previa del robot y deja todo el estado en su valor anterior.
     */
    public void undo()
    {
        if (!started || finished) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No hay movimiento previo para deshacer, o el juego no comenzó/ya terminó.");
            return;
        }

        if (!hasLastMove) {
            ok = false;
            JOptionPane.showMessageDialog(null, "No hay ningún movimiento previo que deshacer.");
            return;
        }

        if (previousWallHit && previousWallX >= 0 && previousWallY >= 0 && walls[previousWallX][previousWallY] != null) {
            walls[previousWallX][previousWallY].resetHit();
        }

        robot.restoreState(previousX, previousY, previousDirection, previousLife);
        life = robot.life();
        ok = true;
        hasLastMove = false;
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

    /**
     * Crea el fondo visual del tablero con una celda por posición del laberinto.
     */
    private void createBackground()
    {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                background[i][j] = new Rectangle();
                background[i][j].changeColor("white");
                background[i][j].changeSize(cellSize, cellSize);
                background[i][j].moveTo(originX + (i * cellSize), originY + (j * cellSize));
            }
        }
    }

    /**
     * Reubica la entrada y la salida en el lienzo según el tamaño de la cuadrícula.
     */
    private void refreshMarkers()
    {
        if (entryMarker != null) {
            entryMarker.moveTo(originX + (entryX * cellSize) + (cellSize / 2),
                              originY + (entryY * cellSize) + (cellSize / 2));
        }
        if (exitMarker != null) {
            exitMarker.moveTo(originX + (exitX * cellSize) + (cellSize / 2),
                             originY + (exitY * cellSize) + (cellSize / 2));
        }
    }

    /**
     * Devuelve las direcciones ordenadas por la distancia resultante a la salida.
     */
    private char[] orderDirectionsByExitDistance()
    {
        char[] dirs = {'N', 'E', 'S', 'W'};
        for (int i = 0; i < dirs.length; i++) {
            for (int j = i + 1; j < dirs.length; j++) {
                if (distanceToExit(nextCell(robot.coordinates()[0], robot.coordinates()[1], dirs[j]))
                    < distanceToExit(nextCell(robot.coordinates()[0], robot.coordinates()[1], dirs[i]))) {
                    char aux = dirs[i];
                    dirs[i] = dirs[j];
                    dirs[j] = aux;
                }
            }
        }
        return dirs;
    }

    /**
     * Calcula la distancia Manhattan desde una casilla posible hasta la salida.
     */
    private int distanceToExit(int[] cell)
    {
        if (cell == null) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(cell[0] - exitX) + Math.abs(cell[1] - exitY);
    }

    /**
     * Calcula la siguiente celda según una dirección cardinal.
     */
    private int[] nextCell(int row, int column, char dir)
    {
        int nextX = row;
        int nextY = column;

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

        return new int[] { nextX, nextY };
    }
}
