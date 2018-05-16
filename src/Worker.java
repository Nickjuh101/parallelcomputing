/**
 * Created by Nick on 0016 16 mei 2018.
 */
public class Worker {

    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private Object lock3 = new Object();

    private String text;

    private int wordCount;

    public void stageOne(){
        synchronized (lock1){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void stageTwo(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void stageThree(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void process(){
        stageOne();
        stageTwo();
        stageThree();
    }

    public void main(){

    }

}
