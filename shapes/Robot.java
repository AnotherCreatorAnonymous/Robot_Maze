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
    private Triangle Robot;


    // Inicio Ciclo 1

    /**
     * Constructor para el robotsito
     */
    public Robot(int posX, int posY)
    {
        // inicializamos las variables de instancia
        Robot = new Triangle();
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
            Robot.moveVertical(step);
        }
        else if (direction == 'S') {
            this.posY -= step;
            Robot.moveVertical(-step);
        }
        else if (direction == 'E') {
            this.posX += step;
            Robot.moveHorizontal(step);
        }
        else if (direction == 'W') {
            this.posX -= step;
            Robot.moveHorizontal(-step);
        }
    }

    /**
     * Gira el robot a la direccion deseada
     *
     */
    public void turn(char direction){
        int currentIndex = directionToIndex(this.direction);
        int targetIndex = directionToIndex(direction);
        int steps = (targetIndex - currentIndex + 4) % 4;

        if (steps == 1) {
            Robot.rotate90('R');
        }
        else if (steps == 2) {
            Robot.rotate180();
        }
        else if (steps == 3) {
            Robot.rotate270('R');
        }

        this.direction = direction;
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


    //Inicio Ciclo 3

    // fin ciclo 3

    //Helpers Privados

    /**
     * helper privado para el método turn.
     * Convierte una dirección cardinal en su índice en sentido horario (N=0, E=1, S=2, W=3).
     */
    private int directionToIndex(char direction){
        switch (direction) {
            case 'N': return 0;
            case 'E': return 1;
            case 'S': return 2;
            case 'W': return 3;
        }
        return -1;  // Retorna -1 si la dirección no es válida y para que compile el helper.
    }
}
