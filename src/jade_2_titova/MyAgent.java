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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author titova_ekaterina
 */
public class MyAgent extends Agent {

    private double N;
    private String[] EdgesId;
    private Random random = new Random();
    private double ratio = 1;
    private double ratioOfDelay = 1;
    private boolean isSend = false;
    private boolean isUpdate = false;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            ratio = Double.parseDouble((String) args[0]);
            ratioOfDelay = Double.parseDouble((String) args[1]);
            N = Integer.parseInt((String) args[2]);
            EdgesId = new String[args.length - 3];
            for (int i = 3; i < args.length; ++i) {
                EdgesId[i - 3] = (String) args[i];
            }
        } else {
            System.out.println("No number");
            doDelete();
        }
        addBehaviour(new SimpleBehaviour(this) {

            public void action() {
                if (getLocalName().compareTo("5") == 0 && !isSend || isUpdate) {
                    ACLMessage startMsg = new ACLMessage(ACLMessage.INFORM);
                    startMsg.setOntology("Send_N");
                    startMsg.setContent((N + random.nextGaussian() * 0.01) + "");
                    for (int i = 0; i < EdgesId.length; i++) {
                        startMsg.addReceiver(new AID(EdgesId[i] + "@10.42.0.1:1099/JADE"));
                    }
                    send(startMsg);
                    isSend = true;
                    isUpdate = false;
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
                    isUpdate = true;
                    if (Math.log(Math.random()) > Math.log(1.0 - ratio)) {
                        if (Math.log(Math.random()) > Math.log(1.0 - ratioOfDelay)) {
                            Random randomNum = new Random();
                            try {
                                Thread.sleep(0 + randomNum.nextInt(2000));
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MyAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        ACLMessage msgUpdate = new ACLMessage(ACLMessage.INFORM);
                        msgUpdate.setOntology("Send_N");
                        msgUpdate.setContent((N + random.nextGaussian() * 0.01) + "");
                        for (int i = 0; i < EdgesId.length; i++) {
                            msgUpdate.addReceiver(new AID(EdgesId[i] + "@10.42.0.1:1099/JADE"));
                        }
                        send(msgUpdate);
                        isUpdate = false;
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
