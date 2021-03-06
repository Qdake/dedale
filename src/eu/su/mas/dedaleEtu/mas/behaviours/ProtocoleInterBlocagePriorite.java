package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

//protocoleInterBlocage
public class ProtocoleInterBlocagePriorite extends OneShotBehaviour {

	private static final long serialVersionUID = 8567677777496787661L;
  
	private int next;
	private int tache = 0;
	private int seuil = 10;
	private int nbBlocages = 0;
	public ProtocoleInterBlocagePriorite(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		System.out.println();
		System.out.println("*****************Resolution Interblocage Priorite "+this.myAgent.getLocalName()+"****************************************************");
		System.out.println();
		

		// 1) envoie de message: "InterBlocage"+n where n est un nombre aleatoire
		String[] receivers = {"Collect1","Collect2"}; //list of localname
		// 1.1) Create the message
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.myAgent.getAID());
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))     
				continue;                   //pas de message pour lui meme
			msg.addReceiver(new AID(receiver, AID.ISLOCALNAME));  
		}
		// 1.2) set message content	
		Random n = new Random();
		int id = this.tache + (this.getAgent().getAID().hashCode() % 10000);
		if(nbBlocages < seuil) {
			msg.setContent("InterBlocage:"+id);
		}
		// 1.3) envoyer
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
				continue;                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}

		// 2) attendre 500ms et retenu le max resu
		int tempAttente = 500;
		long start = System.currentTimeMillis(); 
		int max = id;
		int tonNbAlea;
		String[] content;
      do{
      	msg = this.myAgent.receive();
      	if (msg == null) {
      		block(50);
      		continue;
      	}
      	
			System.out.println(this.myAgent.getLocalName()+"<----Result received from "+
				    msg.getSender().getLocalName()+" ,content= "+msg.getContent());
      	       	
      	if (!msg.getContent().contains("InterBlocage"))
      		continue;
      	
      	content = msg.getContent().split("\\:");
      	
      	if (content.length == 1)
      		continue;
      	try {
      		tonNbAlea = Integer.parseInt(content[1]);
      		if (tonNbAlea>max) {
      			max = tonNbAlea;
      			// 3.1) set message content				
      			msg.setContent("InterBlocage:"+max);
      			// 3.2) envoyer
      			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
      			for (String receiver: receivers) {
      				if (receiver.equals(this.getAgent().getLocalName()) || receiver.equals(msg.getSender().getLocalName()))   //afficher tous les messages envoyes
      					continue;                   
      				System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
      			}
      		}
      	}catch(Exception e) {};
      	
      }while(System.currentTimeMillis()- start < tempAttente);
  
		// 5) si mon nombre n'est pas le max, alors je changer ma position aleatoirement
      if (max > id) {
			System.out.println(this.myAgent.getLocalName()+" perd");
			String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			if (myPosition!=null){
				//1) List of observable from the agent's current position
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				//2) choisir par hazard une position autour de la posiiton courante
				Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
				System.out.println(iter.next() + " " + myAgent.getLocalName()); //Permet de supprimer la position actuelle de la liste
				boolean deplacement = false;
				while (iter.hasNext() && !deplacement) {
					deplacement = deplacement ||((AbstractDedaleAgent)this.myAgent).moveTo(iter.next().getLeft());
				}
				if(deplacement) {
					next = 0;
					System.out.println(this.myAgent.getLocalName()+ " move from "+ myPosition + " to "+((AbstractDedaleAgent)this.myAgent).getCurrentPosition());
				}
				else {
					next = 1;
				}
			}
			block(1000); // laisser passer
      }else {
			System.out.println(this.myAgent.getLocalName()+"gagne");
			next = 1;
			};		
	}
  public int onEnd() {
  	System.out.println("protocole end ," +this.myAgent.getLocalName()+" return "+next);
  	return next;
  }
}
//endProtocoleInterBlocage