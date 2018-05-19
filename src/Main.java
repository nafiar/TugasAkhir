import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

public class Main {

    private static Config noSSL;
    private static Driver driver;
    private static SparqlConverter myConverter;
    private static String tugasAkhirSparql = "http://localhost:3030/tugasakhir/sparql";
    private static long cypherDuration, cypherMemuse;
    private static long sparqlDuration, sparqlMemuse;

    public static void main(String[] args) {

        noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
        driver = GraphDatabase.driver("bolt://localhost:7687",AuthTokens.basic("neo4j","mrwhite"),noSSL); // <password>

        myConverter = new SparqlConverter("resource/almondMilk.rq");

        doCypherQuery();
        doSparqlQuery("resource/almondMilk.rq");

        printTimeAndMemUsage();
    }

    public static void doCypherQuery(){
        String cypher = myConverter.getCypherQuery();
        System.out.println("cypher : " + cypher);

        try (Session session = driver.session())
        {
            long startTime = System.nanoTime();
            long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(cypher,parameters());

            long endTime = System.nanoTime();
            long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
            cypherDuration = (endTime - startTime)/1000000;
            cypherMemuse = (afterUsedMem-beforeUsedMem)/1024;

            System.out.println("Hasil Cypher Quer : ");
            // Each Cypher execution returns a stream of records.
            while (result.hasNext())
            {
                Record record = result.next();
                // Values can be extracted from a record by index or name.
                System.out.println(record.get(myConverter.returnQueryString()).asString());
            }
        }
    }

    public static void doSparqlQuery(String filename){
        System.out.println("Hasil SPARQL Query :");
        Query query = QueryFactory.read(filename);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(tugasAkhirSparql, query)){
            ((QueryEngineHTTP) qexec).addParam("timeout", "10000");

            long startTime = System.nanoTime();
            long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

            // Execute.
            ResultSet rs = qexec.execSelect();
            ResultSetFormatter.out(System.out, rs, query);

            long endTime = System.nanoTime();
            long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
            sparqlDuration = (endTime - startTime)/1000000;
            sparqlMemuse = (afterUsedMem-beforeUsedMem)/1024;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTimeAndMemUsage(){
        System.out.println("Duration of running time Cypher Query : " + cypherDuration + " ms");
        System.out.println("Memory usage of Cypher Query : " + cypherMemuse + " Kb");
        System.out.println("Duration of running time SPARQL Query : " + sparqlDuration + " ms");
        System.out.println("Memory usage of SPARQL Query : " + sparqlMemuse + " Kb");
    }
}

