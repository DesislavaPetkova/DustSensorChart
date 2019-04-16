package serial.connection;

import com.dust.sensor.DustRepository;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import model.DustReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

interface ReportListener{
    void handleReport(DustReport report);
}
public class Serial implements SerialPortEventListener, Runnable,ReportListener{

    SerialPort serialPort;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-1420", // Mac OS X
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


    public void setRepo(DustRepository repo) {
        this.repo = repo;
    }

    private DustRepository repo;

    public void setMiliseconds(long miliseconds) {
        this.miliseconds = miliseconds;
    }

    private long miliseconds;
    private final Object lock = new Object();
    private  DustReport report;

    public Serial() {
        initialize();
    }


    private void initialize() {
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
            System.out.println("Could not find COM port.");
            return;
        }


        try {
            // open serial port, and use class name for the appName.
            System.out.println("opening serial port");
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
            System.err.println(e.toString());
        }
    }

    public void requestSensorData() throws IOException {
        output = serialPort.getOutputStream();
        if (output != null) {
            System.out.println("Requesting data...");
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
                    System.out.println("in");
                    report=new DustReport(LocalDateTime.now(), Double.parseDouble(data[1]), Double.parseDouble(data[3]));
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
        while (true) {
            synchronized (lock) {
                try {
                    lock.wait(miliseconds);
                    requestSensorData();
                    //lock.wait(10000);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

    }

    @Override
    public void handleReport(DustReport report) {
        if(repo!=null) {
            repo.save(report);
            System.out.println("Saved");
        }else{
            System.out.println("repo is null");
        }
    }
}
