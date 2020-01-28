package serial.connection;

import com.dust.sensor.DustRepository;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import model.DustReport;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;


public class Serial implements SerialPortEventListener, Runnable {

    SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-1410", // Mac OS X
            "COM3", // Windows //todo check if it 3
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    private DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy, MM dd, hh:mm");

    //TODO autowire repo here??
    private DustRepository repo;

    private long milliseconds;

    private final Object lock = new Object();
    public boolean isRunning = true;

    public Serial(DustRepository repo) {
        this.repo = repo;
    }


    public void initialize() throws IOException {
        //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/tty.usbserial-1420");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            throw new IOException("Could not find COM port. Sensor is unavailable! ");
        }


        try {
            // open serial port, and use class name for the appName.
            System.out.println("Opening serial port");
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            throw new IOException("Could cot initialize event listener ");
        }
    }

    public void requestSensorData() throws IOException {
        output = serialPort.getOutputStream();
        if (output != null) {
            System.out.println("Requesting sensor data...");
            output.write(2);
            output.flush();
            output.close();
        }

    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            //todo stop thread
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            System.out.println("Serial event handling...");
            try {
                String inputLine = input.readLine().trim();
                System.out.println(inputLine);
                if (!inputLine.equals(" ")) {
                    String[] data = inputLine.split(" ");
                    DustReport report = new DustReport(LocalDateTime.now(), Double.parseDouble(data[1]), Double.parseDouble(data[3]));
                    System.out.println("Report created.. Calling handleReport()..");
                    handleReport(report);

                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
            //lock.notify();
        } else {
            System.out.println("Serial event problem " + serialPortEvent.getEventType());
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    @Override
    public void run() {
        System.out.println("Waiting...."+ isRunning);
        while (isRunning) {
            System.out.println("Waiting...."+ isRunning);
            synchronized (lock) {
                try {
                    System.out.println("Waiting....   hahahaha");
                    lock.wait(milliseconds);
                    requestSensorData();
                    //lock.wait(10000);

                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void handleReport(DustReport report) {
        if (repo != null) {
            repo.save(report);
            System.out.println("Saved in Mongo database");
        } else {
            System.out.println("Mongo database is not available!");
        }
    }

    public void setMilliseconds(long milliseconds) {
        synchronized (lock) {
            lock.notify();
            this.milliseconds = milliseconds;
        }
    }

    public void stopRunnable() {

        synchronized (lock) {
            lock.notify();
            isRunning = false;

        }


    }
}
