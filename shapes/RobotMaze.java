import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

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
    private Set<Long> caminoGarantizado;
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
        if (size < 10) {
            throw new IllegalArgumentException("El tamaño mínimo del laberinto es 10.");
        }

        this.size = size;
        this.cellSize = 20;
        this.originX = 20;
        this.originY = 20;
        this.caminoGarantizado = null;
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
     * Crea un laberinto aleatorio con paredes generadas internamente,
     * garantizando que siempre exista un camino válido desde la entrada hasta la salida.
     *
     * <p>El llamador no controla la cantidad de paredes. La cantidad la decide el
     * algoritmo a partir del tamaño del tablero y del borde obligatorio del mismo.
     * El parámetro {@code paredesAleatorias} solo sirve para distinguir esta sobrecarga
     * de {@link #RobotMaze(int)}; debe ser {@code true}.</p>
     *
     * @param size tamaño del tablero cuadrado (mínimo 10).
     * @param paredesAleatorias debe ser {@code true}.
     */
    public RobotMaze(int size, boolean paredesAleatorias)
    {
        this(size);

        if (!paredesAleatorias) {
            throw new IllegalArgumentException("El parámetro paredesAleatorias debe ser true.");
        }

        generarLaberintoConSolucion();
    }

    /**
     * Devuelve la fila de entrada del laberinto.
     *
     * @return fila de la entrada.
     */
    public int entryX()
    {
        return entryX;
    }

    /**
     * Devuelve la columna de entrada del laberinto.
     *
     * @return columna de la entrada.
     */
    public int entryY()
    {
        return entryY;
    }

    /**
     * Devuelve la fila de salida del laberinto.
     *
     * @return fila de la salida.
     */
    public int exitX()
    {
        return exitX;
    }

    /**
     * Devuelve la columna de salida del laberinto.
     *
     * @return columna de la salida.
     */
    public int exitY()
    {
        return exitY;
    }

    /**
     * Indica si una celda concreta contiene una pared.
     *
     * @param row fila de la celda.
     * @param column columna de la celda.
     * @return true si la celda tiene pared.
     */
    public boolean hasWall(int row, int column)
    {
        return isValidCell(row, column) && walls[row][column] != null;
    }

    /**
     * Devuelve la cantidad de paredes actualmente colocadas en el tablero.
     *
     * @return cantidad de paredes visibles en el laberinto.
     */
    public int wallCount()
    {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (walls[i][j] != null) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Indica si existe un camino transitable entre la entrada y la salida,
     * considerando las paredes actuales del laberinto.
     *
     * @return true si la salida es alcanzable sin cruzar ninguna pared.
     */
    public boolean tieneSolucion()
    {
        return hayCaminoEntradaSalida();
    }

    /**
     * Genera un camino sencillo y protegido desde la entrada hasta la salida.
     *
     * @return conjunto de celdas que quedan reservadas como paso garantizado.
     */
    private Set<Long> celdasDelCaminoGarantizado()
    {
        if (caminoGarantizado != null) {
            return new HashSet<Long>(caminoGarantizado);
        }

        Set<Long> camino = new HashSet<Long>();
        int row = entryX;
        int column = entryY;
        camino.add(claveCelda(row, column));

        while (row != exitX || column != exitY) {
            boolean moverEnFila = (row != exitX) && (column == exitY || Math.random() < 0.5);
            if (moverEnFila) {
                row += (exitX > row) ? 1 : -1;
            }
            else {
                column += (exitY > column) ? 1 : -1;
            }
            camino.add(claveCelda(row, column));
        }

        caminoGarantizado = new HashSet<Long>(camino);
        return new HashSet<Long>(camino);
    }

    /**
     * Calcula la clave única de una celda del tablero para usarla en un conjunto.
     *
     * @param row fila de la celda.
     * @param column columna de la celda.
     * @return clave numérica de la celda.
     */
    private long claveCelda(int row, int column)
    {
        return (long) row * size + column;
    }

    /**
     * Coloca paredes obligatorias en el borde del tablero, excepto en la entrada,
     * la salida y cualquier celda de borde que pertenezca al camino garantizado.
     *
     * @param camino conjunto de celdas protegidas por el camino garantizado.
     * @return conjunto de celdas de borde en las que sí se colocó pared.
     */
    private Set<Long> colocarParedesDeBorde(Set<Long> camino)
    {
        Set<Long> celdasDeBorde = new HashSet<Long>();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                boolean esBorde = (row == 0 || row == size - 1 || column == 0 || column == size - 1);
                if (!esBorde) {
                    continue;
                }

                boolean esEntradaOSalida = (row == entryX && column == entryY)
                    || (row == exitX && column == exitY);
                boolean estaEnElCamino = camino.contains(claveCelda(row, column));

                if (!esEntradaOSalida && !estaEnElCamino) {
                    walls[row][column] = new Wall(row, column);
                    walls[row][column].changeGrid(cellSize, originX, originY);
                    celdasDeBorde.add(claveCelda(row, column));
                }
            }
        }

        return celdasDeBorde;
    }

    /**
     * Genera un laberinto perfecto con DFS + backtracking.
     * El tablero se inicializa lleno de paredes y se va excavando en pasos de 2
     * celdas para dejar pasillos de un ancho de una celda.
     */
    private void generarLaberintoConSolucion()
    {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                walls[row][column] = new Wall(row, column);
                walls[row][column].changeGrid(cellSize, originX, originY);
            }
        }

        Stack<int[]> pila = new Stack<int[]>();
        pila.push(new int[] { 1, 1 });
        walls[1][1] = null;

        int[][] direcciones = {
            { 0, 2 },
            { 2, 0 },
            { 0, -2 },
            { -2, 0 }
        };

        while (!pila.isEmpty()) {
            int[] actual = pila.peek();
            int row = actual[0];
            int column = actual[1];

            List<int[]> vecinos = new ArrayList<int[]>();
            for (int[] direccion : direcciones) {
                int filaSiguiente = row + direccion[0];
                int columnaSiguiente = column + direccion[1];

                if (isValidCell(filaSiguiente, columnaSiguiente)
                    && walls[filaSiguiente][columnaSiguiente] != null) {
                    vecinos.add(new int[] { filaSiguiente, columnaSiguiente });
                }
            }

            if (!vecinos.isEmpty()) {
                int[] siguiente = vecinos.get((int)(Math.random() * vecinos.size()));
                int paredFila = row + (siguiente[0] - row) / 2;
                int paredColumna = column + (siguiente[1] - column) / 2;

                walls[paredFila][paredColumna] = null;
                walls[siguiente[0]][siguiente[1]] = null;
                pila.push(siguiente);
            }
            else {
                pila.pop();
            }
        }

        Set<Long> camino = celdasDelCaminoGarantizado();
        colocarParedesDeBorde(camino);

        walls[entryX][entryY] = null;
        walls[exitX][exitY] = null;

        if (entryX == 0 && isValidCell(1, entryY)) {
            walls[1][entryY] = null;
        }
        else if (entryX == size - 1 && isValidCell(size - 2, entryY)) {
            walls[size - 2][entryY] = null;
        }
        else if (entryY == 0 && isValidCell(entryX, 1)) {
            walls[entryX][1] = null;
        }
        else if (entryY == size - 1 && isValidCell(entryX, size - 2)) {
            walls[entryX][size - 2] = null;
        }

        if (exitX == 0 && isValidCell(1, exitY)) {
            walls[1][exitY] = null;
        }
        else if (exitX == size - 1 && isValidCell(size - 2, exitY)) {
            walls[size - 2][exitY] = null;
        }
        else if (exitY == 0 && isValidCell(exitX, 1)) {
            walls[exitX][1] = null;
        }
        else if (exitY == size - 1 && isValidCell(exitX, size - 2)) {
            walls[exitX][size - 2] = null;
        }
    }

    /**
     * Busca si hay un camino entre la entrada y la salida sin atravesar paredes.
     *
     * @return true si existe un camino transitable.
     */
    private boolean hayCaminoEntradaSalida()
    {
        boolean[][] visitado = new boolean[size][size];
        Queue<int[]> pendientes = new LinkedList<int[]>();
        pendientes.add(new int[] { entryX, entryY });
        visitado[entryX][entryY] = true;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!pendientes.isEmpty()) {
            int[] actual = pendientes.poll();
            if (actual[0] == exitX && actual[1] == exitY) {
                return true;
            }

            for (int i = 0; i < 4; i++) {
                int nx = actual[0] + dx[i];
                int ny = actual[1] + dy[i];
                if (isValidCell(nx, ny) && !visitado[nx][ny] && walls[nx][ny] == null) {
                    visitado[nx][ny] = true;
                    pendientes.add(new int[] { nx, ny });
                }
            }
        }
        return false;
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
