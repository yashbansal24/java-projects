 package BlockingQueue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlockingQueue {

    //private static Long maxInMenorySize = 1L;
    private static Long minFlushSize = 3L;

    private static String baseDirectory = "temp/";
    private static String fileNameFormat = "Table-2";

    private static String  currentWriteFile = "";

    private static List<Object>  currentQueue = new LinkedList<Object>();
    private static List<Object>  lastQueue = new LinkedList<Object>();

    static{
        try {
            load();
        } catch (IOException e) {
            System.out.println("Unable To Load");
            e.printStackTrace();
        }
    }

    private static void load() throws IOException{
        File baseLocation = new File(baseDirectory);
        List<String> fileList = new ArrayList<String>();

        for(File entry : baseLocation.listFiles()){
            if(!entry.isDirectory() && entry.getName().contains(fileNameFormat)){
                fileList.add(entry.getAbsolutePath());
            }
        }

        Collections.sort(fileList);

        if(fileList.size()==0){
            //currentQueue = lastQueue = new ArrayList<Object>();
            currentWriteFile = baseDirectory + "Table-1";
            BufferedWriter writer = new BufferedWriter(new FileWriter(currentWriteFile));
            while (!lastQueue.isEmpty()){
                writer.write(lastQueue.get(0).toString()+ "\n");
                lastQueue.remove(0);
            }
            writer.close();
        }else{
            if(fileList.size()>0){
                    BufferedReader reader = new BufferedReader(new FileReader(fileList.get(0)));
                    String line=null;
                    while ((line=reader.readLine())!=null){
                        currentQueue.add(line);
                    }
                    reader.close();
                    File toDelete = new File(fileList.get(0));
                    toDelete.delete();
            }

            if(fileList.size()>0){
                BufferedReader reader = new BufferedReader(new FileReader(fileList.get(fileList.size()-1)));
                currentWriteFile = fileList.get(fileList.size()-1);
                String line=null;
                while ((line=reader.readLine())!=null){
                    lastQueue.add(line);
                }
                reader.close();
                //lastFileNameIndex=Long.parseLong(fileList.get(fileList.size()).substring(6, 9));
            }
        }

    }

    private void loadFirst() throws IOException{
        File baseLocation = new File(baseDirectory);
        List<String> fileList = new ArrayList<String>();

        for(File entry : baseLocation.listFiles()){
            if(!entry.isDirectory() && entry.getName().contains(fileNameFormat)){
                fileList.add(entry.getAbsolutePath());
            }
        }

        Collections.sort(fileList);

        if(fileList.size()>0){
                BufferedReader reader = new BufferedReader(new FileReader(fileList.get(0)));
                String line=null;
                while ((line=reader.readLine())!=null){
                    currentQueue.add(line);
                }
                reader.close();
                File toDelete = new File(fileList.get(0));
                toDelete.delete();
        }
    }

    public Object pop(){
        if(currentQueue.size()>0)
            return  currentQueue.remove(0);

        if(currentQueue.size()==0){
            try {
                loadFirst();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if(currentQueue.size()>0)
            return  currentQueue.remove(0);
        else
            return null;
    }

    public synchronized Object waitTillPop() throws InterruptedException{
        if(currentQueue.size()==0){
            try {
                loadFirst();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(currentQueue.size()==0)
                wait();
        }
        return currentQueue.remove(0);
    }

    public synchronized void push(Object data) throws IOException{
        lastQueue.add(data);
        this.notifyAll();
        if(lastQueue.size()>=minFlushSize){
            BufferedWriter writer = new BufferedWriter(new FileWriter(currentWriteFile));
            while (!lastQueue.isEmpty()){
                writer.write(lastQueue.get(0).toString() + "\n");
                lastQueue.remove(0);
            }
            writer.close();

            currentWriteFile  = currentWriteFile.substring(0,currentWriteFile.indexOf("-")+1) + 
                    (Integer.parseInt(currentWriteFile.substring(currentWriteFile.indexOf("-")+1,currentWriteFile.length())) + 1);
        }
    }

    public static void main(String[] args) {
        try {
            BlockingQueue bq = new BlockingQueue();

            for(int i =0 ; i<=9 ; i++){
                bq.push(""+i);
            }

            System.out.println(bq.pop());
            // System.out.println(bq.pop());
            // System.out.println(bq.pop());
            // System.out.println(bq.pop());

            System.out.println(bq.waitTillPop());
            System.out.println(bq.waitTillPop());
            // System.out.println(bq.waitTillPop());
            // System.out.println(bq.waitTillPop());
            // System.out.println(bq.waitTillPop());
            // System.out.println(bq.waitTillPop());

            // System.out.println(bq.waitTillPop());
            


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}