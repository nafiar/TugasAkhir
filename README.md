# __Catatan TA__

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
* 

### Yang harus disiapin :
* YAGO dataset : 
	* https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/yago/downloads/
	* https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/yago-naga/yago/archive/
* DBLP dataset : 
* install RDF-3x : https://github.com/gh-rdf3x/gh-rdf3x
* install SW-Store : -

### Langkah pengerjaan :
#### Bangun Struktur Data (Indexing)
* Siapin arsitektur
	* Metode penyimpanan data
	* Metode penulisan data pada file
	* Metode pembacaan data dari file
* Metode Indexing 
	* VS-Tree (vertex dalam bentuk adjacency list di hash)

#### Membuat Algoritma Query

### Metode hashing :
BDKR
AP


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
vS ig(v) = eSig(e1)|......|eS ig(en) 
```
where eS ig(e i ) is the edge signature for edge e i adjacent to v and “|” is the bitwise OR operation.

#### 4.3. Given an RDF graph G, its corresponding data signature graph G* is induced by all entity and class vertices in G together with the edges whose endpoints are either entity or class vertices. Each vertex v in G ∗ has its corresponding vertex signature vS ig(v) (defined in Definition 4.2) as its label. Given an edge v1v2 in G ∗ , its edge label is also a signature, denoted as Sig(v1 v2), to denote the property between v 1 and v 2 .

#### 4.4 Consider a data signature graph G* and a query signature graph Q* that has n vertices {v 1 , ..., v n }. A set of n distinct vertices {u1 , ..., un } in G* is said to be a match of Q* , if and only if the following conditions hold:
	(1) vSig(vi )&vSig(ui ) = vSig(vi ), i = 1, ..., n, where ‘&’ is the bitwise AND operator.
