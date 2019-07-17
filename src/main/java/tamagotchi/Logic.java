package tamagotchi;

class Logic {

    Logic() {
        doSleep();
    }

    int hunger = 0;
    int energy = 0;
    int waste = 0;
    int age = 0;

    int happiness = 0;

    static final int AGE_HATCH = 128;
    static final int AGE_MATURE = 796;
    static final int AGE_DEATHFROMNATURALCAUSES = 8192;

    static final int HUNGER_NEEDSTOEAT = 128;
    static final int HUNGER_DEADFROMNOTEATING = 512;

    static final int ENERGY_CANSLEEP = 150;
    static final int ENERGY_TIERD = 64;
    static final int ENERGY_PASSOUT = 8;

    static final int WASTE_EXPUNGE = 256;

    void doCycle() {
        doRandomEvent();
        hunger++;
        waste++;
        energy--;
        age += 2;
        if (waste >= WASTE_EXPUNGE) happiness--;
    }

    private void doSleep() {
        energy += 256;
    }

    private void doRandomEvent() {
        switch ((int) (Math.random() * 32)) {
            case 12:
                hunger++;
            case 16:
                energy--;
            case 18:
                energy++;
            case 20:
                waste++;
            case 7:
                happiness++;
            case 4:
                happiness--;
            default:
                break;
        }
    }
}
