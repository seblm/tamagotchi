package tamagotchi;

public class Animation{
    AnimationFrame[] animation;
    int frames = 0;
    int frame = 0;
    boolean randomframe = false;
    public Animation(AnimationFrame[] anim){
        animation = anim;
        frames = anim.length;
    }
    public Animation(boolean randomframe, AnimationFrame[] anim){
//        this.randomframe = randomframe; // disabled because I dont like how it acts right now and its not worth fixing yet
        animation = anim;
        frames = anim.length;
    }

    public int getNextFrame(){
        if(randomframe){
            frame = (int)(Math.random()*frames);
        }else{
            frame++;
            if(frame>=frames){
                frame = 0;
            }
        }
        return frame;
    }

    int offset = 0;

    public int getOffset(){
        if(Math.random()>0.5f){
            offset = (int) (Math.random()*6)-3;
        }
        return offset;
    }
}
