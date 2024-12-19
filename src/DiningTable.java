package src;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiningTable {
    private ServerSocket serverSocket;
    private final int SERVICE_PORT = 12345;
    private static final int SEAT_COUNT = 5;
    private final ScheduledExecutorService statusMonitor = Executors.newScheduledThreadPool(1);
    private final TableSeat[] seats = new TableSeat[SEAT_COUNT];
    private final Hashi[] utensils = new Hashi[SEAT_COUNT];
    private final Map<Integer, Philosopher> philosopherRegistry = new HashMap<>();
    private final List<SessionManager> activeSessions = Collections.synchronizedList(new ArrayList<>());
    private final int MEDITATION_LIMIT = 20000;

    public DiningTable() {
        for (int i = 0; i < SEAT_COUNT; i++) {
            utensils[i] = new Hashi(i + 1);
        }

        for (int i = 0; i < SEAT_COUNT; i++) {
            seats[i] = new TableSeat(i + 1, utensils[i], utensils[(i + 1) % SEAT_COUNT], MEDITATION_LIMIT);
        }
    }

    public void initializeService() {
        try {
            this.serverSocket = new ServerSocket(this.SERVICE_PORT);
            System.out.println("Dining service started on port " + this.SERVICE_PORT + "\n");

            startConnectionHandler();
            initializeStatusMonitor();

        } catch (IOException e) {
            System.err.println("Service initialization error: " + e.getMessage());
        }
    }

    private void startConnectionHandler() {
        new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection from: " + clientSocket.getInetAddress());
                    SessionManager sessionManager = new SessionManager(clientSocket, this);
                    activeSessions.add(sessionManager);
                    new Thread(sessionManager).start();
                }
            } catch (IOException e) {
                System.err.println("Connection handling error: " + e.getMessage());
            }
        }).start();
    }

    private void initializeStatusMonitor() {
        statusMonitor.scheduleAtFixedRate(this::monitorSeats, 0, 2, TimeUnit.SECONDS);
    }

    private void monitorSeats() {
        if (hasOccupiedSeats()) {
            System.err.println("\n############## TABLE STATUS ##############");
            for (TableSeat seat : seats) {
                if (seat.isTaken()) {
                    System.err.println("Seat " + seat.getSeatNumber() + " - Philosopher " +
                            seat.getDinerId() + " - " + seat.getDinerStatus());
                }
            }
        }
    }

    public TableSeat findAvailableSeat() {
        for (TableSeat seat : seats) {
            if (!seat.isTaken()) {
                return seat;
            }
        }
        return null;
    }

    public Boolean hasOccupiedSeats() {
        for (TableSeat seat : seats) {
            if (seat.isTaken()) {
                return true;
            }
        }
        return false;
    }

    public Philosopher findPhilosopherById(int id) {
        return philosopherRegistry.get(id);
    }

    public void registerPhilosopher(Philosopher philosopher) {
        philosopherRegistry.put(philosopher.getId(), philosopher);
    }

    public int getNextAvailableId() {
        int maxId = 0;
        for (Philosopher philosopher : philosopherRegistry.values()) {
            if (philosopher.getId() > maxId) {
                maxId = philosopher.getId();
            }
        }
        return maxId + 1;
    }

    public List<String> getTableStatus() {
        List<String> status = new ArrayList<>();
        status.add("Table Information: ");
        status.add("Service Port: " + SERVICE_PORT);
        status.add("Total Seats: " + SEAT_COUNT);
        status.add("Meditation Limit: " + MEDITATION_LIMIT);
        status.add("Active Sessions: " + activeSessions.size());
        
        status.add("");
        status.add("Seat Status: ");
        for (String seatInfo : getSeatStatus()) {
            status.add(seatInfo);
        }
        return status;
    }

    private List<String> getSeatStatus() {
        List<String> seatStatusList = new ArrayList<>();
        
        for (TableSeat seat : seats) {
            seatStatusList.add(String.format("Seat ID: %d | Status: %s", 
                                            seat.getSeatNumber(), 
                                            seat.getAvailability()));
        }
        return seatStatusList;
    }

    public int getMeditationLimit() {
        return MEDITATION_LIMIT;
    }
}
