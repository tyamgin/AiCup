import java.awt.*;

import model.*;

public final class LocalTestRendererListener {
    public void beforeDrawScene(Graphics graphics, World world, Game game, double scale) {
        graphics.drawRect(100, 100, 500, 500);
        for (Hockeyist hockeyist : world.getHockeyists()) {
            graphics.drawArc((int) hockeyist.getX() - 50, (int) hockeyist.getY() - 50, 100, 100, 0, 360);
        }
    }

    public void afterDrawScene(Graphics graphics, World world, Game game, double scale) {
        graphics.drawRect(200, 200, 550, 550);
        for (Hockeyist hockeyist : world.getHockeyists()) {
            graphics.drawArc((int) hockeyist.getX() - 40, (int) hockeyist.getY() - 40, 80, 80, 0, 360);
        }
    }
}
