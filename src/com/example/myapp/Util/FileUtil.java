package com.example.myapp.Util;

import android.os.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для работы с файлами на карте и внутренней памяти.
 * Created by Anama on 12.01.2015.
 */
public class FileUtil
{

    /**
     * Читает данные в массив
     * @param filePath полный путь к файлу или частичный если isSeparetePath=true, те относительно корня SD карты, без / вначале
     * @param isSeparetePath
     * @return
     */
    public static  byte[] readFormSD(String filePath, boolean isSeparetePath)
    {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }


        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        File sdFile;

        if(isSeparetePath==true)  sdFile = new File(sdPath.getAbsolutePath() + "/" + filePath);
        else sdFile = new File(filePath);//точный путь




        ByteArrayOutputStream out = null;
        InputStream input = null;
        try{
            out = new ByteArrayOutputStream();
            input = new BufferedInputStream(new FileInputStream(sdFile));
            int data = 0;
            while ((data = input.read()) != -1){
                out.write(data);
            }
        }catch (IOException ex)
        {
            Log.v("Ошибка ввода вывода");
        }
        finally{
            if (null != input){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != out){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out.toByteArray();


    }


    /**
     * Создает директорию на SD карте
     * @param dir
     */
    public static void createDir(String dir)
    {

        // проверяем доступность SD
        if (!isExternalStorageWritable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dir);
        // создаем каталог
        if(!sdPath.exists()) sdPath.mkdirs();

    }



   public static void writeFileSDBinary(String dir,String fileName,byte[] data) throws IOException
   {
        // проверяем доступность SD
        if (!isExternalStorageWritable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dir);
        // создаем каталог
       if(!sdPath.exists()) sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileName);

            // открываем поток для записи
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(sdFile));
            // пишем данные
            bw.write(data);
            // закрываем поток
            bw.close();
            Log.v("Файл записан на SD: " + sdFile.getAbsolutePath());

    }


    public static void writeFileSDText(String dir,String fileName,String data) {
        // проверяем доступность SD
        if (!isExternalStorageWritable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dir);
        // создаем каталог
        if(!sdPath.exists()) sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileName);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(data);
                    // закрываем поток
                            bw.close();
            Log.v("Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static File[] getFilesFromDir(String dir)
    {

        if (!isExternalStorageReadable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }


        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        File sdFile  = new File(sdPath.getAbsolutePath() + "/" + dir);

        if(!sdFile.exists()) return null;

       return sdFile.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if(filename.equals(".")) return false;
                if(filename.equals("..")) return false;
                return true;
            }
        });


    }
    public static List<String> getFileNamesFromDir(String dir)
    {

        if (!isExternalStorageReadable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }


        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        File sdFile  = new File(sdPath.getAbsolutePath() + "/" + dir);

        if(!sdFile.exists()) return null;

        File[] files = sdFile.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                if (filename.equals(".")) return false;
                if (filename.equals("..")) return false;
                return true;
            }
        });
    List<String> res=new ArrayList<String>();
        for (File f:files)
        {
            res.add(f.getName());
        }
return res;
    }


    public static void saveObjectSD(String dir, String filename, Serializable obj) throws IOException
    {
        if (!isExternalStorageWritable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dir);
        // создаем каталог
        if(!sdPath.exists()) sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, filename);

        FileOutputStream fos = new FileOutputStream(sdFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }


    public static Object readObjectFromSD(String dir, String filename) throws IOException, ClassNotFoundException
    {
        // проверяем доступность SD
        if (!isExternalStorageReadable()) {
            Log.v("SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }


        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();

        File sdFile = new File(sdPath.getAbsolutePath() + "/" + filename);

        FileInputStream fis = new FileInputStream("temp.out");
        ObjectInputStream oin = new ObjectInputStream(fis);
        return oin.readObject();



    }

    /**
     *  Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     *  Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }




}
