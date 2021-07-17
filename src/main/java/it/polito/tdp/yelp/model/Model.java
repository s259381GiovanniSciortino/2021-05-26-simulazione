package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	Graph<Business,DefaultWeightedEdge> grafo;
	Map<String, Business> businessIdMap;
	
	public String doCreaGrafo(int anno, String city) {
		YelpDao dao = new YelpDao();
		businessIdMap = new HashMap<>();
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		for(Business b: dao.getAllBusiness()) {
			businessIdMap.put(b.getBusinessId(), b);
		}
		List<String> vertexId = new ArrayList<>(dao.getVertex(anno, city));
		for(String id:vertexId)
			grafo.addVertex(businessIdMap.get(id));
		List<EdgeAndWeight> eaw = new ArrayList<>(dao.getEdgeAndWeight(anno, city));
		for(EdgeAndWeight e: eaw)
			Graphs.addEdge(grafo, businessIdMap.get(e.getBusinessId1()), businessIdMap.get(e.getBusinessId2()), e.getPeso());
		String result = "";
		if(this.grafo==null) {
			result ="Grafo non creato";
			return result;
		}
		result = "Grafo creato con :\n# "+this.grafo.vertexSet().size()+" VERTICI\n# "+this.grafo.edgeSet().size()+" ARCHI\n";
		return result;
	}
	
	public String doLocaleMigliore() {
		Business business=null;
		double migliore = 0.0;
		for(Business b: grafo.vertexSet()) {
			double entranti = 0.0;
			double uscenti = 0.0;
			for(Business b1: Graphs.predecessorListOf(grafo, b))
				entranti+=grafo.getEdgeWeight(grafo.getEdge(b1, b));
			for(Business b2: Graphs.successorListOf(grafo, b))
				uscenti+=grafo.getEdgeWeight(grafo.getEdge(b, b2));
			double diff = entranti-uscenti;
			if(diff>migliore) {
				migliore = diff;
				business=b;
			}
		}
		return business.toString();
	}
	
	public List<String> getAllCity(){
		YelpDao dao = new YelpDao();
		return dao.getAllCity();
	} 
	
}
