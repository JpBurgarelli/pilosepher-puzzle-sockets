package src;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class SessionManager implements Runnable {
    private final Socket clientSocket;
    private final DiningTable diningTable;
    private Philosopher philosopher;
    private PrintWriter out;
    private BufferedReader in;
    private TableSeat seat;

    public SessionManager(Socket clientSocket, DiningTable diningTable) {
        this.clientSocket = clientSocket;
        this.diningTable = diningTable;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Connection established successfully.");

            while (true) {
                String command = in.readLine();
                switch (command) {
                    case "JOIN" -> handleNewDiner();
                    case "RETURN" -> handleExistingDiner();
                    case "DINE" -> handleSeatingRequest();
                    case "STATUS" -> handleTableInfo();
                    case "INFO" -> handleDinerStats();
                    case "LEAVE" -> handleDinerExit();
                    case "END" -> { handleSessionEnd(); return; }
                    default -> out.println("ERROR: Invalid command '" + command + "'.");
                }
            }
        } catch (IOException e) {
            System.err.println("Session error: " + e.getMessage());
        } finally {
            cleanupSession();
        }
    }

    private void handleNewDiner() {
        if (this.philosopher == null) {
            this.philosopher = new Philosopher(diningTable.getNextAvailableId());
            this.philosopher.setConnection(this.clientSocket);
            diningTable.registerPhilosopher(this.philosopher);
            System.out.println("New philosopher registered: ID " + this.philosopher.getId());
            out.println("Welcome Philosopher " + this.philosopher.getId());
        } else {
            out.println("ERROR: Already connected as a philosopher.");
        }
    }

    private void handleExistingDiner() throws IOException {
        if (this.philosopher == null) {
            out.println("ID: ");
            int id = Integer.parseInt(in.readLine());
            Philosopher existingPhilosopher = diningTable.findPhilosopherById(id);
            if (existingPhilosopher != null) {
                if (!existingPhilosopher.isConnected()) {
                    this.philosopher = existingPhilosopher;
                    this.philosopher.setConnection(this.clientSocket);
                    System.out.println("Philosopher " + this.philosopher.getId() + " returned.");
                    out.println("Philosopher" + this.philosopher.getId() + " returned.");
                } else {
                    out.println("ERROR: Philosopher already active.");
                }
            } else {
                out.println("ERROR: No such philosopher found.");
            }
        } else {
            out.println("ERROR: Already connected as a philosopher.");
        }
    }

    private void handleSeatingRequest() throws IOException {
        if (this.philosopher != null) {
            if (this.philosopher.getMeditations() == this.diningTable.getMeditationLimit()) {
                out.println("ERROR: Philosopher has reached meditation limit.");
            } else {
                TableSeat availableSeat = diningTable.findAvailableSeat();
                if (availableSeat != null) {
                    this.seat = availableSeat;
                    this.philosopher.assignSeat(this.seat);
                    this.seat.assignDiner(this.philosopher);
                    out.println("SUCCESS: Philosoper seated at position " + this.seat.getSeatNumber() + " and began dining.");
                    this.seat.occupy();
                } else {
                    out.println("ERROR: All seats are occupied.");
                }
            }
        } else {
            out.println("ERROR: No philosopher connected.");
        }
    }

    private void handleDinerExit() throws IOException {
        if (this.philosopher != null) {
            this.philosopher.setConnection(null);
            System.out.println("Philosopher " + philosopher.getId() + " left.");
            this.philosopher = null;
            out.println("DISCONNECTED");
        } else {
            out.println("ERROR: No philosopher connected.");
        }
    }

    private void handleDinerStats() throws IOException {
        if (this.philosopher != null) {
            out.println("Philosopher " + this.philosopher.getId() + this.philosopher.getStatus());
        } else {
            out.println("ERROR: No philosopher connected.");
        }
    }

    private void handleTableInfo() throws IOException {
        List<String> tableInfo = this.diningTable.getTableStatus();
        for (String info : tableInfo) {
            out.println(info);
        }
        out.println("END");
    }

    private void handleSessionEnd() throws IOException {
        out.println("Terminating session.");
        if (this.philosopher != null) {
            this.philosopher.setConnection(null);
            this.philosopher = null;
        }
    }

    private void cleanupSession() {
        try {
            in.close();
            out.close();
            System.out.println("Session ended: " + this.clientSocket.getInetAddress());
            this.clientSocket.close();
        } catch (IOException e) {
            System.err.println("Cleanup error: " + e.getMessage());
        }
    }
}
