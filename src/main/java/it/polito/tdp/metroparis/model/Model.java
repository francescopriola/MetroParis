package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	private Map<Integer, Fermata> fermateIdMap;
	MetroDAO dao = new MetroDAO();
	
	public List<Fermata> getFermate(){
		if(this.fermate == null) {
			this.fermate = dao.getAllFermate();
			
			fermateIdMap = new HashMap<Integer, Fermata>();
			for(Fermata f : this.fermate) {
				this.fermateIdMap.put(f.getIdFermata(), f);
			}
		}
		return this.fermate;
	}
	
	public List<Fermata> calcolaPercorso(Fermata partenza, Fermata arrivo){
		this.creaGrafo();
		Map<Fermata, Fermata> alberoInverso = visitaGrafo(partenza);
		
		Fermata corrente = arrivo;
		List<Fermata> percorso = new ArrayList<>();
		
		while(corrente != null) {
			percorso.add(0, corrente);
			corrente = alberoInverso.get(corrente);
		}
		
		return percorso;
	}
	
	public Map<Fermata, Fermata> visitaGrafo(Fermata partenza) {
		//GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);	//Ricerca in profondità
		
		Map<Fermata, Fermata> alberoInverso = new HashMap<>();
		alberoInverso.put(partenza, null);
		
		visita.addTraversalListener(new RegistraAlberoDiVisita(alberoInverso, grafo));
		
		while(visita.hasNext()) {
			Fermata f = visita.next();
//			System.out.println(f);
		}
		
		return alberoInverso;
		//Ricostruiamo il percorso a partire dall'albero inverso (pseudo-code)
//		List<Fermata> percorso = new ArrayList<>();
//		fermata = arrivo;
//		while(fermata != null) {
//			fermata = alberoInverso.get(fermata);
//			percorso.add(fermata);
//		}
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);

		Graphs.addAllVertices(this.grafo, this.getFermate());

		List<CoppiaID> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaID coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
		
		
//		System.out.println(this.grafo);
//		System.out.println("Vertici = " + this.grafo.vertexSet().size());
//		System.out.println("Archi = " + this.grafo.edgeSet().size());
	}

}
