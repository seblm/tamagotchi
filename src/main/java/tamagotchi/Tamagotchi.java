/*

          Reddit DailyProgrammer Challenge #180 -- Tamagotchi Emulator
            By Aerospark12 (aka Luke)

    I may have gone a little bit overboard with this one, but I've got fond memories of tamagotchi from when
  I was but a wee laddie, I've always wanted to make something like this, and I really enjoy graphics and animation

    The design of this is partially inspirte by how I understand "low level" ICs to work, as an homage to the real tamagotchi


    I'm definitely not proud of the code in this, a lot of things are poorly implemented, theres lots of room for improvement,
  I wanted to get the base stuff implemented as fast as possible.



        This code is "licensed" under Creative Commons CC-BY


*/
package tamagotchi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static tamagotchi.Logic.AGE_DEATHFROMNATURALCAUSES;
import static tamagotchi.Logic.HUNGER_DEADFROMNOTEATING;

/**
 * @author Luke
 */

public class Tamagotchi {

    static class TamagotchiComponent extends Component {
        int[] gfxbuffer;
        int[] overlaybuffer = new int[32];

        Animation currentAnimation = Animations.IDLE_EGG;
        Animation overlayAnimation = Animations.OVERLAY_ZZZ;

        Logic tom = new Logic();

        int W, H;
        int stage = 0;

        boolean hasOverlayAnimation = false;

        ColorTheme currentTheme = ColorTheme.ORIGINAL;

        BufferedImage components = new BufferedImage(500, 520, BufferedImage.TYPE_INT_RGB);
        BufferedImage selector = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        void renderComponent(Graphics2D g, AnimationFrame fr, int xo, int yo) {
            for (int x = 0; x < 32; x++) {
                int v = fr.framedata[x];
                for (int y = 0; y < 32; y++) {
                    int bv = (v & (1 << y - 1));
                    if (bv != 0)
                        g.setColor(currentTheme.pixel);
                    else
                        g.setColor(currentTheme.nonPixel);
                    g.drawLine(y + xo, x + yo, y + xo, x + yo);
                }
            }
        }

        int off = 0;
        int selid = 0;

        void updatePage() {
            switch (spid) {
                case 0: // hunger
                    statsPage = Components.DISPLAY_HUNGER;
                    break;
                case 1: // age
                    statsPage = Components.DISPLAY_AGE;
                    break;
                case 2: // waste
                    statsPage = Components.DISPLAY_WASTE;
                    break;
                case 3: // energy
                    statsPage = Components.DISPLAY_ENERGY;
                    break;
                case 4: // back
                    statsPage = Components.DISPLAY_BACK;
                    break;
                default:
                    break;
            }
        }

        void createBackground() {
            Graphics2D gs = selector.createGraphics();
            Graphics2D g = components.createGraphics();
            g.setColor(currentTheme.background);
            g.fillRect(0, 0, W, H);
            gs.setColor(currentTheme.pixel);
            for (int x = 0; x < 32; x++) {
                int v = Components.SELECTOR.framedata[x];
                for (int y = 0; y < 32; y++) {
                    int bv = (v & (1 << y - 1));
                    if (bv != 0)
                        gs.drawLine(y, x, y, x);
                }
            }

            renderComponent(g, Components.FEED, 64, 16);
            renderComponent(g, Components.FLUSH, 128, 16);
            renderComponent(g, Components.HEALTH, 192, 16);
            renderComponent(g, Components.ZZZ, 256, 16);
            g.setColor(currentTheme.buttonBorder);
            g.fillOval(64, 420, 64, 64);
            g.fillOval(64 + 192, 420, 64, 64);
            g.fillOval(64 + 96, 420, 64, 64);
            g.setColor(currentTheme.buttonCenter);
            g.fillOval(68 + 96, 424, 56, 56);
            g.fillOval(68 + 192, 424, 56, 56);
            g.fillOval(68, 424, 56, 56);
            g.setColor(currentTheme.pixel);
            g.drawOval(64 + 96, 420, 64, 64);
            g.drawOval(64 + 192, 420, 64, 64);
            g.drawOval(64, 420, 64, 64);
        }

