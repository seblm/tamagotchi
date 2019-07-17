package tamagotchi;

class AnimationFrame {
    int[] framedata;

    public AnimationFrame(int[] framedata){
        this.framedata = framedata;
    }
    public AnimationFrame(byte[][] drawmatrix){
        framedata = new int[32];
        for(int i = 0; i < 32; i++){
            int shift = 0;
            for(int e = 31; e > -1; e--){
                byte b = drawmatrix[i][e];
                shift = (shift<<1) | b;
            }
            framedata[i] = shift;
        }
    }
}
