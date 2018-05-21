# __Catatan TA__

### TODO : 
* __(DONE) Bisa query wildcard__
* tambah query yang lebih kompleks
* testing pakai dataset yang besar -> setting GC biar kuat 
* __(DONE)__ cari case2 yang masih belom bisa dilakuin, upgrade
	* query rule no. 3 : mustahil T_T
* __(DONE)__ tambahin apache jena buat query langsung ke jena-fuseki dari sparql yang udah ada 
* __(DONE)__ hitung running time waktu query neo4j sama query apache jena 
* __(DONE)__ hitung memory usage waktu query neo4j sama query apache jena

#### Tambah ke analisa :
* metode convert sparql jadi cypher (baca __pattern__)
* ga bisa convert beberapa jenis query (sumber : https://github.com/neo4j/neo4j/issues/1696)

#### hapus semua data neo4j
```
sudo service neo4j stop
sudo rm -rf /var/lib/nro4j/
```

#### import data food fact di neo4j
```
CALL semantics.importRDF( "file:///home/nafiar/Documents/TA/20fr.openfoodfacts.org.products.rdf" , "RDF/XML" ,  { shortenUrls: false, typesToLabels: true, commitSize: 25000 })
```

#### import data ahmad yani
```
call semantics.importRDF( "file:///home/nafiar/IdeaProjects/TugasAkhir/resource/Ahmad_Yani.rdf" , "RDF/XML" ,  { shortenUrls: false})
```

### waktu import data
* neo4j : pakai plugin neosemantics __158.745 s__
* jena-fuseki : __96.03 s__

### query langsung 
* neo4j : Started streaming 12 records after __8 ms__ and completed after 1020 ms.
* jena-fuseki : __17 ms __

## Program
#### SPARQL query 
```
PREFIX food: <http://data.lirmm.fr/ontologies/food#>

select ?s where {
	?s food:name "Almondmilk" .
}
```
#### neo4j + neo4j driver
* running time : __7 ms__
* memory usage : __638 Mb__
#### apache-jena-fuseki + apache-jena
* running time : __469 ms__
* memory usage : __27581 Mb__

### Rule berdasarkan variable


#### SPARQL 1
```
select ?c where {
	<Resource:a> <Relationship:b> ?c .
}
```
#### cypher :
* match (a:Resource {Uri : "<Resource:a>"})-[:<Relationship:b>]-(c) 
* wher a.<Relationship:b> as result
* return c.uri as result
* match (a:Resource {Uri: "<Resource:a>"}) return a.`<Relationship:b>`

#### SPARQL 2
```
select ?b where {
	<Resource:a> ?b "String" .
}
```
#### cypher :
* mencari property dari sebuah node yang valuenya "String"

#### SPARQL 3
```
select ?b where {
	<Resource:a> ?b <Resource:c> .
}
```
#### cypher :
* match (:Resource{uri:"<Resource:a>"})-[b]-(:Resource{uri:"<Resource:c>"}) return type(b)

#### SPARQL 4
```
select ?a where {
	?a <Relationship:b> <Resource:c> .
}
```
#### cypher :
* match (a)-[:<Relationship:b>]-(:Resource{uri: "<Resource:c>"}) return a.uri

#### SPARQL 5
```
select ?a where {
	?a <Relationship:b> "String" .
}
```
#### 
* match (a:Resource {<Relationship:b> : "String"}) return a.uri


### Rule 
1. Subjects of triples are mapped to nodes  in Neo4j. A node in Neo4j representing an RDF resource will be labeled :Resource and have a property uri with the resource’s URI.
```
(S,P,O) => (:Resource {uri:S})... 
```
2. a.Predicates of triples are mapped to node properties in Neo4j if the object of the triple is a literal
```
(S,P,O) && isLiteral(O) => (:Resource {uri:S, P:O})
```
2. b.Predicates of triples are mapped to relationships in Neo4j if the object of the triple is a 
resource
```
(S,P,O) && !isLiteral(O) => (:Resource {uri:S})-[:P]->(:Resource {uri:O})
```
3. The rdf:type statements are mapped to categories in Neo4j.
```
(Something ,rdf:type, Category) => (:Category {uri:Something})
```



### Problem Statement :
* data semantic web semakin lama semakin besar, atomatis cost untuk melakukan proses pengolahan data akan semakin besar juga
* sparql query yang ada di system graph database memiliki kelemahan yaitu proses query biasanya matching secara exactly matching, masalahnya ada pada query data literal dimana tipe datanya beragam, bisa berupa string date dll   




### Rumumsan masalah
* pengaruh information retrieval dalam performa sparql query
* bagaimana information retrieval dapat mempengaruhi peningkatan performa sparql query
* 




Graph database :
- not require complex join to retrieve connected/ related data. it use a natural concept of relationship
- 









istilah2 yang ada di paper :
* &		= bitwise AND operator
* | |
* G		= a RDF graph
* G*	= data signature graph
* Q		= a graph query
* Q* 	= query signature graph
* T 		= adjency list table
* u		= data signature graph in G*
* v		= query signature graph in Q*
* CL	= candidate Q* match over G*
* RS 	= the matches of Q over G
* Vc 	= collection of class vertices
* Ve	= collection of entity class
* Vl		= collection of literal
* Vp	= collection of parameter vertice
* Vw	= collection of wildcard vertices
* Lv		= collection of vertice labels
* vID	= vertex ID
* vLabel	= corresponding URI
* eLabel 	= v’s outgoing edge label that corresponds to some property
* nLabel	= v’s neighbor vertex label
* adjList	= list of its out going edges and the corresponding neighbor vertices
* v ∈ Vl 		= its vertex label is its literal value
* v ∈ Vc ∪ Ve	= its vertex label is its corresponding URI
* E = (v1, v2)	= is a collection of directed edges that connect the corresponding subjects and objects.
* Le	= is a collection of edges labels.
* e ∈ E		= its edge label is its corresponding property
* | 		= bitwise OR
* & 		= bitwise AND
* vSig(v)	= denotes signature of vLabel
* eSig(e).e	= denotes signature of eLabel
* eSig(e).n 	= denotes signature of nLabel
* |eSig(e).e|	= 

### Yang harus disiapin :
* YAGO dataset : 
	* https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/yago/downloads/
	* https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/yago/archive/
* DBLP dataset : http://dblp.uni-trier.de/faq/How+can+I+download+the+whole+dblp+dataset
* install RDF-3x : https://github.com/gh-rdf3x/gh-rdf3x
* install SW-Store : -

### Langkah pengerjaan :
#### Bangun Struktur Data (Indexing)
* Siapin arsitektur
	* Metode penyimpanan data : Key-Value
	* Metode penulisan data pada file
	* Metode pembacaan data dari file
	* Metode hash data (Encoding)
		* BDKR
		* AP

* Metode Indexing 
	* Balanced Binary Tree
	* VS-Tree (vertex dalam bentuk adjacency list di hash)

#### Membuat Algoritma Query
* algoritma untuk menjawab filtering yang sudah dibuat
* algoritma prunning menggunakan queue

#### Pengujian
* Menentukan query yang akan digunakan
* Instalasi RDF-3x dan SW-Store
* Menjalankan dan mencatat waktu query pada sistem yang sudah dibuat dan pembanding (RDF-3x, SW-Store)
* 


### __Definition__ : 

#### 2.1 A RDF graph is denoted as G = {V, Lv , E, Le}, where 
	(1) V = Vc ∪ Ve ∪ Vl is a collection of vertices that correspond to all subjects and objects in RDF data 
	(2) Lv is a collection of vertex labels. Given a vertex v ∈ Vl , its vertex label is its literal value. Given a vertex v ∈ Vc ∪Ve , its vertex label is its corresponding URI
	(3) E = (v 1 , v 2 ) is a collection of directed edges that connect the corresponding subjects and objects
	(4) L E is a collection of edge labels. Given an edge e ∈ E, its edge label is its corresponding property.

#### 2.2 A query graph is denoted as Q = {V, Lv , E, Le}, where
	(1) V = Vc ∪ Ve ∪ Vl ∪ Vp ∪ Vw is collection of vertices that correspond to all subjects and objects in a SPARQL query, where Vp and Vw are collections of parameter vertices and wildcard vertices, respectively, and Vc and Ve and Vl are defined in Definition 2.1.
	(2) Lv is a collection of vertex labels. For a vertex v ∈ V p , its vertex label is φ. The vertex label of a vertex v ∈ V w is the substring without the wildcard. A vertex v ∈ V c ∪ V e ∪ V l is defined in Definition 2.1.
	(3) E and Le are defined in Definition 2.1.

#### 2.3 Consider an RDF graph G and a query graph Q that has _n_ vertices {v1 , ..., vn }. A set of _n_ distinct vertices {u1 , ..., un } in G is said to be a match of Q. if and only if the following conditions hold:
	(1) If vi is a literal vertex, vi and ui have the same literal value.
	(2) If vi is an entity or class vertex, vi and ui have the same URI.
	(3) If vi is a parameter vertex, there is no constraint over ui.
	(4) If vi is a wildcard vertex, vi is a substring of ui and ui is a literal value.
	(5) If there is an edge from vi to vj in Q with the property p, there is also an edge from ui to uj in G with the same property p.


#### 2.4 (Problem Definition) Given a query graph Q over an RDF graph G, find all matches of Q over G according to Definition 2.3.

#### 4.1 Given an adjacent edge e(eLabel, nLabel), the edge signature of e is a bitstring, denoted as eSig(e), which has two parts: eSig(e).e, eSig(e).n. The first part eSig(e).e (M bits) denotes the edge label (i.e. eLabel) and the second part eSig(e).n (N bits) denotes the neighbor vertex label (i.e. nLabel).

#### 4.2 Given a class or entity vertex v in the RDF graph, the vertex signature vSig(v) is formed by performing bitwise OR operations over all its adjacent edge signatures. 
Formally, vSig(v) is defined as follows: 
``` 
vSig(v) = eSig(e1)|......|eSig(en) 
```
where eSig(ei) is the edge signature for edge ei adjacent to v and “|” is the bitwise OR operation.

#### 4.3. Given an RDF graph G, its corresponding data signature graph G* is induced by all entity and class vertices in G together with the edges whose endpoints are either entity or class vertices. Each vertex v in G ∗ has its corresponding vertex signature vS ig(v) (defined in Definition 4.2) as its label. Given an edge v1v2 in G ∗ , its edge label is also a signature, denoted as Sig(v1 v2), to denote the property between v 1 and v 2 .

#### 4.4 Consider a data signature graph G* and a query signature graph Q* that has n vertices {v 1 , ..., v n }. A set of n distinct vertices {u1 , ..., un } in G* is said to be a match of Q* , if and only if the following conditions hold:
	(1) vSig(vi )&vSig(ui ) = vSig(vi ), i = 1, ..., n, where ‘&’ is the bitwise AND operator.
	(2) If there is an edge from vi to vj in Q* , there is also an edge from ui to uj in G* .

#### 5.1 Consider a query signature graph Q* with n vertices v i (i=1,...,n) and a summary graph G I in the I-th level of VS-tree. A set of nodes {d i I } (i = 1, ..., n) at G I is called a summary match of Q ∗ over G I , if and only if the following conditions hold:
	(1) vS ig(v i )&d i I .S ig = vS ig(v i ), i = 1, ..., n
	(2) For any edge v1-->v2 in Q , there must exist a super edge dI1-->dI2 in GI and Sig(v1-->v2)&Sig(d1I-->d2I) = Sig(v1-->v2)

#### 5.2 Child State. Given a query signature graph Q* with n vertices v i (i = 1, ..., n), n nodes {d1I , ..., dnI} in VS-tree forms a summary match of Q* , n nodes {d1I' , ..., dnI'} is a child state of {d1I' , ..., dnI'}, if and only if diI is a child node of diI , i = 1, .., n. Furthermore, if {d1I' , ..., dnI'} is also a summary match of Q*, {d1I' , ..., dnI'} is called a valid child state of {d1I , ..., dnI}.


### Struktur Aplikasi

#### Metode Penyimpanan Data
* Disk-base
	* Key-Value
	* format : [vID, vLabel, adjList]
* Encoding technique
	* vLabel -> 
	* eLabel ->  
	* nLabel :
		* represent of 3-gram bit
		*  

#### Metode Indexing
* B-Tree of Hash value
* Height Balancing Tree ->  

#### Metode Traverse 
* queue 


### Catatan Penting bab 4
* bab 4 jelasin metode penyimpanan data sama encoding
* dari sana metode penyimpanan datanya disk-base (file) pakai metode key-value (ada yang nyimpen signature ada yang nyimpen raw stringnya) dibagi menjadi 3 bagian 
	* entity (class, entity, class and entity) 
	* literal (string value)
	* predicate
* metode encoding mendapatkan vSig(v), eSig(e), eSig(e) -> edges and vertex coresponding to vLabel. eSig(e).e into 12 long bitstring, eSig(e).n -> into 16 long bitstring
* parameter M untuk eLabel, N untuk nLabel -> metode has H 
*  

### Catatan Penting bab 5
* VS-Tree is classical height balanced tree
* sumary matches
* Traverse nya pakai bfs


### Progres minggu depan
* observasi aplikasi gStore nya dulu 