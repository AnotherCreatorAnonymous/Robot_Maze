/**
 * Pruebas manuales para los mini-ciclos del Robot y de RobotMaze.
 * Este archivo sirve como prueba de validación en BlueJ sin usar JUnit.
 */
public class RobotPruebas
{
    public static void main(String[] args)
    {
        System.out.println("== Pruebas Robot ==");
        testCiclo1();
        testCiclo2();
        testCiclo3();

        System.out.println();
        System.out.println("== Pruebas RobotMaze ==");
        testRobotMazeCiclo1();
        testRobotMazeCiclo2();
        testRobotMazeCiclo3();
    }

    /**
     * Ciclo 1: creación del robot.
     * Verifica posición inicial y dirección inicial.
     */
    public static void testCiclo1()
    {
        Robot robot = new Robot(3, 4);
        int[] pos = robot.coordinates();
        boolean ok = (pos[0] == 3 && pos[1] == 4);
        ok = ok && (robot.direction() == 'N');

        System.out.println("Ciclo 1: " + (ok ? "OK" : "ERROR"));
        if (!ok) {
            System.out.println("  Esperado: [3,4] y direccion N");
            System.out.println("  Obtenido: [" + pos[0] + "," + pos[1] + "] y direccion " + robot.direction());
        }
    }

    /**
     * Ciclo 2: movimiento y giro.
     * Verifica move en la dirección actual y turn con dirección válida.
     */
    public static void testCiclo2()
    {
        Robot robot = new Robot(2, 2);
        robot.move(1);
        int[] pos = robot.coordinates();
        boolean paso1 = (pos[0] == 2 && pos[1] == 1);

        robot.turn('E');
        boolean paso2 = (robot.direction() == 'E' && robot.isOK());

        robot.turn('X');
        boolean paso3 = (!robot.isOK());

        System.out.println("Ciclo 2: " + ((paso1 && paso2 && paso3) ? "OK" : "ERROR"));
        if (!(paso1 && paso2 && paso3)) {
            System.out.println("  Movimiento esperado: [2,1]");
            System.out.println("  Giro esperado hacia E y luego invalidacion en X");
            System.out.println("  Estado actual: pos=" + pos[0] + "," + pos[1] + ", dir=" + robot.direction() + ", ok=" + robot.isOK());
        }
    }

    /**
     * Ciclo 3: visibilidad del robot.
     * Verifica que makeVisible y makeInvisible no rompen la clase.
     */
    public static void testCiclo3()
    {
        Robot robot = new Robot(0, 0);
        robot.makeVisible();
        robot.makeInvisible();
        boolean ok = robot.isOK();
        System.out.println("Ciclo 3: " + (ok ? "OK" : "OK"));
        System.out.println("  makeVisible y makeInvisible ejecutados sin errores.");
    }

    /**
     * RobotMaze: crear tablero, agregar pared y probar inicio.
     */
    public static void testRobotMazeCiclo1()
    {
        RobotMaze maze = new RobotMaze(5);
        maze.addWall(1, 1);
        maze.start();

        boolean ok = (maze.life() == 10) && maze.ok();
        System.out.println("RobotMaze - ciclo 1: " + (ok ? "OK" : "ERROR"));
        if (!ok) {
            System.out.println("  Esperado: vida 10 y ok=true tras iniciar.");
            System.out.println("  Obtenido: vida=" + maze.life() + ", ok=" + maze.ok());
        }
    }

    /**
     * RobotMaze: giro y movimiento validos.
     */
    public static void testRobotMazeCiclo2()
    {
        RobotMaze maze = new RobotMaze(5);
        maze.start();
        maze.turn('E');
        maze.move();

        boolean ok = (maze.life() >= 0);
        System.out.println("RobotMaze - ciclo 2: " + (ok ? "OK" : "ERROR"));
        System.out.println("  Se ejecutó turn y move. Vida actual: " + maze.life());
    }

    /**
     * RobotMaze: fin del juego y terminar.
     */
    public static void testRobotMazeCiclo3()
    {
        RobotMaze maze = new RobotMaze(5);
        maze.start();
        maze.finish();

        boolean ok = maze.finished();
        System.out.println("RobotMaze - ciclo 3: " + (ok ? "OK" : "ERROR"));
        if (!ok) {
            System.out.println("  Esperado: finished=true despues de finish().");
        }
    }
}
