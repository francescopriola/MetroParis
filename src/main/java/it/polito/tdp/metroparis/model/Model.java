package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo;
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		MetroDAO dao = new MetroDAO();
		List<Fermata> fermate = dao.getAllFermate();
		Map<Integer, Fermata> fermateIdMap= new HashMap<Integer, Fermata>();
		for(Fermata f : fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		
		Graphs.addAllVertices(grafo, fermate);
//		METODO 1: ITERO SU OGNI COPPIA DI VERTICI (NON E' SEMPRE Iò PIU' LENTO)
		
//		for(Fermata partenza : fermate) {
//			for(Fermata arrivo : fermate) {
//				if(dao.isFermateConnesse(partenza, arrivo)) {	//se esiste almeno una connessione tre partenza e arrivo
//					this.grafo.addEdge(partenza, arrivo);
//				}
//			}
//		}
		
//		METODO 2: DATO CIASCUN VERTICE, TROVA I VERTICI AD ESSO ADIACENTI
//		Variante 2a: il DAO restituisce un elenco di ID numerici

//		for(Fermata partenza : fermate) {
//			List<Integer> idConnesse = dao.getIdFernateConnesse(partenza);
//			for(Integer id : idConnesse) {
//				//Fermata arrivo = ;//fermata che possiede questo id
//				Fermata arrivo = null;
//				for(Fermata f : fermate) {
//					if(f.getIdFermata() == id) {
//						arrivo = f;
//						break;
//					}
//				}
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
	
//		METODO 2: DATO CIASCUN VERTICE, TROVA I VERTICI AD ESSO ADIACENTI
//		Variante 2b: il DAO restituisce un elenco di oggetti Fermata
		
//		for(Fermata partenza : fermate) {
//			List<Fermata> arrivi = dao.getFermateConnesse(partenza);
//			for(Fermata arrivo : arrivi) {
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
		
//		METODO 2: DATO CIASCUN VERTICE, TROVA I VERTICI AD ESSO ADIACENTI
//		Variante 2c: il DAO restituisce un elenco di ID numerici che converto in oggetti tramite una Map<Integer, Fermata< - "Identity Map"
		
//		for(Fermata partenza : fermate) {
//			List<Integer> idConnesse = dao.getIdFernateConnesse(partenza);
//			for(int id : idConnesse) {
//				Fermata arrivo = fermateIdMap.get(id);
//				this.grafo.addEdge(partenza, arrivo);
//			}
//		}
		
//		METODO 3: FACCIO UNA SOLA QUERY CHE MI RESTITUISCA LE COPPIE DI FERMATE DA COLLEGARE 
//		Variante preferita 3c : usare Identity Map
		List<CoppiaID> fermateDaCollegare = dao.getAllFermateConnesse();
		for(CoppiaID coppia : fermateDaCollegare) {
			this.grafo.addEdge(fermateIdMap.get(coppia.getIdPartenza()), fermateIdMap.get(coppia.getIdArrivo()));
		}
		
		
		System.out.println(this.grafo);
		System.out.println("Vertici = " + this.grafo.vertexSet().size());
		System.out.println("Archi = " + this.grafo.edgeSet().size());
		
		visitaGrafo(fermate.get(0));
	}
	
	public void visitaGrafo(Fermata partenza) {
		GraphIterator<Fermata, DefaultEdge> visita = new BreadthFirstIterator<>(this.grafo, partenza);
		//GraphIterator<Fermata, DefaultEdge> visita = new DepthFirstIterator<>(this.grafo, partenza);	//Ricerca in profondità
		while(visita.hasNext()) {
			Fermata f = visita.next();
			System.out.println(f);
		}
	}

}
