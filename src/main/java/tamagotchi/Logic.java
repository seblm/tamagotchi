package tamagotchi;

public class Logic {

    public Logic(){
        doSleep();
    }

    int hunger = 0;
    int energy = 0;
    int waste = 0;
    int age = 0;

    // not required
    int happiness = 0;

    public static final int AGE_HATCH = 128;
    public static final int AGE_MATURE = 796;
    public static final int AGE_DEATHFROMNATURALCAUSES = 8192; // :(

    public static final int HUNGER_CANEAT = 32;
    public static final int HUNGER_NEEDSTOEAT = 128;
    //not required
    public static final int HUNGER_SICKFROMNOTEATING = 256;
    public static final int HUNGER_DEADFROMNOTEATING = 512;

    public static final int ENERGY_CANSLEEP = 150;
    public static final int ENERGY_TIERD = 64;
    public static final int ENERGY_PASSOUT = 8;

    public static final int WASTE_EXPUNGE = 256; // lol


    public void doCycle(){
        doRandomEvent();
        hunger++;
        waste++;
        energy--;
        age+=2;
        if(waste>= WASTE_EXPUNGE) happiness--;
    }

    public void doSleep(){
        energy += 256;
    }

    public void doRandomEvent(){

        switch((int)(Math.random()*32)){
            case 12: hunger++;
            case 16: energy--;
            case 18: energy++;
            case 20: waste++;
            case 7: happiness++;
            case 4: happiness--;
            default: break;
        }
    }
}
