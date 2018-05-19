import org.apache.jena.base.Sys;
import  org.apache.jena.riot.system.IRIResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SparqlConverter {

    public String filePath;
    public ArrayList<String> returnedVariable;
    public ArrayList<String> sparqlQueries;
    public ArrayList<String> cypherWhereQueries;
    public ArrayList<String> cypherMatchQueries;
    public HashMap<String, String> prefixes;

    public SparqlConverter(String path){
        filePath = path;
        returnedVariable = new ArrayList<String>();
        sparqlQueries = new ArrayList<String>();
        cypherMatchQueries = new ArrayList<String>();
        cypherWhereQueries = new ArrayList<String>();
        prefixes = new HashMap<String, String>();
        readFile();
    }

    public void readFile(){
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String str;

            while ((str = in.readLine()) != null) {
                String[] tmp = str.split("\\s+");
//                System.out.println(Arrays.toString(tmp));
                if (prefixHandler(tmp[0])){
                    if (prefixes.get(tmp[1]) == null){
                        prefixes.put(tmp[1], tmp[2].substring(1,tmp[2].length()-1));
                    }
                }
                else if (selectHandler(tmp[0])){
                    for(int i = 1; i < tmp.length; i++){
                        if (whereHandler(tmp[i])){
                            continue;
                        }
                        else if (isVariable(tmp[i])){
                            String variable = tmp[i].substring(1,tmp[i].length());
                            returnedVariable.add(variable);
                        }
                    }
                }
                else if (tmp[tmp.length-1].equals(".")){
                    String query = "";
                    String subject = "";
                    String predicate = "";
                    String object = "";
                    boolean strLiteral = false;
                    for(int i = 1; i < tmp.length - 1; i++){
                        if (i == 1) {
                            subject = tmp[i];
                            query += " " + subject;
                        }
                        else if (i == 2) {
                            predicate = tmp[i];
                            query += " " + predicate;
                        }
                        else if (i > 2){
                            String firstChar = tmp[i].substring(0,1);
                            query += " " + tmp[i];
                            if (firstChar.equals(".")){
                                continue;
                            }
                            if (strLiteral){
                                object += " " + tmp[i];
                            }
                            else if (firstChar.equals("\"")){
                                strLiteral = true;
                                object += tmp[i];
                            }
                            else {
                                object += tmp[i];
                            }
                        }
                    }
//                    System.out.println("subject : " + subject + ", predicate : " + predicate + ", object : " + object);
                    String cypher = sparqlToCypher(subject, predicate, object);
//                    System.out.println("cypher : " + cypher);
                    cypherMatchQueries.add(cypher);
//                    System.out.println("query : " + query);
                    sparqlQueries.add(query);
                }
                else{
                }
            }
        } catch (IOException e) {
            System.out.println("Error scan file, " + e + " encountered");
        }
    }

    public void printAllPrefixes(){
        for (Map.Entry m:prefixes.entrySet()){
            System.out.println("URI for prefix (" + m.getKey() + ") is : " + m.getValue());
        }
    }

    public void printAllReturnVariable(){
        System.out.println("SPARQL return variables : " + returnedVariable);
    }

    public void printAllSparqlQuery(){
        System.out.println("SPARQL sparqlQueries : " + sparqlQueries);
    }

    public String getReturnString(){
        String returnStr = "RETURN ";

        for(int i=0; i < returnedVariable.size(); i++){
            if(i == 0) {
                returnStr = returnStr + returnedVariable.get(i);
            }
            else {
                returnStr = returnStr + ", " + returnedVariable.get(i);
            }
        }

        return returnStr;
    }

    public String getMatchString(){
        String matchStr = "MATCH ";

        for(int i=0; i < sparqlQueries.size(); i++){
            String[] tmp = sparqlQueries.get(i).split("\\s+");

            if(isLiteralObject(tmp[2])){

            }
            else if(isURI(getFullURI(tmp[2]))){

            }
            else{
            }
        }

        return matchStr;
    }

    private String whereQueryString() {
        String str = "";
        for(int i = 0; i < cypherWhereQueries.size(); i++){
            if (i == 0){
                str += cypherWhereQueries.get(i);
            }
            else {
                str += ", " + cypherWhereQueries.get(i);
            }
        }

        return str;
    }

    public String matchQueryString(){
        String str = "";
        for(int i = 0; i < cypherMatchQueries.size(); i++){
            if (i == 0){
                str += cypherMatchQueries.get(i);
            }
            else {
                str += ", " + cypherMatchQueries.get(i);
            }
        }

        return str;
    }

    public String returnQueryString(){
        String str = "";
        for (int i = 0; i < returnedVariable.size(); i++){
            if (i == 0){
                str += returnedVariable.get(i);
            }
            else {
                str += ", " + returnedVariable.get(i);
            }
        }

        return str;
    }

    public String getCypherQuery(){
        String query = "MATCH " + matchQueryString();
        if (cypherWhereQueries.size() > 0){
            query += " WHERE " + whereQueryString();
        }
        query += " RETURN " + returnQueryString();

        return query;
    }




    public String getFullURI(String str){
        String[] objects = str.split(":");
        if(objects.length > 1){
            String fullURI = prefixes.get(objects[0]+":") + objects[1];
            return fullURI;
//            if(isURI(fullURI)) {
//                return fullURI;
//            }
//            else
//                return str;
        }
        else {
            return str;
        }
    }

    public String sparqlToCypher(String subject, String predicate, String object){
        String query = "";
        boolean subjectIsVariable = isVariable(subject);
        boolean predicateIsVariable = isVariable(predicate);
        boolean objectIsVariable = isVariable(object);
        boolean isLiteralObject = isLiteralObject(object);

        if (objectIsVariable){
            if (returnedVariable.contains(object.substring(1,object.length()))){
                returnedVariable.set(returnedVariable.indexOf(object.substring(1,object.length())),
                        new String(object.substring(1,object.length()) + ".uri"));
            }
            if (subjectIsVariable){
                query = "(" + subject.substring(1, subject.length()) + ")-[:`" + getFullURI(predicate) + "`]-(" + object.substring(1,object.length()) + ")";
            }
            else {
                query = "(:Resource {uri : \"" + getFullURI(subject) + "\"})-[:`" + getFullURI(predicate) + "`]-(" + object.substring(1,object.length()) + ")";
            }
        }
        else if (predicateIsVariable){
            if (!isLiteralObject){
                if (returnedVariable.contains(predicate.substring(1,predicate.length()))){
                    returnedVariable.set(returnedVariable.indexOf(predicate.substring(1,predicate.length())),
                            new String("type("+ predicate.substring(1,predicate.length()) +")"));
                }
                query = "(:Resource {uri : \"" + getFullURI(subject) + "})-[" + predicate.substring(1,predicate.length()) + "]-(:Resource {uri : \"" + getFullURI(object) + "\"})";
            }
            else{

            }
        }
        else if (subjectIsVariable){
            if(returnedVariable.contains(subject.substring(1,subject.length()))){
                returnedVariable.set(returnedVariable.indexOf(subject.substring(1,subject.length())),
                        new String(subject.substring(1,subject.length()) + ".uri"));
            }
            if(isLiteralObject){
                int wildcard = object.indexOf('*');
                if(wildcard >= 0) {
                    query = "(" + subject.substring(1,subject.length()) + ":Resource)";
                    cypherWhereQueries.add(new String(subject.substring(1,subject.length())
                            + ".`" + getFullURI(predicate) + "` =~ " + object.substring(0, wildcard) + ".*\""));
//                    cypherWhereQueries =
                }
                else {
                    query = "(" + subject.substring(1,subject.length()) + ":Resource {`"+getFullURI(predicate)+"`: " + object + "})";
                }
            }
            else{
                query = "("+subject.substring(1,subject.length())+")-[:`"+getFullURI(predicate)+"`]-(:Resource {uri : \"" + getFullURI(object) +"\"})";
            }
        }
        return query;
    }









    public boolean prefixHandler(String str){
        if (str.equalsIgnoreCase(new String("prefix")))
            return true;
        else
            return false;
    }

    public boolean selectHandler(String str){
        if (str.equalsIgnoreCase(new String("select")))
            return true;
        else
            return false;
    }

    public boolean whereHandler(String str){
        if (str.equalsIgnoreCase(new String("where")) || str.equalsIgnoreCase("{"))
            return true;
        else
            return false;
    }


    public boolean isLiteralObject(String str){
        if(str.substring(0,1).equals("\"") && str.substring(str.length()-1,str.length()).equals("\""))
            return true;
        else
            return false;
    }

    public boolean isURI(String str){

        if(IRIResolver.checkIRI(str))
            return true;
        else
            return false;
    }

    public boolean isVariable(String str){
        if(str.substring(0,1).equals(new String("?")))
            return true;
        else
            return false;
    }

    public boolean isWildcard(String str){
        if(str.substring(0,1).equals("\"") && str.substring(str.length()-1,str.length()).equals("\""))
            return true;
        else
            return false;
    }
}