        {
            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    if (y > 420 && y < 484) {
                        if (x > 64 && x < 128) {
                            if (stats) {
                                spid--;
                                if (spid <= -1) spid = 4;
                                updatePage();
                            } else {
                                selid--;
                                if (selid <= -1) selid = 3;
                            }
                        } else if (x > 64 + 96 && x < 128 + 96) {
                            centerButton();
                        } else if (x > 64 + 192 && x < 128 + 192) {
                            if (stats) {
                                spid++;
                                spid %= 5;
                                updatePage();
                            } else {
                                selid++;
                                selid %= 4;
                            }
                        }
                    }
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }

            });

            W = 382;
            H = 520;

            setSize(W, H);
            setPreferredSize(new Dimension(W, H));
            gfxbuffer = currentAnimation.animation[0].framedata;
            if (hasOverlayAnimation) {
                overlaybuffer = overlayAnimation.animation[0].framedata;
            }

            createBackground();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            if (cleaning) Thread.sleep(100);
                            else
                                Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Tamagotchi.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        if (stage == 0 && tom.age > Logic.AGE_HATCH) {
                            stage++;
                            currentAnimation = Animations.IDLE_BABY;
                            hasOverlayAnimation = false;
                        }
                        if (stage == 1 && tom.age > Logic.AGE_MATURE) {
                            stage++;
                            currentAnimation = Animations.IDLE_MATURE;
                        }
                        int frm = overlayAnimation.getNextFrame();
                        if (eating && frm == overlayAnimation.frames - 1) {
                            eating = false;
                            hasOverlayAnimation = false;
                            overlayAnimation.frame = 0;
                            tom.hunger = 0;
                        }
                        if (hasOverlayAnimation) {
                            overlaybuffer = overlayAnimation.animation[frm].framedata;
                        }
                        if (sleeping) {
                            tom.energy += 8;
                            if (tom.energy >= 256) {
                                sleeping = false;
                                hasOverlayAnimation = false;
                                switch (stage) {
                                    case 0:
                                        currentAnimation = Animations.IDLE_EGG;
                                        break;
                                    case 1:
                                        currentAnimation = Animations.IDLE_BABY;
                                        break;
                                    case 2:
                                        currentAnimation = Animations.IDLE_MATURE;
                                        break;
                                }
                            }
                        }
                        if (cleaning) {
                            off = cleanincr--;
                            if (off == -33) {
                                off = 0;
                                cleanincr = 0;
                                cleaning = false;
                                hasOverlayAnimation = false;
                                tom.waste = 0;
                            }
                        } else {
                            if (!dead) {
                                gfxbuffer = currentAnimation.animation[currentAnimation.getNextFrame()].framedata;
                                off = currentAnimation.getOffset();
                            }
                            if (!sleeping && !dead) tom.doCycle();
                            if (tom.energy < Logic.ENERGY_PASSOUT) {
                                if (stage > 0) {
                                    tom.happiness -= 64;
                                }
                                triggerSleep();
                            }
                        }
                        if (!sleeping && !cleaning && !eating && !dead) {
                            if (tom.waste >= Logic.WASTE_EXPUNGE) {
                                overlayAnimation = Animations.OVERLAY_STINK;
                                hasOverlayAnimation = true;
                            } else if (tom.energy <= Logic.ENERGY_TIERD || tom.hunger >= Logic.HUNGER_NEEDSTOEAT || tom.waste >= Logic.WASTE_EXPUNGE - Logic.WASTE_EXPUNGE / 3) {
                                overlayAnimation = Animations.OVERLAY_EXCLAIM;
                                hasOverlayAnimation = true;
                            }

                            if (tom.hunger >= HUNGER_DEADFROMNOTEATING || tom.age >= AGE_DEATHFROMNATURALCAUSES) {
                                triggerDeath();
                            }
                        }
                        repaint();
                    }
                }

            }).start(); // animation/"cycle" manager
        }

        boolean cleaning = false;
        boolean eating = false;
        boolean stats = false;
        boolean sleeping = false;
        boolean dead = false;

        void triggerSleep() {
            sleeping = true;
            overlayAnimation = Animations.OVERLAY_ZZZ;
            animationSleep().ifPresent(animationSleep -> currentAnimation = animationSleep);
            hasOverlayAnimation = true;
        }

        void triggerDeath() {
            dead = true;
            overlayAnimation = Animations.OVERLAY_DEAD;
            animationSleep().ifPresent(animationSleep -> currentAnimation = animationSleep);
            hasOverlayAnimation = true;
            gfxbuffer = currentAnimation.animation[0].framedata;
            overlaybuffer = overlayAnimation.animation[0].framedata;
            off = 3;
        }

        Optional<Animation> animationSleep() {
            switch (stage) {
                case 1:
                    return Optional.of(Animations.SLEEP_BABY);
                case 2:
                    return Optional.of(Animations.SLEEP_MATURE);
                default:
                    return Optional.empty();
            }
        }

        void centerButton() {
            if (stage > 0 || selid == 2) {
                switch (selid) {
                    case 0:
                        eating = true;
                        overlayAnimation = Animations.OVERLAY_EAT;
                        overlayAnimation.frame = 0;
                        hasOverlayAnimation = true;
                        break;
                    case 1:
                        cleaning = true;
                        overlayAnimation = Animations.OVERLAY_CLEAN;
                        overlayAnimation.frame = 0;
                        hasOverlayAnimation = true;
                        break;
                    case 2:
                        stats = !stats;
                        break;
                    case 3:
                        if (tom.energy <= Logic.ENERGY_CANSLEEP) {
                            triggerSleep();
                        }
                        break;
                }
            }

        }

        AnimationFrame statsPage = Components.DISPLAY_HUNGER;
        int spid = 0;

        int cleanincr = 0;

        @Override
        public void paint(Graphics g) {
            g.drawImage(components, 0, 0, null);
            g.drawImage(selector, 64 + selid * 64, 16, null);
            if (stats) {
                int percv = 0;
                switch (spid) {
                    case 0: // hunger
                        percv = tom.hunger * 27 / Logic.HUNGER_NEEDSTOEAT;
                        break;
                    case 1: // age
                        percv = tom.age * 27 / AGE_DEATHFROMNATURALCAUSES;
                        break;
                    case 2: // waste
                        percv = (tom.waste % Logic.WASTE_EXPUNGE) * 27 / Logic.WASTE_EXPUNGE;
                        break;
                    case 3: // energy
                        percv = tom.energy * 27 / 256;
                        break;
                    default:
                        break;
                }
                if (percv > 27) percv = 27;
                for (int x = 0; x < 32; x++) {
                    int v = statsPage.framedata[x];
                    for (int y = 1; y < 33; y++) {
                        int bv = (v & (1 << y - 1));
                        if (bv != 0 || (percv > 0 && x > 11 && y > 3 && x < 17 && y < 3 + percv))
                            g.setColor(currentTheme.pixel);
                        else
                            g.setColor(currentTheme.nonPixel);
                        g.fillRect((y - 1) * 10 + 32, x * 10 + 64, 8, 8);
                    }
                }
            } else {
                for (int x = 0; x < 32; x++) {
                    int v = gfxbuffer[x];
                    if (hasOverlayAnimation) v |= overlaybuffer[x];
                    for (int y = off; y < 32 + off; y++) {
                        int bv = (v & (1 << y - 1));
                        if (bv != 0)
                            g.setColor(currentTheme.pixel);
                        else
                            g.setColor(currentTheme.nonPixel);
                        g.fillRect((y - off) * 10 + 32, x * 10 + 64, 8, 8);
                    }
                }
            }
        }
    }

    static class ThemeSwitcher implements KeyListener {
        private final TamagotchiComponent tamagotchi;

        ThemeSwitcher(TamagotchiComponent tamagotchi) {
            this.tamagotchi = tamagotchi;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == 't') {
                ColorTheme[] colorThemes = ColorTheme.values();
                int currentIndex = List.of(colorThemes).indexOf(tamagotchi.currentTheme);
                tamagotchi.currentTheme = colorThemes[(currentIndex + 1) % colorThemes.length];
                tamagotchi.createBackground();
                tamagotchi.repaint();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public static void main(String[] args) {
        JFrame j = new JFrame("Tamagotchi Simulator Thing");
        TamagotchiComponent tamagotchi = new TamagotchiComponent();
        j.add(tamagotchi);
        j.addKeyListener(new ThemeSwitcher(tamagotchi));
        j.pack();
        j.setLocationRelativeTo(null);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setVisible(true);
    }

}