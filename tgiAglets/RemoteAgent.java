package tgiAglets;

/**
 * Created on 10/07/2009
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.aglet.Aglet;
import com.ibm.aglet.RequestRefusedException;
import com.ibm.aglet.event.MobilityAdapter;
import com.ibm.aglet.event.MobilityEvent;

public class RemoteAgent extends Aglet {

    BufferedReader sueLineReader = null;

    BufferedReader bolLineReader = null;
    
    BufferedReader scsLineReader = null;

    StringTokenizer token = null;

    String argIP = null;

    String strLineRecd = null;

    String fileName = null;

    String strToToken = null;

    String temp = null;

    String unknownEvent = null;
    
    String allRegs = null;

    boolean isRemote = false;

    int tempNum = 0;
   
    Pattern p = null;
    
    Matcher m = null;
    
    public void onCreation(final Object o) {
        
        //método para inicializar os args para criação do nome do arquivo
        initArguments(o);
        
        addMobilityListener(new MobilityAdapter() {

            public void onArrival(MobilityEvent me) {

                StrAtaques atk = new StrAtaques();

                if (!isRemote) {
                    //se isRemote for false, realiza a leitura do log      
                    searchCommonStrs();        
                } else {
                     //chama método para escrita dos registros coletados
                    createFileOnReturn(argIP);
                     // após realizar escrita dos registros o agente é destruído
                    dispose();
                }

            }
        });
    }

    public void initArguments(Object o){
        
        //criação da data para registro e diferenciar os arquivos criados
        Date date = new Date();		
        
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        
        String dateStr = dateFormat.format(date);
        
        //argumentos inicializados na criação do agente
        this.argIP = (String) o;
        
        this.argIP = argIP + " - " + dateStr;
        
        this.fileName = argIP;       
    }
    
    public void searchCommonStrs(){
        ArrayList patternAtksArray = new ArrayList(); //registros padrão da classe

        allRegs = "";
        String comparePatterns = null;
        String compareRegs = null;
        String temp1 = "";
        String temp2 = "";

        try{

            scsLineReader = new BufferedReader(new FileReader("C:/log.txt"));
            String s;
            
            patternAtksArray = StrAtaques.array; //preenche com os padrões de ataque
            
            while ((s = scsLineReader.readLine()) != null){
                //System.out.println(s);	//exibe o conteúdo do log no console do eclipse
                for (int i = 0; i < patternAtksArray.size(); i++){
                    comparePatterns = (String) patternAtksArray.get(i);
                    p = Pattern.compile(comparePatterns);
                    //System.out.println("padrao sendo procurado : " +padroes);
                    m = p.matcher(s);
                    
                    while(m.find()){
                    	allRegs = allRegs + s + "\n";
                    }
                }
                temp1 += searchBOverflowOnLog(s);
                temp2 += searchUnknownExtensions(s);
                
            }
            
//            allRegs += searchBOverflowOnLog();
//            allRegs += searchUnknownExtensions();
            allRegs = allRegs + temp1 + temp2;
            
            scsLineReader.close();
            
            if(allRegs.length() < 1) {//se não existe caracteres na string <dispose>
                System.out.println("nao houve ameaça detectada!!!!!");
                dispose();
            } 
  
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
            System.out.println("Não foi possível a leitura do arquivo em regexMethod");
            //System.out.println("regex fnfe");
        } catch (IOException ioe){
            ioe.printStackTrace();
            System.out.println("Excessão I/O origem regexMethod");
            //System.out.println("regex ioe");
        } finally {
            //liberando todos os recursos utilizados
            argIP += "\n" + allRegs;
            System.out.println("tamanho de argIP > " +argIP.length());
            System.out.println("conteudo de artIP > " +argIP);
            patternAtksArray = null;
            isRemote = true;
            scsLineReader = null;
            bolLineReader = null;
            strToToken = null;
	        unknownEvent = null;
	        token = null;
            temp = null;
            comparePatterns = null;
            compareRegs = null;
            p = null;
            m = null;
            allRegs = null;
        }
        
        try {
            this.dispatch(new URL("atp://127.0.0.1")); //IP da máquina origem -
            // 192.168.0.2
        } catch (RequestRefusedException rre) {
            rre.printStackTrace();
            System.out.println("Solicitação de dispatch causou uma excessão");
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            System.out.println("Argumento URL no método dispatch está incorreto");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Excessão de I/O referente ao dispatch");
        }
    }
    
    
    public String searchUnknownExtensions(String line){
        ArrayList knownExtensionsArray = new ArrayList(); //registros padrão da classe
        //String searchExt = null;
        String comparingRegStrs = null;
        String strRegex = "\\.\\w\\w\\w";		//formato de uma extensão (.pdf - .ppt - .jar)
        String strTemp = null;
        String returnString = "";
        boolean b = false;
        try {
//            sueLineReader = new BufferedReader(new FileReader("C:/Log.txt"));
//             String s = line;
            
            knownExtensionsArray = StrAtaques.extensions;
            
//            while((s = sueLineReader.readLine()) != null){
                
                p = Pattern.compile(strRegex);
                comparingRegStrs = line;
                m = p.matcher(comparingRegStrs);
                
                while(m.find()){
                    strTemp = m.group();                       
                    //System.out.println("encontrado o registro >" +m.group()+ "<" );
                    for(int i = 0; i < knownExtensionsArray.size(); i++){
                        String keTmpStr = (String)knownExtensionsArray.get(i);
                        //System.out.println("padrao da classe -> ke >" +ke+ "<");
                        //System.out.println("linha sendo avaliada : " +comparingRegStrs);
                        if(strTemp.equals(keTmpStr)){
                            b = true;
                            break;
                        }
                        else {
                            b = false;
                        }
                    }
                }
                if(b == false){
                    returnString += comparingRegStrs + "\n";
                }
//            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Excessão origem searchUnknownExtensions");
        } finally {
        	knownExtensionsArray = null;
            comparingRegStrs = null;
            strRegex = null;
            sueLineReader = null;
            p = null;
            m = null;
        }
        return returnString;
    }

    public String searchBOverflowOnLog(String line) {

        unknownEvent = "";

        try {
                token = new StringTokenizer(line);
                while (token.hasMoreTokens()) {
                    temp = token.nextToken();
                    tempNum = (token.countTokens());

                    switch (tempNum) {

	                    case 8:  //testa o tamanho máximo permitido para os IP´s
	                        unknownEvent = (temp.length() > 15) ? unknownEvent + line + "\n" : unknownEvent + "";
	                        break;

	                    case 2: //testa o tamanho máximo permitido para o campo registro das requisições
	                        unknownEvent = (temp.length() > 256) ? unknownEvent + line + "\n" : unknownEvent + "";
	                        break;
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Excessão origem bufferOverflowAudit");
        }
        //retorna a string com os registros suspeitos ou "" se nada foi encontrado

        return unknownEvent;
    }
    
    public void createFileOnReturn(String result) {
        System.out.println("arquivo sendo criado \n" +result);

        try {
            File file = new File("c:/LogRegs/" +fileName+ ".txt");
            //grava os registros recebidos no arquivo com nome do IP origem dos registros
            PrintWriter printWriter = new PrintWriter(file);
            //PrintWriter printWriter = new PrintWriter("c:/LogRegs/" + fileName+ ".txt");
            printWriter.print(result);
            printWriter.flush();
            printWriter.close();
        } catch (IOException ioe) {
            System.out.println("Erro na criação do arquivo fileName");
        }
    }
}

class StrAtaques {
	
	//atk# são strings não permidas no log. Se forem encontradas caracterizam a ocorrência do ataque 
	
    private String atk1 = "\\~";

    private String atk2 = "\\#";

    private String atk3 = "\\////////";

    private String atk4 = "\\.exe";

    private String atk5 = "\\^";

    private String atk6 = "\\+";

    private String atk7 = "\\%00";

    private String atk8 = "\\.nmap";

    private String atk9 = "\\*";
    
    //knownExt# são extensões permitida dentro do log
    
    private String knownExt1 = ".php";
    
    private String knownExt2 = ".html";
    
    private String knownExt3 =  ".jpg";
    
    private String knownExt4 = ".jsp";
    
    private String knownExt5 = ".htm";
    
    static ArrayList array = new ArrayList();
    
    static ArrayList extensions = new ArrayList();
    

    StrAtaques() {
        array.add(atk1);
        array.add(atk2);
        array.add(atk3);
        array.add(atk4);
        array.add(atk5);
        array.add(atk6);
        array.add(atk7);
        array.add(atk8);
        array.add(atk9);
        
        extensions.add(knownExt1);
        extensions.add(knownExt2);
        extensions.add(knownExt3);
        extensions.add(knownExt4);
        extensions.add(knownExt5);
    }
}
