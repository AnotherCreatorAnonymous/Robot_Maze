/**
 * Robotsito sabroso para el proyecto robot_maze, Objeto principal
 * 
 * @author (AlejoOsp) 
 * @version (1.0.0)
 */
public class Robot
{
    // instance variables - replace the example below with your own
    private int posX;
    private int posY;
    private char direction;
    private int life;
    private boolean ok;


    // Inicio Ciclo 1

    /**
     * Constructor para el robotsito
     */
    public Robot(int posX, int posY)
    {
        // inicializamos las variables de instancia
        this.posX = posX;
        this.posY = posY;
        this.direction = 'N';
        this.life = 10;
        this.ok = true;
    }

    /**
     * Coordinates devuelve las coordenadas del robotsito
     *
     * @return la posición del robot en x y y como arreglo [posX, posY].
     */
    public int[] coordinates()
    {
        // retorna valor en x y y
        return new int[] { this.posX, this.posY };
    }

    /**
     * Direction devuelve la dirección en la que está apuntando el robotsito
     *
     * @return la dirección del robot como carácter.
     */
    public char direction()
    {
        return this.direction;
    }

    // fin ciclo 1

    // Inicio ciclo 2

    /**
     * Método para mover el robotsito en la dirección que está apuntando.
     */
    public void move(int step){
        if (direction == 'N' ) {
            this.posY += step;
        }
        else if (direction == 'S') {
            this.posY -= step;
        }
        else if (direction == 'E') {
            this.posX += step;
        }
        else if (direction == 'W') {
            this.posX -= step;
        }
    }

    /**
     * Comprueba el estado operativo del robot y verifica si el movimiento es válido.
     *
     * @return {@code true} si el robot está operativo; {@code false} si no lo está.
     */
    public boolean isOK()
    {
        return ok;
    }

    // fin ciclo 2
}
