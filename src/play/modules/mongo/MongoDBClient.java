package play.modules.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class MongoDBClient {

    private Mongo mongo;
    private DB db;
    
    private String host;
    private Integer port;
    private String dbname;
    private String username;
    private String password;

	
	public MongoDBClient(String pHost, Integer pPort, String pDBName, String pUserName, String pPass) 
		throws MongoException, UnknownHostException {
		
		host = pHost;
		port = pPort;
		dbname = pDBName;
		username = pUserName;
		password = pPass;
		
		ServerAddress lSA = new ServerAddress(host, port);
		List<MongoCredential> lCred = new ArrayList<MongoCredential>();
		if (username != null && password != null) {
			lCred.add(MongoCredential.createCredential(username, dbname, password.toCharArray()));
		}
		mongo = new MongoClient(lSA, lCred);
		// test connection
		mongo.getConnectPoint();
		// get database
		db = mongo.getDB(dbname);
	}
	
    /**
     * Obtain a reference to the mongo database.
     * 
     * @return - a reference to the Mongo database
     */
	public DB db() {
		return db;
	}

	/**
	 * Close connection to database
	 */
	public void close() {
		mongo.close();
	}
}
