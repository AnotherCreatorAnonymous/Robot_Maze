import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.Test;

public class RobotMazeTest {
    @Test
    public void testTamanoMenorA10LanzaExcepcion() {
        try {
            new RobotMaze(9);
            fail("Se esperaba IllegalArgumentException para un tamaño menor a 10");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("10"));
        }
    }

    @Test
    public void testConstructorVacioSigueSinParedes() {
        RobotMaze maze = new RobotMaze(10);
        assertEquals(0, maze.wallCount());
    }

    @Test
    public void testConstructorAleatorioNoRecibeCantidad() {
        RobotMaze maze = new RobotMaze(10, true);
        assertTrue(maze.wallCount() > 0);
    }

    @Test
    public void testLaberintoAleatorioTieneParedesInterioresSuficientes() {
        RobotMaze maze = new RobotMaze(10, true);
        assertTrue("El laberinto debería estar poblado en el interior", maze.wallCount() >= 40);
        assertTrue("La densidad interior no debe sobrepasar el rango razonable", maze.wallCount() <= 60);
    }

    @Test
    public void testBordeSiguesiendoObligatorio() {
        RobotMaze maze = new RobotMaze(10, true);
        Set<Long> camino = obtenerCaminoGarantizado(maze);
        int size = 10;
        int entryX = maze.entryX();
        int entryY = maze.entryY();
        int exitX = maze.exitX();
        int exitY = maze.exitY();

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                boolean esBorde = (row == 0 || row == size - 1 || column == 0 || column == size - 1);
                if (!esBorde) {
                    continue;
                }

                boolean esApertura = (row == entryX && column == entryY)
                    || (row == exitX && column == exitY);
                long clave = (long) row * size + column;
                boolean esCaminoGarantizado = camino.contains(clave);

                if (!esApertura && !esCaminoGarantizado) {
                    assertTrue("La celda de borde no tiene pared: [" + row + "," + column + "]",
                        maze.hasWall(row, column));
                }
            }
        }
    }

    @Test
    public void testLaberintoAleatorioSiempreTieneSolucion() {
        for (int i = 0; i < 200; i++) {
            RobotMaze maze = new RobotMaze(10, true);
            assertTrue("Laberinto sin solución en la iteración " + i, maze.tieneSolucion());
        }
    }

    @Test
    public void defaultMazeKeepsPathAvailable() {
        RobotMaze maze = new RobotMaze(10);
        assertTrue(maze.tieneSolucion());
        assertFalse(maze.finished());
    }

    @SuppressWarnings("unchecked")
    private Set<Long> obtenerCaminoGarantizado(RobotMaze maze) {
        try {
            Method metodo = RobotMaze.class.getDeclaredMethod("celdasDelCaminoGarantizado");
            metodo.setAccessible(true);
            return (Set<Long>) metodo.invoke(maze);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo inspeccionar el camino garantizado", e);
        }
    }
}

