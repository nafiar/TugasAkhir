import org.neo4j.driver.v1.*;

import java.util.Map;

import static org.neo4j.driver.v1.Values.parameters;

public class PropertyGetter {
    private static Config noSSL;
    private static Driver driver;

    public static void Main(String[] args){
        noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
        driver = GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","mrwhite"),noSSL); // <password>

        String cypher = "match (s:Resource { uri : \"http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co\"}) return properties(s)";

        try (Session session = driver.session()){
            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(cypher,parameters());
            // Each Cypher execution returns a stream of records.
            while (result.hasNext())
            {
                Map<String, Object> mapResult = result.asMap();
//                Record record = result.next();

                // Values can be extracted from a record by index or name.
            }

        }
    }
}
