package code.roadEstimator.entities;

public class DemoNod implements Comparable<DemoNod>{
    private int time = 0;
    private Noduri nod1;
    private Noduri nod2;

    public DemoNod() {
    }

    public DemoNod(int time, Noduri nod1, Noduri nod2) {
        this.time = time;
        this.nod1 = nod1;
        this.nod2 = nod2;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Noduri getNod1() {
        return nod1;
    }

    public void setNod1(Noduri nod1) {
        this.nod1 = nod1;
    }

    public Noduri getNod2() {
        return nod2;
    }

    public void setNod2(Noduri nod2) {
        this.nod2 = nod2;
    }

    public int compareTo(DemoNod o) {
        return Integer.compare(this.getTime(), o.getTime());
    }

}
