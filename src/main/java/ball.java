public class ball extends Throwable {};
class P{
    P target;
    P(P target){
        this.target = target;
    }
    void aim(ball ball){
        try{
            throw ball;
        }
        catch (ball b){
            target.aim(b);
        }
    }
    public static void main(String[] args){
        p parent = new p(null);
        p child = new p(parent);
        parent.target = child;
        parent.aim(new ball());
    }
}

