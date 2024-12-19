package src;
import java.net.Socket;

public class Philosopher {
    private final int id;
    private int mealsConsumed = 0;
    private int meditations = 0;
    private TableSeat currentSeat;
    private Socket clientSocket;

    public Philosopher(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getMealsConsumed() {
        return mealsConsumed;
    }

    public void incrementMeals() {
        this.mealsConsumed++;
    }

    public int getMeditations() {
        return this.meditations;
    }

    public void incrementMeditations() {
        this.meditations++;
    }

    public String getStatus() {
        return " - Meditations: " + this.meditations + " - Meals: " + this.mealsConsumed;
    }

    public boolean isConnected() {
        return this.clientSocket != null && this.clientSocket.isConnected();
    }

    public Socket getConnection() {
        return this.clientSocket;
    }

    public void setConnection(Socket connection) {
        this.clientSocket = connection;
    }

    public TableSeat getCurrentSeat() {
        return this.currentSeat;
    }

    public void assignSeat(TableSeat seat) {
        this.currentSeat = seat;
    }
}
