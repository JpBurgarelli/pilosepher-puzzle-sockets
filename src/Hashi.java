package src;
public class Hashi {
    private final int identifier;
    private TableSeat currentSeat;

    public Hashi(int identifier) {
        this.identifier = identifier;
    }

    public int getIdentifier() {
        return identifier;
    }

    public TableSeat getCurrentSeat() {
        return currentSeat;
    }

    public synchronized void acquire(TableSeat seat) throws InterruptedException {
        while (this.currentSeat != null && !this.currentSeat.equals(seat)) {
            wait();
        }
        this.currentSeat = seat;
    }

    public synchronized void release(TableSeat seat) {
        if (this.currentSeat != null && this.currentSeat.equals(seat)) {
            this.currentSeat = null;
            notifyAll();
        }
    }

    public boolean isInUse() {
        return this.currentSeat != null;
    }

    public boolean isInUseBy(TableSeat seat) {
        try {
            if (this.currentSeat == null) {
                return false;
            }
            return this.currentSeat.equals(seat);
        } catch (NullPointerException e) {
            return false;
        }
    }
}
