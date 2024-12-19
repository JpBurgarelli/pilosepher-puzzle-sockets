package src;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TableSeat {
    private final int seatNumber;
    private final Hashi leftUtensil;
    private final Hashi rightUtensil;
    private Philosopher currentDiner;
    private final int meditationLimit;
    private String availability = "Free";

    public TableSeat(int seatNumber, Hashi leftUtensil, Hashi rightUtensil, int meditationLimit) {
        this.seatNumber = seatNumber;
        this.leftUtensil = leftUtensil;
        this.rightUtensil = rightUtensil;
        this.meditationLimit = meditationLimit;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getAvailability() {
        return this.availability;
    }

    public boolean isTaken() {
        return this.currentDiner != null;
    }

    public void assignDiner(Philosopher diner) {
        this.currentDiner = diner;
        this.availability = "Taken";
    }

    public void vacate() throws IOException {
        System.err.println("Philosopher " + this.currentDiner.getId() + " is leaving the table.");
        notifyClient("Philosopher " + this.getDinerId() + " finished dining.");
        this.availability = "Free";
        this.currentDiner = null;
        this.leftUtensil.release(this);
        this.rightUtensil.release(this);
    }

    public int getDinerMeals() {
        return this.currentDiner.getMealsConsumed();
    }

    public int getDinerMeditations() {
        return this.currentDiner.getMeditations();
    }

    public int getDinerId() {
        return this.currentDiner.getId();
    }

    public String getDinerStatus() {
        return this.currentDiner.getStatus();
    }

    public boolean hasLeftUtensil() {
        return this.leftUtensil.isInUseBy(this);
    }

    public boolean hasRightUtensil() {
        return this.rightUtensil.isInUseBy(this);
    }

    public boolean hasAllUtensils() {
        return this.hasLeftUtensil() && this.hasRightUtensil();
    }

    private void meditate() {
        Random random = new Random();
        double duration = random.nextGaussian() * 2 + 5;
        int meditationTime = (int) Math.max(0, duration);
        try {
            Thread.sleep(meditationTime);
        } catch (InterruptedException e) {
            System.err.println("Meditation interrupted: " + e.getMessage());
        }
    }

    public void contemplate() {
        this.currentDiner.incrementMeditations();
        this.meditate();
    }

    public void dine() throws InterruptedException {
        while (!this.hasAllUtensils()) {
            this.leftUtensil.acquire(this);

            if (!this.rightUtensil.isInUse()) {
                this.rightUtensil.acquire(this);
            } else {
                this.leftUtensil.release(this);
            }
        }

        this.currentDiner.incrementMeals();
        this.rightUtensil.release(this);
        this.leftUtensil.release(this);
    }

    public void occupy() throws IOException {
        try {
            long lastStatusUpdate = System.currentTimeMillis();

            System.out.println("Philosopher " + this.currentDiner.getId() + " took seat " + getSeatNumber() + ".");

            while (this.currentDiner.getMeditations() < this.meditationLimit) {
                this.contemplate();
                this.dine();

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastStatusUpdate >= 2000) {
                    this.notifyClient("Philosopher " + this.getDinerId() + " - " + this.getDinerStatus());
                    lastStatusUpdate = currentTime;
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Seating disrupted: " + e.getMessage());
        } finally {
            this.vacate();
        }
    }

    public void notifyClient(String message) throws IOException {
        synchronized (this.currentDiner.getConnection().getOutputStream()) {
            PrintWriter writer = new PrintWriter(this.currentDiner.getConnection().getOutputStream(), true);
            writer.println(message);
        }
    }
}