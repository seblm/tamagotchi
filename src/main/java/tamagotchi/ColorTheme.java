package tamagotchi;

import java.awt.*;

enum ColorTheme {

    ORIGINAL(
            new Color(160, 178, 129),
            new Color(10, 12, 6),
            new Color(156, 170, 125),
            new Color(128, 12, 24),
            new Color(200, 33, 44)
    ),
    PINK(
            new Color(203, 76, 200),
            new Color(12, 7, 10),
            new Color(191, 73, 188),
            new Color(225, 225, 197, 146),
            new Color(237, 233, 200)
    );

    final Color background;
    final Color pixel;
    final Color nonPixel;
    final Color buttonBorder;
    final Color buttonCenter;

    ColorTheme(Color background, Color pixel, Color nonPixel, Color buttonBorder, Color buttonCenter) {
        this.background = background;
        this.pixel = pixel;
        this.nonPixel = nonPixel;
        this.buttonBorder = buttonBorder;
        this.buttonCenter = buttonCenter;
    }

}
