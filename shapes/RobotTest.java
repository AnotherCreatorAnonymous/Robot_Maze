import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RobotTest {
    @Test
    public void robotStartsAtGivenPositionAndFacingNorth() {
        Robot robot = new Robot(3, 4);

        assertEquals(3, robot.coordinates()[0]);
        assertEquals(4, robot.coordinates()[1]);
        assertEquals('N', robot.direction());
        assertEquals(10, robot.life());
    }

    @Test
    public void robotMovesAndTurnsCorrectly() {
        Robot robot = new Robot(2, 2);

        robot.move(1);
        assertEquals(2, robot.coordinates()[0]);
        assertEquals(1, robot.coordinates()[1]);

        robot.turn('E');
        assertEquals('E', robot.direction());
        assertTrue(robot.isOK());

        robot.turn('X');
        assertFalse(robot.isOK());
    }
}
