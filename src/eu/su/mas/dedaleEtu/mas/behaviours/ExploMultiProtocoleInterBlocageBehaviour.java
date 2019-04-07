package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Random;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ExploreSoloAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


public class ExploMultiProtocoleInterBlocageBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567677731496787661L;

	public ExploMultiProtocoleInterBlocageBehaviour(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
		String myNewPosition;
		System.out.println();
		System.out.println("*****************Resolution Interblocage "+this.myAgent.getLocalName()+"****************************************************");
		System.out.println();
		
		
		// 1) envoie de message: "InterBlocage"+n where n est un nombre aleatoire
		String[] receivers = {"Explo1","Explo2"}; //list of localname
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
		int monNbAlea = n.nextInt(200);
		msg.setContent("InterBlocage:"+monNbAlea);
		// 1.3) envoyer
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		for (String receiver: receivers) {
			if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
				continue;                   
			System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
		}


		// 2) attendre 1000ms et retenu le max resu
		int tempAttente = 1000;
		long start = System.currentTimeMillis(); 
		int max = monNbAlea;
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
        	
        System.out.println(this.myAgent.getLocalName()+" test 1");
        	
        	if (!msg.getContent().contains("InterBlocage"))
        		continue;
        	
            System.out.println(this.myAgent.getLocalName()+" test 2");
        	
        	content = msg.getContent().split("\\:");
        	
            System.out.println(this.myAgent.getLocalName()+" test 3");
        	
        	if (content.length == 1)
        		continue;
        	try {
        		tonNbAlea = Integer.parseInt(content[1]);
        		if (tonNbAlea>max) {
        			max = tonNbAlea;
        			// 3.1) set message content	
        			
        	        System.out.println(this.myAgent.getLocalName()+" test 4");
        			
        			msg.setContent("InterBlocage:"+max);
        			// 3.2) envoyer
        			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
        			
        	        System.out.println(this.myAgent.getLocalName()+" test 5");
        			
        			for (String receiver: receivers) {
        				if (receiver.equals(this.getAgent().getLocalName()))   //afficher tous les messages envoyes
        					continue;                   
        				System.out.println(this.myAgent.getLocalName()+" sent to "+receiver+" ,content= "+msg.getContent());
        			}
        		}
        	}catch(Exception e) {};
        	
        }while(System.currentTimeMillis()- start < tempAttente);
 
        
        System.out.println(this.myAgent.getLocalName()+ "test  2 ok");
    
		// 5) si mon nombre n'est pas le max, alors je changer ma position aleatoirement
        if (max > monNbAlea) {
			System.out.println(this.myAgent.getLocalName()+" perd");
			String myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
			if (myPosition!=null){
				//1) List of observable from the agent's current position
				List<Couple<String,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
				//2) choisir par hazard une position autour de la posiiton courante
				Iterator<Couple<String, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
				while (iter.hasNext()) {
					((AbstractDedaleAgent)this.myAgent).moveTo(iter.next().getLeft());
					myNewPosition = ((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
					if (!myPosition.equals(myNewPosition)){
						System.out.println(this.myAgent.getLocalName()+ " move from "+ myPosition + " to "+myNewPosition);				
						break;
					}
				}
			}
        }else {
			System.out.println(this.myAgent.getLocalName()+"gagne");};

	}

}
