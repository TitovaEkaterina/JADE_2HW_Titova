/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jade_2_titova;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Random;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

/**
 *
 * @author titova_ekaterina
 */
public class MyAgent extends Agent {

    private double N;
    private String[] EdgesId;
    private Random random = new Random();
    private double ratio = 1;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            ratio = Double.parseDouble((String) args[0]);
            N = Integer.parseInt((String) args[1]);
            EdgesId = new String[args.length - 2];
            for (int i = 2; i < args.length; ++i) {
                EdgesId[i - 2] = (String) args[i];
            }
        } else {
            System.out.println("No number");
            doDelete();
        }
        addBehaviour(new SimpleBehaviour(this) {

            public void action() {
                if(getLocalName().compareTo("5") == 0){
                    ACLMessage startMsg = new ACLMessage(ACLMessage.INFORM);
                    startMsg.setOntology("Send_N");
                    startMsg.setContent((N + random.nextGaussian() * 0.01) + "");
                    for (int i = 0; i < EdgesId.length; i++) {
                        startMsg.addReceiver(new AID(EdgesId[i] + "@10.42.0.1:1099/JADE"));
                    }
                    send(startMsg);
                }
                MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                MessageTemplate m2 = MessageTemplate.MatchOntology("Send_N");
                MessageTemplate m3 = MessageTemplate.and(m1, m2);
                ACLMessage msg = blockingReceive(m3, 1200);
                if (msg != null) {
                    String numberOfMes = msg.getContent();
                    System.out.println("Send_N from " + msg.getSender().getLocalName() + " to " + getLocalName() + " = " + numberOfMes);
                    double grad = (Double.parseDouble(numberOfMes) - N);
                    N = N + (0.01) * grad;
                    if (Math.log(Math.random()) > Math.log(1.0 - ratio)) {
                        ACLMessage msgUpdate = new ACLMessage(ACLMessage.INFORM);
                        msgUpdate.setOntology("Send_N");
                        msgUpdate.setContent((N + random.nextGaussian() * 0.01) + "");
                        for (int i = 0; i < EdgesId.length; i++) {
                            msgUpdate.addReceiver(new AID(EdgesId[i] + "@10.42.0.1:1099/JADE"));
                        }
                        send(msgUpdate);
                    } else {
                        System.out.println(getLocalName() + " not send!");
                    }
                }
            }

            @Override
            public boolean done() {
                return false;
            }
        });
    }
}
