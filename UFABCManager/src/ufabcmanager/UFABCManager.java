package ufabcmanager;

import java.io.*;
import java.util.Scanner;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.GridLayout;

public class UFABCManager {
    static ContainerController containerController;
    static AgentController agentController;
    static int TURMAS;
    static int SALAS;
    static int DOCENTES;
    static String[] args;
    static String nomeDocente;
    static String disciplinaDocente;
    static String nomeTurma;
    static String disciplinaTurma;
    static String nomeSala;
    public static FileWriter arq;
    public static PrintWriter gravarArq;
    static String Horarios;
    public static void main(String[] args) throws InterruptedException, IOException 
    {
        Horarios = "";
        

        String agentes="";
        String message = "Bem vindo ao UFABCManager, o\n"+
                         "gerenciador de turmas da UFABC!\n"+
                         "Pronto para começar?";
        JOptionPane.showConfirmDialog(null, message, "Olá!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        startMainContainer("127.0.0.1", Profile.LOCAL_PORT, "UFABC");
        //addAgent(containerController, "rma", jade.tools.rma.rma.class.getName(), null);

        if(true) {
            Scanner scanner = new Scanner(UFABCManager.class.getResourceAsStream("Entrada.txt"));
            String s = new String();
            while(scanner.hasNextLine()){
                
                s = scanner.nextLine();
                String[] split = s.split(" ");
                switch(split[0]) {
                    case "Turma"://Turma Nome_da_turma disciplina
                        args = new String[2];
                        args[0] = split[2];
                        args[1] = split[3];
                        addAgent(containerController, "Turma-"+split[1], Turma.class.getName(), args );
                        agentes = agentes + "Turma-" + split[1] + ",";
                        
                        break;
                    case "Docente"://Docente Nome_do_docente disciplina
                        args = new String[1];
                        args[0] = split[2];
                        addAgent(containerController, "Prof-"+split[1], Docente.class.getName(), args );
                        agentes = agentes + "Prof-"+split[1] + ",";
                        break;
                    case "Sala"://Sala Numero_da_sala
                        addAgent(containerController, "Sala-"+split[1], Sala.class.getName(), null );
                        agentes = agentes + "Sala-"+split[1] + ",";
                        break;
                }
            }
                    addAgent(containerController, "Sniffer", "jade.tools.sniffer.Sniffer", 
                                       new Object[]{"df",";","Sala-201",";","Sala-202",";","Prof-MARIO_LESTON_REY",";","Prof-CONRADOUGUSTUS_DE_MELO",";","Turma-NABIS0005-15SA",";","Turma-NABIJ0207-15SA"});
        } else {
            TURMAS = Integer.parseInt(JOptionPane.showInputDialog("Qual a quantidade de turmas?"));
            SALAS = Integer.parseInt(JOptionPane.showInputDialog("Qual a quantidade de salas?"));
            DOCENTES = Integer.parseInt(JOptionPane.showInputDialog("Qual a quantidade de docentes?"));

            for(int i = 1; i <= DOCENTES; i++) {
                boolean preenchido = false;
                while(preenchido == false) {
                    preenchido = docenteForm();
                }
                args = new String[1];
                args[0] = disciplinaDocente;
                addAgent(containerController, "Prof-"+nomeDocente, Docente.class.getName(), args );
                nomeDocente = "";
            }
            for(int i = 1; i <= TURMAS; i++) {
                boolean preenchido = false;
                while(preenchido == false) {
                    preenchido = turmaForm();
                }
                args = new String[2];
                args[0] = disciplinaTurma;
                args[1] = "";
                addAgent(containerController, "Turma-"+nomeTurma, Turma.class.getName(), args );
                nomeTurma = "";
            }
            for(int i = 1; i <= SALAS; i++) {
                boolean preenchido = false;
                while(preenchido == false) {
                    preenchido = salaForm();
                }
                addAgent(containerController, "Sala-"+nomeSala, Sala.class.getName(), null );
                nomeSala = "";
            }
            
        }
        try
        {
           Thread.sleep(200000);
        }
        catch(Exception e)
        {
           System.out.println("Erro: " + e);
        }
        showLongTextMessageInDialog(Horarios);
        //JOptionPane.showMessageDialog(null, Horarios);

    }

    public static void startMainContainer(String host, String port, String name) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        profile.setParameter(Profile.PLATFORM_ID, name);
        
        containerController = runtime.createMainContainer(profile);
    }

    public static void addAgent(ContainerController cc, String agent, String classe, Object[] args) {
        try {
            agentController = cc.createNewAgent(agent, classe, args);
            agentController.start();
        } catch (StaleProxyException s) {
            s.printStackTrace();
        }
    }
    
    private static boolean docenteForm() {
        JTextField nome = new JTextField("");
        JTextField disciplina = new JTextField("");
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Nome do docente:"));
        panel.add(nome);
        panel.add(new JLabel("Disciplina que lecionará:"));
        panel.add(disciplina);
        int result = JOptionPane.showConfirmDialog(null, panel, "Informações de Docente",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !nome.getText().equals("") && !disciplina.getText().equals("")) {
                nomeDocente = nome.getText();
                disciplinaDocente = disciplina.getText();
                return true;
        } else {
            return false;
        }
    }
    
    private static boolean turmaForm() {
        JTextField nome = new JTextField("");
        JTextField disciplina = new JTextField("");
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Nome da turma:"));
        panel.add(nome);
        panel.add(new JLabel("Disciplina que será lecionada para ela:"));
        panel.add(disciplina);
        int result = JOptionPane.showConfirmDialog(null, panel, "Informações de Turma",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !nome.getText().equals("") && !disciplina.getText().equals("")) {
                nomeTurma = nome.getText();
                disciplinaTurma = disciplina.getText();
                return true;
        } else {
            return false;
        }
    }
    
    private static boolean salaForm() {
        JTextField nome = new JTextField("");
        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Nome da sala:"));
        panel.add(nome);
        int result = JOptionPane.showConfirmDialog(null, panel, "Informações de Sala",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION && !nome.getText().equals("")) {
                nomeSala = nome.getText();
                return true;
        } else {
            return false;
        }
    }
    
    private static void showLongTextMessageInDialog(String longMessage) {
        JTextArea textArea = new JTextArea(longMessage);
        JScrollPane scrollPane = new JScrollPane(textArea);  
        textArea.setLineWrap(true);  
        textArea.setWrapStyleWord(true); 
        scrollPane.setPreferredSize( new Dimension( 600, 600 ) );
        JOptionPane.showMessageDialog(null, scrollPane, "Turmas alocadas", JOptionPane.PLAIN_MESSAGE, null);
    }
}
