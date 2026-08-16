/**
 * Robotsito sabroso para el proyecto robot_maze, Objeto principal.
 * Trabaja en coordenadas de casilla y conoce la geometría del tablero
 * mediante la configuración del grid.
 *
 * @author (AlejoOsp)
 * @version (2.0.0)
 */
public class Robot
{
    private int posX;            // columna dentro del laberinto
    private int posY;            // fila dentro del laberinto
    private char direction;      // N, E, S o W
    private int life;            // puntos de vida
    private boolean ok;          // false si la última operación no pudo hacerse
    private boolean isVisible;   // Triangle no expone un consultor
    private Triangle shape;      // representación gráfica del robot

    private int cellSize;        // pixeles por casilla
    private int originX;         // pixel donde empieza la columna 0
    private int originY;         // pixel donde empieza la fila 0

    private static final char[] DIRECTIONS = {'N', 'E', 'S', 'W'};
    private static final int[] STEP_X = {0, 1, 0, -1};
    private static final int[] STEP_Y = {-1, 0, 1, 0};
    private static final int MAX_LIFE = 10;
    private static final int DEFAULT_CELL = 20;

    /**
     * Constructor para el robotsito. Se crea mirando al norte y con vida completa.
     *
     * @param posX columna inicial.
     * @param posY fila inicial.
     */
    public Robot(int posX, int posY)
    {
        this.posX = posX;
        this.posY = posY;
        this.direction = 'N';
        this.life = MAX_LIFE;
        this.ok = true;
        this.isVisible = false;
        this.cellSize = DEFAULT_CELL;
        this.originX = 0;
        this.originY = 0;
        this.shape = new Triangle();
        relocate();
    }

    /**
     * Coordinates devuelve las coordenadas del robotsito.
     *
     * @return la posición del robot como arreglo [posX, posY].
     */
    public int[] coordinates()
    {
        return new int[] { this.posX, this.posY };
    }

    /**
     * Direction devuelve la dirección en la que está apuntando el robotsito.
     *
     * @return la dirección del robot como carácter (N, E, S o W).
     */
    public char direction()
    {
        return this.direction;
    }

    /**
     * Mueve el robot en la dirección a la que apunta.
     * No valida paredes ni bordes; eso le corresponde al laberinto.
     *
     * @param step cantidad de casillas a avanzar; si es negativo retrocede.
     */
    public void move(int step)
    {
        int index = directionToIndex(this.direction);
        if (index == -1 || life <= 0) {
            ok = false;
            return;
        }

        posX += STEP_X[index] * step;
        posY += STEP_Y[index] * step;
        relocate();
        ok = true;
    }

    /**
     * Gira el robot hacia la dirección deseada.
     *
     * @param direction dirección destino (N, E, S o W).
     */
    public void turn(char direction)
    {
        int targetIndex = directionToIndex(direction);
        if (targetIndex == -1 || life <= 0) {
            ok = false;
            return;
        }

        int steps = (targetIndex - directionToIndex(this.direction) + 4) % 4;
        if (steps == 1) {
            shape.rotate90('R');
        }
        else if (steps == 2) {
            shape.rotate180();
        }
        else if (steps == 3) {
            shape.rotate90('L');
        }

        this.direction = direction;
        ok = true;
    }

    /**
     * Comprueba si la última operación solicitada se pudo realizar.
     *
     * @return true si la última operación fue válida; false en caso contrario.
     */
    public boolean isOK()
    {
        return ok;
    }

    /**
     * Hace visible al robot.
     */
    public void makeVisible()
    {
        isVisible = true;
        shape.makeVisible();
    }

    /**
     * Hace invisible al robot.
     */
    public void makeInvisible()
    {
        shape.makeInvisible();
        isVisible = false;
    }

    /**
     * Ajusta la geometría del tablero sobre el que se dibuja el robot.
     *
     * @param cellSize tamaño en píxeles de cada casilla.
     * @param originX pixel de la columna 0.
     * @param originY pixel de la fila 0.
     */
    public void changeGrid(int cellSize, int originX, int originY)
    {
        this.cellSize = cellSize;
        this.originX = originX;
        this.originY = originY;
        relocate();
    }

    /**
     * Consulta la vida disponible del robot.
     *
     * @return los puntos de vida restantes.
     */
    public int life()
    {
        return life;
    }

    /**
     * Registra un choque: el robot pierde un punto de vida.
     */
    public void hit()
    {
        if (life > 0) {
            life--;
        }
        if (life == 0) {
            shape.changeColor("gray");
        }
        ok = false;
    }

    /**
     * Indica si al robot todavía le queda vida.
     *
     * @return true si tiene al menos un punto de vida.
     */
    public boolean isAlive()
    {
        return life > 0;
    }

    /**
     * Vuelve a ubicar el triángulo en la casilla que indican posX y posY.
     */
    private void relocate()
    {
        int margin = Math.max(2, cellSize / 6);
        shape.changeSize(cellSize - (2 * margin), cellSize - (2 * margin));
        shape.moveTo(originX + (posX * cellSize) + (cellSize / 2),
                     originY + (posY * cellSize) + margin);
    }

    /**
     * Helper privado para turn y move.
     * Convierte una dirección cardinal en su índice en sentido horario (N=0, E=1, S=2, W=3).
     *
     * @return el índice de la dirección, o -1 si el carácter no es válido.
     */
    private int directionToIndex(char direction)
    {
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (DIRECTIONS[i] == direction) {
                return i;
            }
        }
        return -1;
    }
}
