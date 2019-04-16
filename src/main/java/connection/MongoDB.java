package connection;

import java.util.ArrayList;
import java.util.List;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.DustReport;
import org.json.JSONObject;

//TODO not used for now
    public class MongoDB {

        private MongoClient client;
        private MongoDatabase database;
        private MongoCollection<BasicDBObject> collection;
        private static List<DustReport> allReports = new ArrayList<>();
        //private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy, hh:mm:ss"); //to do
        private final static String SENSOR = "sensor";
        private final static String COLLECTION = "dust";
        private final static String DATE = "datetime";
        private final static String VOLT = "volt";
        private final static String DENS = "dens";


        public MongoDB() {

            this.client = new MongoClient();
            System.out.println("Getting dataBase..");
            this.database = client.getDatabase(SENSOR);

            for (String name : database.listCollectionNames()) {
                if (name.equals(COLLECTION)) {
                    System.out.println("Collection dust found...");
                    this.collection = database.getCollection(COLLECTION, BasicDBObject.class);
                    break;
                }

            }
        }


        public void readDB() {

            System.out.println("Reading database");
            if (database != null && collection != null) {

                if (allReports.isEmpty()) {
                    System.out.println("Parsing db reports..");
                    for (BasicDBObject report :
                            collection.find(BasicDBObject.class)) {
                        DustReport dustReport = parseJSON(report);
                        allReports.add(dustReport);
                    }
                }

            } else {
                System.out.println("Database is Null");
            }
        }

        private DustReport parseJSON(BasicDBObject report) {
            DustReport entry = new DustReport();
            JSONObject object = new JSONObject(report.toString());
        /*Date date = null;
        try {
            date = sdf.parse(object.getString(DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
            //entry.setDate(object.getString(DATE));
            entry.setDens(object.getDouble(DENS));
            entry.setVoltage(object.getDouble(VOLT));
            return entry;

        }


        public  static List<DustReport> getAllReports() {
            return allReports;
        }


    }

