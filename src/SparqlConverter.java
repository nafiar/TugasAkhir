import org.apache.jena.base.Sys;
import  org.apache.jena.riot.system.IRIResolver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SparqlConverter {

    public String filePath;
    public ArrayList<String> returnedVariable;
    public ArrayList<String> sparqlQueries;
    public ArrayList<List<String>> tripleStatement;
    public ArrayList<List<Integer>> tripleStatementVariable;
    public ArrayList<String> cypherWhereQueries;
    public ArrayList<String> cypherMatchQueries;
    public HashMap<String, String> prefixes;
    public Integer indexOfTriple;
    public Integer indexOfCypherQuery;

    public SparqlConverter(String path){
        filePath = path;
        returnedVariable = new ArrayList<String>();
        sparqlQueries = new ArrayList<String>();
        tripleStatement = new ArrayList<List<String>>();
        tripleStatementVariable = new ArrayList<List<Integer>>();
        cypherMatchQueries = new ArrayList<String>();
        cypherWhereQueries = new ArrayList<String>();
        prefixes = new HashMap<String, String>();
        indexOfTriple = 0;
        indexOfCypherQuery = 0;
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
                    if (tmp[1].equalsIgnoreCase("FILTER")) {
                        filterHandler(str);
                    } else {
                        for (int i = 1; i < tmp.length - 1; i++) {
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
                        indexOfTriple++;
                        indexOfCypherQuery++;
                    }
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
        query += "\nRETURN " + returnQueryString();

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

    public String getResourceUri(String str){
        String resource = str.substring(1, str.length()-1);

        return resource;
    }

    public String sparqlToCypher(String subject, String predicate, String object){
        List<String> triple = new ArrayList<String>();
        List<Integer> tripleVariable = new ArrayList<Integer>();
        triple.add(subject);
        triple.add(predicate);
        triple.add(object);
        tripleStatement.add(triple);
        String query = "";
        boolean subjectIsVariable = isVariable(subject);
        if (subjectIsVariable) tripleVariable.add(1);
        else tripleVariable.add(0);
        boolean predicateIsVariable = isVariable(predicate);
        if (predicateIsVariable) tripleVariable.add(1);
        else tripleVariable.add(0);
        boolean objectIsVariable = isVariable(object);
        if (objectIsVariable) tripleVariable.add(1);
        else tripleVariable.add(0);
        boolean isLiteralObject = isLiteralObject(object);
        tripleStatementVariable.add(tripleVariable);

        if (objectIsVariable){
            if (returnedVariable.contains(object.substring(1,object.length()))){
                returnedVariable.set(returnedVariable.indexOf(object.substring(1,object.length())),
                        new String(object.substring(1,object.length()) + ".uri as " + object.substring(1,object.length())));
            }
            if (subjectIsVariable){
                String predicateString;
                if (isResourceUri(predicate))
                    predicateString = getResourceUri(predicate);
                else
                    predicateString = getFullURI(predicate);
                query = "(" + subject.substring(1, subject.length()) + ")-[:`" + predicateString + "`]-(" + object.substring(1,object.length()) + ")";

                if (returnedVariable.contains(subject.substring(1,subject.length()))){
                    returnedVariable.set(returnedVariable.indexOf(subject.substring(1,subject.length())),
                            new String(subject.substring(1,subject.length()) + ".uri as " + subject.substring(1,subject.length())));
                }
            }
            else {
                String subjectString;
                String predicateString;
                if (isResourceUri(subject))
                    subjectString = getResourceUri(subject);
                else
                    subjectString = getFullURI(subject);

                if (isResourceUri(predicate))
                    predicateString = getResourceUri(predicate);
                else
                    predicateString = getFullURI(predicate);
                query = "(s:Resource { uri : \"" + subjectString + "\"})\n";
                query += "WHERE s.`"+ predicateString + "` is not null\n";
                query += "RETURN s.`"+ predicateString + "` as "+ object.substring(1,object.length()) +"\n";
                query += "UNION ALL MATCH (:Resource {uri : \"" + subjectString + "\"})-[:`" + predicateString + "`]-(" + object.substring(1,object.length()) + ")";
            }
        }
        else if (predicateIsVariable){
            if (!isLiteralObject){
                String subjectString;
                String objectString;
                if (isResourceUri(object))
                    objectString = getResourceUri(object);
                else
                    objectString = getResourceUri(object);

                if (subjectIsVariable){
                    query = "("+subject.substring(1,subject.length())+":Resource)-["+predicate.substring(1,predicate.length())+"]-(:Resource {uri : \""+ objectString +"\"})\n";
                    query += "WITH " + subject.substring(1,subject.length()) + ".uri as " + subject.substring(1,subject.length()) + ", type("+predicate.substring(1,predicate.length())+") as "+ predicate.substring(1,predicate.length());
                    if (returnedVariable.contains(predicate.substring(1,predicate.length()))){
                        returnedVariable.set(returnedVariable.indexOf(predicate.substring(1,predicate.length())),
                                new String(predicate.substring(1,predicate.length())));
                    }
                    if (returnedVariable.contains(subject.substring(1,subject.length()))){
                        returnedVariable.set(returnedVariable.indexOf(subject.substring(1,subject.length())),
                                new String(subject.substring(1,subject.length())));
                    }
                }
                else {
                    if (isResourceUri(subject))
                        subjectString = getResourceUri(subject);
                    else
                        subjectString = getFullURI(subject);

                    query = "(:Resource {uri : \"" + subjectString + "\"})-[" + predicate.substring(1,predicate.length()) + "]-(:Resource {uri : \"" + objectString + "\"})";
                    if (returnedVariable.contains(predicate.substring(1,predicate.length()))){
                        returnedVariable.set(returnedVariable.indexOf(predicate.substring(1,predicate.length())),
                                new String("type("+ predicate.substring(1,predicate.length()) +")"));
                    }
                }
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
                    String predicateString;
                    if (isResourceUri(predicate))
                        predicateString = getResourceUri(predicate);
                    else
                        predicateString = getFullURI(predicate);
                    query = "(" + subject.substring(1,subject.length()) + ":Resource)";
                    query += "WHERE s.`"+ predicateString + "` =~ " + object.substring(0, wildcard) + ".*\"";
//                    cypherWhereQueries.add(new String(subject.substring(1,subject.length())
//                            + ".`" + getFullURI(predicate) + "` =~ " + object.substring(0, wildcard) + ".*\""));
                }
                else {
                    query = "(" + subject.substring(1,subject.length()) + ":Resource {`"+getFullURI(predicate)+"`: " + object + "})";
                }
            }
            else{
                if (isRdfType(predicate)) {
                    String type;
                    if (isResourceUri(object))
                        type = getResourceUri(object);
                    else
                        type = getFullURI(object);
                    query = "("+subject.substring(1,subject.length())+":`"+type+"`)";
                    if(returnedVariable.contains(subject.substring(1,subject.length()))){
                        returnedVariable.set(returnedVariable.indexOf(subject.substring(1,subject.length())),
                                new String(subject.substring(1,subject.length()) + ".uri"));
                    }
                }
                else {
                    query = "("+subject.substring(1,subject.length())+")-[:`"+getFullURI(predicate)+"`]-(:Resource {uri : \"" + getResourceUri(object) +"\"})";
                }
            }
        }
        return query;
    }








    public void filterHandler(String str){
        String[] filterString ;
        String tmp = "";
        boolean regexFilter = false;
        String variable = "";
        String regexString = "";
        for (int i =0; i < str.length(); i++) {
            if (str.charAt(i) != ' ') {
                if (regexFilter && delimiter(str.charAt(i))) {
                    tmp = "";
                } else {
                    tmp += str.charAt(i);
                }
            } else {
                tmp = "";
                continue;
            }
            if (tmp.equalsIgnoreCase("regex")) {
                regexFilter = true;
            }
            if (!tmp.isEmpty() && tmp.charAt(0) == '?') {
                variable = tmp;
            }
            if (tmp.length() > 1 && tmp.substring(0,1).equals("\"") && tmp.substring(tmp.length()-1,tmp.length()).equals("\"")){
                regexString = tmp;
            }
        }

        if (regexFilter) {
            addRegex(variable, regexString);
        }
    }

    public void addRegex(String variable, String regex) {
        List<String> prevStatement = tripleStatement.get(indexOfTriple-1);
        List<Integer> prevStatementVariable = tripleStatementVariable.get(indexOfTriple-1);
        String subject = prevStatement.get(0);
        String predicate = prevStatement.get(1);
        String object = prevStatement.get(2);
        String query = "";
        if (prevStatement.get(2).equals(variable) && prevStatementVariable.get(0) == 1){
            query = "(" + subject.substring(1, subject.length()) + ")\n";
        } else {
            String subjectString = "";
            if (isResourceUri(subject))
                subjectString = getResourceUri(subject);
            else
                subjectString = getFullURI(subject);

            query = "(s:Resource {uri : \"" + subjectString + "\"})\n";
        }

        String predicateString;
        if (isResourceUri(predicate))
            predicateString = getResourceUri(predicate);
        else
            predicateString = getFullURI(predicate);

        if (regex.charAt(1) != '^' && regex.charAt(regex.length()-2) != '^') {
            if (prevStatementVariable.get(0) == 1) {
                query += "WHERE " + subject.substring(1, subject.length()) + ".`"+ predicateString + "` =~ " + regex ;
            } else {
                query += "WHERE s.`"+ predicateString + "` =~ " + regex ;
            }
        } else {
            String condition = "";
            if (regex.charAt(1) == '^' && regex.charAt(regex.length()-2) != '^') {
                condition = "\"" + regex.substring(2,regex.length()-1) + ".*\"";
            } else if (regex.charAt(regex.length()-2) == '^' && regex.charAt(1) != '^') {
                condition = "\"*." + regex.substring(1,regex.length()-2) + "\"";
            } else {
                condition = "\"*." + regex.substring(2,regex.length()-2) + ".*\"";
            }

            if (prevStatementVariable.get(0) == 1) {
                query += "WHERE " + subject.substring(1, subject.length()) + ".`"+ predicateString + "` =~ " +  condition;
            } else {
                query += "WHERE s.`"+ predicateString + "` =~ " +  condition;
            }
        }

        cypherMatchQueries.set(indexOfTriple-1, query);
    }

    public boolean delimiter(char c) {
        if (c == ',') return true;
        if (c == '(') return true;
        if (c == ')') return true;
        if (c == '.') return true;
        else return false;
    }

    public boolean whiteSpace(char c) {
        return true;
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

    public boolean isRdfType(String str) {
        if(str.equals("rdf:type"))
            return true;
        else
            return false;
    }

    public boolean isResourceUri(String str) {
        if(str.substring(0,1).equals("<") && str.substring(str.length()-1, str.length()).equals(">"))
            return true;
        else
            return false;
    }
}
