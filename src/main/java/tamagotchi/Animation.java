package tamagotchi;

class Animation {
    AnimationFrame[] animation;
    int frames;
    int frame = 0;

    Animation(AnimationFrame[] anim) {
        animation = anim;
        frames = anim.length;
    }

    int getNextFrame() {
        frame++;
        if (frame >= frames) {
            frame = 0;
        }
        return frame;
    }

    private int offset = 0;

    int getOffset() {
        if (Math.random() > 0.5f) {
            offset = (int) (Math.random() * 6) - 3;
        }
        return offset;
    }
}
