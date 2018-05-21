// SPARQL CONDITIONS :
//      s   p   o   .

// s = variable, s on select
// 1. p = <rdf:type>
//```
//  select ?s where {
//      ?s <rdf:type> <resource:o> .
//  }
//````
match (s:`resource:o`)
with s.uri as s
return s



match (:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})-[:`http://data.lirmm.fr/ontologies/food#containsIngredient`]-(o) return o.uri as o
union
match (s:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
return s.`http://data.lirmm.fr/ontologies/food#containsIngredient` as o

`http://data.lirmm.fr/ontologies/food#energyPer100g`

match (:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})-[:`http://data.lirmm.fr/ontologies/food#energyPer100g`]-(o) return o.uri as o
union
match (s:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
return s.`http://data.lirmm.fr/ontologies/food#energyPer100g` as o

// pakai unwind

match (:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})-[:`http://data.lirmm.fr/ontologies/food#containsIngredient`]-(o)
with o.uri as oResult1
match (s:Resource {uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
with s.`http://data.lirmm.fr/ontologies/food#containsIngredient` as oResult2
unwind (oResult1 + oResult2) as o
return o

match (s:Resource { uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
with keys(s) as keys
unwind keys as key
union match (s:Resource { uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
where s.key
return key

match (s:Resource { uri : "http://world-fr.openfoodfacts.org/produit/0011110785787/almondmilk-the-kroger-co"})
with properties(s) as properties
return properties






